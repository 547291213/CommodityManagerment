package com.example.commoditymanagerment.Activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.example.commoditymanagerment.Util.ActivityController;

public class BaseActivity extends AppCompatActivity {

    private ForceOfflineReceiver receiver;
    private Dialog forceOfflineDialog;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityController.addActivity(this);


    }


    @Override
    protected void onDestroy() {
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        ActivityController.removeActivity(this);
        super.onDestroy();

    }

    /*
   在onResume 和 onStop中的操作确保了
   只在处于栈顶的Activity中可以触发强制退出的广播
 */
    @Override
    protected void onResume() {

        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.xkfeng.forceofflinereceiver");
        receiver = new ForceOfflineReceiver();
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }

    }

    public class ForceOfflineReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(final Context context, Intent intent) {
            new AlertDialog.Builder(context)
                    .setTitle("Warnning")
                    .setMessage("The account is logged in elsewhere")
                    .setCancelable(false)
                    .setPositiveButton("Re-Loading", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //关闭所有Activity
                            ActivityController.finishAll();
                            //转换到登陆界面
                            Intent intent1 = new Intent() ;
                            intent1.setClass(context , LoginActivity.class) ;
                            startActivity(intent1);
                        }
                    })
                    .show() ;
        }
    }

    /**
     * 强制下线方法调用
     */
    public void ForceOffline(){


    }
}
