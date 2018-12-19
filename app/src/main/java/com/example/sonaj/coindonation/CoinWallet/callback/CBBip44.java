package com.example.sonaj.coindonation.CoinWallet.callback;

import org.web3j.crypto.Credentials;

import java.util.Map;

public interface CBBip44 {
    void backGeneration(Map<String, String> result, Credentials credentials);
}
