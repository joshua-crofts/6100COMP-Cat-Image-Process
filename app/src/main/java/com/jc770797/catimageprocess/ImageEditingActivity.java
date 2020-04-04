package com.jc770797.catimageprocess;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class ImageEditingActivity extends AppCompatActivity {

    //Image objects
    public Bitmap colourImageMap = ImageCropActivity.getBitmap();
    public static Bitmap greyImageMap, baseGreyImage;
    private ImageView imgSelect;

    private Spinner spinner;
    private ImgState state;
    private SeekBar seekBar, kernelSeekBar;

    private TextView textView, KernelSizeTextView;

    private int threshold = 0, kernelSize = 1;

    private Mat globalTmp;



    public static Bitmap getBitmap() {
        return greyImageMap;
    }


    private enum ImgState {
        IMAGE_SMOOTH,
        IMAGE_ERODE,
        IMAGE_DILATE,
        IMAGE_THRESHOLD,
        IMAGE_FREE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_edit);

        //set the grey image map
        greyImageMap = Bitmap.createBitmap(colourImageMap.getWidth(), colourImageMap.getHeight(), colourImageMap.getConfig());


        //assigning the objects to the layout
        state = ImgState.IMAGE_FREE;
        imgSelect = findViewById(R.id.imageIn);
        imgSelect.setImageBitmap(colourImageMap);

        //main calls
        nextPageListener();
        filterGreyScale();
        buttonListeners();
        seekBarListener();
    }

    //Listener for next page button
    private void nextPageListener() {
        Button nextPageBtn;
        nextPageBtn = findViewById(R.id.nextPageBtn);
        nextPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ImageEditingActivity.this, ImageMorphological.class));
            }
        });
    }

    //additional button/spinner listeners
    private void buttonListeners() {

        spinner = findViewById(R.id.spinner);
        Button  applyBtn, resetImgBtn;


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //disable/enable seeker/text objects
                if(position == 0){
                    seekBar.setEnabled(false);
                    kernelSeekBar.setEnabled(false);
                    textView.setEnabled(false);
                    KernelSizeTextView.setEnabled(false);
                }else if(position == 1 || position == 2){
                    seekBar.setEnabled(false);
                    kernelSeekBar.setEnabled(true);
                    textView.setEnabled(false);
                    KernelSizeTextView.setEnabled(true);
                }else if(position == 3 ||position == 4){
                    seekBar.setEnabled(true);
                    kernelSeekBar.setEnabled(false);
                    textView.setEnabled(true);
                    KernelSizeTextView.setEnabled(false);
                }else {
                    seekBar.setEnabled(false);
                    kernelSeekBar.setEnabled(false);
                    textView.setEnabled(false);
                    KernelSizeTextView.setEnabled(false);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        applyBtn = findViewById(R.id.applyBtn);
        resetImgBtn = findViewById(R.id.applyBaseBtn);
        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionSwitch();
            }
        });
        resetImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetImage();
            }
        });


    }


    private void seekBarListener() {
        seekBar = findViewById(R.id.seekBar);
        kernelSeekBar = findViewById(R.id.seekBarKernel);

        textView = findViewById(R.id.edgeNumText);
        textView.setText("Threshold: " + 0);
        KernelSizeTextView = findViewById(R.id.KernelSizeText);
        KernelSizeTextView.setText("Kernel Size: " + 1);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textView.setText("Threshold: " + i);
                threshold = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

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

    //main method for selecting which edit option to select
    private void optionSwitch() {
        switch (String.valueOf(spinner.getSelectedItem())) {
            case "Image Smooth":
                Toast.makeText(ImageEditingActivity.this, "Applying Gaussian Smooth", Toast.LENGTH_SHORT).show();
                filterSmooth();
                state = ImgState.IMAGE_SMOOTH;
                break;
            case "Erode":
                Toast.makeText(ImageEditingActivity.this, "Applying Erosion", Toast.LENGTH_SHORT).show();
                filterErode();
                state = ImgState.IMAGE_ERODE;
                break;
            case "Dilate":
                Toast.makeText(ImageEditingActivity.this, "Applying Dilation", Toast.LENGTH_SHORT).show();
                filterDilate();
                state = ImgState.IMAGE_DILATE;
                break;
            case "Adaptive Threshold":
                Toast.makeText(ImageEditingActivity.this, "Applying Adaptive Threshold", Toast.LENGTH_SHORT).show();
                adaptiveThreshold();
                state = ImgState.IMAGE_THRESHOLD;
                break;
            case "Threshold":
                Toast.makeText(ImageEditingActivity.this, "Applying Threshold", Toast.LENGTH_SHORT).show();
                filterThreshold();
                break;
        }
    }



    /*
    Separate methods for editing the grey image map
    Each using openCV functions
     */
    private void filterGreyScale() {
        Mat tmp = new Mat(colourImageMap.getWidth(), colourImageMap.getHeight(), CvType.CV_8UC1);
        Utils.bitmapToMat(colourImageMap, tmp);
        Imgproc.cvtColor(tmp, tmp, Imgproc.COLOR_RGB2GRAY);
        Utils.matToBitmap(tmp, greyImageMap);
        imgSelect.setImageBitmap(greyImageMap);
        baseGreyImage = greyImageMap.copy(greyImageMap.getConfig(), true);
    }

    private void filterSmooth() {
        globalTmp = matImageSelector();
        Size si = new Size(5, 5);
        Imgproc.GaussianBlur(globalTmp, globalTmp, si, 0);
        bitmapFrameUpdate(globalTmp);
    }

    private void filterErode() {
        globalTmp = matImageSelector();
        Mat kernel = Mat.ones(kernelSize, kernelSize, CvType.CV_32F);
        Imgproc.erode(globalTmp, globalTmp, kernel);
        bitmapFrameUpdate(globalTmp);
    }

    private void filterDilate() {
        globalTmp = matImageSelector();
        Mat kernel = Mat.ones(kernelSize, kernelSize, CvType.CV_32F);
        Imgproc.dilate(globalTmp, globalTmp, kernel);
        bitmapFrameUpdate(globalTmp);
    }

    private void filterThreshold() {
        globalTmp = matImageSelector();
        Imgproc.cvtColor(globalTmp, globalTmp, Imgproc.COLOR_RGB2GRAY);
        Imgproc.threshold(globalTmp, globalTmp, threshold, 255, 0);
        bitmapFrameUpdate(globalTmp);
    }

    private void adaptiveThreshold() {
        globalTmp = matImageSelector();
        Imgproc.cvtColor(globalTmp, globalTmp, Imgproc.COLOR_RGB2GRAY);
        Imgproc.adaptiveThreshold(globalTmp, globalTmp, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 15, 2);
        bitmapFrameUpdate(globalTmp);
    }



    /*
    Various methods for changing the image type
    or outputting and storing the image.
     */
    private void bitmapFrameUpdate(Mat matIn) {
        Utils.matToBitmap(matIn, greyImageMap);
        imgSelect.setImageBitmap(greyImageMap);
    }

    private Mat matImageSelector() {
        Mat tmpOut = new Mat(greyImageMap.getWidth(), greyImageMap.getHeight(), CvType.CV_8UC1);
        Utils.bitmapToMat(greyImageMap, tmpOut);
        return tmpOut;
    }

    private void resetImage(){
        greyImageMap = baseGreyImage.copy(baseGreyImage.getConfig(),true);
        imgSelect.setImageBitmap(greyImageMap);
    }




}
