package com.jc770797.catimageprocess;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageEditingActivity extends AppCompatActivity {

    //Image objects
    public Bitmap colourImageMap ;//= ImageCropActivity.getBitmap()
    public  Bitmap greyImageMap, baseGreyImage;
    private ImageView imgSelect;

    private Spinner spinner;

    private SeekBar seekBar, kernelSeekBar;

    private TextView textView, KernelSizeTextView;

    private int threshold = 0, kernelSize = 1;

    private Mat globalMat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_edit);

        fileGetter();

        //set the grey image map
        greyImageMap = Bitmap.createBitmap(colourImageMap.getWidth(), colourImageMap.getHeight(), colourImageMap.getConfig());




        //assigning the objects to the layout
        imgSelect = findViewById(R.id.imageIn);
        imgSelect.setImageBitmap(colourImageMap);

        //main calls
        nextPageListener();
        filterGreyScale();
        buttonListeners();
        seekBarListener();
    }

    @Override
    protected void onStart(){
        super.onStart();
        fileGetter();
        greyImageMap = Bitmap.createBitmap(colourImageMap.getWidth(), colourImageMap.getHeight(), colourImageMap.getConfig());
        filterGreyScale();
    }

    //Listener for next page button
    private void nextPageListener() {
        Button nextPageBtn;
        nextPageBtn = findViewById(R.id.nextPageBtn);
        nextPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{

                    //Write the file to storage
                    String tempFilename = "catPr_bitmap.png";
                    fileWriter(tempFilename);
                    greyImageMap.recycle();
                    colourImageMap.recycle();
                    //Create the intent and add the filename to it
                    Intent intent = new Intent(ImageEditingActivity.this, ImageMorphological.class);
                    intent.putExtra("image", tempFilename);
                    startActivity(intent);
                }catch(Exception e){
                    e.printStackTrace();
                }
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
                }else if(position==5){
                    seekBar.setEnabled(true);
                    kernelSeekBar.setEnabled(true);
                    textView.setEnabled(true);
                    KernelSizeTextView.setEnabled(true);
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
                break;
            case "Erode":
                Toast.makeText(ImageEditingActivity.this, "Applying Erosion", Toast.LENGTH_SHORT).show();
                filterErode();
                break;
            case "Dilate":
                Toast.makeText(ImageEditingActivity.this, "Applying Dilation", Toast.LENGTH_SHORT).show();
                filterDilate();
                break;
            case "Adaptive Threshold":
                Toast.makeText(ImageEditingActivity.this, "Applying Adaptive Threshold", Toast.LENGTH_SHORT).show();
                adaptiveThreshold();
                break;
            case "Threshold":
                Toast.makeText(ImageEditingActivity.this, "Applying Threshold", Toast.LENGTH_SHORT).show();
                filterThreshold();
                break;
            case "Testing":
                Toast.makeText(ImageEditingActivity.this, "Test", Toast.LENGTH_SHORT).show();
                testSobel();
                break;
        }
    }

    private void testSobel() {
        globalMat = matImageSelector();
        Mat outPut = new Mat();
        Mat grad_x = new Mat(), grad_y = new Mat();
        Mat abs_grad_x = new Mat(), abs_grad_y = new Mat();
        Imgproc.cvtColor(globalMat, globalMat, Imgproc.COLOR_RGB2GRAY);
        int depth = CvType.CV_16S;
        Imgproc.Sobel(globalMat, grad_x, depth, 1,0,5, 1, 0, Core.BORDER_DEFAULT);
        Imgproc.Sobel(globalMat, grad_y, depth, 0,1,5, 1, 0,Core.BORDER_DEFAULT);

        Imgproc.Sobel(globalMat, grad_x, depth, 1,0,5, 1, 0, Core.BORDER_DEFAULT);
        Imgproc.Sobel(globalMat, grad_y, depth, 0,1,5, 1, 0,Core.BORDER_DEFAULT);

        Core.convertScaleAbs(grad_x,abs_grad_x);
        Core.convertScaleAbs(grad_y,abs_grad_y);

        //Imgproc.threshold(abs_grad_x, abs_grad_x, threshold, 255, 0);
        //Imgproc.threshold(abs_grad_y, abs_grad_y, threshold, 255, 0);


        Core.addWeighted(abs_grad_x,0.5,abs_grad_y, 0.5, 0, outPut);
        Core.bitwise_not(outPut,outPut);

        bitmapFrameUpdate(outPut);
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
        globalMat = matImageSelector();
        Size si = new Size(5, 5);
        Imgproc.GaussianBlur(globalMat, globalMat, si, 0);
        bitmapFrameUpdate(globalMat);
    }

    private void filterErode() {
        globalMat = matImageSelector();
        Mat kernel = Mat.ones(kernelSize, kernelSize, CvType.CV_32F);
        Imgproc.erode(globalMat, globalMat, kernel);
        bitmapFrameUpdate(globalMat);
    }

    private void filterDilate() {
        globalMat = matImageSelector();
        Mat kernel = Mat.ones(kernelSize, kernelSize, CvType.CV_32F);
        Imgproc.dilate(globalMat, globalMat, kernel);
        bitmapFrameUpdate(globalMat);
    }

    private void filterThreshold() {
        globalMat = matImageSelector();

        Imgproc.cvtColor(globalMat, globalMat, Imgproc.COLOR_RGB2GRAY);
        Imgproc.threshold(globalMat, globalMat, threshold, 255, 0);

        bitmapFrameUpdate(globalMat);
    }

    private void adaptiveThreshold() {
        globalMat = matImageSelector();

        Imgproc.cvtColor(globalMat, globalMat, Imgproc.COLOR_RGB2GRAY);
        Imgproc.adaptiveThreshold(globalMat, globalMat, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 15, 2);

        bitmapFrameUpdate(globalMat);
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

    private void fileGetter(){
        String tempFilename = getIntent().getStringExtra("image");
        try {
            FileInputStream is = ImageEditingActivity.this.openFileInput(tempFilename);
            colourImageMap = BitmapFactory.decodeStream(is);
            is.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void fileWriter(String tempFilename) throws IOException {

        FileOutputStream stream = ImageEditingActivity.this.openFileOutput(tempFilename, Context.MODE_PRIVATE);
        greyImageMap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        //Close the steam
        stream.close();
    }


}
