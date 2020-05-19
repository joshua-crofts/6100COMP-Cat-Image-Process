package com.jc770797.catimageprocess.fragments;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.jc770797.catimageprocess.ImageSnakeActivity;
import com.jc770797.catimageprocess.R;

import java.util.ArrayList;

public class SnakeOverlayMainFragment extends Fragment {

    private ImageView mainImage, overlayImage;
    private Button pointsBtn, snakeStartBtn, testBtn;
    private ArrayList<Point> pointArray;

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


        pointsBtn = getView().findViewById(R.id.pointsBtn);
        snakeStartBtn = getView().findViewById(R.id.snakeStartBtn);

        if (((ImageSnakeActivity) this.getActivity()).getPointNum() == 0) {
            snakeStartBtn.setEnabled(false);
            pointsBtn.setEnabled(true);
        } else {
            overlayImage.setImageBitmap(((ImageSnakeActivity) this.getActivity()).getOverlayImageImage());
            pointArray  = ((ImageSnakeActivity) this.getActivity()).getPointArray();
            snakeStartBtn.setEnabled(true);
            pointsBtn.setEnabled(false);
        }

        testBtn = getView().findViewById(R.id.testBtn);
        buttonListener();
    }

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
                //start snake here
                snakeStart();
            }
        });

        testBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                splineTest();
                //testImgSwitch();
            }
        });

    }

    private void splineTest() {

        Canvas spline = new Canvas(((ImageSnakeActivity) this.getActivity()).getOverlayImageImage());
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(2);
        Path path = new Path();

        path.moveTo(pointArray.get(0).x, pointArray.get(0).y);
        for (int i = 0; i < pointArray.size() -1; i++) {
            if(i != pointArray.size()){
                path.quadTo(pointArray.get(i).x, pointArray.get(i).y, pointArray.get(i +1).x, pointArray.get(i + 1).y);
            }else{
                path.quadTo(pointArray.get(i).x, pointArray.get(i).y, pointArray.get(0).x, pointArray.get(0).y);
                path.close();
            }


        }
        paint.setStyle(Paint.Style.STROKE);
        spline.drawPath(path, paint);

        overlayImage.setImageBitmap(((ImageSnakeActivity) this.getActivity()).getOverlayImageImage());
    }

    private void testImgSwitch() {
        switchImage(((ImageSnakeActivity) this.getActivity()).snakeGetTestImg());
    }


    private void snakeStart() {
        ((ImageSnakeActivity) this.getActivity()).snakeStart();
    }

    private void fragmentSwitch() {
        ((ImageSnakeActivity) this.getActivity()).fragmentChange();
    }

    public void switchImage(Bitmap imgIn) {
        mainImage.setImageBitmap(imgIn);
    }

    public void buttonSwitch() {
        snakeStartBtn.setEnabled(true);
        pointsBtn.setEnabled(false);
    }

}
