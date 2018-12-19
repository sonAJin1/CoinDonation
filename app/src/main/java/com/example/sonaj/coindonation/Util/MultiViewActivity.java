package com.example.sonaj.coindonation.Util;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.sonaj.coindonation.R;

import java.util.ArrayList;
import java.util.List;

public abstract class MultiViewActivity extends AppCompatActivity {

    /**본 액티비티에서 사용자가 볼 수 있는 화면들*/
    List<BaseView> viewList;
    public int currentViewPosition    = 0;
    Animation leftToRightIn;
    Animation leftToRightOut;
    Animation rightToLeftIn;
    Animation rightToLeftOut;

    /**뷰 목록*/
    public void initViewList(){
        viewList = new ArrayList<>();
        leftToRightIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.from_left_to_right_in);
        leftToRightOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.from_left_to_right_out);
        rightToLeftIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.from_right_to_left_in);
        rightToLeftOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.from_right_to_left_out);
    }

    /**본 액티비티에서 볼 수 있는 화면 추가
     * @param baseView : 추가할 화면 View*/
    public void addView(BaseView baseView){
        viewList.add(baseView);
    }
    public void addView(BaseView baseView,int position){
        viewList.add(position,baseView);
    }

    /**본 액티비에서 볼 수 있는 화면을 제거
     * @param baseView : 삭제할 화면 View*/
    public void removeView(BaseView baseView) {
        viewList.remove(baseView);
    }
    /**본 액티비에서 볼 수 있는 화면을 제거
     * @param position : 삭제할 화면 View 의 위치*/
    public void removeView(int position){
        viewList.remove(position);
    }

    /**사용자의 선택에 의해 변경되는 화면
     * @param position : 사용자가 선택한 View 위치
     * @return 사
     * 용자가 보고 있는 화면 위치*/
    public int setView(int position){
        //현재 가지고 있는 화면 목록에서
        for(int i = 0; i<viewList.size(); i++){
            //array out of index 예외 처리 해당 값이 없으면 수행하지 않음.
            if(viewList.size() < i) return -1;
            if(position == -1) return -1;

            if(currentViewPosition == -1) currentViewPosition = 0;

            //선택한 화면은 보여주기
            if(i == position) {
                if(currentViewPosition < position){
                    viewList.get(position).getView().startAnimation(rightToLeftIn);
                    viewList.get(currentViewPosition).getView().startAnimation(rightToLeftOut);
                }
                else{
                    viewList.get(position).getView().startAnimation(leftToRightIn);
                    viewList.get(currentViewPosition).getView().startAnimation(leftToRightOut);
                }


                viewList.get(position).getView().setVisibility(View.VISIBLE);
                //현재 보고 있는 화면 위치 업데이트
                currentViewPosition = position;
            }

            //선택하지 않은 화면은 안보여주기
            else {
                viewList.get(i).getView().setVisibility(View.GONE);
            }
        }
        return currentViewPosition;
    }

    /**
     * @return 사용자가 보고 있는 화면의 위치*/
    public int getCurrentViewPosition() {
        return currentViewPosition;
    }

    public abstract void init();


}
