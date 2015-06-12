package com.example.richardjiang.test.networkHandler;

/**
 * Created by Richard Jiang on 6/10/2015.
 */
import java.util.ArrayList;
import java.util.List;

import com.example.richardjiang.test.networkHandler.controller.NetworkController;
import com.example.richardjiang.test.activityMain.PauseResumeListener;
import com.example.richardjiang.test.activityMain.ApplicationHelper;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

abstract public class NetworkActivityTemplate extends Activity{
    protected NetworkController networkController;

    private List<PauseResumeListener> listeners = new ArrayList<PauseResumeListener>();

    //protected boolean performConnectionDiscovery(){return false;}

    protected boolean performConnectionDiscovery() {return false;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationHelper.setInstance(this);

        networkController = new NetworkController(this);
        registerPauseResumeListener(networkController);
        networkController.performConnectionDiscovery = performConnectionDiscovery();
    }

    protected void registerPauseResumeListener(PauseResumeListener listener){
        this.listeners.add(listener);
    }

    @Override
    protected void onResume(){
        super.onResume();
        for(PauseResumeListener listener:listeners)	listener.onResume();
        ApplicationHelper.setInstance(this);
    }

    @Override
    protected void onPause(){
        super.onPause();
        for(PauseResumeListener listener:listeners)	listener.onPause();
    }
}
