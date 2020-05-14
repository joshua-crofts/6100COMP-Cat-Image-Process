package com.jc770797.catimageprocess.fragments;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.fragment.app.Fragment;

import com.jc770797.catimageprocess.ImageSnakeActivity;
import com.jc770797.catimageprocess.R;

public class SnakeOverlayPointsFragment extends Fragment {

    private ImageView mainImage, overlayImage;
    private Button finishBtn;
    float touchx, touchy, imageX, imageY;
    int[] viewCords = new int[2];
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_snake_overlay, container, false);


    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainImage = getView().findViewById(R.id.snakeOverlayImage);
        mainImage.setImageBitmap(((ImageSnakeActivity)this.getActivity()).getImage());
        overlayImage = getView().findViewById(R.id.pointsOverlayImage);
        overlayImage.setImageBitmap(((ImageSnakeActivity)this.getActivity()).getOverlayImageImage());

        finishBtn = getView().findViewById(R.id.finishBtn);

        finishBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                fragmentSwitch();
                }
            }
        );
        pointListener();
    }

    private void pointListener(){


        final int widthImg = ((ImageSnakeActivity)this.getActivity()).getImage().getWidth();
        final int heightImg = ((ImageSnakeActivity)this.getActivity()).getImage().getHeight();
        Log.d("DEBUG_TEST", "IMG" +widthImg + "   " + heightImg);

        mainImage.getLocationOnScreen(viewCords);
        mainImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int widthView = mainImage.getWidth();
                int heightView = mainImage.getHeight();
                touchx = event.getX();
                touchy = event.getY();
                float widthP = touchx/widthView;
                float heightP = touchy/heightView;
                Log.d("DEBUG_TEST", widthP+ "%   " + heightP + "%");
                imageX =  widthImg * widthP;
                imageY = heightImg * heightP;
                Log.d("DEBUG_TEST", "Final Out INT(X: " + (int)imageX + ")(Y: " + (int)imageY + ")");
                pointAdd((int)imageX,(int)imageY);
                return false;
            }
        });

    }

    private void fragmentSwitch(){
        ((ImageSnakeActivity)this.getActivity()).fragmentChange2();
    }
    private void pointAdd(int x, int y){
        ((ImageSnakeActivity)this.getActivity()).pointAdder(x,y);
        overlayImage.setImageBitmap(((ImageSnakeActivity)this.getActivity()).getOverlayImageImage());
    }


}
