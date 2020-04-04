package com.jc770797.catimageprocess;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class ImageMorphological extends AppCompatActivity {

    public static Bitmap greyImageMap = ImageEditingActivity.getBitmap();
    private Button nextPageBtn, applyBtn;
    private ImageView imgSelect;
    private Spinner spinner;
    private SeekBar kernelSeekBar;
    private TextView KernelSizeTextView;
    private Mat globalTmp;
    private int kernelSize;


    public static Bitmap getBitmap() {
        return greyImageMap;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_morph);


        imgSelect = findViewById(R.id.imageIn);
        imgSelect.setImageBitmap(greyImageMap);


        this.greyImageMap = ImageEditingActivity.getBitmap();


        nextPageListener();
        spinnerListener();
        seekBarListener();
    }

    private void nextPageListener() {
        nextPageBtn = findViewById(R.id.continueBtn2);

        nextPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ImageMorphological.this, ImageSnakeActivity.class));
            }
        });
    }

    private void spinnerListener() {
        applyBtn = findViewById(R.id.applyBtn);
        spinner = findViewById(R.id.morphSpinner);

        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionSwitch();
            }
        });
    }

    private void optionSwitch() {
        switch (String.valueOf(spinner.getSelectedItem())) {
            case "Morphological Open":
                Toast.makeText(ImageMorphological.this, "Applying Morphological Open", Toast.LENGTH_SHORT).show();
                filterMorphologicalOpen();

                break;
            case "Morphological Close":
                Toast.makeText(ImageMorphological.this, "Applying Morphological Close", Toast.LENGTH_SHORT).show();
                filterMorphologicalClose();

                break;
            case "Morphological Erode":
                Toast.makeText(ImageMorphological.this, "Applying Morphological Erode", Toast.LENGTH_SHORT).show();
                filterMorphologicalErode();

                break;
            case "Morphological Dilate":
                Toast.makeText(ImageMorphological.this, "Applying Morphological Dilate", Toast.LENGTH_SHORT).show();
                filterMorphologicalDilate();

                break;
            case "Morphological Eclipse":
                Toast.makeText(ImageMorphological.this, "Applying Morphological Eclipse", Toast.LENGTH_SHORT).show();
                filterMorphologicalEclipse();
                break;
            case "Morphological Rect":
                Toast.makeText(ImageMorphological.this, "Applying Morphological Rect", Toast.LENGTH_SHORT).show();
                filterMorphologicalRect();
                break;
        }
    }

    private void seekBarListener(){
        kernelSeekBar = findViewById(R.id.seekBarKernel);
        KernelSizeTextView = findViewById(R.id.seekBarKernelText);
        KernelSizeTextView.setText("Kernel Size: " + 1);
        kernelSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                if(i == 0) i = 1;
                KernelSizeTextView.setText("Kernel Size: " + i);
                kernelSize = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    private void filterMorphologicalOpen() {
        globalTmp = matImageSelector();
        Mat tmp = new Mat();
        Mat kernel = Mat.ones(kernelSize,kernelSize, CvType.CV_32F);
        Imgproc.morphologyEx(globalTmp,tmp, Imgproc.MORPH_OPEN, kernel);
        bitmapFrameUpdate(tmp);
    }

    private void filterMorphologicalClose() {
        globalTmp = matImageSelector();
        Mat tmp = new Mat();
        Mat kernel = Mat.ones(kernelSize,kernelSize, CvType.CV_32F);
        Imgproc.morphologyEx(globalTmp,tmp, Imgproc.MORPH_CLOSE, kernel);
        bitmapFrameUpdate(tmp);
    }
    private void filterMorphologicalErode() {
        globalTmp = matImageSelector();
        Mat tmp = new Mat();
        Mat kernel = Mat.ones(kernelSize,kernelSize, CvType.CV_32F);
        Imgproc.morphologyEx(globalTmp,tmp, Imgproc.MORPH_ERODE, kernel);
        bitmapFrameUpdate(tmp);
    }
    private void filterMorphologicalDilate() {
        globalTmp = matImageSelector();
        Mat tmp = new Mat();
        Mat kernel = Mat.ones(kernelSize,kernelSize, CvType.CV_32F);
        Imgproc.morphologyEx(globalTmp,tmp, Imgproc.MORPH_DILATE, kernel);
        bitmapFrameUpdate(tmp);
    }

    private void filterMorphologicalEclipse() {
        globalTmp = matImageSelector();
        Mat tmp = new Mat();
        Mat kernel = Mat.ones(kernelSize,kernelSize, CvType.CV_32F);
        Imgproc.morphologyEx(globalTmp,tmp, Imgproc.MORPH_ELLIPSE, kernel);
        bitmapFrameUpdate(tmp);
    }

    private void filterMorphologicalRect() {
        globalTmp = matImageSelector();
        Mat tmp = new Mat();
        Mat kernel = Mat.ones(kernelSize,kernelSize, CvType.CV_32F);
        Imgproc.morphologyEx(globalTmp,tmp, Imgproc.MORPH_RECT, kernel);
        bitmapFrameUpdate(tmp);
    }


    private void bitmapFrameUpdate(Mat matIn) {
        Utils.matToBitmap(matIn, greyImageMap);
        imgSelect.setImageBitmap(greyImageMap);
    }

    private Mat matImageSelector() {
        Mat tmpOut = new Mat(greyImageMap.getWidth(), greyImageMap.getHeight(), CvType.CV_8UC1);
        Utils.bitmapToMat(greyImageMap, tmpOut);
        return tmpOut;
    }

}
