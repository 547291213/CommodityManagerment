package com.example.commoditymanagerment.Util;

public class StaticDataUtil {

    //登陆成功时候记录的用户名
    public static final String USER_NAME = "username";

    //商品id
    public static final String GOODS_ID = "goodsId";

    //商品种类相关
    public static final String CATEGORY_HOT = "火爆";
    public static final String CATEGORY_NEW = "新品";
    public static final String CATEGORY_PROMOTION = "促销";
    public static final String CATEGORY_ACTIVITY = "活动";
    public static final String CATEGORY_OUTSTOCK = "缺货";

    //打开相机
    public static final int OPEN_PHOTO = 1;

    //选择相册
    public static final int CHOOSE_ALBUM = 2;

    //读写权限
    public static final int REQUEST_CODE_WRITE = 3;


    //获取商品信息成功
    public static final int GET_GOODS_DATA_SUCCESS = 100;

    //获取商品信息失败
    public static final int GET_GOODS_DATA_ERROR = 101;

    //用户拥有修改信息的权限
    public static final int USER_HAVE_PERMISSION = 105;

    //用户没有修改信息的权限
    public static final int USER_NOT_HAVE_PERMISSION = 106;

    //获取用户权限失败
    public static final int GET_USER_PERMISSION_ERROR = 107;

    //用户权限限制值
    public static final int USER_PERMIISON_LIMIT = 1;


}
