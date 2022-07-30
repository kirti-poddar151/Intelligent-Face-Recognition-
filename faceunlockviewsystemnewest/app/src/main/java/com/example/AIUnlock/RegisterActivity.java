package com.example.AIUnlock;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.AIUnlock.camera.BaseFaceView;
import com.example.AIUnlock.camera.CameraSurfaceView;
import com.example.AIUnlock.imageutil.FaceRecognizerSingleton;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_face.FaceRecognizer;

import java.io.File;
import java.nio.IntBuffer;

import static org.bytedeco.javacpp.opencv_imgproc.cvEqualizeHist;


public class RegisterActivity extends AppCompatActivity {

    private RelativeLayout layout;
    private BaseFaceView baseFaceView;
    private CameraSurfaceView preview;
    private final  static String TAG = "Pankaj";
    private FaceRecognizer faceRecognizer = null;
    private MatVector trainImages = null;
    private Mat trainLabel = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        faceRecognizer = FaceRecognizerSingleton.getInstance(this);
    }

    private int takeNum = 0;
    private IntBuffer labelsBuf = null;
    public void goRegisterCamera(View view) {
        takeNum = 30;
        trainImages = new MatVector(takeNum);
        trainLabel = new Mat(takeNum,1, opencv_core.CV_32SC1);
        labelsBuf = trainLabel.createBuffer();
        counter = 0;
        createCameraView();
    }

    private  int counter = 0;
    private Toast t = null;
    public void takePhoto(View view) {
        IplImage facex = baseFaceView.captureFace();
        if( facex != null) {
            if(takeNum >= 1){
                takeNum--;
                if( takeNum > 15) {
                    toastFactory("Front Faceing Picture click");
                }
                else if(takeNum>7 && takeNum<15) {
                    toastFactory("Rotate Face Slightly Left");
                }
                else {
                    toastFactory("Rotate Face Slightly Right");
                }
                cvEqualizeHist(facex,facex);
                trainImages.put(counter,new Mat(facex));
                labelsBuf.put(counter, 1);
                counter++;
            }
            if( takeNum == 0){
                this.destoryCamereView();
                setContentView(R.layout.activity_register2);
                faceRecognizer.train(trainImages,trainLabel);

                // check train data is exist
/*
                File tmp = new File(this.getFilesDir() + FaceRecognizerSingleton.getSaveFileName());
                if( tmp.exists() ){
                    tmp.delete();
                }
                faceRecognizer.save(this.getFilesDir() + FaceRecognizerSingleton.getSaveFileName());
*/
                File sdcard = Environment.getExternalStorageDirectory();
                File dir = new File(sdcard.getAbsolutePath() + "/faceLockLite");
                dir.mkdir();

                File tmp = new File(dir.getAbsolutePath() + FaceRecognizerSingleton.getSaveFileName());
                if (tmp.exists()) {
                    tmp.delete();
                }
                // File file = new File(dir,"sample.xml");
                faceRecognizer.save( dir.getAbsolutePath() + FaceRecognizerSingleton.getSaveFileName());
            }
        }
    }

    private void createCameraView(){
        ((Button)findViewById(R.id.takePhotoButton)).setVisibility(View.VISIBLE);
        ((Button)findViewById(R.id.startbutton)).setVisibility(View.GONE);
        layout = (RelativeLayout) findViewById(R.id.activity_register);
        baseFaceView = new BaseFaceView(this);
        preview = new CameraSurfaceView(this,baseFaceView);

        layout.addView(preview);
        layout.addView(baseFaceView);
    }

    private  void destoryCamereView(){
        layout.removeView(preview);
        layout.removeView(baseFaceView);
        ((Button)findViewById(R.id.takePhotoButton)).setVisibility(View.GONE);
        ((Button)findViewById(R.id.startbutton)).setVisibility(View.VISIBLE);
    }
    public void UpdateOnclick(View view)
    {
        startActivity(new Intent().setClass(RegisterActivity.this,UpdateActivity.class));
        finish();
    }
    private void toastFactory( String str ){
        if (t != null)t.cancel();
        t = Toast.makeText(this,str, Toast.LENGTH_SHORT);
        t.setGravity(Gravity.CENTER, 0, 0);
        t.show();
    }
}
