package com.neoriddle.wifiscanner;

import android.content.Context;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by zhyuan on 2017/9/12.
 */

public class MobileSensor2 extends ScanNetworks {

    private GPS gps;
    private TextView txtLocation;
    private TextView txtRotation;
    private TextView txtAngle;
    private TextView txtMagnet;
    private TextView txtHeading;
    private TextView txtTrueHeading;
    private Button btnStart;
    private Button btnEnd;

    private SensorManager sensorManager;
    private Sensor magneticSensor;
    private Sensor gyroscopeSensor;
    private GeomagneticField geoField;
    private SensorEventListener gyroscopeListener;
    private SensorEventListener magenticListener;

    private float timestamp;
    private float rotation[] = new float[3];
    private float angleRadian[] = new float[3];
    private float angleDegree[] = new float[3];
    private float magnet[] = new float[3];
    private float heading;
    public static final String TAG = "MobileSensor2";
    // 将纳秒转化为秒
    private static final float NS2S = 1.0f / 1000000000.0f;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor2);

        gps = new GPS(this);
        StringBuilder sb = new StringBuilder();
        if (gps.canGetLocation()) {
            sb.append(String.valueOf(gps.getLatitude()));
            sb.append(String.valueOf(gps.getLongitude()));
        }
        geoField = new GeomagneticField(
                Double.valueOf(gps.getLatitude()).floatValue(),
                Double.valueOf(gps.getLongitude()).floatValue(),
                Double.valueOf(gps.getAltitude()).floatValue(),
                System.currentTimeMillis()
        );

        txtLocation = findViewById(R.id.textView2);
        txtLocation.setText(sb);
        txtRotation = findViewById(R.id.txtRotation);
        txtAngle = findViewById(R.id.txtAngle);
        txtMagnet = findViewById(R.id.txtMagnet);
        txtHeading = findViewById(R.id.txtHeading);
        txtTrueHeading = findViewById(R.id.txtTrueHeading);
        btnStart = findViewById(R.id.btnStart);
        btnEnd = findViewById(R.id.btnEnd);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                angleRadian[0] = angleRadian[1] = angleRadian[2] = 0;
            }
        });
        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = "\nAngle: \n" + angleDegree[2];
                txtAngle.setText(s);
            }
        });

        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                handler.postDelayed(this, 1500);
                String s = "Location: \n" + gps.getLongitude() + "\n" + gps.getLatitude() + "\n";
                txtLocation.setText(s);
                s = "Rotation: \nX: " + rotation[0] + "\nY: " + rotation[1] + "\nZ: " + rotation[2];
                txtRotation.setText(s);
                /*s = "\nAngle: \n" + angleDegree[2];
                txtAngle.setText(s);*/
                s = "\nMagnet field intensity: \nX: " + magnet[0] + "\nY: " + magnet[1] + "\nZ: " + magnet[2];
                txtMagnet.setText(s);
                s = "\nHeading: \n" + heading;
                txtHeading.setText(s);
                float trueHeading = heading - geoField.getDeclination();
                s = "\nTrue Heading: \n" + trueHeading;
                txtTrueHeading.setText(s);
            }
        };
        handler.postDelayed(r, 1500);

        gyroscopeListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (timestamp != 0) {
                    // 得到两次检测到手机旋转的时间差（纳秒），并将其转化为秒
                    final float dT = (event.timestamp - timestamp) * NS2S;
                    rotation[0] = event.values[0];
                    rotation[1] = event.values[1];
                    rotation[2] = event.values[2];
                    // 将手机在各个轴上的旋转角度相加，即可得到当前位置相对于初始位置的旋转弧度
                    angleRadian[0] += event.values[0] * dT;
                    angleRadian[1] += event.values[1] * dT;
                    angleRadian[2] += event.values[2] * dT;
                    // 将弧度转化为角度
                    angleDegree[0] = (float) Math.toDegrees(angleRadian[0]);
                    angleDegree[1] = (float) Math.toDegrees(angleRadian[1]);
                    angleDegree[2] = (float) Math.toDegrees(angleRadian[2]);
                }
                timestamp = event.timestamp;
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        magenticListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                magnet[0] = event.values[0];
                magnet[1] = event.values[1];
                magnet[2] = event.values[2];
                if(magnet[0] > 0)
                    heading = (float) (270 + Math.toDegrees(Math.atan(magnet[2]/magnet[1])));
                else if(magnet[0] < 0)
                    heading = (float) (90 + Math.toDegrees(Math.atan(magnet[2]/magnet[1])));
                else {
                    if(magnet[1] > 0)
                        heading = 0;
                    else
                        heading = 180;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        /*TimerTask task = new TimerTask() {
            public void run() {
                String s;
                s = "Rotation: \nX: " + rotation[0] + "\nY: " + rotation[1] + "\nZ: " + rotation[2];
                txtRotation.setText(s);
                s = "Angle: \nX: " + angleRadian[0] + "\nY: " + angleRadian[1] + "\nZ: " + angleRadian[2];
                txtAngle.setText(s);
            }
        };
        Timer timer = new Timer(true);
        timer.schedule(task, 1000, 1000);*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(gyroscopeListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(magenticListener, magneticSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(gyroscopeListener);
        sensorManager.unregisterListener(magenticListener);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { return super.onCreateOptionsMenu(menu); }
}
