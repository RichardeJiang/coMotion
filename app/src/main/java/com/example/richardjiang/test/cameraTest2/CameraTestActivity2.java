package com.example.richardjiang.test.cameraTest2;

import android.app.Activity;
import android.os.Bundle;

import com.example.richardjiang.test.R;
import com.example.richardjiang.test.networkHandler.NetworkActivityTemplate;

/**
 * Created by Richard Jiang on 6/15/2015.
 */
public class CameraTestActivity2 extends NetworkActivityTemplate {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_2);
        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, Camera2BasicFragment.newInstance())
                    .commit();
        }
    }

}
