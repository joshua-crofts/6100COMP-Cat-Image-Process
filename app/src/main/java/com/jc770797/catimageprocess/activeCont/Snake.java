package com.jc770797.catimageprocess.activeCont;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;


public class Snake {
    private Bitmap img;
    private boolean type;
    private String input;
    private int numPoint = 0;

    public int[][] getPointArray() {
        return pointArray;
    }

    private int[][] pointArray = new int[200][2];
    private KassSnake kSnake;

    public Snake(Bitmap img, boolean type, String input) {
        this.img = img;
        this.type = type;
        this.input = input;
    }


    public void createPoint(int x, int y){
            pointArray[numPoint][0] = x;
            pointArray[numPoint][1] = y;
            numPoint++;
    }



    public void start(Context currentContext){
        Toast.makeText(currentContext, "Beginning Snake",Toast.LENGTH_SHORT).show();
        if(input == "Greedy"){

           // greedySnake gSnake = new greedySnake(pointArray, a, b, g ,s ,max, numPoint);
            //kSnake.start();
        }else if(input == "Kass"){

            double alpha = 0.05;
            double beta = 0.0005;
            double delta = 1;
            double sigma = 3;
            double itterations = 500;


            kSnake = new KassSnake(img,pointArray, alpha, beta, delta ,sigma ,itterations, numPoint);
            kSnake.start();
        }else{

        }


    }

    public void printPoints(){
        for(int i = 0; i < numPoint; i++){
            Log.d("DEBUG_TEST", "Point: " +numPoint +"(X:" +  pointArray[numPoint][0] + ")(Y: " + pointArray[numPoint][1] + ")");
        }

    }


    public Bitmap getImg() {
        return kSnake.returnImage();
    }

    public int getNumPoints() {
        return numPoint;
    }
}
