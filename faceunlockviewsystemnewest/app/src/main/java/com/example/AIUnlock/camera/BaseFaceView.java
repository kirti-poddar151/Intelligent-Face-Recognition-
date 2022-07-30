package com.example.AIUnlock.camera;

import static org.bytedeco.javacpp.helper.opencv_objdetect.cvHaarDetectObjects;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.javacpp.opencv_core.cvClearMemStorage;
import static org.bytedeco.javacpp.opencv_core.cvCopy;
import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvFlip;
import static org.bytedeco.javacpp.opencv_core.cvGetSeqElem;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_core.cvLoad;
import static org.bytedeco.javacpp.opencv_core.cvSetImageROI;
import static org.bytedeco.javacpp.opencv_core.cvTranspose;
import static org.bytedeco.javacpp.opencv_objdetect.CV_HAAR_DO_ROUGH_SEARCH;
import static org.bytedeco.javacpp.opencv_objdetect.CV_HAAR_FIND_BIGGEST_OBJECT;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.View;

import com.example.AIUnlock.imageutil.FaceRecognition;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_objdetect.CvHaarClassifierCascade;
import org.bytedeco.javacpp.presets.opencv_objdetect;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

//import org.bytedeco.javacpp.opencv_core.*;



@SuppressWarnings("deprecation")
public class BaseFaceView extends View implements Camera.PreviewCallback {

    private opencv_core.CvMemStorage storage;
    private CvHaarClassifierCascade classifier;

    public static final int SUBSAMPLING_FACTOR = 4;
    private opencv_core.CvSeq faces;
    private opencv_core.IplImage grayImage = null;
    private FaceRecognition activity = null;
    boolean flag=false;
    public BaseFaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        try {
            init(context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public BaseFaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        try {
            init(context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public BaseFaceView(Context context) {
        super(context);
        try {
            init(context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setActivity(FaceRecognition activity,boolean flag){
        this.activity = activity; this.flag=flag;
    }
    private void init(Context context) throws IOException{
        File classifierFile = Loader.extractResource(getClass(),
                "/org/ViewSystem/haarcascade_frontalface_alt.xml",
                context.getCacheDir(), "classifier", ".xml");
        if (classifierFile == null || classifierFile.length() <= 0) {
            throw new IOException("Could not extract the classifier file from Java resource.");
        }
        Loader.load(opencv_objdetect.class);
        classifier = new CvHaarClassifierCascade(cvLoad(classifierFile.getAbsolutePath()));
        classifierFile.delete();
        if (classifier.isNull()) {
            throw new IOException("Could not load the classifier file.");
        }
        storage = opencv_core.CvMemStorage.create();
    }
    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        try {
            Camera.Size size = camera.getParameters().getPreviewSize();
            if(this.flag) {
                processImages(data, size.width, size.height);
            }
            else
                processImage(data, size.width, size.height);
            camera.addCallbackBuffer(data);
        } catch (RuntimeException e) {
        }
    }
    public opencv_core.IplImage processImage(byte[] data, int width, int height) {
        int f = SUBSAMPLING_FACTOR;
        opencv_core.IplImage transposed = null;
        if (grayImage == null || grayImage.width() != width/f || grayImage.height() != height/f) {
            grayImage = opencv_core.IplImage.create(width/f, height/f, IPL_DEPTH_8U, 1);
            transposed = opencv_core.IplImage.create(height/f, width/f, IPL_DEPTH_8U, 1);
        }
        int imageWidth  = grayImage.width();
        int imageHeight = grayImage.height();
        int dataStride = f*width;
        int imageStride = grayImage.widthStep();
        ByteBuffer imageBuffer = grayImage.createBuffer();
        for (int y = 0; y < imageHeight; y++) {
            int dataLine = y*dataStride;
            int imageLine = y*imageStride;
            for (int x = 0; x < imageWidth; x++) {
                imageBuffer.put(imageLine + x, data[dataLine + f*x]);
            }
        }
        cvTranspose(grayImage, transposed);
        cvFlip(transposed, transposed, 0);
        grayImage = transposed;
        cvClearMemStorage(storage);
        faces = cvHaarDetectObjects(grayImage, classifier, storage, 1.1, 3,
                CV_HAAR_FIND_BIGGEST_OBJECT | CV_HAAR_DO_ROUGH_SEARCH);
        postInvalidate();
        return captureFace();
    }

    public opencv_core.IplImage processImages(byte[] data, int width, int height) {
        opencv_core.IplImage face = processImage(data, width, height);
        if( face != null && activity != null ){
            activity.execute(face);
        }
        return face;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(20);

        String s = "FacePreview - This side up.";
        float textWidth = paint.measureText(s);
        canvas.drawText(s, (getWidth()-textWidth)/2, 20, paint);
        if (faces != null) {

            paint.setStrokeWidth(2);
            paint.setStyle(Paint.Style.STROKE);
            float scaleX = (float)getWidth()/grayImage.width();
            float scaleY = (float)getHeight()/grayImage.height();
            int total = faces.total();
//            Log.d("ViewSystem", "Find Face " + total);
            for (int i = 0; i < total; i++) {
                opencv_core.CvRect r = new opencv_core.CvRect(cvGetSeqElem(faces, i));
                int x = r.x(), y = r.y(), w = r.width(), h = r.height();
                canvas.drawRect(x*scaleX, y*scaleY, (x+w)*scaleX, (y+h)*scaleY, paint);
            }
//            faces = null;
        }
    }

    public opencv_core.IplImage captureFace(){
        if( faces == null || grayImage == null){return null; }
        if( faces.total() == 1) {
            return cropFace(grayImage, new opencv_core.CvRect(cvGetSeqElem(faces, 0)));
        }
        else
            return null;
    }
    public opencv_core.IplImage cropFace(opencv_core.IplImage frame, opencv_core.CvRect rect){
        cvSetImageROI(frame, rect);
        opencv_core.IplImage face = cvCreateImage(cvGetSize(frame), frame.depth(), frame.nChannels());
        cvCopy(frame, face);
        return  face;
    }


}