package com.example.commoditymanagerment.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.commoditymanagerment.Activity.LoginActivity;
import com.example.commoditymanagerment.Activity.ModifyPasswordActivity;
import com.example.commoditymanagerment.Bean.ResultCode;
import com.example.commoditymanagerment.Bean.User;
import com.example.commoditymanagerment.DrawableView.BottomDialog;
import com.example.commoditymanagerment.NetWork.HttpHelper;
import com.example.commoditymanagerment.NetWork.NetCallBackResultBean;
import com.example.commoditymanagerment.R;
import com.example.commoditymanagerment.Util.ActivityController;
import com.example.commoditymanagerment.Util.DensityUtil;
import com.example.commoditymanagerment.Util.UserPermissionExchange;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.example.commoditymanagerment.Util.StaticDataUtil.USER_NAME;
import static com.example.commoditymanagerment.Util.UrlHelper.USER_INFO_URL;
import static com.example.commoditymanagerment.Util.UrlHelper.USER_UPDATE_DATA_URL;

public class PersonalCenterFragment extends Fragment {

    @BindView(R.id.tv_setBackText)
    TextView tvSetBackText;
    @BindView(R.id.ll_titleLayout)
    LinearLayout llTitleLayout;
    @BindView(R.id.view_divider1)
    View viewDivider1;
    @BindView(R.id.et_userNameEt)
    EditText etUserNameEt;
    @BindView(R.id.ll_userNameLayout)
    LinearLayout llUserNameLayout;
    @BindView(R.id.view_divider2)
    View viewDivider2;
    @BindView(R.id.et_userNickNameEt)
    EditText etUserNickNameEt;
    @BindView(R.id.ll_userNickNameLayout)
    LinearLayout llUserNickNameLayout;
    @BindView(R.id.view_divider3)
    View viewDivider3;
    @BindView(R.id.et_userPermissionEt)
    EditText etUserPermissionEt;
    @BindView(R.id.ll_userPermissionLayout)
    LinearLayout llUserPermissionLayout;
    @BindView(R.id.view_divider4)
    View viewDivider4;
    @BindView(R.id.ll_modiifyNickNameLayout)
    LinearLayout llModiifyNickNameLayout;
    @BindView(R.id.view_divider5)
    View viewDivider5;
    @BindView(R.id.ll_modiifyPasswordLayout)
    LinearLayout llModiifyPasswordLayout;
    @BindView(R.id.view_divider6)
    View viewDivider6;
    @BindView(R.id.ll_userLogoutLayout)
    LinearLayout llUserLogoutLayout;
    Unbinder unbinder;


    private View view;
    private Context mContext;
    private Activity mActivity;

