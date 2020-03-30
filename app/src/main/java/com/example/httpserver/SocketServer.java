package com.example.httpserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.Semaphore;

import android.os.Handler;
import android.util.Log;

import com.example.httpserver.handlers.HttpHandler;

public class SocketServer extends Thread {

    public final int port = 12345;

    ServerSocket serverSocket;
    private final Semaphore semaphore;
    private boolean isRunning;

    private List<HttpHandler> httpHandlers;
    private Handler handler;

    public SocketServer(List<HttpHandler> httpHandlers, Handler handler) {
        this.handler = handler;
        this.semaphore = new Semaphore(10);
        this.httpHandlers = httpHandlers;
    }

    public void run() {
        try {
            Log.d("SERVER", "Creating Socket");
            serverSocket = new ServerSocket(port);
            isRunning = true;

            while (isRunning) {
                Log.d("SERVER", "Socket Waiting for connection");
                Socket socket = serverSocket.accept();
                Log.d("SERVER", "Socket Accepted");

                Thread t = new Thread(new SocketThread(socket, semaphore, httpHandlers, handler));
                t.start();
            }
        } catch (IOException e) {
            Log.d("SERVER", "Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            serverSocket = null;
            isRunning = false;
        }
    }

    public void close() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            Log.d("SERVER", "Error, probably interrupted in accept(), see log");
            e.printStackTrace();
        }
        isRunning = false;
    }
}
