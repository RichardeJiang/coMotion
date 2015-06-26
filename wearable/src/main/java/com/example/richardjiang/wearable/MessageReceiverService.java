package com.example.richardjiang.wearable;

/**
 * Created by Richard Jiang on 6/25/2015.
 */
import android.content.Intent;
import android.hardware.Sensor;
import android.net.Uri;
import android.util.Log;


import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class MessageReceiverService extends WearableListenerService {
    private static final String TAG = "MessageReceiverService";

    private ClientService deviceClient;

    @Override
    public void onCreate() {
        super.onCreate();

        deviceClient = ClientService.getInstance(this);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        super.onDataChanged(dataEvents);

        for (DataEvent dataEvent : dataEvents) {
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                DataItem dataItem = dataEvent.getDataItem();
                Uri uri = dataItem.getUri();
                String path = uri.getPath();

                if (path.startsWith("/filter")) {
                    DataMap dataMap = DataMapItem.fromDataItem(dataItem).getDataMap();
                    int filterById = dataMap.getInt(Utils.FILTER);
                    deviceClient.setSensorFilter(filterById);
                }
            }
        }
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "Received message: " + messageEvent.getPath());

        if (messageEvent.getPath().equals(Utils.START_MEASUREMENT)) {
            startService(new Intent(this, SensorService.class));
        }

        if (messageEvent.getPath().equals(Utils.STOP_MEASUREMENT)) {
            stopService(new Intent(this, SensorService.class));
        }
    }
}
