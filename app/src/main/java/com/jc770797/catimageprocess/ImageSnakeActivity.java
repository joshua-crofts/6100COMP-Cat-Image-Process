package com.jc770797.catimageprocess;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.jc770797.catimageprocess.activeCont.KassSnake;
import com.jc770797.catimageprocess.fragments.SnakeOverlayMainFragment;
import com.jc770797.catimageprocess.fragments.SnakeOverlayPointsFragment;

import java.io.FileInputStream;
import java.util.ArrayList;

public class ImageSnakeActivity extends AppCompatActivity {

    public Bitmap greyImageMap, overlayImage;
    private Button continueBtn;
    private TextView iterationCounter;
    private FragmentManager frm;
    private FragmentTransaction fragmentTransaction;
    private Fragment mainOverlay = new SnakeOverlayMainFragment();

    private SeekBar iterationBar;

    private int numPoint = 0;
    ArrayList<ArrayList> listOfpointers = new ArrayList<>();
    private ArrayList<Point> pointArray = new ArrayList<>();
    private boolean uiSwitch = false;

    private KassSnake kSnake;
    private String snakeType = "Kass";

    private int iteratorAmount = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snake);

        fileGetter();
        continueBtn = findViewById(R.id.continueBtn);

        frm = getSupportFragmentManager();
        fragmentTransaction = frm.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentHolder, mainOverlay);
        fragmentTransaction.disallowAddToBackStack();
        fragmentTransaction.commit();

        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        overlayImage = Bitmap.createBitmap(greyImageMap.getWidth(), greyImageMap.getHeight(), config);

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                Intent intent = new Intent(ImageSnakeActivity.this, ImageResultsActivity.class);
                b.putSerializable("Array", pointArray);
                b.putInt("numPoint", numPoint);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        iterationCounter = findViewById(R.id.numIteration);
        iterationBar = findViewById(R.id.iterationBar);
        iterationBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                iterationCounter.setText("Number of Itterations: " + progress);
                iteratorAmount = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    //starts the snake
    public void snakeStart() {
        Toast.makeText(ImageSnakeActivity.this, "Beginning Snake", Toast.LENGTH_SHORT).show();
        if (snakeType == "Greedy") {

        } else if (snakeType == "Kass") {
            double alpha = 0.05;
            double beta = 0.0005;
            double delta = 1;
            double sigma = 3;
            double itterations = iteratorAmount;
            kSnake = new KassSnake(greyImageMap, pointArray, alpha, beta, delta, sigma, itterations);
            listOfpointers = kSnake.start();
            Toast.makeText(ImageSnakeActivity.this, "Snake Complete", Toast.LENGTH_SHORT).show();
        } else {

        }
    }

    //changes the fragment to the point adder
    public void fragmentChange() {
        uiDisable();
        Fragment pointsOverlay = new SnakeOverlayPointsFragment();
        fragmentTransaction = frm.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentHolder, pointsOverlay);
        fragmentTransaction.disallowAddToBackStack();
        fragmentTransaction.commit();
    }

    //changes the fragment to the main overlay
    public void fragmentChange2() {
        uiDisable();
        fragmentTransaction = frm.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentHolder, mainOverlay);
        fragmentTransaction.disallowAddToBackStack();
        fragmentTransaction.commit();
        overlayBuilder();
    }

    //disables various parts of the activity layout based on current fragment
    private void uiDisable() {
        int visibility = (uiSwitch == true) ? View.VISIBLE : View.INVISIBLE;
        iterationCounter.setVisibility(visibility);
        iterationBar.setVisibility(visibility);
        uiSwitch = !uiSwitch;
    }

    //Initial overlay builder for the point adder class
    public void pointAdder(int x, int y) {
        createPoint(x, y);
        Canvas canvas = new Canvas(overlayImage);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(2);
        canvas.drawCircle(x, y, 5, paint);
    }

    //builds the overlay bitmap using Android canvas and point array
    public Bitmap overlayBuilder() {
        if (pointArray.size() != 0) {
            Bitmap imageReplace = Bitmap.createBitmap(overlayImage.getWidth(), overlayImage.getHeight(), overlayImage.getConfig());
            Canvas canvas = new Canvas(imageReplace);
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStrokeWidth(2);
            for (int i = 0; i < pointArray.size(); i++) {

                canvas.drawCircle(pointArray.get(i).x, pointArray.get(i).y, 5, paint);
            }
            paint.setColor(Color.BLUE);
            paint.setStrokeWidth(3);
            Path path = new Path();
            path.moveTo(pointArray.get(0).x, pointArray.get(0).y);
            for (int i = 0; i < pointArray.size(); i++) {
                if (i < pointArray.size() - 1) {
                    path.quadTo(pointArray.get(i).x, pointArray.get(i).y, pointArray.get(i + 1).x, pointArray.get(i + 1).y);
                }
            }
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawPath(path, paint);
            overlayImage = imageReplace;
            return imageReplace;
        }
        return overlayImage;
    }

    //generic file getter function to pull images from local storage
    private void fileGetter() {
        String tempFilename = getIntent().getStringExtra("image");
        try {
            FileInputStream is = ImageSnakeActivity.this.openFileInput(tempFilename);
            greyImageMap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //create points based on x, y coordinates
    public void createPoint(int x, int y) {
        Point point = new Point(x, y);
        pointArray.add(point);
        numPoint++;
    }

    //used to iterate through the snake history array
    public void snakePlayback(int i) {
        ArrayList<Point> pointerArray = listOfpointers.get(i);
        pointArray = pointerArray;
    }


    //Various getters
    public int getPointNum() {
        return numPoint;
    }

    public ArrayList<Point> getPointArray() {
        return pointArray;
    }

    public int sizeOfPointerList() {
        return listOfpointers.size();
    }

    public Bitmap getImage() {
        return this.greyImageMap;
    }

    public Bitmap getOverlayImageImage() {
        return this.overlayImage;
    }
}
