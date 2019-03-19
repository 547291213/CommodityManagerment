package com.example.commoditymanagerment.DrawableView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class InnerViewPager extends ViewPager {
    public InnerViewPager(@NonNull Context context) {
        super(context);
    }

    public InnerViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onFilterTouchEventForSecurity(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.onFilterTouchEventForSecurity(event);
    }
}
