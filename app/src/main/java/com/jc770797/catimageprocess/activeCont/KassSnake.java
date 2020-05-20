package com.jc770797.catimageprocess.activeCont;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;

import com.jc770797.catimageprocess.ImageSnakeActivity;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

public class KassSnake extends ImageSnakeActivity{

    private Bitmap img, test;
    private ArrayList<Point> pointArray ;
    private double alpha;
    private double beta;
    private double delta;
    private double sigma;
    private double maxIterations;


    private int numPoints;
    /**
     * @param img Mat based img
     * @param pointArray The array of points
     * @param alpha The alpha controlling elasticity
     * @param beta The beta controlling curvature
     * @param delta Step size
     * @param sigma The neighborhood size
     * @param maxItter The max number of itterations
     * @param numPoints number of snake points
     */
    public KassSnake(Bitmap img,ArrayList<Point>  pointArray, double alpha, double beta, double delta, double sigma, double maxItter, int numPoints) {
        this.img = img;
        this.pointArray = pointArray;
        this.alpha = alpha;
        this.beta = beta;
        this.delta = delta;
        this.sigma = sigma;
        this.maxIterations = maxItter;
        this.numPoints = numPoints;
    }

    public void start() {

    double stopThresh = 0.0008;
    int stop = 0;
    int scale = 1;
    double energyArray[][] = getImageEnergy(img, true);
    double maxArrayEnrg = 0;
    double minArrayEnrg = 200;

        for (int i = 0; i < energyArray.length; i++) {
            for (int j = 0; j < energyArray[i].length; j++) {
                if(energyArray[i][j] < maxArrayEnrg){
                    maxArrayEnrg = energyArray[i][j];
                }else if(energyArray[i][j] > minArrayEnrg){
                    minArrayEnrg = energyArray[i][j];
                }


            }
        }

        for (int i = 0; i < energyArray.length; i++) {
            for (int j = 0; j < energyArray[i].length; j++) {
                energyArray[i][j] = (energyArray[i][j] - maxArrayEnrg)/(minArrayEnrg-maxArrayEnrg);
            }
        }


        test = Bitmap.createBitmap(energyArray.length, energyArray[1].length, Bitmap.Config.ARGB_8888);
        for (int i = 0; i < energyArray.length; i++) {
            for (int j = 0; j < energyArray[i].length; j++) {
                //Log.d("DEBUG_TEST", "Val: " + energyArray[i][j] * 1000);
                double out1 =  energyArray[i][j];
                int out = (int) (out1* 1000);
                //Log.d("DEBUG_TEST", "Val: " + out );
                int colOut = Color.argb(255, out , out,out);
                test.setPixel(i,j, colOut );
            }
        }


        //Initialise the alpha&beta on the points
        int h = 1;

        //create a matrix using the contructA method
        double exampleArray[][] = arrayConstruct();

        //inversion of matrix

        beginSnakeRefactor();

    }

    private void beginSnakeRefactor() {

        for (int i = 0; i < maxIterations; i++) {
            //change this statement to alter iteration counter
                if((i % 15) == 0){
                    super.reSample();
                }

            double energyX = 0;//interpolation here
            double energyY = 0;//interpolation here


        }
    }

    private void snakeResample() {
        //alter the snake image to move the objects
    }

    private double[][] arrayConstruct() {
       double testArray[][] = new double[1][1];

        return testArray;
    }

    private double[][] getDerivatives() {
      double arrayOut[][] = new double[1][1];




        return arrayOut;
    }

    /**
     *
     * @param img Input image
     * @param inverted Returns an inverted image if true
     * @return
     */
    public double[][] getImageEnergy(Bitmap img,  boolean inverted){
        int width = img.getWidth();
        int height = img.getHeight();
        double[][] energyArray = new double[width][height];


        Mat matImg = new Mat(img.getWidth(), img.getHeight(), CvType.CV_8UC1);
        Utils.bitmapToMat(img, matImg);

        Imgproc.cvtColor(matImg, matImg, Imgproc.COLOR_RGB2GRAY);

        Bitmap energyImg = Bitmap.createBitmap(width,height, img.getConfig());


        Mat outPut = new Mat();
        Mat grad_x = new Mat(), grad_y = new Mat();
        Mat abs_grad_x = new Mat(), abs_grad_y = new Mat();

        int depth = CvType.CV_16S;
        Imgproc.Sobel(matImg, grad_x, depth, 1,0,5, 1, 0, Core.BORDER_DEFAULT);
        Imgproc.Sobel(matImg, grad_y, depth, 0,1,5, 1, 0,Core.BORDER_DEFAULT);

        Core.convertScaleAbs(grad_x,abs_grad_x);
        Core.convertScaleAbs(grad_y,abs_grad_y);

        Core.addWeighted(abs_grad_x,0.5,abs_grad_y, 0.5, 0, outPut);

        if(inverted){
            Core.bitwise_not(outPut,outPut);
        }

        Utils.matToBitmap(outPut, energyImg);


        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                try{
                    //Log.d("DEBUG_TEST", "Pix Val: " + Math.sqrt(Color.blue(energyImg.getPixel(x,y))));
                    energyArray[x][y] = Math.sqrt(Color.blue(energyImg.getPixel(x,y)));
                }catch (Exception e){
                    Log.d("DEBUG_TEST", "Error");
                }
            }
        }
        Log.d("DEBUG_TEST", "Done");


        return energyArray;
    }

    public Bitmap returnImage(){
        return test;
    }
}
