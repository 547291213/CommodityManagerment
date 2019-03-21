package com.example.commoditymanagerment.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.commoditymanagerment.Bean.ResultCode;
import com.example.commoditymanagerment.Bean.User;
import com.example.commoditymanagerment.NetWork.HttpHelper;
import com.example.commoditymanagerment.NetWork.NetCallBackResultBean;
import com.example.commoditymanagerment.R;
import com.example.commoditymanagerment.Util.DensityUtil;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.commoditymanagerment.Util.UrlHelper.USER_UPDATE_DATA_URL;

public class ModifyPasswordActivity extends BaseActivity {

    @BindView(R.id.tv_setBackText)
    TextView tvSetBackText;
    @BindView(R.id.ll_titleLayout)
    LinearLayout llTitleLayout;
    @BindView(R.id.ll_headerLayout)
    LinearLayout llHeaderLayout;
    @BindView(R.id.ll_InputLayoutOrinPaw)
    LinearLayout llInputLayoutOrinPaw;
    @BindView(R.id.input_layout_psw)
    LinearLayout inputLayoutPsw;
    @BindView(R.id.ll_inputLayoutRepsw)
    LinearLayout llInputLayoutRepsw;
    @BindView(R.id.main_btn_login)
    TextView mainBtnLogin;

    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);
        ButterKnife.bind(this);
        user = (User) getIntent().getSerializableExtra("user");
        if (user == null) {
            Toast.makeText(this, "获取用户信息失败", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "成功", Toast.LENGTH_SHORT).show();
        }

        initIndexTitleLayout();

    }


    /**
     * 设置顶部标题栏相关属性
     */
    private void initIndexTitleLayout() {
        //沉浸式状态栏
        DensityUtil.fullScreen(this);
//        设置内边距
//        其中left right bottom都用现有的
//        top设置为现在的topPadding+状态栏的高度
//        表现为将indexTitleLayout显示的数据放到状态栏下面
        llTitleLayout.setPadding(llTitleLayout.getPaddingLeft(), llTitleLayout.getPaddingTop() + DensityUtil.getStatusHeight(this),
                llTitleLayout.getPaddingRight(), llTitleLayout.getPaddingBottom());
    }


    @OnClick({R.id.main_btn_login, R.id.tv_setBackText})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.main_btn_login:

                modiftyPassword();
                break;

            case R.id.tv_setBackText:
                finish();
                break;
        }
    }


    private String updateUserUrl;
    private MyHandler myHandler ;
    private void modiftyPassword() {

        /**
         *  1 判空
         */
        if (TextUtils.isEmpty(((EditText) llInputLayoutOrinPaw.getChildAt(1)).getText().toString()) ||
                TextUtils.isEmpty(((EditText) inputLayoutPsw.getChildAt(1)).getText().toString()) ||
                TextUtils.isEmpty(((EditText) llInputLayoutRepsw.getChildAt(1)).getText().toString())) {

            Toast.makeText(this, "输入不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        /**
         * 2 判断两次输入的密码是否相同
         */
        else if (!((EditText) inputLayoutPsw.getChildAt(1)).getText().toString()
                .equals(((EditText) llInputLayoutRepsw.getChildAt(1)).getText().toString())) {
            Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }
        /**
         * 检测原密码是否正确
         */
        else if (!((EditText) llInputLayoutOrinPaw.getChildAt(1)).getText().toString().equals(user.getPassword())){
            Toast.makeText(this, "初始密码输入错误", Toast.LENGTH_SHORT).show();
            return ;
        }
        /**
         * 4 修改密码处理
         */
        else {
            myHandler = new MyHandler(this) ;
            updateUserUrl = USER_UPDATE_DATA_URL + user.getUserId() +
                    "&userName=" + user.getUserName() +
                    "&permissions=" + user.getPermissions() +
                    "&password=" + ((EditText) inputLayoutPsw.getChildAt(1)).getText().toString() +
                    "&nickName=" + user.getNickName();
            HttpHelper httpHelper = HttpHelper.getInstance(getApplicationContext());
            httpHelper.getRequest(updateUserUrl, null, HttpHelper.JSON_DATA_1, new NetCallBackResultBean<ResultCode>() {
                @Override
                public void onSuccess(ResultCode resultCode) {
                    myHandler.sendEmptyMessage(0) ;
                }

                @Override
                public void onSuccess(List result) {
                    myHandler.sendEmptyMessage(1) ;

                }

                @Override
                public void Failed(String string) {
                    myHandler.sendEmptyMessage(1) ;

                }
            });
        }
    }

    private static class MyHandler extends Handler {
        private WeakReference<ModifyPasswordActivity> weakReference;

        public MyHandler(ModifyPasswordActivity modifyPasswordActivity) {
            weakReference = new WeakReference<>(modifyPasswordActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            ModifyPasswordActivity activity = weakReference.get();
            if (activity == null) {
                return;
            }

            switch (msg.what) {
                case 0:
                    Toast.makeText(activity, "修改密码成功", Toast.LENGTH_SHORT).show();
                    activity.finish();
                    break;

                case 1:
                    Toast.makeText(activity, "修改密码失败", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    Toast.makeText(activity, "未知请求", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
