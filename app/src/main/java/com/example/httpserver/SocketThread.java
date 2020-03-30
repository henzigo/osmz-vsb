package com.example.httpserver;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.httpserver.handlers.HttpHandler;
import com.example.httpserver.parsers.RequestParser;
import com.example.httpserver.parsers.ResponseParser;

import java.io.InputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.Semaphore;

public class SocketThread implements Runnable {

    private Socket socket;
    private Semaphore semaphore;
    private List<HttpHandler> httpHandlers;
    private Handler handler;

    public SocketThread(Socket socket, Semaphore semaphore, List<HttpHandler> httpHandlers, Handler handler) {
        this.socket = socket;
        this.semaphore = semaphore;
        this.httpHandlers = httpHandlers;
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            semaphore.acquire();
            InputStream inputStream = socket.getInputStream();

            RequestParser request = new RequestParser(inputStream);
            ResponseParser response = new ResponseParser(socket.getOutputStream());

            Message m = handler.obtainMessage();
            m.obj = "Thread: " + Thread.currentThread().getId() + " PATH: " + request.getPath() + "\n";
            m.sendToTarget();

            boolean showOutput = true;

            for (HttpHandler handler : this.httpHandlers) {
                if (handler.shouldHandle(request)) {
                    Log.d("SERVER", "Handler Matched: " + handler.getClass().toString());
                    showOutput = handler.handle(request, response);
                    break;
                }
            }

            if (showOutput) {
                Log.d("SERVER", "Send Response to output");
                response.outputContent();
            }

            socket.close();
            Log.d("SERVER", "Socket Closed");
        } catch (Exception e) {
            Log.d("SERVER", "run: " + e.getMessage());
            e.printStackTrace();
        } finally {
            semaphore.release();
        }
    }
}
