package com.example.richardjiang.test.networkHandler;

/**
 * Created by Richard Jiang on 6/10/2015.
 */
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;

public class Utils {
    @SuppressWarnings("finally")
    public static byte[] getIPv4AddressOfInterface(String interfacePrefix){
        byte addr[] = null;
        try {
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface netint : Collections.list(nets)){
                String interfaceName = netint.getName();
                if(!interfaceName.startsWith(interfacePrefix))	continue;

                Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
                for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                    byte thisAddr[] = inetAddress.getAddress();
                    if(thisAddr.length != 4) continue;	// only retrieve the IPv4 address
                    addr = thisAddr;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } finally{
            return addr;
        }
    }

    public static String getIpAddressAsString(byte addr[]){
        if(addr == null) return "";

        StringBuilder sb = new StringBuilder(); boolean first = true;
        for(byte b:addr){
            if(first)	first = false;
            else		sb.append('.');
            sb.append((b & 0xFF));
        }
        return sb.toString();
    }

    public static byte[] getBytesFromIp(String ipAddr) {
        String splits[] = ipAddr.split("\\.");
        byte res[] = new byte[splits.length];

        for(int i = 0 ; i < splits.length; i++){
            res[i] = (byte) Integer.parseInt(splits[i]);
        }
        return res;
    }
}
