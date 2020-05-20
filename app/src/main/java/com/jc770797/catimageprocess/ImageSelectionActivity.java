package com.jc770797.catimageprocess;

import android.content.Context;
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
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageSelectionActivity extends AppCompatActivity {

    //Button references
    Button uploadBtn, nextPageBtn, results;
    //Image View
    ImageView imgView;
    //Image holders
    public  Uri imageUri;
    public  Bitmap imageMap;
    //Image check bool
    private boolean imgSelected = false;


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
        results = findViewById(R.id.resultsBtn);
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
                    try{
                        //Write the file to storage
                        String tempFilename = "catPr_bitmap.png";
                        fileWriter(tempFilename);
                        //Create the intent and add the filename to it
                        Intent intent = new Intent(ImageSelectionActivity.this, ImageCropActivity.class);
                        intent.putExtra("image", tempFilename);
                        intent.putExtra("imageUri", imageUri);
                        startActivity(intent);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(ImageSelectionActivity.this,"Please Select an image",Toast.LENGTH_SHORT).show();
                }
            }
        });

                results.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ImageSelectionActivity.this, ResultListActivity.class));
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

    //Save the image to storage to be called in the next activity
    private void fileWriter(String tempFilename) throws IOException {
        FileOutputStream stream = ImageSelectionActivity.this.openFileOutput(tempFilename, Context.MODE_PRIVATE);
        imageMap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        //Close the steam
        stream.close();
    }


}
