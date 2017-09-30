package com.neoriddle.wifiscanner;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by zhyuan on 2017/8/19.
 */

public class WiFiPeerListAdapter extends ArrayAdapter<WifiP2pDevice> {
    private final Context context;
    private List<WifiP2pDevice> items;

    /**
     * @param context
     * @param textViewResourceId
     * @param objects
     */
    public WiFiPeerListAdapter(Context context, int textViewResourceId, List<WifiP2pDevice> objects) {
        super(context, textViewResourceId, objects);
        this.items = objects;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.peer_list_row, null);
        }
        WifiP2pDevice device = items.get(position);
        if (device != null) {
            TextView txtDevice = (TextView) v.findViewById(R.id.txtDevice);
            txtDevice.setText(device.toString());
        }
        return v;

    }
}
