package com.example.sonaj.coindonation.CoinWallet;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.sonaj.coindonation.R;
import com.example.sonaj.coindonation.Util.WalletSettingListener;

import java.math.BigInteger;

public class WalletChoiceSettingDialog extends Dialog implements View.OnClickListener {

    private Context context;

    private ImageButton btnWalletSettingCancel;
    private Button btnCreateWallet;
    private Button btnGetWallet;
    private Button btnWalletOK;
    private EditText etWalletAddress;
    private EditText etWalletName;

    private int STATUS_WALLET_CREATE = 0;
    private int STATUS_WALLET_GET =1;
    private int walletStatus;

    private WalletSettingListener walletSettingListener;



    public WalletChoiceSettingDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallet_choice_setting_dialog);

        init();
    }

    private void init(){
        btnCreateWallet = (Button)findViewById(R.id.btn_create_wallet);
        btnGetWallet = (Button)findViewById(R.id.btn_get_wallet);
        btnWalletOK = (Button)findViewById(R.id.btn_wallet_ok);
        btnWalletSettingCancel = (ImageButton)findViewById(R.id.btn_wallet_setting_cancel);
        etWalletAddress = (EditText)findViewById(R.id.et_wallet_address);
        etWalletName = (EditText)findViewById(R.id.et_wallet_name);

        btnCreateWallet.setOnClickListener(this);
        btnGetWallet.setOnClickListener(this);
        btnWalletSettingCancel.setOnClickListener(this);
        btnWalletOK.setOnClickListener(this);
    }

    public void setDialogListner(WalletSettingListener dialogListner){
        this.walletSettingListener = dialogListner;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_wallet_setting_cancel:
                dismiss();
                break;
            case R.id.btn_create_wallet: // 지갑 새로 만드는 버튼
                walletStatus = STATUS_WALLET_CREATE;
                etWalletName.setVisibility(View.VISIBLE);
                etWalletAddress.setVisibility(View.VISIBLE);
                btnCreateWallet.setVisibility(View.GONE);
                btnGetWallet.setVisibility(View.GONE);
                btnWalletOK.setVisibility(View.VISIBLE);

                etWalletName.setHint("지갑 이름을 입력해주세요");
                etWalletAddress.setHint("지갑 비밀번호를 입력해주세요");
                //비밀번호처럼 입력한 내용 안보이게 설정
                PasswordTransformationMethod passwdtm = new PasswordTransformationMethod();
                etWalletAddress.setTransformationMethod(passwdtm);
                break;
            case R.id.btn_get_wallet: // 지갑 가져오는 버튼
                walletStatus = STATUS_WALLET_GET;
                etWalletName.setVisibility(View.VISIBLE);
                btnCreateWallet.setVisibility(View.GONE);
                btnGetWallet.setVisibility(View.GONE);
                etWalletAddress.setVisibility(View.VISIBLE);
                btnWalletOK.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_wallet_ok: // CoinWalletView 로 지갑 주소 전달
                if(etWalletAddress.getText()!=null && etWalletName.getText()!=null){
                    if(walletStatus==STATUS_WALLET_CREATE){
                        walletSettingListener.onWalletCreate(String.valueOf(etWalletName.getText()),String.valueOf(etWalletAddress.getText()));
                    }else{
                        // 입력한 privateKey BigInter 로 변경해서 넘겨주기
                       BigInteger privateKey = new BigInteger(String.valueOf(etWalletAddress.getText()), 16);
                        walletSettingListener.onWalletGet(String.valueOf(etWalletName.getText()), privateKey);
                    }
                }else{
                    Toast.makeText(context,"지갑 정보를 입력해주세요",Toast.LENGTH_LONG).show();
                }
                dismiss();
                break;
        }

    }
}
