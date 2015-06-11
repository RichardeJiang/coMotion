package com.example.richardjiang.test.networkHandler.controller;

/**
 * Created by Richard Jiang on 6/10/2015.
 */
import com.example.richardjiang.test.networkHandler.NetworkService.MessageHandleListener;
import com.example.richardjiang.test.networkHandler.impl.NetworkMessageObject;
import com.example.richardjiang.test.networkHandler.model.Phone;
import com.example.richardjiang.test.activityMain.ApplicationHelper;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;

public class WiFiDirectBroadcastConnectionHelper {
    static final PeerListListener peerListListener = new PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peers) {
            Phone.initList(peers.getDeviceList());
        }
    };

    public static final WifiP2pManager.ActionListener peerActionListener = new WifiP2pManager.ActionListener(){
        @Override
        public void onSuccess() {
            Log.i("WIFIDIRECT", "Peer Discovery Succeeded. Called from Controller");
        }

        @Override
        public void onFailure(int reason) {
            Log.i("WIFIDIRECT", "Peer Discovery Failed. Called from Controller");
        }
    };

    static final ActionListener peerDiscoveryActionalListener = new ActionListener() {
        @Override
        public void onSuccess() {
            Log.i("WIFIDIRECT", "Connection Successful.");
        }

        @Override
        public void onFailure(int reason) {
            Log.i("WIFIDIRECT", "Connection Failed. Reason Code: " +reason);
        }
    };

    protected static final MessageHandleListener messageHandler = new MessageHandleListener() {
        @Override
        public boolean handleMessage(NetworkMessageObject messageObj) {
            return false;
        }
    };
}
