package com.example.commoditymanagerment.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.commoditymanagerment.Bean.Goods;
import com.example.commoditymanagerment.MyApplication.MyAapplication;
import com.example.commoditymanagerment.NetWork.HttpHelper;
import com.example.commoditymanagerment.NetWork.NetCallBackResultBean;
import com.example.commoditymanagerment.R;

import java.lang.ref.WeakReference;
import java.util.List;

import static com.example.commoditymanagerment.Util.StaticDataUtil.GOODS_ID;
import static com.example.commoditymanagerment.Util.UrlHelper.GOODS_DESCRIBE_URL;

public class GoodsDescribeActivity extends BaseActivity {

    private static final String TAG = "GoodsDescribeActivity";
    private int goodsId;
    private String url;
    private MyHandler myHandler = new MyHandler(this);
    private Goods goods ;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_goods_describe);
        goodsId = (int) getIntent().getIntExtra(GOODS_ID, -1);

        Log.d(TAG, "onCreate: goodsId is " + goodsId);

        initData();

    }

    private void initData() {
        url = GOODS_DESCRIBE_URL + goodsId;
        HttpHelper httpHelper = HttpHelper.getInstance(getApplicationContext());
        httpHelper.getRequest(url, null, HttpHelper.JSON_DATA_1, new NetCallBackResultBean<Goods>() {
            @Override
            public void onSuccess(Goods goodsData) {

                goods = goodsData ;
                myHandler.sendEmptyMessage(0) ;
            }

            @Override
            public void onSuccess(List result) {
                myHandler.sendEmptyMessage(1) ;
            }

            @Override
            public void Failed(String string) {
                myHandler.sendEmptyMessage(1) ;
            }
        });

    }

    private static class MyHandler extends Handler {

        private WeakReference<GoodsDescribeActivity> weakReference;

        public MyHandler(GoodsDescribeActivity goodsDescribeActivity) {
            weakReference = new WeakReference<>(goodsDescribeActivity);
        }

        @Override
        public void handleMessage(Message msg) {

            GoodsDescribeActivity activity = weakReference.get();

            if (activity == null) {
                return;
            }

            switch (msg.what) {
                case 0:
                    Log.d(TAG, "handleMessage: " +
                            activity.goods.getGoodsName() + " " +
                            activity.goods.getGoodsDescribe());
                    break;

                default:

                    Toast.makeText(activity ,
                            "获取商品详情失败" ,Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

}
