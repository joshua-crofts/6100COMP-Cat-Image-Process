package com.jc770797.catimageprocess;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jc770797.catimageprocess.activeCont.Snake;


import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.FileInputStream;


public class ImageSnakeActivity extends AppCompatActivity {


    public Bitmap greyImageMap ;
    private ImageView imgSelect, imgOverlay;
    private Button nextPageBtn, beginBtn,snakeBegin;
    int[] viewCords = new int[2];
    float touchx, touchy, imageX, imageY;
    Snake snake;

    private TextView numPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snake);

        fileGetter();

        imgOverlay = findViewById(R.id.imageViewSnakeOverlay);
        imgSelect = findViewById(R.id.imageViewSnake);
        imgSelect.setImageBitmap(greyImageMap);

        beginBtn = findViewById(R.id.beginBtn);
        snakeBegin = findViewById(R.id.startSnakeBtn);
        numPoints = findViewById(R.id.numOfPoints);


        snakeBegin.setEnabled(false);
        snakeBegin.setVisibility(View.INVISIBLE);


        snakeStartListener();
        //pointListener();
    }

    private void snakeStartListener() {
        beginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentOverlay = new Intent(ImageSnakeActivity.this, SnakeOverlayActivity.class);
                String tempFilename = "catPr_bitmap.png";
                intentOverlay.putExtra("image", tempFilename);
                startActivityForResult(intentOverlay, 0);

                Mat imageOut = new Mat(greyImageMap.getWidth(), greyImageMap.getHeight(), CvType.CV_8UC1);
                Utils.bitmapToMat(greyImageMap,imageOut);
                snake = new Snake(imageOut,true,"input");
//                beginBtn.setEnabled(false);

//                isPointReady = true;
            }
        });

//        stopBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                imgSelect.setOnTouchListener(null);
//                snake.printPoints();
//                isPointReady = false;
//                stopBtn.setEnabled(false);
//                snakeBegin.setEnabled(true);
//                beginBtn.setVisibility(View.INVISIBLE);
//                stopBtn.setVisibility(View.INVISIBLE);
//                snakeBegin.setVisibility(View.VISIBLE);
//                imgOverlay.setImageBitmap(snake.createBitmap());
//                imgSelect.setVisibility(View.INVISIBLE);
//            }
//        });

        snakeBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //snake.start(ImageSnakeActivity.this, 1.2, 1, 1.2, 5, 200);
            }
        });
    }

    private void fileGetter(){
        String tempFilename = getIntent().getStringExtra("image");
        try {
            FileInputStream is = ImageSnakeActivity.this.openFileInput(tempFilename);
            greyImageMap = BitmapFactory.decodeStream(is);
            is.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }




}
