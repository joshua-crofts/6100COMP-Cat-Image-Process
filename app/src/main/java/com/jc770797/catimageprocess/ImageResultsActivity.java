package com.jc770797.catimageprocess;


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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class ImageResultsActivity extends AppCompatActivity {

    private Button exitBtn, toTextBtn, testDataBtn;
    private int[][] pointArray;
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
    private void buttonListener() {
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        toTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (exportString != null) {
                    FileOutputStream fileOutputStream = null;
                    try {
                        fileOutputStream = openFileOutput(FILE_NAME, MODE_PRIVATE);
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
                pointArray = new int[random][2];
                for (int i = 0; i < random; i++) {

                    pointArray[i][0] = new Random().nextInt(3000);//x
                    pointArray[i][1] = new Random().nextInt(3000);//y
                }
                chartSetup();
            }
        });


    }

    private int[][] getData() {
        int[][] arrayIN, tempArray;
        int numPoints = 0;
        try {
            Bundle b = getIntent().getExtras();
            tempArray = (int[][]) b.getSerializable("Array");
            numPoints = b.getInt("numPoint");
            if (numPoints != 0) {
                arrayIN = new int[numPoints][2];
                for (int i = 0; i < numPoints; i++) {
                    arrayIN[i] = tempArray[i];
                }
                arrayEmpty = false;
            } else {
                arrayIN = null;
            }

        } catch (Exception e) {
            arrayIN = null;
        }

        return arrayIN;
    }



    private void chartSetup() {
        double[] textVals = setTextValues();


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

        Line scatterSeriesLine = chartScatter.line(getLineData(textVals));
        GradientKey gradientKey[] = new GradientKey[]{
                new GradientKey("#abcabc", 0d, 1d),
                new GradientKey("#cbacba", 40d, 1d)
        };
        LinearGradientStroke linearGradientStroke = new LinearGradientStroke(0d, null, gradientKey, null, null, true, 1d, 2d);
        scatterSeriesLine.stroke(linearGradientStroke, 3d, null, (String) null, (String) null);

        regressionChart.setChart(chartScatter);
    }

    private double[] setTextValues() {
        double[] linearVals = leastSquare();

        TextView finalValTxt = findViewById(R.id.textView1);
        TextView b0Txt = findViewById(R.id.textView2);
        TextView b1Txt = findViewById(R.id.textView3);
        TextView countTxt = findViewById(R.id.textView4);
        TextView xMeanTxt = findViewById(R.id.textView5);
        TextView yMeanTxt = findViewById(R.id.textView6);

        finalValTxt.setText("Final Value: " + linearVals[2]);
        b0Txt.setText("b0: " + linearVals[0]);
        b1Txt.setText("b1: " + linearVals[1]);
        countTxt.setText("Number of Points: " + pointArray.length);
        xMeanTxt.setText("xMean: " + linearVals[3]);
        yMeanTxt.setText("yMean: " + linearVals[4]);

        exportString = "Final Value: " + linearVals[2] + " -b0: " + linearVals[0] + " -b1: " + linearVals[1] + " -Number of Points: " + pointArray.length + " -xMean: " + linearVals[3] + " -yMean: " + linearVals[4];
        return linearVals;
    }

    private double[] leastSquare() {

        float xMean = 0, yMean = 0, xXSum = 0, xYSum = 0;
        double finalVal = 0;
        double b1 = 0, b0 = 0;
        double[] aOut = new double[5];
        for (int i = 0; i < pointArray.length; i++) {
            xMean += pointArray[i][0];
            yMean += pointArray[i][1];
        }
        xMean = xMean / pointArray.length;
        yMean = yMean / pointArray.length;


        for (int i = 0; i < pointArray.length; i++) {

            xXSum += (pointArray[i][0] - xMean) * (pointArray[i][0] - xMean);
            xYSum += (pointArray[i][0] - xMean) * (pointArray[i][1] - yMean);

        }

        b1 = xYSum / xXSum;
        b0 = yMean - (b1 * xMean);
        //b0 = 4 - (.6*(3))
        finalVal = b0 + (b1 * xMean);

        aOut[0] = b0;
        aOut[1] = b1;
        aOut[2] = finalVal;
        aOut[3] = xMean;
        aOut[4] = yMean;

        return aOut;
    }

    private List<DataEntry> getLineData(double[] textVals) {
        List<DataEntry> data = new ArrayList<>();

        data.add(new ValueDataEntry(0, textVals[0]));
        data.add(new ValueDataEntry(textVals[3], textVals[4]));

        double gradX = textVals[3] - 0;
        double gradY = textVals[4] - textVals[0];

        data.add(new ValueDataEntry(gradX + textVals[3], gradY + textVals[4]));

        return data;
    }

    private List<DataEntry> getMarkerData() {
        List<DataEntry> data = new ArrayList<>();

        for (int i = 0; i < pointArray.length; i++) {

            int x = pointArray[i][0];
            int y = pointArray[i][1];

            data.add(new ValueDataEntry(x, y));
        }


        return data;
    }


}
