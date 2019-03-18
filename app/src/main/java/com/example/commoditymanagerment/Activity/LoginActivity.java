package com.example.commoditymanagerment.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.commoditymanagerment.Bean.ResultCode;
import com.example.commoditymanagerment.DrawableView.CustomDialog;
import com.example.commoditymanagerment.DrawableView.DrawableTextEdit;
import com.example.commoditymanagerment.NetWork.HttpHelper;
import com.example.commoditymanagerment.NetWork.NetCallBackResultBean;
import com.example.commoditymanagerment.NetWork.OkHttpProcesser;
import com.example.commoditymanagerment.R;
import com.example.commoditymanagerment.Util.ActivityController;
import com.example.commoditymanagerment.Util.UrlHelper;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

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
    @BindView(R.id.bt_loginBtn)
    Button btLoginBtn;
    @BindView(R.id.tv_forgetPasswordTv)
    TextView tvForgetPasswordTv;
    @BindView(R.id.tv_registerUserTv)
    TextView tvRegisterUserTv;
    @BindView(R.id.tv_protocolTv)
    TextView tvProtocolTv;
    @BindView(R.id.rl_loginRelayout)
    RelativeLayout rlLoginRelayout;

    private String loginUrl;
    private static final String TAG = "LoginActivity";
    private MyHandler myHandler;
    private static SharedPreferences.Editor editor ;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

    }


    @OnClick({R.id.bt_loginBtn, R.id.tv_forgetPasswordTv, R.id.tv_registerUserTv})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.bt_loginBtn:

                loginBtnClick();
                break;

            case R.id.tv_registerUserTv:
                Intent intent = new Intent();
                intent.setClass(this, RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void loginBtnClick() {

        if (TextUtils.isEmpty(tietUserEdit.getText()) || TextUtils.isEmpty(tietPasswordEdit.getText())) {
            Toast.makeText(this, "用户名或者密码不能为空", Toast.LENGTH_SHORT).show();
        } else {

            myHandler = new MyHandler(LoginActivity.this);

            //正在登陆
            final CustomDialog dialog = new CustomDialog(this, R.style.CustomDialog);
            dialog.setText("正在登陆");
            dialog.show();


            loginUrl = UrlHelper.LOGIN_URL + tietUserEdit.getText().toString() + "&password=" + tietPasswordEdit.getText().toString();

            Log.d(TAG, "loginBtnClick: url :" + loginUrl);

            HttpHelper httpHelper = HttpHelper.getInstance(getApplicationContext());
            httpHelper.getRequest(loginUrl, null, HttpHelper.JSON_DATA_1, new NetCallBackResultBean<ResultCode>() {
                @Override
                public void onSuccess(ResultCode code) {
                    dialog.dismiss();
                    myHandler.sendEmptyMessage(Integer.parseInt(code.getCode()));
                }

                @Override
                public void onSuccess(List result) {
                    dialog.dismiss();
                }

                @Override
                public void Failed(String string) {
                    myHandler.sendEmptyMessage(5);
                    dialog.dismiss();
                }
            });

        }

    }

    private static class MyHandler extends Handler {
        private WeakReference<LoginActivity> weakReference;

        public MyHandler(LoginActivity loginActivity) {
            weakReference = new WeakReference<>(loginActivity);
        }

        @Override
        public void handleMessage(Message msg) {

            LoginActivity loginActivity = weakReference.get();
            if (loginActivity == null) {
                return;
            }

            switch (msg.what) {
                case 0:
                    Toast.makeText(loginActivity, "登录成功", Toast.LENGTH_SHORT).show();
                    /**
                     *   当用户可以实现登陆时候
                     *   就将未登陆界面的所有Activity都给移除TASK
                     *
                     */
                    ActivityController.finishAll();

                    /**
                     * 记录当前登陆用户的用户名
                     */
                    editor = loginActivity.getApplicationContext().getSharedPreferences("username" ,MODE_PRIVATE).edit() ;
                    editor.putString("username" , loginActivity.tietUserEdit.getText().toString()) ;
                    editor.commit();

                    //启动到用户界面
                    Intent intent = new Intent();
                    intent.setClass(loginActivity, MainActivity.class);
                    loginActivity.startActivity(intent);

                    break;

                case 2:
                    Toast.makeText(loginActivity, "用户名未找到", Toast.LENGTH_SHORT).show();
                    break;

                case 3:
                    Toast.makeText(loginActivity, "密码错误", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    Toast.makeText(loginActivity, "出现未知错误", Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    }
}
