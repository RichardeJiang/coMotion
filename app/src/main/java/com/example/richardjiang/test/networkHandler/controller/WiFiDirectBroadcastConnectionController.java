package com.example.richardjiang.test.networkHandler.controller;

/**
 * Created by Richard Jiang on 6/10/2015.
 */
import com.example.richardjiang.test.networkHandler.NetworkService;
import com.example.richardjiang.test.networkHandler.Utils;
import com.example.richardjiang.test.networkHandler.impl.ClientService;
import com.example.richardjiang.test.networkHandler.impl.ServerService;
import com.example.richardjiang.test.activityMain.ApplicationHelper;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.util.Log;


/**
 * A BroadcastReceiver that notifies of important WiFi p2p events.
 *
 * TODO: This should also handle the disconnect request
 */
public class WiFiDirectBroadcastConnectionController extends BroadcastReceiver{
    private final ConnectionInfoListener connectionInfoListener = new ConnectionInfoListener() {
        public void onConnectionInfoAvailable(WifiP2pInfo info) {
            System.out.println("Inside WiFi Direct onConectiionInfoAvailable");

            if(networkService != null) return;

            if(info.isGroupOwner){
                Log.v("DEBUG","This is group owner");
                networkService = new ServerService();
                System.out.println("My addr as group owner: " +
                        Utils.getIpAddressAsString(Utils.getIPv4AddressOfInterface("p2p")));
            } else{
                networkService = new ClientService(info.groupOwnerAddress.getAddress());
                System.out.println("Group owner's Addr from the client side: " +
                        info.groupOwnerAddress.getHostAddress());
            }

//			networkService.sendMessage(MessageCode.message, "This is a test message");
            System.out.println("Going to start service");
            Log.v("DEBUG", "Going to start service");
            networkService.start();
            Log.v("DEBUG","Network service started.");
        }
    };;

    private WifiP2pManager mManager;
    private Channel mChannel;
    private Activity mActivity;
    private IntentFilter mIntentFilter;

    private NetworkService networkService;

    private static WiFiDirectBroadcastConnectionController instance;

    private WiFiDirectBroadcastConnectionController(Activity act){
        mActivity = act;
        mManager = (WifiP2pManager) mActivity.getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(mActivity, mActivity.getMainLooper(), null);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    public IntentFilter getIntentFilter(){
        return mIntentFilter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        //ApplicationHelper.showToastMessage("OnReceive called. Action = " + action);
        Log.v("DEBUG", "OnReceive called. Action = " + action);

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state != WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                ApplicationHelper.finishWithMessage("Wifi P2P not enabled", "Please enable the Wifi P2P first before using this app.");
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            mManager.requestPeers(mChannel, WiFiDirectBroadcastConnectionHelper.peerListListener);
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            if (mManager == null)  return;

            NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {
                mManager.requestConnectionInfo(mChannel, connectionInfoListener);
            } else{
                // TODO: handle disconnect.. the current handle might not be very clean
                if(networkService != null) networkService.interrupt();
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's WiFi state changing
        }
    }

    public void discoverPeers() {
        mManager.discoverPeers(mChannel, WiFiDirectBroadcastConnectionHelper.peerActionListener);
    }

    public void connectToDevice(WifiP2pDevice device, ActionListener actionCallback) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        config.groupOwnerIntent = 15; // set 15 to be the group Owner
        mManager.connect(mChannel, config, actionCallback);
    }

    public static WiFiDirectBroadcastConnectionController getInstance() {
        if(instance == null) instance = new WiFiDirectBroadcastConnectionController(ApplicationHelper.getActivityInstance());
        return instance;
    }

    public static NetworkService getNetworkService(){
        if(instance == null) return null;
        return instance.networkService;
    }
}
