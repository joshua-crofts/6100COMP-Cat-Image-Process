package com.jc770797.catimageprocess.activeCont;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import org.opencv.core.Mat;

import java.lang.reflect.Array;


public class Snake {

    private Mat imgIn;
    private boolean type;
    private String input;
    private int numPoint = 0;
    private int[][] pointArray;


    public Snake(Mat imgIn, boolean type, String input) {
        this.imgIn = imgIn;
        this.type = type;
        this.input = input;
        //pointArray = new int[imgIn.height()][imgIn.width()];
        Log.d("DEBUG_TEST", "Creating Snake with: y" + imgIn.height() + " X x" + imgIn.width()) ;

    }


    public void createPoint(float x, float y){
//        Log.d("DEBUG_TEST", "ARRAY X : " + (int) x);
//        Log.d("DEBUG_TEST", "ARRAY Y : " + (int) y);

            pointArray[numPoint][0] = (int) x;
            pointArray[numPoint][1] = (int) y;
            numPoint++;
    }


    /**
     *
     * @param currentContext The context from the current activity
     * @param a The alpha controlling elasticity
     * @param b The beta controlling curvature
     * @param g The strength of image energy
     * @param s The neighborhood size
     * @param max The max number of itterations
     */
    public void start(Context currentContext, double a, double b, double g, double s, double max){
        Toast.makeText(currentContext, "Beginning Snake",Toast.LENGTH_SHORT).show();

        if(input == "Greedy"){
            GreedySnake.start(imgIn, pointArray, a, b, g ,s ,max);
        }else if(input == "Kass"){

        }else{

        }


    }



    public Bitmap createBitmap(){
        int width = pointArray.length;
        int height = pointArray[0].length;
        Bitmap.Config config = Bitmap.Config.ARGB_8888;
       Bitmap out = Bitmap.createBitmap(height,width,config);

//        for(int y = 0; y < imgIn.height(); y++){
//            for(int x = 0; x < imgIn.width(); x++){
//                try{
//                    if(pointArray[y][x] == 1){
//
//                            out.setPixel(x-1,y-1, Color.RED);
//                            out.setPixel(x-1,y+1, Color.RED);
//                            out.setPixel(x-1,y, Color.RED);
//
//                            out.setPixel(x,y+1, Color.RED);
//                            out.setPixel(x,y, Color.RED);
//                            out.setPixel(x,y-1, Color.RED);
//
//                            out.setPixel(x+1,y, Color.RED);
//                            out.setPixel(x+1,y-1, Color.RED);
//                            out.setPixel(x+1,y+1, Color.RED);
//
//
//                    }else if(out.getPixel(x,y) != Color.RED){
//
//                            out.setPixel(x,y, Color.TRANSPARENT);
//
//
//                    }
//                }catch (ArrayIndexOutOfBoundsException e){
//                    Log.d("DEBUG_TEST", "Array print OOB  X:" + x + "  Y: " + y);
//                }
//
//            }
//        }

       return out;
    }


    public void printPoints(){

        for(int y = 0; y < imgIn.height(); y++){
            for(int x = 0; x < imgIn.width(); x++){
                try{
                    if(pointArray[y][x] == 1){
                        Log.d("DEBUG_TEST", "Point at X: " + x +" Y: " + y);
                    }else {

                    }
                }catch (ArrayIndexOutOfBoundsException e){
                    Log.d("DEBUG_TEST", "Array print OOB  X:" + x + "  Y: " + y);
                }

            }
        }

    }

    public int getNumPoints() {
        return numPoint;
    }
}
