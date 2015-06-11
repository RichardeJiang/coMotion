package com.example.richardjiang.test.networkHandler.impl;

/**
 * Created by Richard Jiang on 6/10/2015.
 */
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.example.richardjiang.test.networkHandler.NetworkService;
import com.example.richardjiang.test.networkHandler.Utils;
import com.example.richardjiang.test.networkHandler.controller.WiFiDirectBroadcastConnectionController;
import com.example.richardjiang.test.networkHandler.model.ConnectedDevices;

import android.R.array;
import android.util.Log;


public class ServerService extends NetworkService{
    public static final int PORT_NUM = 8080;
    private static final byte BroadcastAddr[] = new byte[]{(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF};
    private final Map<String, ConcurrentLinkedQueue<NetworkMessageObject> > sendQueueMap = new TreeMap<String, ConcurrentLinkedQueue<NetworkMessageObject> >();
    private final Map<String, SelectionKey> keyMap = new TreeMap<String, SelectionKey>();

    private static MessageHandleListener internalMessageListener = new MessageHandleListener() {
        @Override public boolean handleMessage(NetworkMessageObject message) {
            ServerService thisService = (ServerService) WiFiDirectBroadcastConnectionController.getNetworkService();
            Log.v("DEBUG","got message: " + message);

            // if not giving to itself, nor broadcast, then forward message
            if(!Arrays.equals(message.targetIP, thisService.myIp)){
                thisService.sendMessage(message);
                return true;
            }

            if(message.isInternalMessage()){
                switch(message.internalCode){
                    case InternalMessage.requestPhoneIps:
                        byte responseTargetIp[] = message.sourceIP;
                        Set<String> ipSet = thisService.keyMap.keySet();
                        String registeredIpsMessageStr = InternalMessage.getMessageOfRegisteredIps(ipSet);
                        thisService.sendMessage(new InternalMessage(registeredIpsMessageStr, InternalMessage.responsePhoneIps, thisService.myIp, responseTargetIp));
                        ConnectedDevices.setConnectedIps(ipSet, thisService.myIp);
                        return true;
                }
                return false;
            } else{
                return false;
            }
        }
    };


    public ServerService(){
        registerMessageHandler(internalMessageListener);
    }

    @Override
    public void sendMessage(NetworkMessageObject messageObject){
        System.out.println("Sending Target Addr: " + Utils.getIpAddressAsString(messageObject.targetIP));

        if(Arrays.equals(messageObject.targetIP,messageObject.sourceIP)) return;

        if(messageObject.isToBroadcast()){
            broadcastMessage(messageObject);
        } else{
            String targetIpAddress = Utils.getIpAddressAsString(messageObject.getTargetIP());
            sendQueueMap.get(targetIpAddress).add(messageObject);
            keyMap.get(targetIpAddress).interestOps(SelectionKey.OP_WRITE | SelectionKey.OP_READ);
        }
    }

    private void broadcastMessage(NetworkMessageObject messageObject){
        for(Map.Entry<String,ConcurrentLinkedQueue<NetworkMessageObject>> entry: sendQueueMap.entrySet()){
            String targetIpStr = entry.getKey();
            // Excluding the original source when broadcasting
            if(Utils.getIpAddressAsString(messageObject.sourceIP).equals(targetIpStr)) continue;

            NetworkMessageObject messageObj = messageObject.clone();
            messageObj.targetIP = Utils.getBytesFromIp(targetIpStr);
            sendMessage(messageObj);
        }

        if(!Arrays.equals(messageObject.sourceIP, myIp)){
            NetworkMessageObject messageObj = messageObject.clone();
            messageObj.targetIP = myIp;
            handleRecvMessage(messageObj);
        }
    }

    @Override
    public void run(){
        try{
            ServerSocketChannel serverChannel = channelSetup();
            serve(serverChannel);
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    private void serve(ServerSocketChannel channel)
            throws IOException, ClosedChannelException,
            UnsupportedEncodingException {
        channel.configureBlocking(false);
        Selector selector = Selector.open();
        channel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("Serve in progress");

        while(selector.isOpen()){
            selector.select();

            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();

            while(iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();

                if(key.isAcceptable()){
                    ServerSocketChannel server = (ServerSocketChannel) key.channel();
                    SocketChannel client = server.accept();
                    byte ipAddr[] = client.socket().getInetAddress().getAddress();
                    String ipAddrString = Utils.getIpAddressAsString(ipAddr);

                    client.configureBlocking(false);
                    SelectionKey k2 = client.register(selector, SelectionKey.OP_READ);
                    k2.attach(new SocketAttachment(ipAddr));

                    sendQueueMap.put(ipAddrString, new ConcurrentLinkedQueue<NetworkMessageObject>());
                    keyMap.put(ipAddrString, k2);

                    updateMyIp();

                    System.out.println("Handled Request from: " + ipAddr);

                    // Broadcast this info
                    Set<String> ipSet = keyMap.keySet();
                    String registeredIpsMessageStr = InternalMessage.getMessageOfRegisteredIps(ipSet);
                    sendMessage(new InternalMessage(registeredIpsMessageStr, InternalMessage.responsePhoneIps, myIp, BroadcastAddr));
                    ConnectedDevices.setConnectedIps(ipSet, myIp);
                } else if(key.isReadable()){
                    SocketChannel client = (SocketChannel) key.channel();
                    SocketAttachment recvObj = (SocketAttachment) key.attachment();

                    // Handle the End of Stream logic here
                    // TODO: Need to verify the behavior
                    if(!client.isConnected()){
                        key.cancel();
                        String remoteIpAddr = Utils.getIpAddressAsString(client.socket().getInetAddress().getAddress());
                        sendQueueMap.remove(remoteIpAddr);
                        keyMap.remove(remoteIpAddr);
                    }

                    List<NetworkMessageObject> messages = recvObj.readMessagesFrom(client);
                    if(messages != null){
                        for(NetworkMessageObject message:messages){
                            handleRecvMessage(message);
                        }
                    }
                } else if(key.isWritable()){
                    SocketChannel client = (SocketChannel) key.channel();
                    SocketAttachment sendingObj = (SocketAttachment) key.attachment();

                    boolean previousSendDoneSuccessfully = true;
                    while(previousSendDoneSuccessfully){
                        previousSendDoneSuccessfully = sendingObj.sendMessageTo(null,client);
                        if(previousSendDoneSuccessfully){
                            NetworkMessageObject packageObj = getSendMessage(sendingObj.targetIpAddr);
                            // stop if there's nothing more to send
                            if(packageObj == null) break;
                            previousSendDoneSuccessfully = sendingObj.sendMessageTo(packageObj,client);
                        }
                    }

                    // All sent, nothing to write already
                    if(previousSendDoneSuccessfully){
                        //key.interestOps(SelectionKey.OP_READ);
                    }
                }
            }
        }
    }

    private ServerSocketChannel channelSetup() throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        ServerSocket serverSocket = serverChannel.socket();
        InetSocketAddress address = new InetSocketAddress(PORT_NUM);
        serverSocket.bind(address);
        return serverChannel;
    }

    private NetworkMessageObject getSendMessage(byte[] targetIpAddr) {
        String targetIpString = Utils.getIpAddressAsString(targetIpAddr);
        return sendQueueMap.get(targetIpString).poll();
    }
}