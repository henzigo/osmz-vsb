package com.example.httpserver.callback;

import android.hardware.Camera;

public class CameraCallback implements Camera.PictureCallback {

    private byte[] imageData = null;

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        imageData = data;
        camera.startPreview();
    }

    public byte[] getPicture() {
        return this.imageData;
    }
}
