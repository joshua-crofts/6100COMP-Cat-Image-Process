package com.jc770797.catimageprocess;


import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Scatter;
import com.anychart.core.scatter.series.Line;
import com.anychart.core.scatter.series.Marker;
import com.anychart.enums.HoverMode;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipDisplayMode;
import com.anychart.graphics.vector.GradientKey;
import com.anychart.graphics.vector.LinearGradientStroke;
import com.anychart.graphics.vector.SolidFill;
import com.anychart.graphics.vector.text.HAlign;



import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class ImageResultsActivity extends AppCompatActivity {

    private Button exitBtn, toTextBtn, testDataBtn;
    private ArrayList<Point> pointArray;
    private boolean arrayEmpty = true;
    private static final String FILE_NAME = "Cat-Imaging-Export.txt";
    private String exportString = null;


    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_results);
        pointArray = getData();
        exitBtn = findViewById(R.id.exitBtn);
        toTextBtn = findViewById(R.id.toTextBtn);
        testDataBtn = findViewById(R.id.testArray);

        if (!arrayEmpty) {
            testDataBtn.setEnabled(false);
            testDataBtn.setVisibility(View.GONE);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    chartSetup();
                }
            }, 1000);
        }
        buttonListener();
    }
    //Generic button listener
    private void buttonListener() {
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = getBaseContext().getPackageManager().
                        getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });
        toTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (exportString != null) {
                    FileOutputStream fileOutputStream = null;
                    try {
                        fileOutputStream = openFileOutput(FILE_NAME, MODE_APPEND);
                        fileOutputStream.write(exportString.getBytes());
                        Toast.makeText(ImageResultsActivity.this, "Saved to: " + getFilesDir() + "/" + FILE_NAME, Toast.LENGTH_LONG).show();
                    }catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (fileOutputStream != null) {
                            try {
                                fileOutputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }else{
                    Toast.makeText(ImageResultsActivity.this, "Cannot save without results" , Toast.LENGTH_LONG).show();
                }
            }
        });
        testDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int random = new Random().nextInt(30) + 20;

                for (int i = 0; i < random; i++) {
                    Point point = new Point(new Random().nextInt(3000), new Random().nextInt(3000));
                    pointArray.add(point);
                }
                chartSetup();
            }
        });
    }
    //try and get the serialised point array
    private ArrayList<Point> getData() {
        ArrayList<Point> arrayIN;
        int numPoints = 0;
        try {
            Bundle b = getIntent().getExtras();
            arrayIN = (ArrayList<Point>) b.getSerializable("Array");
            numPoints = b.getInt("numPoint");
            if (numPoints != 0) {
                arrayEmpty = false;
                }
        } catch (Exception e) {
            arrayIN = null;
        }
        return arrayIN;
    }
    //basic chart setup
    private void chartSetup() {
        double[] leastSquareData = setTextValues();

        AnyChartView regressionChart = findViewById(R.id.mainChart);
        regressionChart.setChart(null);
        Scatter chartScatter = AnyChart.scatter();
        chartScatter.animation(true);
        chartScatter.title("Linear Regression");
        chartScatter.yAxis(0).title("Y pos");
        chartScatter.xAxis(0).title("X pos").drawFirstLabel(false).drawLastLabel(false);
        chartScatter.interactivity().hoverMode(HoverMode.BY_SPOT).spotRadius(30d);
        chartScatter.tooltip().displayMode(TooltipDisplayMode.SINGLE);

        Marker marker = chartScatter.marker(getMarkerData());
        marker.type(MarkerType.CROSS)
                .size(4d);
        marker.hovered()
                .size(7d)
                .fill(new SolidFill("gold", 1d))
                .stroke("anychart.color.darken(gold)");
        marker.tooltip()
                .hAlign(HAlign.START)
                .format("Y pos:  {%Value} \\nX pos: {%X}");

        Line scatterSeriesLine = chartScatter.line(getLineData(leastSquareData));
        GradientKey gradientKey[] = new GradientKey[]{
                new GradientKey("#abcabc", 0d, 1d),
                new GradientKey("#cbacba", 40d, 1d)
        };
        LinearGradientStroke linearGradientStroke = new LinearGradientStroke(0d, null, gradientKey, null, null, true, 1d, 2d);
        scatterSeriesLine.stroke(linearGradientStroke, 3d, null, (String) null, (String) null);

        regressionChart.setChart(chartScatter);
    }
    //set the text value for exporting data to text
    private double[] setTextValues() {
        double[] leastSquareData = leastSquare();

        TextView finalValTxt = findViewById(R.id.textView1);
        TextView b0Txt = findViewById(R.id.textView2);
        TextView b1Txt = findViewById(R.id.textView3);
        TextView countTxt = findViewById(R.id.textView4);
        TextView xMeanTxt = findViewById(R.id.textView5);
        TextView yMeanTxt = findViewById(R.id.textView6);

        finalValTxt.setText("Final Value: " + leastSquareData[2]);
        b0Txt.setText("b0: " + leastSquareData[0]);
        b1Txt.setText("b1: " + leastSquareData[1]);
        countTxt.setText("Number of Points: " + pointArray.size());
        xMeanTxt.setText("xMean: " + leastSquareData[3]);
        yMeanTxt.setText("yMean: " + leastSquareData[4]);

        exportString = " Final Value: " + leastSquareData[2] + " -b0: " + leastSquareData[0] + " -b1: " + leastSquareData[1] + " -Number of Points: " + pointArray.size() + " -xMean: " + leastSquareData[3] + " -yMean: " + leastSquareData[4] ;
        return leastSquareData;
    }
    //least square method used to calculate the regression line
    private double[] leastSquare() {
        float xMean = 0, yMean = 0, xXSum = 0, xYSum = 0;
        double finalVal = 0;
        double b1 = 0, b0 = 0;
        double[] dataOutputArray = new double[5];
        //find the mean value of the X and Y coordinates
        for (int i = 0; i < pointArray.size(); i++) {
            xMean += pointArray.get(i).x;
            yMean += pointArray.get(i).y;
        }
        xMean = xMean / pointArray.size();
        yMean = yMean / pointArray.size();
        for (int i = 0; i < pointArray.size(); i++) {
            xXSum += (pointArray.get(i).x - xMean) * (pointArray.get(i).x - xMean);
            xYSum += (pointArray.get(i).x - xMean) * (pointArray.get(i).y - yMean);
        }
        //Calculate b1(line gradient), b0(Y intersect) and the final value (Ymean)
        b1 = xYSum / xXSum;
        b0 = yMean - (b1 * xMean);
        finalVal = b0 + (b1 * xMean);
        //save the data to an output array
        dataOutputArray[0] = b0;
        dataOutputArray[1] = b1;
        dataOutputArray[2] = finalVal;
        dataOutputArray[3] = xMean;
        dataOutputArray[4] = yMean;
        return dataOutputArray;
    }
    //uses the data to produce the regression line
    private List<DataEntry> getLineData(double[] leastSquareData) {
        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry(0, leastSquareData[0]));
        data.add(new ValueDataEntry(leastSquareData[3], leastSquareData[4]));
        double gradX = leastSquareData[3] - 0;
        double gradY = leastSquareData[4] - leastSquareData[0];
        data.add(new ValueDataEntry(gradX + leastSquareData[3], gradY + leastSquareData[4]));
        return data;
    }
    //uses point data to create the scatter points
    private List<DataEntry> getMarkerData() {
        List<DataEntry> data = new ArrayList<>();
        for (int i = 0; i < pointArray.size(); i++) {
            int x = (int) pointArray.get(i).x;
            int y = (int) pointArray.get(i).y;
            data.add(new ValueDataEntry(x, y));
        }
        return data;
    }
}
