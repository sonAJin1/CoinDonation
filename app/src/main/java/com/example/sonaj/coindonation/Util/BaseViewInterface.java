package com.example.sonaj.coindonation.Util;

import android.view.View;

/**
 * Description : 뷰 관련된 메소드명을 통일하기 위해 만든 class
 *
 * 1. init()        : View 를 초기화 하는 메소드
 * 2. getView()     : View 를 가져오는 메소드
 */
public interface BaseViewInterface {

    /** 초기화를 위한 함수*/
    void init();

    /** 현재 화면을 담은 View*/
    View getView();

}