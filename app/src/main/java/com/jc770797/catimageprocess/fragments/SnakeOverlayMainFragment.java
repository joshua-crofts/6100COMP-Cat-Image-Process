package com.jc770797.catimageprocess.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.jc770797.catimageprocess.ImageSnakeActivity;
import com.jc770797.catimageprocess.R;

public class SnakeOverlayMainFragment extends Fragment {

    private ImageView mainImage;
    private Button pointsBtn, snakeStartBtn;

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
        mainImage.setImageBitmap(((ImageSnakeActivity)this.getActivity()).getImage());

        pointsBtn = getView().findViewById(R.id.pointsBtn);
        snakeStartBtn = getView().findViewById(R.id.snakeStartBtn);


        buttonListener();
    }

    private void buttonListener(){
        pointsBtn.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             fragmentSwitch();
                                             Toast.makeText(getContext(), "Test", Toast.LENGTH_SHORT).show();


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


    }


    private void snakeStart(){((ImageSnakeActivity)this.getActivity()).snakeStart();}

    private void fragmentSwitch(){
       ((ImageSnakeActivity)this.getActivity()).fragmentChange();
    }



}
