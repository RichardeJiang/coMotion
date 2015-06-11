package com.example.richardjiang.test.networkHandler.model;

/**
 * Created by Richard Jiang on 6/10/2015.
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.net.wifi.p2p.WifiP2pDevice;
import android.util.Log;

public class Phone{
    public WifiP2pDevice deviceInfo;

    private static PhoneObservable observable = new PhoneObservable();
    private static List<Phone> phoneList = new LinkedList<Phone>();

    public Phone(WifiP2pDevice d) {
        this.deviceInfo = d;
    }

    public static void initList(Collection<WifiP2pDevice> deviceList) {
        phoneList = new ArrayList<Phone>();
        for(WifiP2pDevice d: deviceList){
            Log.v("DEBUG", "device status: " + d.status);
            phoneList.add(new Phone(d));
        }

        observable.notifyObservers(phoneList);
        Log.v("DEBUG", "going to update the list adapter with phone list" + phoneList.size());
    }

    public static void addObserver(Observer observer){
        observable.addObserver(observer);
    }

    public static List<Phone> getCurrentList(){
        return phoneList;
    }
}

class PhoneObservable extends Observable{
    @Override
    public void notifyObservers(Object data) {
        setChanged();
        super.notifyObservers(data);
    }
}
