package com.example.sonaj.coindonation.CoinWallet.wallet;

import android.os.AsyncTask;

import com.example.sonaj.coindonation.CoinWallet.callback.CBSendingEther;
import com.example.sonaj.coindonation.CoinWallet.callback.CBSendingToken;
import com.example.sonaj.coindonation.CoinWallet.smartcontract.TokenERC20;

import org.web3j.abi.datatypes.Int;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert;

import java.math.BigInteger;


public class SendingToken {

    private Credentials mCredentials;
    private Web3j mWeb3j;
    private String fromAddress;
    private String mValueGasPrice;
    private String mValueGasLimit;

    private CBSendingEther cbSendingEther;
    private CBSendingToken cbSendingToken;

    private TokenERC20 token;

    public SendingToken(Web3j web3j, Credentials credentials, String valueGasPrice, String valueGasLimit){
        mWeb3j = web3j;
        mCredentials = credentials;
        fromAddress = credentials.getAddress();
        mValueGasPrice = valueGasPrice;
        mValueGasLimit = valueGasLimit;
    }

    private BigInteger getGasPrice(){
        return BigInteger.valueOf(Long.valueOf(mValueGasPrice));
    }

    private BigInteger getGasLimit(){
        return BigInteger.valueOf(Long.valueOf(mValueGasLimit));
    }

    public void Send(String smartContractAddress, String toAddress, String valueAmmount) {
        new SendToken().execute(smartContractAddress,toAddress,valueAmmount);
    }

    private class SendToken extends AsyncTask<String,Void,TransactionReceipt> {

        @Override
        protected TransactionReceipt doInBackground(String... value) {

          //  BigInteger ammount = BigInteger.valueOf(Long.parseLong(value[2])); //잘못된 단위로 0.00000000x 단위로 보내졌었음
            BigInteger ammount2 = Convert.toWei(value[2], Convert.Unit.ETHER).toBigInteger(); //이건 x0000000000000000 단위로 나오는데 왜 보내고 이더스캔에서 확인하면 제대로 나오는가...?

            System.out.println(getGasPrice());
            System.out.println(getGasLimit());

            token = TokenERC20.load(value[0], mWeb3j, mCredentials, getGasPrice(), getGasLimit());
            try {
                TransactionReceipt result = token.transfer(value[1], ammount2).send();
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(TransactionReceipt result) {
            super.onPostExecute(result);
            cbSendingToken.backSendToken(result);
        }
    }

    public void registerCallBackToken(CBSendingToken cbSendingToken){
        this.cbSendingToken = cbSendingToken;
    }

}
