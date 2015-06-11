package com.example.richardjiang.test.cameraHandler;

/**
 * Created by Richard Jiang on 6/10/2015.
 */
import android.app.Activity;
import android.os.Bundle;

import com.example.richardjiang.test.R;

public class CameraActivity extends Activity {

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