package com.example.httpserver.handlers.camera;

import com.example.httpserver.callback.CameraCallback;
import com.example.httpserver.handlers.HttpHandler;
import com.example.httpserver.parsers.ByteBuffer;
import com.example.httpserver.parsers.RequestParser;
import com.example.httpserver.parsers.ResponseParser;

import static com.example.httpserver.HttpServerActivity.camera;

public class CameraSnapshotHandler implements HttpHandler {
    @Override
    public boolean shouldHandle(RequestParser request) {
        return request.getHeaderByName(RequestParser.METHOD).equals("GET") && request.getPath().startsWith("/camera/snapshot");
    }

    @Override
    public boolean handle(RequestParser request, ResponseParser response) {
        response.setHeader("Content-Type", "multipart/x-mixed-replace; boundary=\"OSMZ_boundary\"");

        CameraCallback cameraCallback = new CameraCallback();

        camera.takePicture(null, null, cameraCallback);
        while (cameraCallback.getPicture() == null) {}

        writeImage(cameraCallback.getPicture(), response.getBody());

        return true;
    }

    private void writeImage(byte[] data, ByteBuffer body)
    {
        body.write("--OSMZ_boundary\r\n");
        body.write("Content-Type: image/jpeg\r\n");
        body.write("Content-Length: " + String.valueOf(data.length) + "\r\n");
        body.write("\r\n");
        body.write(data);
        body.write("--OSMZ_boundary\r\n");
    }
}
