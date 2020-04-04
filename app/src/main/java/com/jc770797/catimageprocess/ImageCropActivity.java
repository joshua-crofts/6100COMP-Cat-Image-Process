package com.jc770797.catimageprocess;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

public class ImageCropActivity extends AppCompatActivity {


    //Button references
    Button cropBtn, nextPageBtn;
    //Image View
    ImageView imgCrop;

    //Image holders
    public static Bitmap imageMap;
    public Uri imageUri = ImageSelectionActivity.getUri();

    public static Bitmap getBitmap(){
        return imageMap;
    }
    private void setBitmap(){
        this.imageMap = ImageSelectionActivity.getBitmap();
    }
    private void setBitmap(Bitmap map){
        this.imageMap = map;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_crop);

        //assigning the objects to the layout
        imgCrop = findViewById(R.id.imageIn);
        cropBtn = findViewById(R.id.cropBtn);
        nextPageBtn = findViewById(R.id.nextPageBtn);

        setBitmap();

        imgCrop.setImageBitmap(imageMap);
        cropBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCrop(v);
            }
        });

        nextPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ImageCropActivity.this, ImageEditingActivity.class));
            }
        });

    }

    //start crop activity view
    public void startCrop(View v){
        CropImage.activity(imageUri).setActivityTitle("Image Crop").setCropShape(CropImageView.CropShape.RECTANGLE).setCropMenuCropButtonTitle("Done")
                .start(this);

    }


    //handle the image on activity result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try{
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    imgCrop.setImageBitmap(bitmap);
                    setBitmap(bitmap);
                }catch (IOException e){
                    e.printStackTrace();
                }


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


}
