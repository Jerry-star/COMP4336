package com.neoriddle.wifiscanner;

import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zhyuan on 2017/8/30.
 */

public class Client extends ScanNetworks {

    private TextView txtLog;
    private Socket socket;
    private WifiInfo wifiInfo;
    private String lastAP, thisAP;
    private long tStart, tEnd, tElapsed;

    // 主线程Handler
    // 用于将从服务器获取的消息显示出来
    private Handler mMainHandler;

    // 线程池
    // 为了方便展示,此处直接采用线程池进行线程管理,而没有一个个开线程
    private ExecutorService mThreadPool;

    private static final int PORT = 80;
    private static final int RED = -1;
    private static final int GREEN = 0;
    private static final int RANGE = 128;
    private static final String TAG = "TCP Client";
    private static final String SERVER = "www.google.com";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client);

        txtLog = findViewById(R.id.txtLog);
        Button btnMeasure = findViewById(R.id.btnMeasure);
        // 初始化线程池
        mThreadPool = Executors.newCachedThreadPool();

        // 实例化主线程,用于更新接收过来的消息
        mMainHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String s = msg.getData().getString("msg");
                txtLog.append(s);
                Log.v(TAG, "msg: " + msg.getData().getString("msg"));
                switch (msg.what) {
                    case GREEN:
                        //txtLog.setTextColor(Color.green(RANGE));
                    case RED:
                        //txtLog.setTextColor(Color.red(RANGE));
                }
            }
        };
        btnMeasure.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // 利用线程池直接开启一个线程 & 执行该线程
                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        initClient();
                    }

                });
            }
        });

        Intent intent = getIntent();
        wifiInfo = intent.getExtras().getParcelable("WifiInfo");
        if(wifiInfo != null)
            lastAP = wifiInfo.getBSSID();
        while(true) {
            if(wifiInfo != null)
                thisAP = wifiInfo.getBSSID();
            if(!thisAP.equals(lastAP)) {
                tEnd = System.currentTimeMillis();
                break;
            }
        }
        tElapsed = (tEnd - tStart)/1000;
        Toast.makeText(Client.this, tElapsed + " seconds passed", Toast.LENGTH_LONG).show();
    }

    public void initClient() {
        mkmsg(0, "Connecting to server\n");
        Log.v(TAG, "connecting to server");
        try {
            socket = new Socket(SERVER, PORT);
            if(socket.isConnected()) {
                mkmsg(0, "Connection established\n");
                Log.v(TAG, "connection established");
            }
            char[] buffer = new char[255];
            //获取Socket的输出流，用来发送数据到服务端
            PrintStream out = new PrintStream(socket.getOutputStream());
            //获取Socket的输入流，用来接收从服务端发送过来的数据
            BufferedReader in =  new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while(true) {
                if((in.read(buffer)) < 0) {
                    tStart = System.currentTimeMillis();
                    break;
                }
                mkmsg(0, "Server response: " + new String(buffer) + "\n");
            }
        }
        catch (UnknownHostException e) {
            mkmsg(-1, "Unknown host: " + e.toString() + "\n");
        }
        catch (IOException e) {
            mkmsg(-1, "IOException: " + e.toString() + "\n");
        }
        finally {
            try {
                socket.close();
            }
            catch (IOException e){
                mkmsg(-1, "Cannot close: " + e.toString() + "\n");
            }
        }
        mkmsg(0, "Connection closed\n");
    }

    public void mkmsg(int code, String str) {
        //handler junk, because thread can't update screen!
        Message msg = Message.obtain();
        msg.what = code;
        Bundle b = new Bundle();
        b.putString("msg", str);
        msg.setData(b);
        mMainHandler.sendMessage(msg);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
