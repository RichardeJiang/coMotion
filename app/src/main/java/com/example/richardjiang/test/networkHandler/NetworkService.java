package com.example.richardjiang.test.networkHandler;

/**
 * Created by Richard Jiang on 6/10/2015.
 */
import java.util.LinkedList;

import com.example.richardjiang.test.networkHandler.impl.NetworkMessageObject;
import com.example.richardjiang.test.networkHandler.impl.ServerService;


public abstract class NetworkService extends Thread{
    public static interface MessageHandleListener {
        /**
         * @param message
         * @return
         * 	true if the handling process is all done -- return from the execution process
         *
         * Assumption: at the time when this method is called, the network service object has been successfully initialized
         */
        public boolean handleMessage(NetworkMessageObject message);
    }

    protected byte[] myIp;

    /**
     * @param messageObject - the message object containing info
     * 	- targetIP
     * 		- 4 bytes, the target IP address sending to.
     *    	  In case of broadcasting, send 0.0.0.0
     */
    public abstract void sendMessage(NetworkMessageObject messageObject);

    private static LinkedList<MessageHandleListener> msgListeners = new LinkedList<MessageHandleListener>();

    public static void registerMessageHandler(MessageHandleListener msgListener){
        for(MessageHandleListener existingListener: msgListeners){
            if(existingListener.equals(msgListener))	return;
        }
        msgListeners.add(msgListener);
    }

    protected void handleRecvMessage(NetworkMessageObject message){
        for(MessageHandleListener listener: msgListeners){
            if(listener.handleMessage(message)) break;
        }
    }
    protected void updateMyIp() {
        if(this.myIp == null){	// NOTE: update the IP address of self
            this.myIp = Utils.getIPv4AddressOfInterface("p2p");
            if(this.myIp == null)	throw new RuntimeException("Not getting IP Address");
        }
    }

    public byte[] getMyIp(){
        return this.myIp;
    }

    public boolean isGroupOwner(){
        return this instanceof ServerService;
    }
}
