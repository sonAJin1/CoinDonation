package com.example.sonaj.coindonation.CoinWallet;

public class CoinKindsItem {
    String coinName;
    String coinSymbol;
    String coinBigSymbol;
    String coinBalance;

    public CoinKindsItem(String coinName, String coinBigSymbol, String coinSymbol, String coinBalance) {
        this.coinName = coinName;
        this.coinSymbol = coinSymbol;
        this.coinBigSymbol = coinBigSymbol;
        this.coinBalance = coinBalance;
    }

    public void setCoinName(String coinName){
        this.coinName = coinName;
    }
    public String getCoinName(){
        return coinName;
    }
    public void setCoinSymbol(String coinSymbol){
        this.coinSymbol = coinSymbol;
    }

    public String getCoinSymbol() {
        return coinSymbol;
    }

    public void setCoinBigSymbol(String coinBigSymbol) {
        this.coinBigSymbol = coinBigSymbol;
    }

    public String getCoinBigSymbol() {
        return coinBigSymbol;
    }

    public void setCoinBalance(String coinBalance) {
        this.coinBalance = coinBalance;
    }

    public String getCoinBalance() {
        return coinBalance;
    }
}

