package com.jc770797.catimageprocess.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.jc770797.catimageprocess.R;

public class SnakeOverlayActivityOLD extends Fragment {

    public Bitmap greyImageMap ;
    private ImageView imgSelect;
    private Button btn;
    int[] viewCords = new int[2];
    float touchx, touchy, imageX, imageY;
    private boolean isPointReady = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        return inflater.inflate(R.layout.fragment_snake_overlay, container, false);



    }



    private void empty(){

//        fileGetter();
//        btn  = findViewById(R.id.button2);
//        imgSelect = findViewById(R.id.snakeOverlayImage);
//        imgSelect.setImageBitmap(greyImageMap);
//
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                //SET RETURN
//                finish();
//            }
//        });
//        pointListener();

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

//    private void fileGetter(){
//        String tempFilename = getIntent().getStringExtra("image");
//        try {
//            FileInputStream is = SnakeOverlayFragment.this.openFileInput(tempFilename);
//            greyImageMap = BitmapFactory.decodeStream(is);
//            is.close();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//    }

}
