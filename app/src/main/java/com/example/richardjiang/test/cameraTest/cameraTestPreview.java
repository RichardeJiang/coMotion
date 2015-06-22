package com.example.richardjiang.test.cameraTest;

/**
 * Created by Richard Jiang on 6/12/2015.
 */

import android.content.Context;
import android.hardware.Camera;
import android.hardware.camera2.CameraDevice;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class cameraTestPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder myHolder;
    private Camera myCamera;

    public cameraTestPreview(Context context, Camera camera) {
        super(context);
        myCamera = camera;

        myHolder = getHolder();
        myHolder.addCallback(this);
        myHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            myCamera.setPreviewDisplay(holder);
            myCamera.startPreview();
        } catch (IOException e) {
            //
            Log.d("creation", e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        //
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (myHolder.getSurface() == null) {
            return;
        }

        try {
            myCamera.stopPreview();
        } catch (Exception e) {
            //
        }

        try {
            myCamera.setPreviewDisplay(myHolder);
            myCamera.startPreview();
        } catch (IOException e) {
            //
            Log.d("change", e.getMessage());
        }


    }


}