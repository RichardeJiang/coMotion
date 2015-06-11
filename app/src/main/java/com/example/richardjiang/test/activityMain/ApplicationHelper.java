package com.example.richardjiang.test.activityMain;

/**
 * Created by Richard Jiang on 6/10/2015.
 */
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.wifi.p2p.WifiP2pDevice;
import android.widget.Toast;

import com.example.richardjiang.test.networkHandler.NetworkActivityTemplate;

public class ApplicationHelper {
    private static NetworkActivityTemplate appInstance;

    public static void finishWithMessage(String title,String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(appInstance);
        alert.setTitle(title);
        alert.setMessage(message);
        DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                appInstance.finish();
            }
        };
        alert.setPositiveButton("OK", clickListener);
        alert.show();
    }

    // TODO: may want to change the Length of the toast message,
    // 		 and the way of showing the message.
    public static void showToastMessage(final String message) {
        getActivityInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(appInstance, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    public static String getDeviceStatusStr(int status) {
        switch(status){
            case WifiP2pDevice.CONNECTED:return "Connected";
            case WifiP2pDevice.INVITED:	return "Invited";
            case WifiP2pDevice.FAILED:	return "Failed";
            case WifiP2pDevice.AVAILABLE:	return "Available";
            case WifiP2pDevice.UNAVAILABLE:	return "Unavailable";
            default:	return "Unknown";
        }
    }

    public static void setInstance(NetworkActivityTemplate activityInstance) {
        appInstance = activityInstance;
    }

    public static NetworkActivityTemplate getActivityInstance(){
        return appInstance;
    }
}