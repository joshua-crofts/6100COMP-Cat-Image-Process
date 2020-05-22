package com.jc770797.catimageprocess.fragments;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.jc770797.catimageprocess.ImageSnakeActivity;
import com.jc770797.catimageprocess.R;

import java.util.ArrayList;

public class SnakeOverlayMainFragment extends Fragment {

    private ImageView mainImage, overlayImage;
    private Button pointsBtn, snakeStartBtn;
    private ArrayList<Point> pointArray;
    private TextView pointerCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_snake_main, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainImage = getView().findViewById(R.id.imageViewSnake);
        mainImage.setImageBitmap(((ImageSnakeActivity) this.getActivity()).getImage());
        overlayImage = getView().findViewById(R.id.imageViewSnakeOverlay);

        pointerCount = getView().findViewById(R.id.numOfPoints);
        try {
            pointerCount.setText("Number of points: " + ((ImageSnakeActivity) this.getActivity()).getPointNum());
        } catch (Exception e) {
            pointerCount.setText("Number of points: 0");
        }

        pointsBtn = getView().findViewById(R.id.pointsBtn);
        snakeStartBtn = getView().findViewById(R.id.snakeStartBtn);

        if (((ImageSnakeActivity) this.getActivity()).getPointNum() == 0) {
            snakeStartBtn.setEnabled(false);
            pointsBtn.setEnabled(true);
        } else {
            overlayImage.setImageBitmap(((ImageSnakeActivity) this.getActivity()).getOverlayImageImage());
            pointArray = ((ImageSnakeActivity) this.getActivity()).getPointArray();
            snakeStartBtn.setEnabled(true);
            pointsBtn.setEnabled(false);
        }

        buttonListener();
    }

    //generic button listener class
    private void buttonListener() {
        pointsBtn.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             fragmentSwitch();
                                         }
                                     }
        );
        snakeStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long startTime = System.nanoTime();
                snakeStart();
                long stopTime = System.nanoTime();
                Log.d("FUNCTION_TIME_TEST", "Start Time: " + startTime + "  Stop Time: " + stopTime);
            }
        });

    }

    //calls the snake start class from the activity, iterates over snake history upon completion
    private void snakeStart() {
        ((ImageSnakeActivity) this.getActivity()).snakeStart();
        for (int i = 0; i < ((ImageSnakeActivity) this.getActivity()).sizeOfPointerList(); i++) {
            ((ImageSnakeActivity) this.getActivity()).snakePlayback(i);
            overlayImage.setImageBitmap(((ImageSnakeActivity) this.getActivity()).overlayBuilder());
        }
    }

    //switches the fragment
    private void fragmentSwitch() {
        ((ImageSnakeActivity) this.getActivity()).fragmentChange();
    }
}
