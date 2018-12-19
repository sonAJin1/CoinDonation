package com.example.sonaj.coindonation.Main;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.sonaj.coindonation.CoinWallet.CoinWalletActivity;
import com.example.sonaj.coindonation.CoinWallet.CoinWalletView;
import com.example.sonaj.coindonation.Data.DBHelper;
import com.example.sonaj.coindonation.R;
import com.example.sonaj.coindonation.Util.MultiViewActivity;
import com.example.sonaj.coindonation.databinding.MainBinding;

public class MainActivity extends MultiViewActivity {

    Button btn_wallet;


    MainBinding binding;
    BottomNavigationView navigation;

    // Context, System
    private Context context;
    private Activity m_oMainActivity = this;

    OnClick onClick;

    //SQLite
    private DBHelper dbHelper;


    //View list
    CoinWalletView coinWalletView;
    firstView firstView;

    // View 의 위치를 나타내는 값
    final int POSITION_SALON_View = 0;
    final int POSITION_CONTENTS_VIEW = 1;
    final int POSITION_MARKET_VIEW = 2;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    onClick.showHome();
                    return true;
                case R.id.navigation_wallet:
                    onClick.showWallet();
                    return true;

            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        //----- System, Context
        context = this;	//.getApplicationContext();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        /** 내부 저장소 읽기쓰기 권한요청*/
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkVerify();
        }

        /**초기화*/
        init();
    }


    public void init(){
      //  btn_wallet = (Button)findViewById(R.id.btn_wallet);
        //사용자들이 선택해서 보여질 VIEW
        firstView = new firstView(this, binding.appBarContent.viewFirst);
        coinWalletView = new CoinWalletView(this, binding.appBarContent.viewWallet);


        //네비게이션 바
        navigation = binding.navigation;
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //선택에 의해 보여질 화면들 관리를 위해 리스트에 추가
        initViewList();
        addView(firstView);
        addView(coinWalletView);

        //뷰 클릭 메소드 xml 과 연결
        onClick = new OnClick();

        //sqlite 생성
        dbHelper = new DBHelper(context,"WalletAddress02.db",null,1);

    }

    public class OnClick {

        // 살롱 화면 보여주기
        public void showHome() {
            setView(POSITION_SALON_View);
          //  firstView.setPostView(); // 화면을 보여줄 때 서버에서 데이터 가져오기
        }

        // 콘텐츠 추천 화면 보여주기
        public void showWallet() {
            if(currentViewPosition!=POSITION_CONTENTS_VIEW) {
                setView(POSITION_CONTENTS_VIEW);
                /**TODO: 지갑이 앱에 등록이 되어있으면 지갑 정보를 CoinWalletView에 보여줄것 */
                /**TODO: 지갑이 앱에 등록되어있지 않으면 지갑 정보를 받을 수 있는 다이얼로그를 띄워줄것 */
                Log.e("db in main",dbHelper.getResult());
                if(dbHelper.getResult().length()==0){
                    coinWalletView.showCoinWalletDialog();
                }else{
                   coinWalletView.setCoinWalletView();
                }

            }
        }
    }

    /** 내부 저장소 읽기 쓰기 권한 요청 */
    @TargetApi(Build.VERSION_CODES.M)
    public void checkVerify()
    {
        if (
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                        checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                )
        {
            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE))
            {
                // ...
            }
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }

    }

    /** 내부 저장소 읽기 쓰기 권한 요청에 대한 결과값 */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1)
        {
            if (grantResults.length > 0)
            {
                for (int i=0; i<grantResults.length; ++i)
                {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED)
                    {
                        // 하나라도 거부한다면.
                        new AlertDialog.Builder(this).setTitle("알림").setMessage("권한을 허용해주셔야 앱을 이용할 수 있습니다.")
                                .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        m_oMainActivity.finish();
                                    }
                                }).setNegativeButton("권한 설정", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                        .setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                                getApplicationContext().startActivity(intent);
                            }
                        }).setCancelable(false).show();

                        return;
                    }
                }
            }
        }
    }

}
