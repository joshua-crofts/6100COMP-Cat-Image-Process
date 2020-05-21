package com.jc770797.catimageprocess.activeCont;

import android.graphics.Bitmap;
import android.graphics.Point;

import com.jc770797.catimageprocess.ImageSnakeActivity;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayDeque;
import java.util.ArrayList;

public class KassSnake extends ImageSnakeActivity {

    private Bitmap img, test;
    private ArrayList<Point> pointArray;
    private double alpha;
    private double beta;
    private double delta;
    private double sigma;
    private double maxIterations;


    private int numPoints;

    /**
     * @param img        Bitmap based img
     * @param pointArray The array of points
     * @param alpha      The alpha controlling elasticity
     * @param beta       The beta controlling curvature
     * @param delta      Step size
     * @param sigma      The neighborhood size
     * @param maxItter   The max number of itterations
     * @param numPoints  number of snake points
     */
    public KassSnake(Bitmap img, ArrayList<Point> pointArray, double alpha, double beta, double delta, double sigma, double maxItter, int numPoints) {
        this.img = img;
        this.pointArray = pointArray;
        this.alpha = alpha;
        this.beta = beta;
        this.delta = delta;
        this.sigma = sigma;
        this.maxIterations = maxItter;
        this.numPoints = numPoints;
    }

    public ArrayList<ArrayList> start() {
        Mat energyArray = getImageEnergy(img, true);

        Core.normalize(energyArray, energyArray, Core.NORM_MINMAX);

        int h = 1;

        float floatArray[][] = matrixConstruct();
        Mat gradientX = new Mat(energyArray.width(), energyArray.height(), CvType.CV_32FC1);
        Mat gradientY = new Mat(energyArray.width(), energyArray.height(), CvType.CV_32FC1);
        Imgproc.Sobel(energyArray, gradientX, CvType.CV_32FC1, 1, 0, 5, 1, 0, Core.BORDER_DEFAULT);
        Imgproc.Sobel(energyArray, gradientY, CvType.CV_32FC1, 0, 1, 5, 1, 0, Core.BORDER_DEFAULT);

        return beginSnakeRefactor(gradientX, gradientY, floatArray);
    }

    private ArrayList<ArrayList> beginSnakeRefactor(Mat gradientX, Mat gradientY, float[][] floatArray) {

        Mat pointX = new Mat(2, pointArray.size(), CvType.CV_32FC1);
        Mat pointY = new Mat(2, pointArray.size(), CvType.CV_32FC1);
        Mat coefMatrix = new Mat(floatArray.length, floatArray[0].length, CvType.CV_32FC1);
        ArrayList<ArrayList> listOfPointers = new ArrayList<>();

        for (int i = 0; i < coefMatrix.height(); i++) {
            coefMatrix.put(i, 0, floatArray[i]);
        }

        Mat pointXY = new Mat(2, pointArray.size(), CvType.CV_32FC1);
        float[] xTrans = new float[pointArray.size()];
        float[] yTrans = new float[pointArray.size()];


        for (int i = 0; i < pointArray.size(); i++) {
            Point point = pointArray.get(i);
            xTrans[i] = point.x;
            yTrans[i] = point.y;
        }
        float[] test = {0, 0, 0, 0};


        pointXY.put(0, 0, xTrans);
        pointXY.put(1, 0, yTrans);
        pointX.put(0, 0, xTrans);
        pointX.put(1, 0, test);
        pointY.put(0, 0, yTrans);
        pointY.put(1, 0, test);


        for (int i = 0; i < maxIterations; i++) {
            Mat outPutX = new Mat(2, pointArray.size(), CvType.CV_32FC1);
            Mat outPutY = new Mat(2, pointArray.size(), CvType.CV_32FC1);

            //change this statement to alter iteration counter

            Imgproc.remap(gradientX, outPutX, pointX, pointY, Imgproc.INTER_LINEAR);
            Imgproc.remap(gradientY, outPutY, pointX, pointY, Imgproc.INTER_LINEAR);


            Core.flip(outPutY, outPutY, 0);


            //Imgproc.filter2D(outPutX,outPutX, CvType.CV_32FC1, coefMatrix);
            //Imgproc.filter2D(outPutY,outPutY, CvType.CV_32FC1, coefMatrix);
            Mat combineXY = new Mat(pointArray.size(), 2, CvType.CV_32FC1);
            Core.add(outPutX, outPutY, combineXY);
            Core.add(pointXY, combineXY, pointXY);

            if ((i % 10) == 0) {
            ArrayList<Point> pointTest = new ArrayList<>();

            for (int j = 0; j < pointXY.width(); j++) {
                Point newPoint = new Point();
                double[] data0 = pointXY.get(0, j);
                double[] data1 = pointXY.get(1, j);
                newPoint.x = (int) data0[0];
                newPoint.y = (int) data1[0];
                pointTest.add(newPoint);
            }
            listOfPointers.add(pointTest);
            }
        }





        return listOfPointers;
    }


