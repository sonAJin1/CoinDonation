package com.example.sonaj.coindonation.Data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    // DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // DB를 새로 생성할 때 호출되는 함수
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 새로운 테이블 생성
        /* 이름은 MONEYBOOK이고, 자동으로 값이 증가하는 _id 정수형 기본키 컬럼과
        coinName 문자열 컬럼, coinSymbol 문자형 컬럼, coinBigSymbol 문자형 컬럼, coinBalance 문자열 컬럼
        transferStatus 정수형 컬럼, date 문자형 컬럼, txHash 문자형 컬럼 으로 구성된 테이블을 생성.

        _id
   */
        db.execSQL("CREATE TABLE WalletTransfer01 (_id INTEGER PRIMARY KEY AUTOINCREMENT, coinName TEXT, coinSymbol TEXT, coinBigSymbol TEXT, coinBalance TEXT, transferStatus INTEGER, date TEXT, txHash TEXT);");
    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(String coinName, String coinSymbol, String coinBigSymbol, String coinBalance, int transferStatus, String date, String txHash) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        // DB에 입력한 값으로 행 추가
        db.execSQL("INSERT INTO WalletTransfer01 VALUES(null, '" + coinName + "', '" + coinSymbol + "', '"+coinBigSymbol+"', '"+coinBalance+"', '"+transferStatus+"', '"+date+"', '"+txHash+"' );");
        db.close();
    }

    public void update(int index, int transferStatus) {
        SQLiteDatabase db = getWritableDatabase();
        // id와 일치하는 transferStatus 를 변경
       // db.execSQL("UPDATE WalletTransfer01 SET transferStatus='" + transferStatus + "' WHERE _id='" + index + "';");
        db.execSQL("UPDATE WalletTransfer01 SET transferStatus=0 ;");
        db.close();
    }

    public void delete(int index) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행 삭제
        db.execSQL("DELETE FROM WalletTransfer01 WHERE _id='" + index + "';");
        db.close();
    }

    public String getResult() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM WalletTransfer01", null);
        while (cursor.moveToNext()) {
            result += cursor.getString(1) //coinName
                    + " , "
                    + cursor.getString(2) //coinSymbol
                    + " , "
                    + cursor.getString(3) //coinBigSymbol
                    + " , "
                    + cursor.getString(4) //coinBalance
                    + " , "
                    + cursor.getInt(5) //transferStatus
                    + " , "
                    + cursor.getString(6) //date
                    + " , "
                    + cursor.getString(7) //txHash
                    + "\n";
//            coinName 문자열 컬럼, coinSymbol 문자형 컬럼, coinBigSymbol 문자형 컬럼, coinBalance 문자열 컬럼
//            transferStatus 정수형 컬럼, date 문자형 컬럼, txHash
        }
        return result;
    }

    public int getLastPosition(){
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        // txHash 에 0이 들어가있는 행 중 가장 최근 값을 가져온다
        Cursor cursor = db.rawQuery("SELECT * FROM WalletTransfer01 WHERE txHash='0'", null);
        cursor.moveToLast();
        return cursor.getPosition();
    }
}


