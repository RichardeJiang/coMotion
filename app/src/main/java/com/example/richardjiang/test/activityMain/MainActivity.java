package com.example.richardjiang.test.activityMain;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.richardjiang.test.R;
import com.example.richardjiang.test.networkHandler.NetworkActivityTemplate;
import com.example.richardjiang.test.networkHandler.activity.PeerSettingActivity;
import com.example.richardjiang.test.networkHandler.controller.WiFiDirectBroadcastConnectionController;


public class MainActivity extends NetworkActivityTemplate {

    private ActivityStatus activityStatus = ActivityStatus.NONE;
    private String debugTag = "MAIN_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Context context = this;
        Button btnFindPeer = (Button) findViewById(R.id.btnPeerSettings);
        btnFindPeer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ApplicationHelper.getActivityInstance(), PeerSettingActivity.class);
                startActivity(intent);
            }
        });

        Button btnHelp = (Button) findViewById(R.id.btnHelp);
        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(context)
                        //.setMessage(R.string.intro_message)
                        .setMessage(R.string.help_message)
                        .setPositiveButton(R.string.got_it, null)
                        .show();
            }
        });

        WiFiDirectBroadcastConnectionController.getInstance().discoverPeers();
    }

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
}
