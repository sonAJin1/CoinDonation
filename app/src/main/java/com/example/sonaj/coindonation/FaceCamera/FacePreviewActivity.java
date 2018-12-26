package com.example.sonaj.coindonation.FaceCamera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.sonaj.coindonation.R;

public class FacePreviewActivity extends AppCompatActivity {

    ImageView ivFacePreview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_preview);
        init();

        Intent intent = getIntent();
//        byte[] bytes = intent.getByteArrayExtra("faceImage");
//        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//        ivFacePreview.setImageBitmap(bmp);
    }
    public void init(){
        ivFacePreview = (ImageView)findViewById(R.id.ivFacePreview);
    }
}
