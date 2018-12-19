package com.example.sonaj.coindonation.CoinWallet.web3j;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.http.HttpService;

public class Initiate {

    public static Web3j sWeb3jInstance;

    public Initiate(String urlNode){
        sWeb3jInstance = Web3jFactory.build(new HttpService(urlNode));
    }
}
