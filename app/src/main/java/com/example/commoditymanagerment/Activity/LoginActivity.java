package com.example.commoditymanagerment.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.commoditymanagerment.DrawableView.CustomDialog;
import com.example.commoditymanagerment.DrawableView.DrawableTextEdit;
import com.example.commoditymanagerment.R;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

    }


    @OnClick({R.id.bt_loginBtn , R.id.tv_forgetPasswordTv , R.id.tv_registerUserTv})
    public void onViewClick(View view){
        switch (view.getId()){
            case R.id.bt_loginBtn :

                loginBtnClick() ;
                Toast.makeText(this , "登陆",Toast.LENGTH_SHORT).show();
                break ;


            case R.id.tv_registerUserTv :
                Intent intent = new Intent() ;
                intent.setClass(this , RegisterActivity.class) ;
                startActivity(intent);
                Toast.makeText(this , "注册",Toast.LENGTH_SHORT).show();
                break ;
        }
    }

    private void loginBtnClick(){

        if (TextUtils.isEmpty(tietUserEdit.getText()) || TextUtils.isEmpty(tietPasswordEdit.getText())) {
           Toast.makeText(this , "用户名胡总和密码不能为空",Toast.LENGTH_SHORT).show();
        } else {
            //正在登陆
            final CustomDialog dialog = new CustomDialog(this, R.style.CustomDialog);
            dialog.setText("正在登陆");
            dialog.show();
        }

    }
}
