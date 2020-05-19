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

import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.jc770797.catimageprocess.activeCont.Snake;
import com.jc770797.catimageprocess.fragments.SnakeOverlayMainFragment;
import com.jc770797.catimageprocess.fragments.SnakeOverlayPointsFragment;

import java.io.FileInputStream;
import java.util.ArrayList;


public class ImageSnakeActivity extends AppCompatActivity {


    public Bitmap greyImageMap, overlayImage ;
    private Button continueBtn;
    private TextView pointerCount;
    private FragmentManager frm;
    private FragmentTransaction fragmentTransaction;
    private Fragment mainOverlay = new SnakeOverlayMainFragment();
    private Snake snake;
    boolean snakeBool = false, addPointsBool=true;

    public Bitmap getImage(){
        return this.greyImageMap;
    }
    public Bitmap getOverlayImageImage(){
        return this.overlayImage;
    }

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

        snake = new Snake(greyImageMap,true, "Kass");

        pointerCount = findViewById(R.id.numOfPoints);

        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        overlayImage = Bitmap.createBitmap(greyImageMap.getWidth(),greyImageMap.getHeight(),config);


        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                Intent intent = new Intent(ImageSnakeActivity.this, ImageResultsActivity.class);
                b.putSerializable("Array", snake.getPointArray());
                b.putInt("numPoint", snake.getNumPoints());
                intent.putExtras(b);
                startActivity(intent);
            }
        });
    }

    public void fragmentChange(){
        Fragment pointsOverlay = new SnakeOverlayPointsFragment();
        fragmentTransaction = frm.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentHolder, pointsOverlay);
        fragmentTransaction.disallowAddToBackStack();
        fragmentTransaction.commit();
    }
    public void fragmentChange2(){
        pointerCount.setText("Number of points: " + snake.getNumPoints());
        fragmentTransaction = frm.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentHolder, mainOverlay);
        fragmentTransaction.disallowAddToBackStack();
        fragmentTransaction.commit();

    }


    public void pointAdder(int x, int y){
        snake.createPoint(x,y);

        Canvas canvas = new Canvas(overlayImage);

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(2);

        canvas.drawCircle(x, y, 5,paint);



    }
    public ArrayList<Point> getPointArray(){
        return snake.getPointArray();
    }
    public int getPointNum(){return snake.getNumPoints();}
    public void snakeStart(){
        snake.start(this);
    }
    public Bitmap snakeGetTestImg(){
        return snake.getImg();
    }
    private void fileGetter(){
        String tempFilename = getIntent().getStringExtra("image");
        try {
            FileInputStream is = ImageSnakeActivity.this.openFileInput(tempFilename);
            greyImageMap = BitmapFactory.decodeStream(is);
            is.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }






}
