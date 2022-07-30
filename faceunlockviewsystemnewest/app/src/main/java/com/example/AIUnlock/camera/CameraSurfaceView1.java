package com.example.AIUnlock.camera;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class CameraSurfaceView1 extends SurfaceView implements SurfaceHolder.Callback {

    private Camera.PreviewCallback previewCallback;
    private Camera mCamera = null;
    private SurfaceHolder mHolder;
    private boolean isRunPreview = false;

    public CameraSurfaceView1(Context context, Camera.PreviewCallback previewCallback) {
        super(context);
        this.previewCallback = previewCallback;

        this.mHolder = getHolder();
        this.mHolder.addCallback(this);
        this.mHolder.setType(SurfaceHolder.SURFACE_TYPE_HARDWARE);

    }

    public CameraSurfaceView1(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CameraSurfaceView1(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(mCamera == null){
            mCamera = Camera.open(1);
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.set("orientation", "portrait");
            mCamera.setDisplayOrientation(90);
            mCamera.setParameters(parameters);
            try {
                mCamera.setPreviewDisplay(holder);
            } catch (IOException e) {
                mCamera.release();
                mCamera = null;
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(1280,720);
        parameters.setExposureCompensation(20);
        mCamera.setParameters(parameters);
        if (previewCallback != null) {
            mCamera.setPreviewCallbackWithBuffer(previewCallback);
            Camera.Size size = parameters.getPreviewSize();
            byte[] data = new byte[size.width*size.height*
                    ImageFormat.getBitsPerPixel(parameters.getPreviewFormat())/8];
            mCamera.addCallbackBuffer(data);
        }
        startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.setPreviewCallback(null);
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    public boolean startPreview(){
        if (!isRunPreview && mCamera != null){
            isRunPreview = true;
            mCamera.startPreview();
        }
        return isRunPreview;
    }
}