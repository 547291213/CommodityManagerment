package com.example.commoditymanagerment.DrawableView;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.commoditymanagerment.R;

public class GoodsListView extends ListView {

    private Context mContext;

    //每页显示的商品数目
    private static final int PAGE_COUNT = 20;


    /**
     * header
     */
    private RelativeLayout headerLayout;
    private LinearLayout headerLinerLayout;

    /**
     * footer
     */
    private RelativeLayout footerLayout;
    private LinearLayout footerLinerLayout;
    private ImageView footerImg;


    public GoodsListView(Context context) {
        this(context, null);
    }

    public GoodsListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GoodsListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context  ;
        initHeader();
        initFooter();

    }

    private void initHeader() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        headerLayout = (RelativeLayout) inflater.inflate(R.layout.network_not_use_item, this, false);
        headerLinerLayout = headerLayout.findViewById(R.id.list_header);
        addHeaderView(headerLayout);
    }

    private void initFooter() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerLayout = (RelativeLayout) inflater.inflate(R.layout.item_refresh, this, false);
        footerLinerLayout = footerLayout.findViewById(R.id.ll_loadingLayout);
        footerImg = footerLayout.findViewById(R.id.iv_loadingImg);

        addFooterView(footerLayout);
    }


    public void setFooterViewShow() {
        footerLayout.setVisibility(VISIBLE);
        footerImg.setVisibility(View.VISIBLE);
        AnimationDrawable drawable = (AnimationDrawable) footerImg.getDrawable();
        drawable.start();

    }

    public void setFooterViewHide(int selection) {
        footerLayout.setVisibility(GONE);
        footerImg.setVisibility(View.GONE);
        setSelection(selection);
    }

    public void setHeaderViewShow(){
        headerLayout.setVisibility(VISIBLE);
        headerLinerLayout.setVisibility(VISIBLE);
        setSelection(0);
    }

    public void setHeaderViewHide(){
        headerLayout.setVisibility(GONE);
        headerLinerLayout.setVisibility(GONE);
        setSelection(1);
    }





}
