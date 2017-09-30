package com.neoriddle.wifiscanner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

/**
 * Created by zhyuan on 2017/8/12.
 */

public class WiFiDirectReceiver extends BroadcastReceiver {

    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;
    private WifiDirect activity;

    public WiFiDirectReceiver(WifiP2pManager wifiP2pManager, WifiP2pManager.Channel channel, WifiDirect wifiDirect) {
        super();
        this.activity = wifiDirect;
        this.channel = channel;
        this.wifiP2pManager = wifiP2pManager;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // UI update to indicate wifi p2p status.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi Direct mode is enabled
                activity.setIsWifiP2pEnabled(true);
            } else {
                activity.setIsWifiP2pEnabled(false);
                //activity.resetData();
            }
            Log.d(WifiDirect.TAG, "P2P state changed - " + state);
        }
        else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            if (wifiP2pManager != null) {
                wifiP2pManager.requestPeers(channel, (WifiP2pManager.PeerListListener) activity);
            }
            Log.d(WifiDirect.TAG, "P2P peers changed");
        }
    }
}
