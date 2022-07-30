package com.example.AIUnlock;

import android.app.Activity;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.AIUnlock.camera.BaseFaceView;
import com.example.AIUnlock.imageutil.FaceRecognition;
import com.example.AIUnlock.imageutil.FaceRecognizerSingleton;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_face.FaceRecognizer;
import org.bytedeco.javacpp.opencv_imgproc;

import java.io.File;
import java.io.IOException;


public class RecognizerActivity extends Activity implements FaceRecognition {

    private FaceRecognizer faceRecognizer =null;
    private BaseFaceView baseFaceView;
    private Camera.PreviewCallback previewCallback;
    private boolean flag = true;
    private Handler mhandler;
    private  final  static String TAG = "ViewSystem";
    private Camera mCamera = null;
    SurfaceTexture surfaceTexture = new SurfaceTexture(0);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_recognizer);

        File [] externalStorageVolumes = ContextCompat.getExternalFilesDirs(getApplicationContext(),null);
        File primaryExternalStorage = externalStorageVolumes[0];
        File folder = new File(primaryExternalStorage,"AIUnlock");
        if(!folder.exists())
            folder.mkdir();

        FileLog.open(folder.getAbsolutePath(), Log.VERBOSE);
        File sdcard = Environment.getExternalStorageDirectory();
        File dir = new File(sdcard.getAbsolutePath() + "/faceLockLite");
        File tmp = new File(dir.getAbsolutePath() + FaceRecognizerSingleton.getSaveFileName());

        if( new File(tmp.getAbsolutePath()).exists()){
//            layout = (RelativeLayout) findViewById(R.id.activity_recongizer);
            baseFaceView = new BaseFaceView(this);
            baseFaceView.setActivity(this,true);
            this.previewCallback = baseFaceView;
//            layout.addView(preview);
//            layout.addView(baseFaceView);
            fun() ;
            faceRecognizer = FaceRecognizerSingleton.getInstance(this);
            faceRecognizer.load(tmp.getAbsolutePath());
            mhandler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
          //          baseFaceView.setVisibility(View.INVISIBLE);
                    onBackPressed();
                    finish();
                }
            };
        }
    }
    public void fun(){
        mCamera = Camera.open(1);
        Camera.Parameters params = mCamera.getParameters();
        /*params.set("ois","still");
        params.setAutoWhiteBalanceLock(false);
        params.setAutoExposureLock(false);*/
        params.setPreviewSize(1280, 720);
        mCamera.setPreviewCallbackWithBuffer(previewCallback);
        Camera.Size size = params.getPreviewSize();
        byte[] data = new byte[size.width * size.height *
                ImageFormat.getBitsPerPixel(params.getPreviewFormat()) / 8];
        mCamera.addCallbackBuffer(data);
        //mCamera.
//        int currentBrightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(getApplicationContext())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 200);
            }
        }
        Settings.System.putInt(getContentResolver(),Settings.System.SCREEN_BRIGHTNESS, (int) (255));
        /*Log.d(TAG, " Brightness currentValue : " + currentBrightness + " updated : " + 255);
        Log.i(TAG, "Supported Exposure Modes:" + params.get("exposure-mode-values"));
        Log.i(TAG, "Supported White Balance Modes:" + params.get("whitebalance-values"));*/
        params.setExposureCompensation(10);
        mCamera.setParameters(params);
        try {
            mCamera.setPreviewTexture(surfaceTexture);
        } catch (IOException e1) {
        }
        try {
            mCamera.startPreview();
        } catch (java.lang.RuntimeException E) {
            E.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        //Log.d(TAG, "onStart: recognition");
        flag = true;
//        baseFaceView.setVisibility(View.VISIBLE);
        super.onStart();
    }
    @Override
    public void onBackPressed() {
        //Log.d(TAG, "onBackPressed: recongition");
        moveTaskToBack(true);
        super.onBackPressed();
    }
    @Override
    protected void onDestroy() {
        //Log.d(TAG, "onDestroy: ");
        mCamera.stopPreview();
        super.onDestroy();
    }
    @Override
    protected void onResume() {
//        startPreview();
        super.onResume();
    }

    @Override
    protected void onPause(){
        //Log.d(TAG, "onPause: recognition");
        mCamera.stopPreview();
        flag =false;
        super.onPause();
    }
    private Toast toast = null;
    private int count = 0;
    @Override
    public void execute(IplImage face) {
        if (flag) {
            opencv_imgproc.cvEqualizeHist(face,face);
            int predict=0;
            double start_time = System.currentTimeMillis();
            predict = faceRecognizer.predict(new Mat(face));
 //           FileLog.d("Predict Time: ", String.valueOf(System.currentTimeMillis() - start_time));
            if (predict == 1) {
                Settings.System.putInt(getContentResolver(),Settings.System.SCREEN_BRIGHTNESS, (int) (85));
                flag = false;
 //               FileLog.d("Predict = 1 , Count: ",String.valueOf(count));
                if(toast!=null) toast.cancel();
                toast = Toast.makeText(RecognizerActivity.this, "Login Success!!", Toast.LENGTH_SHORT);
                toast.show();
                mhandler.sendMessage(Message.obtain());
            }
            else if(count>1){
                if (toast != null) toast.cancel();
                toast = Toast.makeText(RecognizerActivity.this, "Login Fail!! " + count, Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        count++;
    }
}