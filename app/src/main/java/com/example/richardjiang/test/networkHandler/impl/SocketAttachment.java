package com.example.richardjiang.test.networkHandler.impl;

/**
 * Created by Richard Jiang on 6/10/2015.
 */
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

enum MessageReadState{
    Idle, ReadHeader, ReadMessage;

    public static MessageReadState decideStateBasedOn(SocketAttachment attachment){
        ByteBuffer header = attachment.recvHeaderBuffer, message = attachment.recvBuffer;

        if(header == null && message == null) return Idle;
        else if(header != null && message == null) return ReadHeader;
        else if(!header.hasRemaining() && message != null) return ReadMessage;

        throw new RuntimeException("Having Unknown State: " + attachment);
    }
}

class SocketAttachment{
    public ByteBuffer sendBuffer;

    public ByteBuffer recvBuffer;
    public ByteBuffer recvHeaderBuffer;

    public byte[] targetIpAddr;

    SocketAttachment(byte[] targetIp) {
        this.targetIpAddr = targetIp;
    }

    @Override
    public String toString(){
        return "recvHeaderBuffer null: " + (recvHeaderBuffer == null)
                + ", recvBuffer null: " + (recvBuffer == null);
    }

    /**
     * Returns true if and only if all message sendings are done.
     * In the case when there's nothing to send, simply returns true
     */
    boolean sendMessageTo(NetworkMessageObject packageObj, SocketChannel client) throws IOException {
        if(hasSomethingToSend()){
            NetworkMessageObject.continuePrevWritting(client,sendBuffer);
            if(sendBuffer.hasRemaining()) return false;
            // FIXME: old code is true, changed to false, not sure if correct
        }

        if(packageObj == null) return true;

        sendBuffer = packageObj.getNewSendBuffer();
        NetworkMessageObject.continuePrevWritting(client,sendBuffer);
        return !sendBuffer.hasRemaining();
    }

    boolean hasSomethingToSend() {
        return sendBuffer != null;
    }

    // In the case when there's no message (most cases), simply return null
    List<NetworkMessageObject> readMessagesFrom(SocketChannel client) throws IOException {
        NetworkMessageObject message = readMessageFrom(client);
        if(message == null) return null;

        ArrayList<NetworkMessageObject> messages = new ArrayList<NetworkMessageObject>();
        messages.add(message);

        while( (message = readMessageFrom(client)) != null) messages.add(message);

        return messages;
    }


    private NetworkMessageObject readMessageFrom(SocketChannel client) throws IOException {
        MessageReadState currentState = MessageReadState.decideStateBasedOn(this);

        switch(currentState){
            case Idle:
                recvHeaderBuffer = NetworkMessageObject.allocateHeaderBuffer();
            case ReadHeader:
                NetworkMessageObject.continuePrevReading(client,recvHeaderBuffer);
                if(recvHeaderBuffer.hasRemaining()) return null;

                recvBuffer = NetworkMessageObject.allocateMessageBufferBasedOnHeader(recvHeaderBuffer);
            case ReadMessage:
                NetworkMessageObject.continuePrevReading(client,recvBuffer);
                if(recvBuffer.hasRemaining()) return null;
                return decodeReadMessageAndClear();
            default: throw new RuntimeException(this.toString());
        }
    }

    private NetworkMessageObject decodeReadMessageAndClear() {
        NetworkMessageObject message = NetworkMessageObject.decodeMessage(recvHeaderBuffer, recvBuffer);
        recvHeaderBuffer = null; recvBuffer = null;
        return message;
    }
}