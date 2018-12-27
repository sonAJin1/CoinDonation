package com.example.sonaj.coindonation.CoinWallet;

public class CoinTransferItem {
    // 코인 이름, 심볼, BIG 심볼, status (보내는중, 완료, 실패), 날짜, 가격,
    String coinName;
    String coinSymbol;
    String coinBigSymbol;
    String coinBalance;
    String transferStatus;
    String date;
    String txHash;

    public CoinTransferItem(String coinName, String coinBigSymbol, String coinSymbol, String coinBalance, String transferStatus, String date, String txHash) {
        this.coinName = coinName;
        this.coinSymbol = coinSymbol;
        this.coinBigSymbol = coinBigSymbol;
        this.coinBalance = coinBalance;
        this.transferStatus = transferStatus;
        this.date = date;
        this.txHash = txHash;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinSymbol(String coinSymbol) {
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

    public void setTransferStatus(String transferStatus) {
        this.transferStatus = transferStatus;
    }

    public String getTransferStatus() {
        return transferStatus;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }
}


