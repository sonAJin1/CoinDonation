package com.example.sonaj.coindonation.Util;

import java.math.BigInteger;

public interface WalletSettingListener {
    public void onWalletCreate(String name,String password);
    public void onWalletGet(String name,BigInteger address);

}
