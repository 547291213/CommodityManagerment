package com.example.commoditymanagerment.Util;

public class UrlHelper {
    //登陆请求的通用网址
    public static final String LOGIN_URL = "http://192.168.43.85:8083/android/login?userName=" ;
   //    ?userName=123451&password=123451

    //注册请求的通用网址
    public static final String REGISTER_URL = "http://192.168.43.85:8083/android/register?userName=" ;
    //    ?userName=123451&password=123451

    //通过用户名获取用户信息
    public static final String USER_INFO_URL = "http://192.168.43.85:8083/android//getUserInfo?userName=" ;
    //123456


    //拉取商品列表的通用网址
    public static final String GOODS_LIST_URL = "http://192.168.43.85:8083/goods/page/getGoodsById?goodsCategory=" ;
   //2&current=1&rowCount=20

    //根据商品id获取商品详情的网址
    public static final String GOODS_DESCRIBE_URL = "http://192.168.43.85:8083/goods/getGoodsById?goodsId=" ;
    //2

    //添加商品的通用网址
    public static final String GOODS_ADD_URL = "http://192.168.43.85:8083/goods/goodsDataAdd?&goodsName=" ;
    //%E7%BA%AF%E7%89%9B%E5%A5%B6&goodsCount=24&goodsDescribe=%E4%BC%8A%E5%88%A9%E7%BA%AF%E7%89%9B%E5%A5%B6&goodsCategory=0&goodsImg=heheda&lastModifyTime=2019-03-18%2020:26&lastModifyUser=kefeng&originalPrice=68&presentPrice=60


    //添加商品图片的通用网址
    public static final String GOODS_ADD_IMG_URL = "http://192.168.43.85:8083/goods/addGoods?" ;


    //访问商品图片通用URL
    public static final String GOODS_GET_IMG_URL = "http://192.168.43.85:8083/upload/" ;
    //155308100531537.jpeg

}
