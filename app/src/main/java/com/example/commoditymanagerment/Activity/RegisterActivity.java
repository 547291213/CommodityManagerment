package com.example.commoditymanagerment.Activity;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.commoditymanagerment.DrawableView.DrawableTextEdit;
import com.example.commoditymanagerment.R;

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
                Toast.makeText(this, "点击了注册按钮", Toast.LENGTH_SHORT).show();
                if (!tilPasswrod.getEditText().getText().toString().
                        equals(tilRePasswrod.getEditText().getText().toString())) {
                    Toast.makeText(this,"请检查输入的密码的合法性",Toast.LENGTH_SHORT).show();
                } else {

                    //对本地服务器进行访问。
                }

                break;


        }
    }


}
