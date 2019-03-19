package com.example.commoditymanagerment.Util;

public class UrlHelper {
    //登陆请求的通用网址
    public static final String LOGIN_URL = "http://192.168.43.85:8083/android/login?userName=" ;
   //    ?userName=123451&password=123451

    //注册请求的通用网址
    public static final String REGISTER_URL = "http://192.168.43.85:8083/android/register?userName=" ;
    //    ?userName=123451&password=123451


    //拉取商品列表的通用网址
    public static final String GOODS_LIST_URL = "http://192.168.43.85:8083/goods/page/getGoodsById?goodsCategory=" ;
   //2&current=1&rowCount=20

    //根据商品id获取商品详情的网址
    public static final String GOODS_DESCRIBE_URL = "http://192.168.43.85:8083/goods//getGoodsById?goodsId=" ;
    //2


}
