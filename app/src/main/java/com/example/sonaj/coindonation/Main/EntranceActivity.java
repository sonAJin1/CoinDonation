package com.example.sonaj.coindonation.Main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.airbnb.lottie.LottieAnimationView;
import com.example.sonaj.coindonation.AR.UnityPlayerActivity;
import com.example.sonaj.coindonation.FaceCamera.FaceActivity;
import com.example.sonaj.coindonation.R;

public class EntranceActivity extends AppCompatActivity {

    Button btnEnterance;
    LottieAnimationView animationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrance);

        init();

    }

    public void init(){
//        btnEnterance = (Button)findViewById(R.id.ib_enter);
//        btnEnterance.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), FaceActivity.class);
//                startActivity(intent);
//            }
//        });

//        //Lottie Animation
//        animationView = (LottieAnimationView) findViewById(R.id.animation_view);
//        animationView.setAnimation("faceid_green.json");
//        animationView.loop(true);
//        //Lottie Animation start
//        animationView.playAnimation();
//        animationView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), FaceActivity.class);
//                startActivity(intent);
//            }
//        });
    }
}
