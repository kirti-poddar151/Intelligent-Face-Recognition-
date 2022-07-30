package com.example.AIUnlock;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import org.bytedeco.javacpp.opencv_face;

public class MainActivity extends AppCompatActivity {
    private opencv_face.FaceRecognizer faceRecognizer =null;
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RequestUserPermission requestUserPermission = new RequestUserPermission(this);
        requestUserPermission.verifyCameraPermissions();
    }
    public void registerOnClick(View view) {
        startActivity(new Intent().setClass(MainActivity.this,RegisterActivity.class));
    }
    public void UpdateOnClick(View view) {
        startActivity(new Intent().setClass(MainActivity.this,UpdateActivity.class));
    }
    public void testOnClick(View view) {
        startActivity(new Intent().setClass(MainActivity.this,RecognizerActivity.class));
    }
}