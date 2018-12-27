package com.example.sonaj.coindonation.CoinWallet;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.sonaj.coindonation.CoinWallet.callback.CBBip44;
import com.example.sonaj.coindonation.CoinWallet.callback.CBGetCredential;
import com.example.sonaj.coindonation.CoinWallet.callback.CBLoadSmartContract;
import com.example.sonaj.coindonation.CoinWallet.callback.CBSendingEther;
import com.example.sonaj.coindonation.CoinWallet.callback.CBSendingToken;
import com.example.sonaj.coindonation.CoinWallet.smartcontract.LoadSmartContract;
import com.example.sonaj.coindonation.CoinWallet.utils.InfoDialog;
import com.example.sonaj.coindonation.CoinWallet.utils.ToastMsg;
import com.example.sonaj.coindonation.CoinWallet.utils.qr.Generate;
import com.example.sonaj.coindonation.CoinWallet.utils.qr.ScanIntegrator;
import com.example.sonaj.coindonation.CoinWallet.wallet.Balance;
import com.example.sonaj.coindonation.CoinWallet.wallet.SendingEther;
import com.example.sonaj.coindonation.CoinWallet.wallet.SendingToken;
import com.example.sonaj.coindonation.CoinWallet.web3j.Initiate;
import com.example.sonaj.coindonation.Main.MainActivity;
import com.example.sonaj.coindonation.R;
import com.example.sonaj.coindonation.databinding.SendCoinBinding;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class SendCoinActivity extends AppCompatActivity {


    private String mNodeUrl = config.addressethnode(2);
    private String mSmartcontract = config.addresssmartcontract(1);
    SendCoinBinding binding;
    final Context context = this;

    OnClick onClick;
    private ToastMsg toastMsg;
    IntentIntegrator qrScan;
    private SendingEther sendingEther;
    private SendingToken sendingToken;
    private Web3j mWeb3j;
    private BigInteger mGasPrice;
    private BigInteger mGasLimit;
    private Credentials mCredentials;

    String coinType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_send_coin);

        init();
        GetFee();
        getWeb3j();

        Intent intent = getIntent();
        coinType = intent.getExtras().getString("type");
        binding.tvWalletTitleName.setText(coinType);

    }

    public void init(){
        toastMsg = new ToastMsg();
        //뷰 클릭 메소드 xml 과 연결
        onClick = new OnClick();
        binding.setOnClick(onClick);
        qrScan = new IntentIntegrator(this);
        binding.sbGasLimit.setOnSeekBarChangeListener(seekBarChangeListenerGL);
        binding.sbGasPrice.setOnSeekBarChangeListener(seekBarChangeListenerGP);

    }

    /* Get Web3j*/
    private void getWeb3j(){
        new Initiate(mNodeUrl);
        mWeb3j = Initiate.sWeb3jInstance;
    }



    public void GetFee(){
        setGasPrice(getGasPrice());
        setGasLimit(getGasLimit());

        BigDecimal fee = BigDecimal.valueOf(mGasPrice.doubleValue()*mGasLimit.doubleValue());
        BigDecimal feeresult = Convert.fromWei(fee.toString(),Convert.Unit.ETHER);
        binding.tvFee.setText(feeresult.toPlainString() + " ETH");
    }

    private String getGasPrice(){
        return binding.tvGasPrice.getText().toString();
    }

    private void setGasPrice(String gasPrice){
        mGasPrice = Convert.toWei(gasPrice,Convert.Unit.GWEI).toBigInteger();
    }

    private String getGasLimit() {
        return binding.tvGasLimit.getText().toString();
    }

    private void setGasLimit(String gasLimit){
        mGasLimit = BigInteger.valueOf(Long.valueOf(gasLimit));
    }

    /* QR Scan */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                //toastMsg.Short(context, "Result Not Found");
                Toast.makeText(context,"주소를 찾을 수 없습니다",Toast.LENGTH_LONG).show();
            } else {
                String[] array = result.getContents().split(":"); // 앞에 ether: 를 없애기 위한 장치
                binding.sendtoaddress.setText(array[1]);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    /* Sending */
//    private void sendEther(){
//        sendingEther = new SendingEther(mWeb3j,
//                mCredentials,
//                getGasPrice(),
//                getGasLimit());
//        sendingEther.registerCallBack(this);
//        sendingEther.Send(getToAddress(),getSendEtherAmmount());
//    }
//
//
//    private void sendToken(){
//        sendingToken = new SendingToken(mWeb3j,
//                mCredentials,
//                getGasPrice(),
//                getGasLimit());
//        sendingToken.registerCallBackToken(this);
//        sendingToken.Send(mSmartcontract,getToAddress(),getSendTokenAmmount());
//    }
//
    private String getToAddress(){
        return binding.sendtoaddress.getText().toString();
    }

    /* Get Send Ammount */
    private String getSendCoinAmmount(){
        return binding.etSendCoinValue.getText().toString();
    }

//    private String getSendTokenAmmount(){
//        return binding.SendTokenValue.getText().toString();
//    }
    /* End Sending */

    /* SeekBar Listener */
    private SeekBar.OnSeekBarChangeListener seekBarChangeListenerGL = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            GetGasLimit(String.valueOf(seekBar.getProgress()*1000+2000000));
        }
        @Override public void onStartTrackingTouch(SeekBar seekBar) { }
        @Override public void onStopTrackingTouch(SeekBar seekBar) { }
    };
    private SeekBar.OnSeekBarChangeListener seekBarChangeListenerGP = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            GetGasPrice(String.valueOf(seekBar.getProgress()+12));
        }
        @Override public void onStartTrackingTouch(SeekBar seekBar) { }
        @Override public void onStopTrackingTouch(SeekBar seekBar) { }
    };

    public void GetGasLimit(String value) {
         binding.tvGasLimit.setText(value);
        GetFee();
    }
    public void GetGasPrice(String value) {
         binding.tvGasPrice.setText(value);
        GetFee();
    }
    /* End SeekBar Listener */


    public class OnClick {
        public void sendCoin(View view){
            if(getToAddress()!=null){
                // activity 를 끄면서 main으로 보내는 가격, gasLimit, gasPrice, 보내는 주소를 보내서 coinWalletView 에서 보내게 할 것
                // 연산한 결과 값을 resultIntent 에 담아서 MainActivity 로 전달하고 현재 Activity 는 종료.
                Intent resultIntent = new Intent();
                resultIntent.putExtra("gasPrice",getGasPrice());
                resultIntent.putExtra("gasLimit",getGasLimit());
                resultIntent.putExtra("toAddress",getToAddress());
                resultIntent.putExtra("sendAmmount",getSendCoinAmmount());
                setResult(RESULT_OK,resultIntent);
                finish();
            }
        }

        public void scanQR(View view){
            new ScanIntegrator((Activity) context).startScan();
        }
        public void back(View view){
            finish();
        }
    }

}
