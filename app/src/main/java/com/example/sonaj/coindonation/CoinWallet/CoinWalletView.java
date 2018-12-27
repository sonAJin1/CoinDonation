package com.example.sonaj.coindonation.CoinWallet;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
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
import com.example.sonaj.coindonation.CoinWallet.wallet.generate.Bip44;
import com.example.sonaj.coindonation.CoinWallet.web3j.Initiate;
import com.example.sonaj.coindonation.Data.DBHelper;
import com.example.sonaj.coindonation.R;
import com.example.sonaj.coindonation.Util.BaseView;
import com.example.sonaj.coindonation.Util.WalletSettingListener;
import com.example.sonaj.coindonation.databinding.CoinWalletViewBinding;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.QRCodeWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.FunctionEncoder;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthAccounts;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public class CoinWalletView extends BaseView  implements CBGetCredential, CBLoadSmartContract, CBBip44, CBSendingEther, CBSendingToken {

    Context context;
    CoinWalletViewBinding binding;
    OnClick onClick;

    String[] createAddress;

    //sqlite
    private DBHelper dbHelper;

    /**Adapter*/
    CoinKindAdapter coinKindAdapter;
    CoinTransferAdapter coinTransferAdapter;

    /** adapter item */
    List<CoinKindsItem> coinKindsItemList;
    List<CoinTransferItem> coinTransferItemList;

    private static final Logger log = LoggerFactory.getLogger(CoinWalletView.class);

    private String mNodeUrl = config.addressethnode(2);
    private String mPasswordwallet = config.passwordwallet();
    private String mSmartcontract = config.addresssmartcontract(1);
    ImageView qr_big;
    IntentIntegrator qrScan;
    private Web3j mWeb3j;
    private File keydir;
    public Credentials mCredentials;
    private BigInteger mGasPrice;
    private BigInteger mGasLimit;
    private SendingEther sendingEther;
    private SendingToken sendingToken;
    private ToastMsg toastMsg;
    private InfoDialog mInfoDialog;

    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");



    /**TODO https://dwfox.tistory.com/79 에서 QR CODE 읽는 부분 만들기*/

    /**
     * 생성자에서 view 를 설정하므로 setView 메소드를 생성하지 않음.
     *
     * @param context     : View 가 그렬질 영역의 context
     * @param dataBinding : xml 의 View 들을 담고 있는 데이터 바인딩
     */
    public CoinWalletView(Context context, CoinWalletViewBinding dataBinding) {
        super(context, dataBinding);
        this.context = context;
        this.binding = dataBinding;
        coinKindsItemList = new ArrayList<>();
        coinTransferItemList = new ArrayList<>();
        init();
        dbHelper = new DBHelper(context,"WalletTransfer01.db",null,1);
    }

    public void showWalletView(){
        //GetFee();
        getWeb3j();
        keydir = context.getFilesDir();
        File[] listfiles = keydir.listFiles();
        if (listfiles.length == 0 ) {
            CreateWallet();
        } else {
            getCredentials(keydir);
        }
        setRecyclerView(); // 코인 종류 보여주는 listView

    }

    @Override
    public void init() {
        onClick = new OnClick();
        binding.setOnClick(onClick);
        toastMsg = new ToastMsg();
        qrScan = new IntentIntegrator((Activity) context);
        mInfoDialog = new InfoDialog(context);

    }

    public void setCoinWalletView(){
        Log.e("db",dbHelper.getResult());
    }

    public void showCoinWalletDialog(){
        WalletChoiceSettingDialog dialog = new WalletChoiceSettingDialog(context);
        dialog.setDialogListner(new WalletSettingListener() {
            @Override
            public void onWalletCreate(String name,String password) {

            }
            @Override
            public void onWalletGet(String name,BigInteger privateKey) {

            }
        });
        dialog.show();
    }

    public void showCoinWalletInfoDialog(){
        WalletInformationDialog dialog = new WalletInformationDialog(context,getEthAddress());
        dialog.show();

    }

    public void setTransferHistory(){
        dbHelper.update(0,0);
        String db = dbHelper.getResult();
        String[] result = db.split("\\n"); // 줄바꿈으로 나누기
        Log.e("result",result[0]);

        for(String dbResult : result){
            String[] listContent = dbResult.split(" , ");
            coinTransferItemList.add(new CoinTransferItem(listContent[0],listContent[2],listContent[1],"-"+listContent[3],listContent[4],listContent[5],listContent[6]));

        }

    }

    /* Create Wallet */
    private void CreateWallet(){
        Bip44 bip44 = new Bip44();
        bip44.registerCallBack(this);
        bip44.execute(mPasswordwallet);
        mInfoDialog.Get("Wallet generation", "Please wait few seconds");
    }

    @Override
    public void backGeneration(Map<String, String> result, Credentials credentials) {
        mCredentials = credentials;
        setEthAddress(result.get("address"));
        setEthBalance(getEthBalance());
        new SaveWallet(keydir,mCredentials,mPasswordwallet).execute();

    }
    /* End Create Wallet*/

    /* Get Web3j*/
    private void getWeb3j(){
        new Initiate(mNodeUrl);
        mWeb3j = Initiate.sWeb3jInstance;
    }

    /* Get Credentials */
    private void getCredentials(File keydir){
        File[] listfiles = keydir.listFiles();
        try {
            mInfoDialog.Get("Load Wallet","Please wait few seconds");
//            binding.avi.show();
//            binding.ivLoadingBackground.setVisibility(View.VISIBLE);
            GetCredentials getCredentials = new GetCredentials();
            getCredentials.registerCallBack(this);
            getCredentials.FromFile(listfiles[0].getAbsolutePath(),mPasswordwallet);
        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (CipherException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void backLoadCredential(Credentials credentials) {
        mCredentials = credentials;
        LoadWallet();
    }

    /* End Get Credentials */

    private void LoadWallet(){
        setEthAddress(getEthAddress());
        setEthBalance(getEthBalance());
        GetTokenInfo();
    }
    /* Get Address Ethereum */
    private String getEthAddress(){
        return mCredentials.getAddress();
    }

    /* Set Address Ethereum */
    private void setEthAddress(String address){
        binding.tvWalletAddress.setText(address);
    }


    /* Get Balance */
    private String getEthBalance(){
        try {
            return new Balance(mWeb3j,getEthAddress()).getInEther().toString();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /* Set Balance */
    private void setEthBalance(String ethBalance){
        /* coin kind recyclerView 에 이더 item 추가 */
        String strNumber = String.format("%.9f", Float.valueOf(ethBalance));
        coinKindAdapter.add(0,new CoinKindsItem("ETHEREUM","E","ETH",strNumber));
        mInfoDialog.Dismiss();
//        binding.avi.smoothToHide();
//        binding.ivLoadingBackground.setVisibility(View.GONE);
    }

    /*Get Token Info*/
    private void GetTokenInfo(){
        LoadSmartContract loadSmartContract = new LoadSmartContract(mWeb3j,mCredentials,mSmartcontract,mGasPrice,mGasLimit);
        loadSmartContract.registerCallBack(this);
        loadSmartContract.LoadToken();
    }


    @Override
    public void backLoadSmartContract(Map<String, String> result) {
        setTokenBalance(result.get("tokenbalance"));
        String convertTokenBalance = String.valueOf(Convert.fromWei(result.get("tokenbalance"),Convert.Unit.ETHER));

        /* coin kind recyclerView 에 토큰 item 추가 */
        coinKindAdapter.add(1,new CoinKindsItem("AJIN TOKEN","A","AJT",convertTokenBalance));
    }
    private void setTokenBalance(String value){
        /**단위가 정말 중요하다 wei 형식으로 값이 들어오기때문에 ether의 단위까지 줄여서 보여줘야한다. 내보낼때도 마찬가지!*/
        String convertTokenBalance = String.valueOf(Convert.fromWei(value,Convert.Unit.ETHER));
    }
    /* End Get Token*/

    /* Sending */
    public void sendEther(String GasPrice, String GasLimit, String toAddress, String EtherAmmount){
        sendingEther = new SendingEther(mWeb3j,
                mCredentials,
                GasPrice,
                GasLimit);
        sendingEther.registerCallBack(this);
        sendingEther.Send(toAddress,EtherAmmount);

        //db에 넣고 adapter에 추가해서 list에 바로 저장할 수 있게
        dbHelper.insert("ETHER","ETH","E",EtherAmmount,1,getTime(),"0");
        coinTransferAdapter.add(new CoinTransferItem("ETHER","E","ETH","-"+EtherAmmount,"1",getTime(),"0"));
    }

    @Override
    public void backSendEthereum(EthSendTransaction result) {
        toastMsg.Long(context,result.getTransactionHash());
        LoadWallet();

        //db랑 adapter 에 transferStatus 값 0으로 변경
        int lastPosition = dbHelper.getLastPosition();
        dbHelper.update(lastPosition,0);
        coinTransferAdapter.update(lastPosition,"0",result.getTransactionHash(),getTime());
    }

    public void sendToken(String GasPrice, String GasLimit, String toAddress, String TokenAmmount){
        sendingToken = new SendingToken(mWeb3j,
                mCredentials,
                GasPrice,
                GasLimit);
        sendingToken.registerCallBackToken(this);
        sendingToken.Send(mSmartcontract,toAddress,TokenAmmount);

        //db에 넣고 adapter에 추가해서 list에 바로 저장할 수 있게
        dbHelper.insert("AJIN TOKEN","AJT","A",TokenAmmount,1,getTime(),"0");
        coinTransferAdapter.add(new CoinTransferItem("AJIN TOKEN","A","AJT","-"+TokenAmmount,"1",getTime(),"0"));

    }

    @Override
    public void backSendToken(TransactionReceipt result) {
        toastMsg.Long(context,result.getTransactionHash()); //결과값이 여기서 들어온다
        LoadWallet();
        getTransactionStatus(result);

        //db랑 adapter 에 transferStatus 값 0으로 변경
        int lastPosition = dbHelper.getLastPosition();
        dbHelper.update(lastPosition,0);
        coinTransferAdapter.update(lastPosition,"0",result.getTransactionHash(),getTime());
    }
    /* End Sending */


    /* Start get transaction status (지금부터 보내지는 토큰을 listView에 추가할 것*/
    public void getTransactionStatus(TransactionReceipt receipt){

        if(receipt.getBlockNumber()==null){ //대기중인 트랜잭션

        }else{ //거래완료
            for(int i=0; i<receipt.getLogs().size(); i++){
                receipt.getLogs().get(i);
               Log.e("blockNumberRaw", receipt.getBlockNumberRaw());
            }
        }
    }



    /* coin kind recyclerView */
    private void setRecyclerView(){

        coinKindsItemList.clear();

        /** Coin kinds recyclerView */
        /* coin kind recyclerView 에 이더 item 추가 */
        coinKindsItemList.add(new CoinKindsItem("ETHER","E","ETH","0"));
        /* coin kind recyclerView 에 토큰 item 추가 */
        coinKindsItemList.add(new CoinKindsItem("AJIN TOKEN","A","AJT","0"));

        coinKindAdapter = new CoinKindAdapter(context, coinKindsItemList);
        binding.rvCoinRecyclerview.setAdapter(coinKindAdapter);

        //recyclerView 스크롤 방향 설정 (가로로 스크롤 > HORIZONTAL)
        binding.rvCoinRecyclerview.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));


        if(binding.rvCoinRecyclerview.getItemDecorationCount()>0){ // 전에 설정된 간격이 있으면
            binding.rvCoinRecyclerview.removeItemDecorationAt(0); // 전에 간격 없애기
        }

        // recyclerView 사이 간격 설정
        binding.rvCoinRecyclerview.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.right = 5; // recyclerView 사이 간격 10
            }
        });

        /** Coin transfer recyclerView*/
        coinTransferItemList.clear();
        /* coin kind recyclerView 에 이더 item 추가 */
        setTransferHistory(); // sqlite 에서 데이터 가져옴

        coinTransferAdapter = new CoinTransferAdapter(context, coinTransferItemList);
        binding.rvCoinTransferList.setAdapter(coinTransferAdapter);

        //recyclerView 스크롤 방향 설정 (가로로 스크롤 > HORIZONTAL)
        binding.rvCoinTransferList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));


        if(binding.rvCoinTransferList.getItemDecorationCount()>0){ // 전에 설정된 간격이 있으면
            binding.rvCoinTransferList.removeItemDecorationAt(0); // 전에 간격 없애기
        }

        // recyclerView 사이 간격 설정
        binding.rvCoinTransferList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = 10; // recyclerView 사이 간격 10
            }
        });


    }
    private String getTime(){
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }




    public class OnClick{
        public void showSetting(View view) {
            showCoinWalletDialog(); // 지갑 만들거나 가져올 수 있는 dialog 띄우기
        }
        public void copyAddress(View view){
            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("address",binding.tvWalletAddress.getText());
            cm.setPrimaryClip(clipData);

            //복사가 되었다면 토스트메시지 노출
            Toast.makeText(context,"지갑 주소가 복사되었습니다.",Toast.LENGTH_SHORT).show();
        }


        public void showQRBig(View view){
            showCoinWalletInfoDialog();
        }
        public void scanQR(View view){
            new ScanIntegrator((Activity) context).startScan();
        }
    }
}
