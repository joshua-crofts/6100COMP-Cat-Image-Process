package com.jc770797.catimageprocess;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageCropActivity extends AppCompatActivity {


    //Button references
    Button cropBtn, nextPageBtn;
    //Image View
    ImageView imgCrop;

    //Image holders
    public  Bitmap imageMap;
    public Uri imageUri;
    boolean test = true;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_crop);


        fileGetter();
        imageUri = getIntent().getParcelableExtra("imageUri");

        //assigning the objects to the layout
        imgCrop = findViewById(R.id.imageIn);
        cropBtn = findViewById(R.id.cropBtn);
        nextPageBtn = findViewById(R.id.nextPageBtn);

        //setBitmap();

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

                try{
                    //Write the file to storage
                    String tempFilename = "catPr_bitmap.png";
                    fileWriter(tempFilename);
                    //imageMap.recycle();

                    //Create the intent and add the filename to it
                    Intent intent = new Intent(ImageCropActivity.this, ImageEditingActivity.class);
                    intent.putExtra("image", tempFilename);
                    startActivity(intent);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    protected void onStart(){
        super.onStart();
        if(test){

            fileGetter();
            try {
                imgCrop.setImageBitmap(imageMap);
            }catch (Exception e){
                e.printStackTrace();

            }


            imageUri = Uri.fromFile(new File(ImageCropActivity.this.getFilesDir() + "/catPr_bitmap.png"));
            test = true;
        }
    }

    //start crop activity view
    public void startCrop(View v){
        CropImage.activity(imageUri).setActivityTitle("Image Crop").setCropShape(CropImageView.CropShape.RECTANGLE).setCropMenuCropButtonTitle("Done")
                .start(this);

    }


    //handle the image on activity result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);

                    imageMap = bitmap;
                    imgCrop.setImageBitmap(imageMap);
                    test = false;
                } catch (IOException e) {
                    e.printStackTrace();
                }


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void fileGetter(){
        String tempFilename = getIntent().getStringExtra("image");
        try {
            FileInputStream is = ImageCropActivity.this.openFileInput(tempFilename);
            imageMap = BitmapFactory.decodeStream(is);
            is.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void fileWriter(String tempFilename) throws IOException {
        FileOutputStream stream = ImageCropActivity.this.openFileOutput(tempFilename, Context.MODE_PRIVATE);
        imageMap.compress(Bitmap.CompressFormat.PNG, 100, stream);


        //Close the steam
        stream.close();
    }

}
