package com.example.richardjiang.wearable;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Richard Jiang on 6/24/2015.
 */
public class SensorService extends Service implements SensorEventListener {
    private static final String TAG = "SensorService";

    private final static int SENS_LINEAR_ACCELERATION = Sensor.TYPE_LINEAR_ACCELERATION;

    SensorManager mSensorManager;
    ClientService client;

    public void onCreate() {
        super.onCreate();

        client = ClientService.getInstance(this);

        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("coMotion");
        builder.setContentText("Collecting sensor data..");
        builder.setSmallIcon(R.drawable.ic_launcher);

        startForeground(1, builder.build());

        startMeasurement();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        client.sendSensorData(event.sensor.getType(), event.accuracy, event.timestamp, event.values);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopMeasurement();
    }


    private void startMeasurement() {
        mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));

        Sensor linearAccelerationSensor = mSensorManager.getDefaultSensor(SENS_LINEAR_ACCELERATION);

        if(mSensorManager != null) {
            if(linearAccelerationSensor != null) {
                mSensorManager.registerListener(this, linearAccelerationSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.d(TAG, "No Linear Acceleration Sensor found");
            }
        }

    }

    private void stopMeasurement() {
        if(mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }
    }

}
