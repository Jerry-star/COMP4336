package com.neoriddle.wifiscanner;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhyuan on 2017/8/12.
 */

public class WifiDirect extends ScanNetworks implements WifiP2pManager.PeerListListener {

    private WifiP2pManager.Channel channel;
    private WifiP2pManager wifiDirectManager;

    private BroadcastReceiver wifiDirectReceiver = null;

    private Button checkButton;
    private Button discoverButton;
    private ListView peersList;

    private List<WifiP2pDevice> peers;
    private WiFiPeerListAdapter peerListAdapter;

    private ProgressDialog progressDialog;

    private boolean isWifiP2pEnabled = false;

    public static final String TAG = "Wifi-Direct";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_direct);

        checkButton = findViewById(R.id.btnCheck);
        checkButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                checkButtonOnclick(v);
            }
        });
        discoverButton = findViewById(R.id.btnDiscover);
        discoverButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                discoverButtonOnclick(v);
            }
        });
        wifiDirectManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = wifiDirectManager.initialize(this, getMainLooper(), null);
    }

    // event function of check wifi-direct button
    public void checkButtonOnclick(View view) {
        if (isWifiP2pEnabled) {
            Toast.makeText(this, R.string.wifiDirect_on_msg, Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this, R.string.wifiDirect_off_msg, Toast.LENGTH_LONG).show();
        }
    }

    // event function of peer discovery button
    public void discoverButtonOnclick(View view) {
        if (!isWifiP2pEnabled) {
            Toast.makeText(this, R.string.wifiDirect_off_msg, Toast.LENGTH_SHORT).show();
        }
        else {
            peers = new ArrayList<WifiP2pDevice>();
            peerListAdapter = new WiFiPeerListAdapter(this, R.layout.peer_list_row, peers);
            peersList = (ListView) findViewById(R.id.peersList);
            peersList.setAdapter(peerListAdapter);
            onInitiateDiscovery();
            wifiDirectManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(WifiDirect.this, "Discovery Initiated", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onFailure(int reasonCode) {
                    Toast.makeText(WifiDirect.this, "Discovery Failed: " + reasonCode, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void onInitiateDiscovery() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(this, "Press back to cancel", "finding peers", true, true,
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) { progressDialog.cancel(); }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // check and listen to the wifi-direct status
        wifiDirectReceiver = new WiFiDirectReceiver(wifiDirectManager, channel, this);
        registerReceiver(wifiDirectReceiver, new IntentFilter(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION));
        registerReceiver(wifiDirectReceiver, new IntentFilter(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION));
        registerReceiver(wifiDirectReceiver, new IntentFilter(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION));
        registerReceiver(wifiDirectReceiver, new IntentFilter(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION));
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        peers.clear();
        peers.addAll(peerList.getDeviceList());
        peerListAdapter.notifyDataSetChanged();
        //((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
        if (peers.size() == 0)
            Log.d(TAG, "No devices found");
    }

    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(wifiDirectReceiver);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}
