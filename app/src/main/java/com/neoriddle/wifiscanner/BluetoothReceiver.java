package com.neoriddle.wifiscanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

/**
 * Created by zhyuan on 2017/8/29.
 */

public class BluetoothReceiver extends android.content.BroadcastReceiver {

    private Bluetooth bluActivity;
    private ArrayAdapter<String> peerListAdapter;

    public BluetoothReceiver(Bluetooth bluActivity, ArrayAdapter<String> arrAdapter) {
        this.bluActivity = bluActivity;
        this.peerListAdapter = arrAdapter;
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        // When discovery finds a device
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            // Get the BluetoothDevice object from the Intent
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            // Add the name and address to an array adapter to show in a ListView
            peerListAdapter.add(device.getName() + "\n" + device.getAddress());
            Log.v("debug", "device found");
            Toast.makeText(bluActivity, "Device found", Toast.LENGTH_SHORT).show();
        }
        else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
            Toast.makeText(bluActivity, "Start discovering", Toast.LENGTH_LONG).show();
        }
        else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            Toast.makeText(bluActivity, "Finish discovering", Toast.LENGTH_LONG).show();
        }

    }

}
