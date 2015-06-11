package com.example.richardjiang.test.networkHandler.activity;

/**
 * Created by Richard Jiang on 6/10/2015.
 */
import com.example.richardjiang.test.R;
import com.example.richardjiang.test.activityMain.ApplicationHelper;
import com.example.richardjiang.test.cameraHandler.CameraActivity;
import com.example.richardjiang.test.networkHandler.controller.WiFiDirectBroadcastConnectionController;
import com.example.richardjiang.test.networkHandler.model.Phone;
import com.example.richardjiang.test.networkHandler.view.WifiListAdapter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

//import com.example.sesame_social_gaming_project.R;

import com.example.richardjiang.test.networkHandler.NetworkActivityTemplate;

public class PeerSettingActivity extends NetworkActivityTemplate implements OnItemClickListener{
    private static final int ACTION_DISCONNECT = 1;
    private static final int ACTION_CONNECT = 2;
    private ListView lv;
    WifiListAdapter adapter;

    @Override
    protected boolean performConnectionDiscovery(){return true;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peer_setting);

        lv = (ListView) findViewById(R.id.lvPhonesList);
        adapter = new WifiListAdapter(this);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
        WiFiDirectBroadcastConnectionController.getInstance().discoverPeers();
    }


    //Notice that there is no listener for the button here
    //instead in the layout file an onClick is set for the button
    public void clickToRefreshPeerList(View v){
        WiFiDirectBroadcastConnectionController.getInstance().discoverPeers();
    }

    public void startCamera(View view){
        Intent intent = new Intent(ApplicationHelper.getActivityInstance(), CameraActivity.class);
        startActivity(intent);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        final Phone p = (Phone) adapter.getItem(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final int action;

        if(p.deviceInfo.status == WifiP2pDevice.CONNECTED){
            action = ACTION_DISCONNECT;
        } else{
            action = ACTION_CONNECT;
        }

        builder.setMessage(getActionStr(action, true) + " phone?")
                .setPositiveButton(getActionStr(action,false), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        switch(action){
                            case ACTION_CONNECT:
                                Log.v("DEBUG", "Going to connect the phone");
                                networkController.connectToPhone(p);
                                break;
                            case ACTION_DISCONNECT:
                                Log.v("DEBUG", "Going to disconnect the phone");
                                break;
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {}
                });
        builder.show();
    }

    private String getActionStr(int actionType, boolean isMessage){
        switch(actionType){
            case ACTION_CONNECT: return "Connect" + ((isMessage) ? " to" : "");
            case ACTION_DISCONNECT: return "Disconnect" + ((isMessage) ? " from" : "");
            default: return "OK";
        }
    }
}

