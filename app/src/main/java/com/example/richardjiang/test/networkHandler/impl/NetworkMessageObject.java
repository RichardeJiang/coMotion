package com.example.richardjiang.test.networkHandler.impl;

/**
 * Created by Richard Jiang on 6/10/2015.
 */
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Date;

import com.example.richardjiang.test.networkHandler.Utils;


public class NetworkMessageObject {
    public static final byte InternalCode = 0;
    private long sendTime;
    public byte code;
    public byte internalCode = 0;

    // IPv4 IP address, when targetIP = 0.0.0.0, do broadcast
    byte[] sourceIP; byte[] targetIP;
    byte[] messageInBytes;

    public NetworkMessageObject clone(){
        NetworkMessageObject clone = new NetworkMessageObject(messageInBytes, sendTime, code, sourceIP, targetIP);
        clone.internalCode = this.internalCode;
        return clone;
    }

    // sendTime (long) + code (byte) + + internalCode(byte) + sourceIP(4 bytes) + targetIP(4 bytes) + messageLength(4bytes)
    private static final int headerSize = 8 + 1*2 + 4*2 + 4;
    NetworkMessageObject(byte[] messageByteArray, long sendTime, byte code, byte[] sourceIP, byte[] targetIP) {
        this.sendTime = sendTime; this.messageInBytes = messageByteArray;
        this.code = code; this.sourceIP = sourceIP; this.targetIP = targetIP;
    }
    public NetworkMessageObject(byte[] messageByteArray, byte code, byte[] sourceIP, byte[] targetIP) {
        this.sendTime = (new Date()).getTime(); this.messageInBytes = messageByteArray;
        this.code = code; this.sourceIP = sourceIP; this.targetIP = targetIP;
    }

    private NetworkMessageObject() {}

    ByteBuffer getNewSendBuffer() {
        ByteBuffer buf = ByteBuffer.allocate(headerSize + messageInBytes.length);
        buf.putLong(sendTime); buf.put(code);buf.put(internalCode);
        buf.put(sourceIP); buf.put(targetIP);
        buf.putInt(messageInBytes.length);	buf.put(messageInBytes);

        buf.flip();return buf;
    }

    static NetworkMessageObject decodeMessage(ByteBuffer headerBuffer,
                                              ByteBuffer contentBuffer){
        headerBuffer.flip();
        NetworkMessageObject msgObj = new NetworkMessageObject();
        msgObj.sendTime = headerBuffer.getLong();
        msgObj.code = headerBuffer.get();
        msgObj.internalCode = headerBuffer.get();
        msgObj.sourceIP = new byte[4]; headerBuffer.get(msgObj.sourceIP);
        msgObj.targetIP = new byte[4]; headerBuffer.get(msgObj.targetIP);
        msgObj.messageInBytes = contentBuffer.array();

        return msgObj;
    }

    public Date getSendTime(){
        return new Date(sendTime);
    }

    public byte getMessageCode(){
        return this.code;
    }

    public byte[] getTargetIP() {
        return targetIP;
    }

    public boolean isToBroadcast() {
        return targetIP != null && targetIP.length == 4
                && targetIP[0] == -1 && targetIP[1] == -1
                && targetIP[2] == -1 && targetIP[3] == -1;
    }

    static void continuePrevWritting(SocketChannel client,
                                     ByteBuffer prevBuffer) throws IOException {
        if(prevBuffer == null) return;
        while(prevBuffer.hasRemaining() && client.write(prevBuffer) > 0) continue;
    }

    static void continuePrevReading(SocketChannel client,
                                    ByteBuffer prevBuffer) throws IOException {
        if(prevBuffer == null) return;
        while(prevBuffer.hasRemaining() && client.read(prevBuffer) > 0) continue;
    }

    static ByteBuffer allocateMessageBufferBasedOnHeader(
            ByteBuffer recvHeaderBuffer) {
        recvHeaderBuffer.flip(); int cap = recvHeaderBuffer.capacity();
        recvHeaderBuffer.position(cap - 4);

        int msgLength = recvHeaderBuffer.getInt();
        return ByteBuffer.allocate(msgLength);
    }

    public static ByteBuffer allocateHeaderBuffer() {
        return ByteBuffer.allocate(headerSize);
    }

    public String getSourceIP() {
        return Utils.getIpAddressAsString(sourceIP);
    }

    public byte[] getMessageInBytes(){
        return this.messageInBytes;
    }

    @Override
    /**
     * For testing purpose only
     */
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Source IP: " + Utils.getIpAddressAsString(this.sourceIP) + "\n");
        sb.append("Target IP: " + Utils.getIpAddressAsString(this.targetIP) + "\n");
        sb.append("Send Time: " + getSendTime() + "\n");
        sb.append("Current Time: " + (new Date()) + "\n");
        sb.append("Message Code: " + this.code + "(" + this.internalCode + ")\n");
        sb.append("Message first 3 bytes: ");
        for(int i = 0 ; i < 3 && i < messageInBytes.length ; i++){
            sb.append((messageInBytes[i] + " "));
        }
        sb.append('\n');
        return sb.toString();
    }
    public boolean isInternalMessage() {
        return internalCode != 0;
    }

    public byte getInternalCode(){
        return this.internalCode;
    }
}
