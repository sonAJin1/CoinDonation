package com.example.sonaj.coindonation.CoinWallet;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sonaj.coindonation.CoinWallet.utils.qr.Generate;
import com.example.sonaj.coindonation.R;

public class WalletInformationDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private String walletAddress;
    private ImageView qrCode;
    private TextView tvWalletAddress;
    private Button btnETHscan;
    private ImageButton btnCancle;

    public WalletInformationDialog(@NonNull Context context,String walletAddress) {
        super(context);
        this.context = context;
        this.walletAddress = walletAddress;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallet_information_dialog);

        init();
        setQrCode();
        tvWalletAddress.setText(walletAddress);

    }

    public void init(){
        qrCode = (ImageView)findViewById(R.id.qr_code);
        tvWalletAddress = (TextView) findViewById(R.id.tv_address_dialog);
        btnETHscan = (Button) findViewById(R.id.btn_show_eth_scan);
        btnCancle = (ImageButton) findViewById(R.id.btn_wallet_info_cancel);
    }

    public void setQrCode(){
        qrCode.setImageBitmap(new Generate().Get(walletAddress,600,600));
    }

    @Override
    public void onClick(View view) {

    }
}
