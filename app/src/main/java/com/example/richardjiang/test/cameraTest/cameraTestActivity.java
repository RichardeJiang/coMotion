package com.example.richardjiang.test.cameraTest;

/**
 * Created by Richard Jiang on 6/12/2015.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.richardjiang.test.R;
import com.example.richardjiang.test.activityMain.ApplicationHelper;
import com.example.richardjiang.test.networkHandler.NetworkActivityTemplate;
import com.example.richardjiang.test.networkHandler.NetworkService;
import com.example.richardjiang.test.networkHandler.NetworkService.MessageHandleListener;
import com.example.richardjiang.test.networkHandler.Utils;
import com.example.richardjiang.test.networkHandler.controller.WiFiDirectBroadcastConnectionController;
import com.example.richardjiang.test.networkHandler.impl.InternalMessage;
import com.example.richardjiang.test.networkHandler.impl.NetworkMessageObject;

public class cameraTestActivity extends NetworkActivityTemplate {
    private static final String TAG = "MainActivity";

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private Camera mCamera;
    private cameraTestPreview mPreview;
    private MediaRecorder mMediaRecorder;
    private Button captureButton;
    private boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_test);

        mCamera = getCameraInstance();

        mPreview = new cameraTestPreview(this,mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        WiFiDirectBroadcastConnectionController.getInstance().discoverPeers();

        NetworkService.registerMessageHandler(internalMessageListener);

        Button captureButton = (Button) findViewById(R.id.button_capture);
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {


                        try {
                            //byte[] targetIP = Utils.getBytesFromIp("255.255.255.255");
                            byte[] targetIP = Utils.getBytesFromIp("-1.-1.-1.-1");
                            byte[] myIP = WiFiDirectBroadcastConnectionController.getNetworkService().getMyIp();
                            String messageToSend = "startNow";
                            WiFiDirectBroadcastConnectionController.getNetworkService().sendMessage(
                                    new NetworkMessageObject(
                                            messageToSend.getBytes(),
                                            InternalMessage.startNow,
                                            myIP,
                                            targetIP));
                            ApplicationHelper.showToastMessage("I send " + messageToSend + " from " + myIP.toString());
                        } catch (Exception e) {
                            ApplicationHelper.showToastMessage("Failed to send: startNow");
                        }





                        mCamera.takePicture(null,null,mPicture);
                    }
                }
        );


    }

    private NetworkService.MessageHandleListener internalMessageListener = new NetworkService.MessageHandleListener() {

        /*
        @Override
        public boolean handleMessage(NetworkMessageObject message) {
            String messageContent = "";
            messageContent = InternalMessage.getMessageString(message);
            System.out.println(message.getSourceIP() + " says: " + messageContent);

            switch(message.code){
                case InternalMessage.startNow: {
                    ApplicationHelper.showToastMessage(message.getSourceIP() + " send to "
                            + Utils.getIpAddressAsString(message.getTargetIP())
                            + " and says "
                            + messageContent);
                    if (mIsRecordingVideo) {
                        return false;
                    } else {
                        startRecordingVideo();
                        return true;
                    }
                }

                case InternalMessage.stopNow: {
                    ApplicationHelper.showToastMessage(message.getSourceIP() + " send to "
                        + Utils.getIpAddressAsString(message.getTargetIP())
                        + " and says "
                        +messageContent);
                    if (mIsRecordingVideo) {
                        stopRecordingVideo();
                        return true;
                    } else {
                        return false;
                    }
                }
            }
            return false;
        }
        */


        @Override
        public boolean handleMessage(NetworkMessageObject message) {


            //mCamera = getCameraInstance();
            mCamera.takePicture(null,null,mPicture);
            return true;
        }
    };

    @Override
    protected boolean performConnectionDiscovery() {return true;}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
            //c = this.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.
        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }
        return mediaFile;
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile==null) {
                Log.d(TAG,"Error creating files");
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch(FileNotFoundException e) {
                Log.d(TAG,"File not found"+e.getMessage());
            } catch(IOException e) {
                Log.d(TAG,"Error accessing"+e.getMessage());
            }
        }
    };

    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    protected void releaseCamera() {
        if(mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }



}
