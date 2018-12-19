package com.example.sonaj.coindonation.Util;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.ViewDataBinding;
import android.view.View;

/**
 * Description : View 들의 공통 사항을 담아 놓은 클래스
 */
public abstract class BaseView extends BaseObservable implements BaseViewInterface{

    ViewDataBinding dataBinding;
    Context context;
    View view;

    /**
     * 생성자에서 view 를 설정하므로 setView 메소드를 생성하지 않음.
     * @param context       : View 가 그렬질 영역의 context
     * @param dataBinding   : xml 의 View 들을 담고 있는 데이터 바인딩
     * */
    public BaseView(Context context, ViewDataBinding dataBinding){
        this.context        = context;
        this.dataBinding    = dataBinding;
        this.view           = dataBinding.getRoot();
    }

    public View getView() {
        return view;
    }

}