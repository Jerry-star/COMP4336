package com.neoriddle.wifiscanner;

import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * 
 * @author Israel Buitron
 *
 */
public class ScanResultsAdapter extends BaseAdapter {

	private final Context context;
	private final List<ScanResult> results;

	/**
	 * @param context
	 * @param results Wifi scan results list.
	 */
	public ScanResultsAdapter(Context context, List<ScanResult> results) {
		this.context = context;
		this.results = results;
	}

	@Override
	public int getCount() {
		return results.size();
	}

	@Override
	public Object getItem(int position) {
		return results.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ScanResult result = results.get(position);

		if(convertView==null) {
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.network_list_row, null);
		}

		// Get textview fields
		TextView txtSSID = (TextView)convertView.findViewById(R.id.txtSSID);
		TextView txtBSSID = (TextView)convertView.findViewById(R.id.txtBSSID);
		TextView txtCapabilities = (TextView)convertView.findViewById(R.id.txtCapabilities);
		TextView txtFrecuency = (TextView)convertView.findViewById(R.id.txtFrecuency);
		TextView txtLevel = (TextView)convertView.findViewById(R.id.txtLevel);
		TextView txtProtocol = (TextView)convertView.findViewById(R.id.txtProtocol);

		// Set fields values
		txtSSID.setText(convertView.getContext().getString(R.string.ssid_msg, result.SSID));
		txtBSSID.setText(convertView.getContext().getString(R.string.bssid_msg, result.BSSID));
		txtCapabilities.setText(convertView.getContext().getString(R.string.capabilities_msg, result.capabilities));
		double freq = result.frequency*1.0/1000;
		txtFrecuency.setText(convertView.getContext().getString(R.string.frecuency_msg, String.format("%.1f", freq)));
		txtLevel.setText(convertView.getContext().getString(R.string.signal_level_msg, Integer.toString(result.level)));
		if(Math.floor(freq) == 2)
			txtProtocol.setText(convertView.getContext().getString(R.string.protocol, "802.11 b/g"));
		else
			txtProtocol.setText(convertView.getContext().getString(R.string.protocol, "802.11 a/ac"));
		return convertView;
	}

}
