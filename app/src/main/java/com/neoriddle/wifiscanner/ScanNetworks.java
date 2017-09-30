package com.neoriddle.wifiscanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.neoriddle.wifiscanner.utils.AndroidUtils;

/**
 * This activity show a list which has all detected wifi networks.
 */
public class ScanNetworks extends Activity implements AdapterView.OnItemClickListener {

	private WifiManager wifiManager;
	private WifiReceiver wifiReceiver;
    private WifiInfo wifiInfo;

    private NetworkStateReceiver networkReceiver;

    private Button scanButton;
	private ListView networksList;;
	private List<ScanResult> results;
    private ScanResult selectedWifi;
    private HashMap<String, ScanResult> filtered;

    private ScanResultsAdapter networkListAdapter;

    private String identity;
    private String networkPass;
    private EditText textIdentity;
    private EditText textNetworkPass;
    private TextView counter;

    private boolean reconnected = true;
    //private boolean showConnection = false;

	private static final int ABOUT_DIALOG = -1;
    private static final int WIFI_DIALOG = -2;

	@Override
    // called when application restart
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.network_list);

        counter = findViewById(R.id.counter);
        scanButton = findViewById(R.id.scanButton);
        scanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                scanButtonOnClick(v);
            }
        });

        wifiReceiver = new WifiReceiver();
        networkReceiver = new NetworkStateReceiver();
		wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
	}

	// event function of scan button
    public void scanButtonOnClick(View view) {
        if(wifiManager.isWifiEnabled()) {
            scanNetworks();
            networksList = (networksList == null) ? (ListView)findViewById(R.id.lstNetworks) : networksList;
            networksList.setOnItemClickListener(this);
            networkListAdapter = new ScanResultsAdapter(this, results);
            networksList.setAdapter(networkListAdapter);
        } else {
            Toast.makeText(this, R.string.wifi_is_not_enabled_msg, Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
    // called when menu is opened and option is clicked
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
            case R.id.mnuRescan:
                scanNetworks();
                networkListAdapter.notifyDataSetChanged();
                return true;
            case R.id.mnuAbout:
                showDialog(ABOUT_DIALOG);
                return true;
            case R.id.mnuWifiDirect:
                startActivity(new Intent(this, WifiDirect.class));
                return true;
            case R.id.mnuBluetooth:
                startActivity(new Intent(this, Bluetooth.class));
                return true;
            case R.id.mnuSensor1:
                startActivity(new Intent(this, MobileSensor.class));
                return true;
            case R.id.mnuSensor2:
                startActivity(new Intent(this, MobileSensor2.class));
                return true;
            case R.id.mnuTCP:
                Intent intent = new Intent(this, Client.class);
                intent.putExtra("WifiInfo", wifiInfo);
                startActivity(intent);
                return true;
            default:

			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onResume() {
        // check and listen to the wifi status
		registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        // check and listen to the network connection status
        registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		super.onResume();
	}

	@Override
	protected void onPause() {
		unregisterReceiver(wifiReceiver);
        unregisterReceiver(networkReceiver);
		super.onPause();
	}

	/**
	 * Trigger a wifi network scanning.
	 */
	public void scanNetworks() {

		boolean scan = wifiManager.startScan();

		if(scan) {
            results = wifiManager.getScanResults();
            /*filtered = new HashMap<String, ScanResult>();

            // remove wifi with duplicate SSID
            for(ScanResult res: results) {
                filtered.put(res.SSID, res);
            }
            results = new ArrayList<ScanResult>(filtered.values());
            // sort wifi based on the signal level
            Collections.sort(results, new Comparator<ScanResult>() {
                @Override
                public int compare(ScanResult lhs, ScanResult rhs) {
                    return rhs.level - lhs.level;
                }
            });
            // only show the first four stronger distinct network
            for(int i = results.size()-1; i >= 4; i--) {
                results.remove(i);
            }*/
            int i = 0;
            for(ScanResult s: results) {
                if(s.SSID.equals("uniwide"))
                    i++;
            }
            String s = "#uniwide: " + i;
            counter.setText(s);
            // display the message after showing the list
            // Toast.makeText(this, getString(R.string.networks_found_msg, results.size()), Toast.LENGTH_LONG).show();
		} else
			switch(wifiManager.getWifiState()) {
			case WifiManager.WIFI_STATE_DISABLING:
				Toast.makeText(this, R.string.wifi_disabling_msg, Toast.LENGTH_LONG).show();
				break;
			case WifiManager.WIFI_STATE_DISABLED:
				Toast.makeText(this, R.string.wifi_disabled_msg, Toast.LENGTH_LONG).show();
				break;
			case WifiManager.WIFI_STATE_ENABLING:
				Toast.makeText(this, R.string.wifi_enabling_msg, Toast.LENGTH_LONG).show();
				break;
			case WifiManager.WIFI_STATE_ENABLED:
				Toast.makeText(this, R.string.wifi_enabled_msg, Toast.LENGTH_LONG).show();
				break;
			case WifiManager.WIFI_STATE_UNKNOWN:
				Toast.makeText(this, R.string.wifi_unknown_state_msg, Toast.LENGTH_LONG).show();
				break;
			}

	}

	// network list listener
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Log.i("HelloListView", "You clicked Item: " + id + " at position:" + position);
        // get the wifi selected by user
        selectedWifi = (ScanResult)networksList.getItemAtPosition(position);
        showDialog(position);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        final AlertDialog.Builder dialog;
        LayoutInflater factory = LayoutInflater.from(this);

        switch (id) {
            case ABOUT_DIALOG:
                final View aboutView = factory.inflate(R.layout.about_dialog, null);
                TextView versionLabel = (TextView)aboutView.findViewById(R.id.version_label);
                versionLabel.setText(getString(R.string.version_msg, AndroidUtils.getAppVersionName(getApplicationContext())));

                return new AlertDialog.Builder(this).
                        setIcon(R.drawable.icon).
                        setTitle(R.string.app_name).
                        setView(aboutView).
                        setPositiveButton(R.string.close, null).
                        create();

            case WIFI_DIALOG:
                final View wifiInfoView = factory.inflate(R.layout.wifi_dialog, null);
                TextView txtBSSID = (TextView) wifiInfoView.findViewById(R.id.BSSID);
                TextView txtIP = (TextView) wifiInfoView.findViewById(R.id.IP);
                TextView txtFreq = (TextView) wifiInfoView.findViewById(R.id.Freq);
                TextView txtSpeed = (TextView) wifiInfoView.findViewById(R.id.SPEED);
                TextView txtProtocol = (TextView) wifiInfoView.findViewById(R.id.Protocol);

                int ip = wifiInfo.getIpAddress();
                String decimalIP = String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff));
                double freq = wifiInfo.getFrequency()*1.0/1000;
                txtIP.setText(wifiInfoView.getContext().getString(R.string.ip, decimalIP));
                txtBSSID.setText(wifiInfoView.getContext().getString(R.string.bssid_msg, wifiInfo.getBSSID()));
                txtFreq.setText(wifiInfoView.getContext().getString(R.string.frecuency_msg, String.format("%.1f", freq)));
                txtSpeed.setText(wifiInfoView.getContext().getString(R.string.speed, String.valueOf(wifiInfo.getLinkSpeed())));
                if(Math.floor(freq) == 2) {
                    if(wifiInfo.getLinkSpeed() <= 11)
                        txtProtocol.setText(wifiInfoView.getContext().getString(R.string.protocol, "802.11 b"));
                    else if(wifiInfo.getLinkSpeed() <= 54)
                        txtProtocol.setText(wifiInfoView.getContext().getString(R.string.protocol, "802.11 g"));
                    else
                        txtProtocol.setText(wifiInfoView.getContext().getString(R.string.protocol, "802.11 n"));
                }
                else {
                    if(wifiInfo.getLinkSpeed() <= 54)
                        txtProtocol.setText(wifiInfoView.getContext().getString(R.string.protocol, "802.11 a"));
                    else if(wifiInfo.getLinkSpeed() <= 72)
                        txtProtocol.setText(wifiInfoView.getContext().getString(R.string.protocol, "802.11 n"));
                    else
                        txtProtocol.setText(wifiInfoView.getContext().getString(R.string.protocol, "802.11 ac"));
                }
                dialog = new AlertDialog.Builder(this);
                dialog.setTitle(wifiInfo.getSSID());
                dialog.setMessage("properties");
                dialog.setView(wifiInfoView);
                dialog.setPositiveButton(R.string.close, null);
                return dialog.create();

            default:
                final View wifiView = factory.inflate(R.layout.sign_in, null);
                dialog = new AlertDialog.Builder(this);
                dialog.setTitle(selectedWifi.SSID);
                dialog.setMessage(selectedWifi.capabilities);
                Log.v("rht", "selectedWifi is : " + selectedWifi.SSID);
                dialog.setView(wifiView);
                textIdentity = (EditText) wifiView.findViewById(R.id.identity);
                textNetworkPass = (EditText) wifiView.findViewById(R.id.networkpass);
                dialog.setPositiveButton(R.string.signin, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                        reconnected = true;
                        identity = textIdentity.getText().toString();
                        networkPass = textNetworkPass.getText().toString();
                        Intent signIn = new Intent(ScanNetworks.this, SignInActivity.class);
                        signIn.putExtra("SelectedWifi", selectedWifi);
                        signIn.putExtra("Identity", identity);
                        signIn.putExtra("NetworkPass", networkPass);
                        signIn.putExtra("Reconnected", reconnected);
                        startActivity(signIn);

                    }
                });
                dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                return dialog.create();
        }

    }

    public class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context c, Intent intent) {
            //results = wifiManager.getScanResults();
            //networkListAdapter.notifyDataSetChanged();
            if(results != null)
                Log.v("rhi", "results received in WifiReceiver");
        }
    }

    public class NetworkStateReceiver extends BroadcastReceiver {

        long tStart, tEnd, tElapsed;
        private static final String TAG = "NetworkStateReceiver";

        @Override
        public void onReceive(final Context context, final Intent intent) {
            Log.d(TAG, "Network connectivity change");
            tStart = tEnd = tElapsed = 0;
            if (intent.getExtras() != null) {
                final ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
                final NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

                if (ni != null && ni.isConnectedOrConnecting()) {
                    Log.i(TAG, "Network " + ni.getTypeName() + " connected");
                    wifiInfo = wifiManager.getConnectionInfo();
                    tEnd = System.currentTimeMillis();
                    if(tStart != 0) {
                        tElapsed = (tEnd - tStart) / 1000;
                        Toast.makeText(ScanNetworks.this, tElapsed + " seconds passed", Toast.LENGTH_LONG).show();
                    }
                    if(reconnected && wifiInfo != null) {
                        showDialog(WIFI_DIALOG);;
                        reconnected = false;
                    }

                } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                    Toast.makeText(ScanNetworks.this, "There's no network connectivity", Toast.LENGTH_LONG).show();
                    tStart = System.currentTimeMillis();

                }
            }
        }
    }

}

