package com.example.sonaj.coindonation.Main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.example.sonaj.coindonation.AR.UnityPlayerActivity;
import com.example.sonaj.coindonation.CoinWallet.TestCoinWalletView;
import com.example.sonaj.coindonation.FaceCamera.FaceActivity;
import com.example.sonaj.coindonation.Util.BaseView;
import com.example.sonaj.coindonation.databinding.FirstViewBinding;

public class firstView extends BaseView {
    /**
     * 생성자에서 view 를 설정하므로 setView 메소드를 생성하지 않음.
     *
     * @param context     : View 가 그렬질 영역의 context
     * @param dataBinding : xml 의 View 들을 담고 있는 데이터 바인딩
     */
    Context context;
    FirstViewBinding binding;
    OnClick onClick;


    public firstView(Context context, FirstViewBinding dataBinding) {
        super(context, dataBinding);
        this.context = context;
        this.binding = dataBinding;

        init();
    }

    @Override
    public void init() {
        onClick = new OnClick();
        binding.setOnClick(onClick);
    }

    public class OnClick{
        // google vision 카메라 뷰로 이동
        public void showFaceCamera(View view) {
            Intent intent = new Intent(context, FaceActivity.class);
            context.startActivity(intent);
        }
        public void showWallet(View view){
            Intent intent = new Intent(context, TestCoinWalletView.class);
            context.startActivity(intent);
        }
        public void showAR(View view){
            Intent intent = new Intent(context, UnityPlayerActivity.class);
            context.startActivity(intent);
//            ((Activity) context).startActivityForResult(intent,2);
        }
        public void deleteInfo(View view){
           // binding.
        }

    }
}
