package com.neoriddle.wifiscanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import java.util.List;

/**
 * Created by zhyuan on 2017/8/12.
 */

public class SignInActivity extends Activity {

    private WifiManager wifiManager;
    private ScanResult selectedWifi;
    private String identity;
    private String networkPass;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.network_list);

        Intent intent = getIntent();
        selectedWifi = intent.getExtras().getParcelable("SelectedWifi");
        identity = intent.getExtras().getString("Identity");
        networkPass = intent.getExtras().getString("NetworkPass");
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        connectWiFi(selectedWifi);
        finish();
    }

    public void connectWiFi(ScanResult scanResult) {
        try {

            Log.v("rht", "Item clicked, SSID " + scanResult.SSID + " Security : " + scanResult.capabilities);
            Log.v("rht", "Username: " + identity + " Password: " + networkPass);

            String networkSSID = scanResult.SSID;

            WifiConfiguration conf = new WifiConfiguration();
            conf.SSID = networkSSID;
            conf.status = WifiConfiguration.Status.ENABLED;

            if (scanResult.capabilities.toUpperCase().contains("WEP")) {
                Log.v("rht", "Configuring WEP");
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);

                if (networkPass.matches("^[0-9a-fA-F]+$")) {
                    conf.wepKeys[0] = networkPass;
                } else {
                    conf.wepKeys[0] = "\"".concat(networkPass).concat("\"");
                }

                conf.wepTxKeyIndex = 0;

            } else if (scanResult.capabilities.toUpperCase().contains("WPA")) {
                Log.v("rht", "Configuring WPA");

                WifiEnterpriseConfig enterpriseConf = new WifiEnterpriseConfig();
                enterpriseConf.setIdentity(identity);
                enterpriseConf.setPassword(networkPass);
                enterpriseConf.setEapMethod(WifiEnterpriseConfig.Eap.PEAP);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);
                conf.enterpriseConfig = enterpriseConf;
                /*for non Enterprise Network
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                conf.preSharedKey = "\"" + networkPass + "\"";*/

            } else {
                Log.v("rht", "Configuring OPEN network");
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                conf.allowedAuthAlgorithms.clear();
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            }

            int networkId = wifiManager.addNetwork(conf);

            Log.v("rht", "Add result " + networkId);

            List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
            for (WifiConfiguration i : list) {
                if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                    Log.v("rht", "WifiConfiguration SSID " + i.SSID);

                    boolean isDisconnected = wifiManager.disconnect();
                    Log.v("rht", "isDisconnected : " + isDisconnected);

                    boolean isEnabled = wifiManager.enableNetwork(i.networkId, true);
                    Log.v("rht", "isEnabled : " + isEnabled);

                    boolean isReconnected = wifiManager.reconnect();
                    Log.v("rht", "isReconnected : " + isReconnected);

                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
