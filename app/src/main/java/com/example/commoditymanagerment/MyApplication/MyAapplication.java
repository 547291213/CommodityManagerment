package com.example.commoditymanagerment.MyApplication;

import android.app.Application;

import com.example.commoditymanagerment.NetWork.HttpHelper;
import com.example.commoditymanagerment.NetWork.OkHttpProcesser;

public class MyAapplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        /**
         * 网络框架选择的初始化
         */
        HttpHelper.initHttpProcesser(new OkHttpProcesser());

    }
}
