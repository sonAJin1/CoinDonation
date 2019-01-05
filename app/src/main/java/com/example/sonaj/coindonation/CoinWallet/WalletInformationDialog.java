package com.example.sonaj.coindonation.CoinWallet;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sonaj.coindonation.CoinWallet.utils.qr.Generate;
import com.example.sonaj.coindonation.R;

public class WalletInformationDialog extends Dialog {

    private Context context;
    private String walletAddress;
    private ImageView qrCode;
    private TextView tvWalletAddress;
    private Button btnETHscan;
    private ImageButton btnCancle;
    private Button btnCopyAddress;


    public WalletInformationDialog(@NonNull Context context, String walletAddress) {
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
        btnCopyAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("address",tvWalletAddress.getText());
                cm.setPrimaryClip(clipData);

                //복사가 되었다면 토스트메시지 노출
                Toast.makeText(context,"지갑 주소가 복사되었습니다.",Toast.LENGTH_SHORT).show();
            }
        });
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public void init() {
        qrCode = (ImageView) findViewById(R.id.qr_code);
        tvWalletAddress = (TextView) findViewById(R.id.tv_address_dialog);
        btnETHscan = (Button) findViewById(R.id.btn_show_eth_scan);
        btnCancle = (ImageButton) findViewById(R.id.btn_wallet_info_cancel);
        btnCopyAddress = (Button) findViewById(R.id.btn_copy_address);
    }

    public void setQrCode() {
        qrCode.setImageBitmap(new Generate().Get(walletAddress, 600, 600));
    }

}
