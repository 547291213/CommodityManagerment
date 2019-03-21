package com.example.commoditymanagerment.Util;

import android.widget.Toast;

public class UserPermissionExchange {

    public static String exChange(int permissions) {
        switch (permissions) {
            case 0:

                return "普通用户" ;
            case 1:

                return "管理员" ;

            case 2:
                return "超级管理员" ;

            default:

                return "未知用户" ;
        }
    }
}
