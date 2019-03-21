package com.example.commoditymanagerment.Util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.commoditymanagerment.Bean.Goods;
import com.example.commoditymanagerment.Bean.GoodsGrid;
import com.example.commoditymanagerment.NetWork.HttpHelper;
import com.example.commoditymanagerment.NetWork.NetCallBackResultBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 拉取商品数据
 * 废弃
 */
@Deprecated
public class DataService {

    //每页显示的商品数目
    public static final String ROW_COUNT = "10";
    private GoodsGrid goods;
    private String url;
    private Context mContext;

    private static final String TAG = "DataService";

    public DataService(Context context) {
        mContext = context;
    }

    /**
     * okHttp 网络访问是异步进行，在子线程中实现，
     * 如果这里直接返回列表数据，返回为null
     *
     * @param goodsCategory
     * @param current
     * @return
     */
    public void getData(final int goodsCategory, int current ) {
        url = UrlHelper.GOODS_LIST_URL + goodsCategory + "&current=" + current + "&rowCount=" + ROW_COUNT;
        HttpHelper httpHelper = HttpHelper.getInstance(mContext.getApplicationContext());
        httpHelper.getRequest(url, null, HttpHelper.JSON_DATA_1,
                new NetCallBackResultBean<GoodsGrid>() {
                    @Override
                    public void onSuccess(GoodsGrid goodsGrid) {
                        goods = goodsGrid ;
                        Log.d(TAG, "onSuccess: " + goodsGrid.getRows().get(0).getGoodsName());
                    }

                    @Override
                    public void onSuccess(List result) {
                        Log.d(TAG, "onSuccess: result");
                    }
                    @Override
                    public void Failed(String string) {
                        Log.d(TAG, "Failed: " + string);
                    }
                });
    }

    private static class MyHanlder extends Handler{

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }
}
