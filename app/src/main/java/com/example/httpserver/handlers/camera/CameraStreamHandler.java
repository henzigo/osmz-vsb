package com.example.httpserver.handlers.camera;

import android.util.Log;

import com.example.httpserver.callback.CameraCallback;
import com.example.httpserver.handlers.HttpHandler;
import com.example.httpserver.parsers.ByteBuffer;
import com.example.httpserver.parsers.RequestParser;
import com.example.httpserver.parsers.ResponseParser;

import java.io.IOException;

import static com.example.httpserver.HttpServerActivity.camera;

public class CameraStreamHandler implements HttpHandler {
    @Override
    public boolean shouldHandle(RequestParser request) {
        return request.getHeaderByName(RequestParser.METHOD).equals("GET") && request.getPath().startsWith("/camera/stream");
    }

    @Override
    public boolean handle(RequestParser request, ResponseParser response) {
        response.setHeader("Content-Type", "multipart/x-mixed-replace; boundary=\"OSMZ_boundary\"");

        CameraCallback cameraCallback = new CameraCallback();
        response.outputHeaders();

        try {
            response.getOutputStream().write("<!DOCTYPE html><html><head><meta charset=\"utf-8\"><title></title></head><body>".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(int i = 1 ; i < 15 ; i++) {
            camera.takePicture(null, null, cameraCallback);
            while (cameraCallback.getPicture() == null) {}

            try {
                response.getOutputStream().write(writeImage(cameraCallback.getPicture()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            response.getOutputStream().write("</body></html>".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private byte[] writeImage(byte[] data)
    {
        ByteBuffer body = new ByteBuffer();
        body.write("--OSMZ_boundary\r\n");
        body.write("Content-Type: image/jpeg\r\n");
        body.write("Content-Length: " + String.valueOf(data.length) + "\r\n");
        body.write("\r\n");
        body.write(data);
        body.write("--OSMZ_boundary\r\n");

        return body.getBuffer();
    }
}
