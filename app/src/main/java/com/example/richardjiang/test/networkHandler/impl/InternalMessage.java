package com.example.richardjiang.test.networkHandler.impl;

/**
 * Created by Richard Jiang on 6/10/2015.
 */
import java.io.UnsupportedEncodingException;
import java.util.Set;

public class InternalMessage extends NetworkMessageObject{


    public InternalMessage(String messageString, byte internalCode,
                           byte[] sourceIP, byte[] targetIP) {
        super(null, NetworkMessageObject.InternalCode, sourceIP, targetIP);

        try{
            this.messageInBytes = messageString.getBytes("UTF8");
            this.internalCode = internalCode;
        } catch(UnsupportedEncodingException e){}
    }

    public static final byte requestPhoneIps = 1;
    public static final byte responsePhoneIps = 2;
    public static final byte startNow = 3;
    public static final byte stopNow = 4;
    public static final byte updateSkeleton = 5;
    public static final byte sendCoordinateNow = 6;
    public static final byte requestCoordinate = 7;

    public static String getMessageString(NetworkMessageObject msgObj){
        try {
            return new String(msgObj.messageInBytes, "UTF8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;	// Got to fail, gracefully
        }
    }

    public static String getMessageOfRegisteredIps(Set<String> ips) {
        StringBuilder msgBuilder = new StringBuilder();
        boolean first = true;
        for(String ip:ips){
            if(first)first = false;
            else msgBuilder.append(',');

            msgBuilder.append(ip);
        }
        return msgBuilder.toString();
    }
}