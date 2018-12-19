package com.example.sonaj.coindonation.CoinWallet;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sonaj.coindonation.R;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class CoinWalletActivity extends AppCompatActivity{

    EditText etPassword;
    Button btnCreateWallet;
    TextView tvCreateWllet;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_wallet);

        init();


        btnCreateWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String[] result = createWallet(String.valueOf(etPassword.getText()));
               if(result[1]!=null){
                   String address = result[1];
                   tvCreateWllet.setText(address);
               }

            }
        });
    }

    public void init(){
        etPassword = (EditText)findViewById(R.id.et_password);
        btnCreateWallet = (Button)findViewById(R.id.btn_create_wallet);
        tvCreateWllet = (TextView)findViewById(R.id.tv_create_wallet);
    }

    /** 지갑 생성 */
    public String[] createWallet(final String password) { //Password 를 인자로 주면 string 배열에 wallet이 저장된 path와 wallet 주소를 반환해주는 메소드
        String[] result = new String[2];
        try {
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS); //다운로드 path 가져오기
            if (!path.exists()) {
                path.mkdir();
            }
            String fileName = WalletUtils.generateLightNewWalletFile(password, new File(String.valueOf(path))); //패스워드와 path를 이용해 지갑생성
            result[0] = path+"/"+fileName;

            Credentials credentials = WalletUtils.loadCredentials(password,result[0]);

            result[1] = credentials.getAddress();


            return result;
        } catch (NoSuchAlgorithmException
                | NoSuchProviderException
                | InvalidAlgorithmParameterException
                | IOException
                | CipherException e) {
            e.printStackTrace();
            return null;
        }
    }


}
