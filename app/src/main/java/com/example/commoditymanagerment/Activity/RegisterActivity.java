package com.example.commoditymanagerment.Activity;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.commoditymanagerment.Bean.ResultCode;
import com.example.commoditymanagerment.DrawableView.DrawableTextEdit;
import com.example.commoditymanagerment.NetWork.HttpHelper;
import com.example.commoditymanagerment.NetWork.NetCallBackResultBean;
import com.example.commoditymanagerment.R;
import com.example.commoditymanagerment.Util.UrlHelper;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends BaseActivity {


    @BindView(R.id.iv_backImage)
    ImageView ivBackImage;
    @BindView(R.id.ll_titleLayout)
    LinearLayout llTitleLayout;
    @BindView(R.id.tiet_userEdit)
    DrawableTextEdit tietUserEdit;
    @BindView(R.id.til_user)
    TextInputLayout tilUser;
    @BindView(R.id.tiet_passwordEdit)
    DrawableTextEdit tietPasswordEdit;
    @BindView(R.id.til_passwrod)
    TextInputLayout tilPasswrod;
    @BindView(R.id.til_rePasswrod)
    TextInputLayout tilRePasswrod;
    @BindView(R.id.bt_registerBtn)
    Button btRegisterBtn;


    private static final String TAG = "RegisterActivity";
    private String registerUrl;
    private MyHandler myHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_resgister);
        ButterKnife.bind(this);
    }


    @OnClick(R.id.bt_registerBtn)
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.bt_registerBtn:
                if (TextUtils.isEmpty(tietUserEdit.getText().toString())) {
                    Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(tilPasswrod.getEditText().getText().toString())) {
                    Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!tilPasswrod.getEditText().getText().toString().
                        equals(tilRePasswrod.getEditText().getText().toString())) {
                    Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    myHandler = new MyHandler(RegisterActivity.this)  ;
                    //对本地服务器进行访问。
                    registerUrl = UrlHelper.REGISTER_URL + tietUserEdit.getText().toString() + "&password=" + tilPasswrod.getEditText().getText().toString();
                    Log.d(TAG, "onViewClick: registerUrl :" + registerUrl);
                    HttpHelper httpHelper = HttpHelper.getInstance(getApplicationContext());
                    httpHelper.getRequest(registerUrl, null, HttpHelper.JSON_DATA_1, new NetCallBackResultBean<ResultCode>() {
                        @Override
                        public void onSuccess(ResultCode code) {
                            myHandler.sendEmptyMessage(Integer.parseInt(code.getCode()));
                        }

                        @Override
                        public void onSuccess(List result) {
                            myHandler.sendEmptyMessage(2);
                        }

                        @Override
                        public void Failed(String string) {
                            myHandler.sendEmptyMessage(2);
                        }
                    });

                }
                break;
        }

    }


    private static class MyHandler extends Handler {

        private WeakReference<RegisterActivity> weakReference;

        public MyHandler(RegisterActivity registerActivity) {
            weakReference = new WeakReference<>(registerActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            RegisterActivity registerActivity = weakReference.get();
            if (registerActivity == null) {
                return;
            }
            switch (msg.what) {
                case 0:
                    Toast.makeText(registerActivity, "注册成功", Toast.LENGTH_SHORT).show();
                    registerActivity.finish();
                    break;

                case 1:
                    Toast.makeText(registerActivity, "用户已经存在", Toast.LENGTH_SHORT).show();
                    break;

                case 2:
                default:
                    Toast.makeText(registerActivity, "出现未知错误", Toast.LENGTH_SHORT).show();
                    break;

            }


        }
    }

}
