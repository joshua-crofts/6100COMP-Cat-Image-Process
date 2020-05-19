package com.jc770797.catimageprocess.activeCont;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;
import android.widget.Toast;



import java.util.ArrayList;


public class Snake {
    private Bitmap img;
    private boolean type;
    private String input;
    private int numPoint = 0;

    public ArrayList<Point> getPointArray() {
        return pointArray;
    }
    private ArrayList<Point> pointArray= new ArrayList<>();
    //private int[][] pointArray = new int[200][2];
    private KassSnake kSnake;

    public Snake(Bitmap img, boolean type, String input) {
        this.img = img;
        this.type = type;
        this.input = input;
    }


    public void createPoint(int x, int y){
        Point point = new Point(x,y);
        pointArray.add(point);//            pointArray[numPoint][0] = x;
//            pointArray[numPoint][1] = y;
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
            Log.d("DEBUG_TEST", "Point: " +numPoint +"(X:" +  pointArray.get(i).x + ")(Y: " + pointArray.get(i).y + ")");
        }

    }


    public Bitmap getImg() {
        return kSnake.returnImage();
    }

    public int getNumPoints() {
        return numPoint;
    }
}
