package com.example.richardjiang.test.remoteSensorHandler;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by Richard Jiang on 6/26/2015.
 */
public class WearableMessageService extends IntentService {
    private static final String TAG = "WearableMessageService";
    private String commandType;

    private GoogleApiClient mGoogleApiClient;

    //default constructor for IntentService
    public WearableMessageService() {
        super("WearableMessageService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();


    }

    @Override
    public void onStart(Intent intent, int startID) {
        super.onStart(intent, startID);
        Bundle message = intent.getExtras();
        if(message == null) {
            Log.d(TAG, "Command not received");
        }
        else {
            commandType = (String) message.get(Utils.STORE_COMMAND);
        }
    }

    /*

    the corresponding putExtra method which should be inserted inside the caller

    Intent serviceIntent = new Intent(this,ListenLocationService.class);
    serviceIntent.putExtra("From", "Main");
    startService(serviceIntent);
    */
    @Override
    protected void onHandleIntent(Intent intent) {
        if(commandType.equals(Utils.START_MEASUREMENT)) {
            for (String node : getNodes()) {
                sendMessage(node, Utils.START_MEASUREMENT);
            }
        }
        else if(commandType.equals(Utils.STOP_MEASUREMENT)) {
            for (String node : getNodes()) {
                sendMessage(node, Utils.STOP_MEASUREMENT);
            }
        }
    }

    private Collection<String> getNodes() {
        HashSet<String> results = new HashSet<String>();
        NodeApi.GetConnectedNodesResult nodes =
                Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();

        for (Node node : nodes.getNodes()) {
            results.add(node.getId());
        }

        return results;
    }

    private void sendMessage(String node, final String message) {
        Log.d(TAG, "Sending Message: " + message + " to Node: " + node);
        Wearable.MessageApi.sendMessage(
                mGoogleApiClient, node, message, new byte[0]).setResultCallback(
                new ResultCallback<MessageApi.SendMessageResult>() {
                    @Override
                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                        if (!sendMessageResult.getStatus().isSuccess()) {
                            Log.e(TAG, "Failed to send message with status code: "
                                    + sendMessageResult.getStatus().getStatusCode());
                        }
                    }
                }
        );
    }

}
