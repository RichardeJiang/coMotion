package com.example.richardjiang.test.networkHandler.model;

/**
 * Created by Richard Jiang on 6/10/2015.
 */
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Observer;

import com.example.richardjiang.test.networkHandler.Utils;


public class ConnectedDevices{
    private static String[] connectedIps = new String[0];
    private static List<Observer> observables = new LinkedList<Observer>();

    public static void setConnectedIps(String[] ips, byte[] groupOwnerAddr) {
        connectedIps = new String[ips.length + 1];
        connectedIps[0] = Utils.getIpAddressAsString(groupOwnerAddr);
        for(int i = 0 ; i < ips.length ; i++){
            connectedIps[i+1] = ips[i];
        }

        System.out.println("Calling update method, observables size: " + observables.size());
        System.out.println("ConnectedIP Siez: " + connectedIps.length);
        update(connectedIps);
    }

    public static void setConnectedIps(Collection<String> ips, byte[] groupOwnerAddr) {
        String ipArr[] = new String[ips.size() + 1];
        ipArr[0] =  Utils.getIpAddressAsString(groupOwnerAddr);
        int cnt = 1; for(String ip:ips) ipArr[cnt++] = ip;
        connectedIps = ipArr;
        update(ipArr);
    }

    public static void addObserver(Observer observer){
        observables.add(observer);
    }

    private static void update(String[] data) {
        for(Observer obv:observables){
            obv.update(null, data);
        }
    }

    public static String[] getConnectedIps(){
        return connectedIps;
    }
}
