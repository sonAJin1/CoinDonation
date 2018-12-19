package com.example.sonaj.coindonation.CoinWallet;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.sonaj.coindonation.CoinWallet.callback.CBBip44;
import com.example.sonaj.coindonation.CoinWallet.callback.CBGetCredential;
import com.example.sonaj.coindonation.CoinWallet.callback.CBLoadSmartContract;
import com.example.sonaj.coindonation.CoinWallet.callback.CBSendingEther;
import com.example.sonaj.coindonation.Data.DBHelper;
import com.example.sonaj.coindonation.Util.BaseView;
import com.example.sonaj.coindonation.Util.WalletSettingListener;
import com.example.sonaj.coindonation.databinding.CoinWalletViewBinding;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
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
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public class CoinWalletView extends BaseView {

    Context context;
    CoinWalletViewBinding binding;
    OnClick onClick;

    String[] createAddress;

    //sqlite
    private DBHelper dbHelper;

    private static final Logger log = LoggerFactory.getLogger(CoinWalletView.class);



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
        init();

        dbHelper = new DBHelper(context,"WalletAddress02.db",null,1);
    }

    @Override
    public void init() {
        onClick = new OnClick();
        binding.setOnClick(onClick);
    }

    public void setCoinWalletView(){
        Log.e("db",dbHelper.getResult());
    }

    public void showCoinWalletDialog(){
        WalletChoiceSettingDialog dialog = new WalletChoiceSettingDialog(context);
        dialog.setDialogListner(new WalletSettingListener() {
            @Override
            public void onWalletCreate(String name,String password) {
                if(name!=null){
                    createAddress = createWallet(password);
                    if(createAddress[1]!=null){
                        String address = createAddress[1];
                        binding.tvWalletName.setText(name);
                        binding.tvWalletAddress.setText(address);
                        //QR 코드 생성
                        generateRQCode(address);
                        //sqlite 에 주소 저장
                        dbHelper.insert(name,address);
                    }
                }
            }

            @Override
            public void onWalletGet(String name,BigInteger privateKey) {
                if(privateKey!=null){
                    String walletAddress = getWallet(privateKey); // 받아온 privateKey 에서 지갑 주소를 받아온다
                    binding.tvWalletName.setText(name); // 화면에 지갑 이름 받아온거 출력
                    binding.tvWalletAddress.setText(walletAddress); // 화면에 지갑 주소 받아온거 출력
                    //QR 코드 생성
                    generateRQCode(walletAddress);
                    //잔액조회
                    String ETHBalance = getBalance(walletAddress);
                    binding.tvEth.setText(ETHBalance);
                }
            }
        });
        dialog.show();
    }

    /** 지갑 생성 */
    public String[] createWallet(final String name) { //Password 를 인자로 주면 string 배열에 wallet이 저장된 path와 wallet 주소를 반환해주는 메소드
        String[] result = new String[2];
        try {
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS); //다운로드 path 가져오기
            if (!path.exists()) {
                path.mkdir();
            }
            String fileName = WalletUtils.generateLightNewWalletFile(name, new File(String.valueOf(path))); //패스워드와 path를 이용해 지갑생성
            result[0] = path+"/"+fileName;

            Credentials credentials = WalletUtils.loadCredentials(name,result[0]);

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

    /**지갑 잔액 가져오기 (ETH)*/
    public String getBalance(String address) {
        //통신할 노드의 주소를 지정해준다.
        Web3j web3 = Web3jFactory.build(new HttpService("https://ropsten.infura.io/v3/16c61d6fcb924a6ca8d9c3f9a2bbdefb"));
        String result = null;
        EthGetBalance ethGetBalance = null;
        try {
            //이더리움 노드에게 지정한 Address 의 잔액을 조회한다.
            ethGetBalance = web3.ethGetBalance(address, DefaultBlockParameterName.LATEST).sendAsync().get();
            BigInteger wei = ethGetBalance.getBalance();

            EthAccounts ethAccounts= web3.ethAccounts().sendAsync().get();
            List<String> wei2 = ethAccounts.getAccounts();

            //wei 단위를 ETH 단위로 변환 한다.
            result = Convert.fromWei(wei.toString() , Convert.Unit.ETHER).toString();

        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.e("e", String.valueOf(e));
        } catch (ExecutionException e) {
            e.printStackTrace();
            Log.e("e", String.valueOf(e));
        }
        return result;

    }

    /** privateKey 로 지갑주소 가져오기 */
    public String getWallet(BigInteger privateKey){

        Credentials credentials = Credentials.create(ECKeyPair.create(privateKey)); // 입력받은 privateKey 로 지갑 가져오기
        return credentials.getAddress(); // 지갑주소 return

    }


    /** QR cord 생성 */
    public void generateRQCode(String contents) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            Bitmap bitmap = toBitmap(qrCodeWriter.encode(contents, BarcodeFormat.QR_CODE, 300, 300));
            binding.ivQrcord.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap toBitmap(BitMatrix matrix) {
        int height = matrix.getHeight();
        int width = matrix.getWidth();
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bmp.setPixel(x, y, matrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bmp;
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

    }
}
