package com.example.richardjiang.test.networkHandler.impl;

/**
 * Created by Richard Jiang on 6/10/2015.
 */
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import com.example.richardjiang.test.networkHandler.NetworkService;
import com.example.richardjiang.test.networkHandler.Utils;
import com.example.richardjiang.test.networkHandler.controller.WiFiDirectBroadcastConnectionController;
import com.example.richardjiang.test.networkHandler.model.ConnectedDevices;

import com.example.richardjiang.test.activityMain.ApplicationHelper;

import android.util.Log;

public class ClientService extends NetworkService{

    public ClientService(byte[] groupOwnerAddr) {
        this.groupOwnerAddr = groupOwnerAddr; this.groupOwnerPortNumber = ServerService.PORT_NUM;
        registerMessageHandler(internalMessageListener);
    }

    private static MessageHandleListener internalMessageListener = new MessageHandleListener() {
        @Override
        public boolean handleMessage(NetworkMessageObject message) {
            ClientService clientService = (ClientService) WiFiDirectBroadcastConnectionController.getNetworkService();
            Log.v("DEBUG","******************************************");
            Log.v("DEBUG","client got message: " + message);
            if(message.isInternalMessage()){
                switch(message.internalCode){
                    case InternalMessage.responsePhoneIps:
                        String messageContent = InternalMessage.getMessageString(message);
                        String ips[] = messageContent.split(",");

                        System.out.println("Got Internal Message, code: " + message.internalCode);
                        System.out.println("Message Content: " + messageContent);

                        ConnectedDevices.setConnectedIps(ips, clientService.groupOwnerAddr);
                        return true;
                }

                return false;
            } else{
                return false;
            }
        }
    };

    private byte[] groupOwnerAddr; private int groupOwnerPortNumber;
    private ConcurrentLinkedQueue<NetworkMessageObject> sendQueue = new ConcurrentLinkedQueue<NetworkMessageObject>();

    @Override
    public void sendMessage(NetworkMessageObject msgObj){
        sendQueue.add(msgObj);
    }

    @Override
    public void run(){
        try{
            InetAddress inetAddr = InetAddress.getByAddress(groupOwnerAddr);
            SocketAddress addr = new InetSocketAddress(inetAddr, groupOwnerPortNumber);

            SocketChannel client = null;

            long timeBeforeTryingToConnect = System.currentTimeMillis();
            updateMyIp();

            while(System.currentTimeMillis() - timeBeforeTryingToConnect < 10000){
                try{
                    client = SocketChannel.open();
                    client.configureBlocking(false);
                    client.connect(addr);

                    System.out.println("Start to wait for pending connection");

                    while(!client.finishConnect()){
                        System.out.println("Connection is pending");
                        TimeUnit.MILLISECONDS.sleep(100);
                    }

                    System.out.println("Connection is no longer pending");
                    if(client.isConnected()) break;
                } catch(IOException e){
                    e.printStackTrace();
                    client.close();
                    TimeUnit.MICROSECONDS.sleep(100);
                    break;
                }
            }

            if(!client.isConnected()){
                System.err.println("Unable to initiate connection to the group owner. groupownder address: " + groupOwnerAddr);
                throw new RuntimeException("Unable to initiate connection to the group owner");
            }

            // Try to send info to group owner, for registration
//			sendMessage(new InternalMessage("", InternalMessage.requestPhoneIps, Utils.getIPv4AddressOfInterface("p2p"), groupOwnerAddr));
            Log.v("DEBUG","message sent, code: 0");

            System.out.println("From the client, connection setup :)");

            SocketAttachment socketObj = new SocketAttachment(groupOwnerAddr);
            while(client.isConnected()){
                boolean hasWorkToDo = false;

                List<NetworkMessageObject> messages = socketObj.readMessagesFrom(client);
                if(messages != null){
                    for(NetworkMessageObject message: messages){
                        handleRecvMessage(message);
                        hasWorkToDo = true;
                    }
                }

                NetworkMessageObject message = sendQueue.poll();
                if(message != null){
                    socketObj.sendMessageTo(message, client);
                    hasWorkToDo = true;
                }

                if(!hasWorkToDo){
                    TimeUnit.MILLISECONDS.sleep(100);
                }
            }

            System.out.println("Connection Ended.");
        } catch(IOException e){
            e.printStackTrace();	// FIXME: there're definitely more to do rather than simply print the stack trace
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

