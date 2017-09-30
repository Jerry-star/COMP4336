package com.neoriddle.wifiscanner;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by zhyuan on 2017/9/12.
 */

public class Accelerometer extends Activity {

    private TextView txtOrientation;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private SensorEventListener mAccListener;

    private double[] raw_acceleration;
    private double[] linear_acceleration;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.accelerometer);

        raw_acceleration = new double[3];
        linear_acceleration = new double[3];

        Button showAcc = findViewById(R.id.btnAcc);
        showAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAcceleration();
            }
        });
        txtOrientation = findViewById(R.id.txtOri);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mAccListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                // alpha is calculated as t / (t + dT)
                // with t, the low-pass filter's time-constant
                // and dT, the event delivery rate
                final int x = 0;
                final int y = 1;
                final int z = 2;
                final double g = 9.8;
                final double alpha = 0.8;
                double gravity[] = new double[3];

                gravity[x] = alpha * gravity[x] + (1 - alpha) * sensorEvent.values[0];
                gravity[y] = alpha * gravity[y] + (1 - alpha) * sensorEvent.values[1];
                gravity[z] = alpha * gravity[z] + (1 - alpha) * sensorEvent.values[2];

                raw_acceleration[x] = sensorEvent.values[0];
                raw_acceleration[y] = sensorEvent.values[1];
                raw_acceleration[z] = sensorEvent.values[2];

                linear_acceleration[x] = sensorEvent.values[0] - gravity[x];
                linear_acceleration[y] = sensorEvent.values[1] - gravity[y];
                linear_acceleration[z] = sensorEvent.values[2] - gravity[z];

                if(Math.abs(raw_acceleration[z] - g) < 2)
                    txtOrientation.setText(String.valueOf("On the table"));
                else if(Math.abs(raw_acceleration[x] - g) < 2)
                    txtOrientation.setText(String.valueOf("Left"));
                else if(Math.abs(raw_acceleration[x] - g*(-1)) < 2)
                    txtOrientation.setText(String.valueOf("Right"));
                else if(Math.abs(raw_acceleration[y] - g) < 2)
                    txtOrientation.setText(String.valueOf("Default"));
                else if(Math.abs(raw_acceleration[y] - g*(-1)) < 2)
                    txtOrientation.setText(String.valueOf("Upside Down"));
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
    }

    public void showAcceleration() {
        String s;
        String axis = null;
        Locale locale = Locale.getDefault();
        TextView txtAcc = findViewById(R.id.txtAcc);
        txtAcc.setText(String.valueOf("Acceleration force including gravity:\n"));
        for(int i = 0; i < 3; i++) {
            if(i == 0)
                axis = "X";
            else if(i == 1)
                axis = "Y";
            else
                axis = "Z";
            s = String.format(locale, "%s: %f\n", axis, raw_acceleration[i]);
            txtAcc.append(s);
        }
        txtAcc.append(String.valueOf("\nAcceleration force without gravity:\n"));
        for(int i = 0; i < 3; i++) {
            if(i == 0)
                axis = "X";
            else if(i == 1)
                axis = "Y";
            else
                axis = "Z";
            s = String.format(locale, "%s: %f\n", axis, linear_acceleration[i]);
            txtAcc.append(s);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mAccListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mAccListener);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
