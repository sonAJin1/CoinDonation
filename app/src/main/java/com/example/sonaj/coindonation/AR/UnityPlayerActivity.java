package com.example.sonaj.coindonation.AR;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.Toast;

import com.example.sonaj.coindonation.CoinWallet.SendCoinActivity;
import com.example.sonaj.coindonation.Main.MainActivity;
import com.unity3d.player.UnityPlayer;

public class UnityPlayerActivity extends Activity
{
    protected UnityPlayer mUnityPlayer; // don't change the name of this variable; referenced from native code
    public static UnityPlayerActivity unityPlayerActivity;

    // Setup activity layout
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        mUnityPlayer = new UnityPlayer(this);
        setContentView(mUnityPlayer);
        mUnityPlayer.requestFocus();
        unityPlayerActivity = UnityPlayerActivity.this;

    }

    //coin object 를 클릭하면 unity 에서 호출하는 메소드
    public void showSendCoin(final String QRCode)
    {
        final Activity a = UnityPlayer.currentActivity;
        final String _QRCode = QRCode;


        a.runOnUiThread(new Runnable()
        {
            public void run() {
                Intent intent = new Intent(getApplicationContext(), SendCoinActivity.class);
                intent.putExtra("QRAddress",_QRCode);
                intent.putExtra("type","AJIN TOKEN");
                startActivityForResult(intent,0);

            }
        });
    }

    /* send coin */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String gasPrice = data.getStringExtra("gasPrice");
            String gasLimit = data.getStringExtra("gasLimit");
            String toAddress = data.getStringExtra("toAddress");
            String sendTokenAmmount = data.getStringExtra("sendAmmount");

            switch (requestCode) {
                case 0: //sendToken 에서 보낸 후 내용을 가져옴
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("gasPrice", gasPrice);
                    intent.putExtra("gasLimit", gasLimit);
                    intent.putExtra("toAddress", toAddress);
                    intent.putExtra("sendAmmount", sendTokenAmmount);
                    startActivity(intent);

//                    Intent resultIntent = new Intent();
//                    resultIntent.putExtra("gasPrice",gasPrice);
//                    resultIntent.putExtra("gasLimit",gasLimit);
//                    resultIntent.putExtra("toAddress",toAddress);
//                    resultIntent.putExtra("sendAmmount",sendTokenAmmount);
//                    setResult(RESULT_OK,resultIntent);
//                    finish();
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() { //back button event
        super.onBackPressed();
        Toast.makeText(this,"뒤로가기 버튼",Toast.LENGTH_LONG);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onNewIntent(Intent intent)
    {
        // To support deep linking, we need to make sure that the client can get access to
        // the last sent intent. The clients access this through a JNI api that allows them
        // to get the intent set on launch. To update that after launch we have to manually
        // replace the intent with the one caught here.
        setIntent(intent);
    }

    // Quit Unity
    @Override
    protected void onDestroy ()
    {
        mUnityPlayer.quit();
        super.onDestroy();
    }

    // Pause Unity
    @Override
    protected void onPause()
    {
        super.onPause();
        mUnityPlayer.pause();
    }

    // Resume Unity
    @Override
    protected void onResume()
    {
        super.onResume();
        mUnityPlayer.resume();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        mUnityPlayer.start();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        mUnityPlayer.stop();
    }

    // Low Memory Unity
    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        mUnityPlayer.lowMemory();
    }

    // Trim Memory Unity
    @Override
    public void onTrimMemory(int level)
    {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_RUNNING_CRITICAL)
        {
            mUnityPlayer.lowMemory();
        }
    }

    // This ensures the layout will be correct.
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        mUnityPlayer.configurationChanged(newConfig);
    }

    // Notify Unity of the focus change.
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        mUnityPlayer.windowFocusChanged(hasFocus);
    }

    // For some reason the multiple keyevent type is not supported by the ndk.
    // Force event injection by overriding dispatchKeyEvent().
    @Override
    public boolean dispatchKeyEvent(KeyEvent event)
    {
        if (event.getAction() == KeyEvent.ACTION_MULTIPLE)
            return mUnityPlayer.injectEvent(event);
        return super.dispatchKeyEvent(event);
    }

    // Pass any events not handled by (unfocused) views straight to UnityPlayer
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)     { return mUnityPlayer.injectEvent(event); }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)   { return mUnityPlayer.injectEvent(event); }
    @Override
    public boolean onTouchEvent(MotionEvent event)          { return mUnityPlayer.injectEvent(event); }
    /*API12*/ public boolean onGenericMotionEvent(MotionEvent event)  { return mUnityPlayer.injectEvent(event); }
}
