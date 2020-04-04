package com.jc770797.catimageprocess;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;

public class SnakeOverlayActivity extends AppCompatActivity {

    public Bitmap greyImageMap ;
    private ImageView imgSelect;
    private Button btn;
    int[] viewCords = new int[2];
    float touchx, touchy, imageX, imageY;
    private boolean isPointReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snake_overlay);

        fileGetter();

        btn  = findViewById(R.id.button2);
        imgSelect = findViewById(R.id.snakeOverlayImage);
        imgSelect.setImageBitmap(greyImageMap);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //SET RETURN
                finish();
            }
        });




        pointListener();
    }



        private void pointListener(){
        imgSelect.getLocationOnScreen(viewCords);
        imgSelect.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                touchx = event.getX();
                touchy = event.getY();
                imageX = touchx;
                imageY = touchy ;

                    Log.d("DEBUG_TEST", "X : " + imageX);
                    Log.d("DEBUG_TEST", "Y : " + imageY);
                    //snake.createPoint(imageX,imageY);



                return false;
            }
        });

    }

    private void fileGetter(){
        String tempFilename = getIntent().getStringExtra("image");
        try {
            FileInputStream is = SnakeOverlayActivity.this.openFileInput(tempFilename);
            greyImageMap = BitmapFactory.decodeStream(is);
            is.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
