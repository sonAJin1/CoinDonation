package com.example.sonaj.coindonation.CoinWallet;

public class config {

    public static String addressethnode(int node) {
//        switch(node){
//            case 1:
//                return "http://176.74.13.102:18087";
//            case 2:
//                return "http://192.168.0.33:8547";
//            default:
//                return "https://mainnet.infura.io/avyPSzkHujVHtFtf8xwY";
//        }
        return "https://ropsten.infura.io/v3/16c61d6fcb924a6ca8d9c3f9a2bbdefb";
    }

    public static String addresssmartcontract(int contract) {
//        switch (contract){
//            case 1:
//                return "0x5C456316Da36c1c769FA277cE677CB8F690c5767";
//            default :
//                return "0x89205A3A3b2A69De6Dbf7f01ED13B2108B2c43e7";
//        }
        return "0x17ded0925ea57b9c1da89a3b99f5f780dac58964";
    }

    public static String passwordwallet() {
        return "";
    }


}
