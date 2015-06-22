package com.example.richardjiang.test.cameraTest2;

import android.app.Activity;
import android.os.Bundle;

import com.example.richardjiang.test.R;
import com.example.richardjiang.test.cameraTest2.Camera2BasicFragment;
import com.example.richardjiang.test.networkHandler.NetworkActivityTemplate;
import com.example.richardjiang.test.networkHandler.NetworkService;
import com.example.richardjiang.test.networkHandler.controller.WiFiDirectBroadcastConnectionController;
import com.example.richardjiang.test.networkHandler.impl.NetworkMessageObject;

/**
 * Created by Richard Jiang on 6/15/2015.
 */
public class CameraTestActivity2 extends NetworkActivityTemplate {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_2);

        /*
        WiFiDirectBroadcastConnectionController.getInstance().discoverPeers();

        NetworkService.registerMessageHandler(internalMessageListener);
        */

        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, Camera2BasicFragment.newInstance())
                    .commit();
        }
    }

    @Override
    protected boolean performConnectionDiscovery() {
        return true;
    }

    /*
    private NetworkService.MessageHandleListener internalMessageListener = new NetworkService.MessageHandleListener() {

        @Override
        public boolean handleMessage(NetworkMessageObject message) {
            takePicture();

            return true;

        }
    };
    */


}
