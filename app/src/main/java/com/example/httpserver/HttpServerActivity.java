package com.example.httpserver;

import android.Manifest;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

public class HttpServerActivity extends Activity implements OnClickListener {

    private Intent serviceIntent;

    private static final int PERMISSIONS_ID = 1;
    private static final String PERMISSIONS[] = {
            Manifest.permission.CAMERA,
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    public static Handler HANDLER;
    public static final Camera camera = getCameraInstance();
    public static CameraPreview cameraPreview;

    public static Camera getCameraInstance(){
        Camera camera = null;
        try {
            camera = Camera.open();
        }
        catch (Exception e){
            Log.d("Camera", "Camera is used already or doesn't exist.");
        }
        return camera;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http_server);

        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSIONS_ID);

        serviceIntent = new Intent(this, BackgroundService.class);

        Button btn1 = (Button) findViewById(R.id.button1);
        Button btn2 = (Button) findViewById(R.id.button2);
        final TextView log = (TextView) findViewById(R.id.textView);

        HANDLER = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                log.append("\n" + msg.obj);
            }
        };

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);

        cameraPreview = new CameraPreview(this);
        FrameLayout previewLayout = (FrameLayout) findViewById(R.id.camera_preview);
        previewLayout.addView(cameraPreview);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.http_server, menu);
        return true;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button1) {
            startService(serviceIntent);
        }

        if (v.getId() == R.id.button2) {
            stopService(serviceIntent);
        }
    }
}
