package com.neoriddle.wifiscanner;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

import static android.R.attr.gravity;
import static android.R.attr.start;


/**
 * Created by zhyuan on 2017/9/11.
 */

public class MobileSensor extends ScanNetworks {

    public Button showSensor;
    public ListView lstSensors;
    private SensorManager mSensorManager;

    public static final String TAG = "MobileSensor";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor);

        showSensor = findViewById(R.id.btnShowSensor);
        lstSensors = findViewById(R.id.lstSensors);
        showSensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSensors();
            }
        });
        lstSensors.setOnItemClickListener(this);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

    }

    public void showSensors() {
        List<Sensor> sensorList;
        ArrayAdapter<Sensor> sensorListAdapter;

        sensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        sensorListAdapter = new ArrayAdapter<Sensor>(this, R.layout.sensor_list_row, R.id.txtSensor, sensorList);
        lstSensors.setAdapter(sensorListAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent intent = new Intent(MobileSensor.this, Accelerometer.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

}
