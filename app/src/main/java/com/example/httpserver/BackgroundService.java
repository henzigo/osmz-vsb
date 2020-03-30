package com.example.httpserver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.httpserver.handlers.CommandHandler;
import com.example.httpserver.handlers.ListingFileHandler;
import com.example.httpserver.handlers.HttpHandler;
import com.example.httpserver.handlers.camera.CameraSnapshotHandler;
import com.example.httpserver.handlers.camera.CameraStreamHandler;

import java.util.ArrayList;
import java.util.List;

public class BackgroundService extends Service {
    private SocketServer socketServer;
    private List<HttpHandler> handlers = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Background Service","Started");

        this.handlers.add(new CameraSnapshotHandler());
        this.handlers.add(new CameraStreamHandler());
        this.handlers.add(new CommandHandler());
        this.handlers.add(new ListingFileHandler());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        socketServer.close();

        try {
            socketServer.join();
        } catch (InterruptedException e) {
            Log.d("Background Service", e.getMessage());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        socketServer = new SocketServer(this.handlers, HttpServerActivity.HANDLER);
        socketServer.start();

        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
