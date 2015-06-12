package com.example.richardjiang.test.cameraHandler;

/**
 * Created by Richard Jiang on 6/10/2015.
 */
import android.app.Activity;
import android.os.Bundle;

import com.example.richardjiang.test.R;
import com.example.richardjiang.test.networkHandler.NetworkActivityTemplate;
import com.example.richardjiang.test.networkHandler.controller.WiFiDirectBroadcastConnectionController;

public class CameraActivity extends NetworkActivityTemplate {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, CameraFragment.newInstance())
                    .commit();
        }
    }

}