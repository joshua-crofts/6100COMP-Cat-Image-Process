package com.jc770797.catimageprocess;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {

    Button enterBtn;

    private static String TAG = "MainActivity";
    static{
        if (OpenCVLoader.initDebug()){
            Log.d(TAG, "openCV is connected successfully");
        }else{
            Log.d(TAG, "openCV is not connected successfully");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        enterBtn = findViewById(R.id.enterBtn);
        enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent( MainActivity.this, ImageSelectionActivity.class));

            }
        });


    }
}