    private String updateUserDataUrl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fg_personalcenter, container, false);
        mContext = getContext();
        mActivity = getActivity();
        unbinder = ButterKnife.bind(this, view);

        initIndexTitleLayout();
        initUserData();
        return view;
    }


    /**
     * 设置顶部标题栏相关属性
     */
    private void initIndexTitleLayout() {
        //沉浸式状态栏
        DensityUtil.fullScreen(mActivity);
//        设置内边距
//        其中left right bottom都用现有的
//        top设置为现在的topPadding+状态栏的高度
//        表现为将indexTitleLayout显示的数据放到状态栏下面
        llTitleLayout.setPadding(llTitleLayout.getPaddingLeft(), llTitleLayout.getPaddingTop() + DensityUtil.getStatusHeight(mContext),
                llTitleLayout.getPaddingRight(), llTitleLayout.getPaddingBottom());
    }


    private User user;
    private String userInfoUrl;
    private SharedPreferences preferences;
    private MyHandler myHandler;

    private void initUserData() {
        myHandler = new MyHandler(this);
        preferences = mActivity.getSharedPreferences(USER_NAME, Context.MODE_PRIVATE);
        userInfoUrl = USER_INFO_URL + preferences.getString(USER_NAME, "");
        HttpHelper httpHelper = HttpHelper.getInstance(getContext().getApplicationContext());
        httpHelper.getRequest(userInfoUrl, null, HttpHelper.JSON_DATA_1, new NetCallBackResultBean<User>() {
            @Override
            public void onSuccess(User resultCode) {
                user = resultCode;
                myHandler.sendEmptyMessage(0);

            }

            @Override
            public void onSuccess(List result) {
                myHandler.sendEmptyMessage(1);
            }

            @Override
            public void Failed(String string) {
                myHandler.sendEmptyMessage(1);

            }
        });
    }


    private static class MyHandler extends Handler {

        private WeakReference<PersonalCenterFragment> weakReference;

        public MyHandler(PersonalCenterFragment personalCenterFragment) {
            weakReference = new WeakReference<>(personalCenterFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            PersonalCenterFragment fragment = weakReference.get();
            if (fragment == null) {
                return;
            }

            switch (msg.what) {
                case 0:
                    fragment.etUserNameEt.setText(fragment.user.getUserName());
                    fragment.etUserNickNameEt.setText(fragment.user.getNickName());
                    fragment.etUserPermissionEt.setText(UserPermissionExchange.exChange(fragment.user.getPermissions()));
                    break;

                case 1:
                    Toast.makeText(fragment.mContext, "获取用户信息失败", Toast.LENGTH_SHORT).show();
                    break;

                case 2:
                    Toast.makeText(fragment.mContext, "修改用户昵称成功", Toast.LENGTH_SHORT).show();
                    fragment.user.setNickName(msg.obj.toString());
                    fragment.etUserNickNameEt.setText(msg.obj.toString());
                    break;

                case 3:
                    Toast.makeText(fragment.mContext, "修改用户昵称失败", Toast.LENGTH_SHORT).show();

                    break;

                default:
                    Toast.makeText(fragment.mContext, "未知请求", Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    }

    @OnClick({R.id.ll_modiifyNickNameLayout, R.id.ll_modiifyPasswordLayout, R.id.ll_userLogoutLayout})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.ll_modiifyNickNameLayout:
                alertNickNameEdit();
                break;

            case R.id.ll_modiifyPasswordLayout:
                modifyPassword();
                break;

            case R.id.ll_userLogoutLayout:
                logout();
                break;
        }
    }


    /**
     * 修改用户昵称
     */
    public void alertNickNameEdit() {
        final EditText et = new EditText(mContext);
        new AlertDialog.Builder(mContext).setTitle("请输入昵称")
                .setIcon(android.R.drawable.sym_def_app_icon)
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //按下确定键后的事件
//                        Toast.makeText(mContext.getApplicationContext(), et.getText().toString(), Toast.LENGTH_LONG).show();
                        updateUserDataUrl = USER_UPDATE_DATA_URL + user.getUserId() +
                                "&userName=" + user.getUserName() +
                                "&permissions=" + user.getPermissions() +
                                "&password=" + user.getPassword() +
                                "&nickName=" + et.getText().toString();
                        HttpHelper httpHelper = HttpHelper.getInstance(getContext().getApplicationContext());
                        httpHelper.getRequest(updateUserDataUrl, null, HttpHelper.JSON_DATA_1, new NetCallBackResultBean<ResultCode>() {
                            @Override
                            public void onSuccess(ResultCode resultCode) {
                                if (resultCode.getCode().equals("0")) {
                                    Message message = Message.obtain();
                                    message.obj = et.getText().toString();
                                    message.what = 2;
                                    myHandler.sendMessage(message);
                                } else {
                                    myHandler.sendEmptyMessage(3);

                                }
                            }

                            @Override
                            public void onSuccess(List result) {
                                myHandler.sendEmptyMessage(3);

                            }

                            @Override
                            public void Failed(String string) {
                                myHandler.sendEmptyMessage(3);
                            }
                        });
                    }

                }).setNegativeButton("取消", null).show();
    }


    private void modifyPassword(){

        final String item1 = "";
        final String item2 = "确定修改";
        final String item3 = "取消";
        final BottomDialog dialog = new BottomDialog(mContext, item1, item2, item3);
        dialog.setBackground(Color.WHITE);
        dialog.setItem1TextColor(1 , Color.BLACK);
        dialog.setItem1TextColor(2 , Color.BLACK);
        dialog.setItem1TextColor(3 , Color.BLACK);
        dialog.setItemClickListener(new BottomDialog.ItemClickListener() {
            @Override
            public void onItem1Click(View view) {
                dialog.dismiss();
            }

            @Override
            public void onItem2Click(View view) {
                //关闭弹出窗口
                dialog.dismiss();
                //转换到修改密码界面
                Intent intent = new Intent() ;
                intent.setClass(mContext , ModifyPasswordActivity.class) ;
                intent.putExtra("user" , user) ;
                startActivity(intent);
            }

            @Override
            public void onItem3Click(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * 退出当前账号
     */
    private void logout(){
        final String item1 = "";
        final String item2 = "确定退出";
        final String item3 = "取消";
        final BottomDialog dialog = new BottomDialog(mContext, item1, item2, item3);
        dialog.setItemClickListener(new BottomDialog.ItemClickListener() {
            @Override
            public void onItem1Click(View view) {
                dialog.dismiss();
            }

            @Override
            public void onItem2Click(View view) {

                //将当前所有Activity退栈
                ActivityController.finishAll();

                //转换到登陆界面
                startActivity(new Intent(mContext, LoginActivity.class));

                //关闭弹出窗口
                dialog.dismiss();
            }

            @Override
            public void onItem3Click(View view) {

                dialog.dismiss();
            }
        });
        dialog.show();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
