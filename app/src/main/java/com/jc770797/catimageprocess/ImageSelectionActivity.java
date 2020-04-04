package com.jc770797.catimageprocess;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class ImageSelectionActivity extends AppCompatActivity {

    //Button references
    Button uploadBtn, nextPageBtn;
    //Image View
    ImageView imgView;
    //Image holders
    public static Uri imageUri;
    public static Bitmap imageMap;
    //Image check bool
    private boolean imgSelected = false;
    //Image Getters
    public static Bitmap getBitmap() {
        return imageMap;
    }
    public static Uri getUri() {
        return imageUri;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_selector);

        //assigning the objects to the layout
        imgView = findViewById(R.id.imgView);

        buttonListener();
    }

    //Button Listeners
    private void buttonListener(){
        //assigning the objects to the layout
        uploadBtn = findViewById(R.id.uploadButton);
        nextPageBtn = findViewById(R.id.continueBtn2);

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(gallery, "Select Picture"), 1);
            }
        });

        nextPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imgSelected == true) {
                    startActivity(new Intent(ImageSelectionActivity.this, ImageCropActivity.class));
                }else{
                    Toast.makeText(ImageSelectionActivity.this,"Please Select an image",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }



    //handles image upload and conversion to bitmap.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            imageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                Bitmap bitOut = bitmap.copy(Bitmap.Config.ARGB_8888,true);
                imageMap = bitOut;
                imgView.setImageBitmap(bitOut);

            } catch (IOException e) {
                e.printStackTrace();
            }
            imgSelected = true;
        }
    }


}