    private float[][] matrixConstruct() {
        double h = 1;

        double a = 0, b = 0, c = 0, d = 0, e = 0;

        for (int i = 0; i < pointArray.size(); i++) {
            a = beta / Math.pow(h, 4.0);
            b = (-2 * (beta + beta) / Math.pow(h, 4.0) - alpha + alpha / Math.pow(h, 2.0)) + 1;
            c = (beta + 4 * beta + beta) / Math.pow(h, 4.0) + (alpha + alpha) / Math.pow(h, 2.0);
            d = (-2 * (beta + beta) / Math.pow(h, 4.0) - alpha + alpha / Math.pow(h, 2.0));
            e = beta / Math.pow(h, 4.0);
        }

        ArrayDeque<Double> B = new ArrayDeque<>();

        B.add(c);
        B.add(d);
        B.add(e);
        for (int i = 0; i < pointArray.size() - 4; i++) {
            B.add((double) 0);
        }
        B.add(a);
        B.add(b);


        float returnArray[][] = new float[pointArray.size()][pointArray.size()];
        for (int i = 0; i < pointArray.size(); i++) {
            for (int j = 0; j < pointArray.size(); j++) {
                double val = B.pop();
                returnArray[j][i] = (float) val;
                B.add(val);
            }
        }

        return returnArray;
    }

    /**
     * @param img      Input image
     * @param inverted Returns an inverted image if true
     * @return
     */
    public Mat getImageEnergy(Bitmap img, boolean inverted) {
        Mat matImg = new Mat(img.getWidth(), img.getHeight(), CvType.CV_32FC1);

        Utils.bitmapToMat(img, matImg);
        Imgproc.cvtColor(matImg, matImg, Imgproc.COLOR_RGB2GRAY);

        Mat outPutMat = new Mat(img.getWidth(), img.getHeight(), CvType.CV_32FC1);
        Mat grad_x = new Mat(img.getWidth(), img.getHeight(), CvType.CV_32FC1), grad_y = new Mat(img.getWidth(), img.getHeight(), CvType.CV_32FC1);
        Mat abs_grad_x = new Mat(img.getWidth(), img.getHeight(), CvType.CV_32FC1), abs_grad_y = new Mat(img.getWidth(), img.getHeight(), CvType.CV_32FC1);

        int depth = CvType.CV_32FC1;
        Imgproc.Sobel(matImg, grad_x, depth, 1, 0, 5, 1, 0, Core.BORDER_DEFAULT);
        Imgproc.Sobel(matImg, grad_y, depth, 0, 1, 5, 1, 0, Core.BORDER_DEFAULT);

        Core.convertScaleAbs(grad_x, abs_grad_x);
        Core.convertScaleAbs(grad_y, abs_grad_y);

        Core.addWeighted(abs_grad_x, 0.5, abs_grad_y, 0.5, 0, outPutMat);

        if (inverted) {
            Core.bitwise_not(outPutMat, outPutMat);
        }

        outPutMat.convertTo(outPutMat, CvType.CV_32FC1);
        Core.sqrt(outPutMat, outPutMat);

        return outPutMat;
    }

    public Bitmap returnTestImage() {
        return test;
    }
}
