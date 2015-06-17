package com.example.richardjiang.test.cameraHandler;

/**
 * Created by Richard Jiang on 6/10/2015.
 */
import android.app.Activity;
import android.os.Bundle;

import com.example.richardjiang.test.R;
import com.example.richardjiang.test.activityMain.ApplicationHelper;
import com.example.richardjiang.test.networkHandler.NetworkActivityTemplate;
import com.example.richardjiang.test.networkHandler.NetworkService;
import com.example.richardjiang.test.networkHandler.controller.WiFiDirectBroadcastConnectionController;
import com.example.richardjiang.test.networkHandler.impl.NetworkMessageObject;
import com.example.richardjiang.test.cameraHandler.CameraFragment;

public class CameraActivity extends NetworkActivityTemplate {

    @Override
    protected boolean performConnectionDiscovery() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);



        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, CameraFragment.newInstance())
                    .commit();

            //for testing
            System.out.println("THIS IS INSIDE THE FRAGMENT INITIALIZATION!");
        }
    }

}