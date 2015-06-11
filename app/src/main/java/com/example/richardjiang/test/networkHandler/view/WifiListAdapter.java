package com.example.richardjiang.test.networkHandler.view;

/**
 * Created by Richard Jiang on 6/10/2015.
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import com.example.richardjiang.test.R;
import com.example.richardjiang.test.networkHandler.model.Phone;


import com.example.richardjiang.test.activityMain.ApplicationHelper;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

public class WifiListAdapter extends BaseAdapter implements ListAdapter,
        Observer {
    List<Phone> phoneList = new ArrayList<Phone>();
    Context mContext;

    public WifiListAdapter(Context applicationContext) {
        mContext = applicationContext;
        Phone.addObserver(this);
    }

    public int getCount() {
        return phoneList.size();
    }

    public Object getItem(int position) {
        return phoneList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView == null ? View.inflate(mContext,
                R.layout.connection_list_adapter, null) : convertView;
        view.setPadding(10, 10, 10, 10);
        Phone p = (Phone) getItem(position);
        WifiP2pDevice device = p.deviceInfo;
        TextView tvName = (TextView) view.findViewById(R.id.wifi_device_name);
        TextView tvAddr = (TextView) view.findViewById(R.id.wifi_device_addr);
        TextView tvSts = (TextView) view.findViewById(R.id.wifi_device_status);
        tvName.setText(device.deviceName);
        tvAddr.setText(device.deviceAddress);
        tvSts.setText(ApplicationHelper.getDeviceStatusStr(device.status));
        return view;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void update(Observable observable, Object data) {
        phoneList = (List<Phone>) data;
        notifyDataSetChanged();
    }
}
