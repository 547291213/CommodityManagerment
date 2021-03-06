package com.example.commoditymanagerment.DrawableView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ViewPagerSlide extends ViewPager {

    private boolean isSlide = true;

    public ViewPagerSlide(@NonNull Context context) {
        super(context);
    }

    public ViewPagerSlide(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setSlide(boolean slide) {
        isSlide = slide;
    }


    /**
     * 是否拦截事件
     *
     * @param ev 点击事件
     * @return 是否拦截
     */
    @Override
    public boolean onInterceptHoverEvent(MotionEvent ev) {
        return isSlide;
    }

    /**
     * 点击事件是否处理
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return isSlide;
    }


}