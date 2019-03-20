package com.example.commoditymanagerment.Util;

public class GoodsCategoryExchange {



    public static int exChange(String value) {

        if (StaticDataUtil.CATEGORY_HOT .equals(value)){
            return 0 ;
        }

        if (StaticDataUtil.CATEGORY_NEW .equals(value)){
            return 1 ;
        }


        if (StaticDataUtil.CATEGORY_PROMOTION .equals(value)){
            return 2 ;
        }


        if (StaticDataUtil.CATEGORY_ACTIVITY .equals(value)){
            return 3 ;
        }


        if (StaticDataUtil.CATEGORY_OUTSTOCK .equals(value)){
            return 4 ;
        }


        return -1 ;

    }


}
