package com.example.commoditymanagerment.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.commoditymanagerment.Activity.AddGoodsActivity;
import com.example.commoditymanagerment.Activity.SearchActivity;
import com.example.commoditymanagerment.Bean.ResultCode;
import com.example.commoditymanagerment.Bean.User;
import com.example.commoditymanagerment.DrawableView.InnerViewPager;
import com.example.commoditymanagerment.DrawableView.TopTabFragmentAdapter;
import com.example.commoditymanagerment.Fragment.GoodsCategory.ActivityFragment;
import com.example.commoditymanagerment.Fragment.GoodsCategory.HotFragment;
import com.example.commoditymanagerment.Fragment.GoodsCategory.NewProductFragment;
import com.example.commoditymanagerment.Fragment.GoodsCategory.OutOfStockFragment;
import com.example.commoditymanagerment.Fragment.GoodsCategory.PromotionFragment;
import com.example.commoditymanagerment.NetWork.HttpHelper;
import com.example.commoditymanagerment.NetWork.NetCallBackResultBean;
import com.example.commoditymanagerment.R;
import com.example.commoditymanagerment.Util.DensityUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.example.commoditymanagerment.Util.StaticDataUtil.ADD_GOODS_ACTIVITY_REQUEST_CODE;
import static com.example.commoditymanagerment.Util.StaticDataUtil.USER_NAME;
import static com.example.commoditymanagerment.Util.UrlHelper.USER_INFO_URL;

public class HomepageFragment extends Fragment {

    @BindView(R.id.tv_setBackText)
    TextView tvSetBackText;
    @BindView(R.id.ll_titleLayout)
    LinearLayout llTitleLayout;
    @BindView(R.id.et_searchEdit)
    TextView etSearchEdit;
    @BindView(R.id.toolbar_tab)
    TabLayout toolbarTab;
    @BindView(R.id.view_pager)
    InnerViewPager viewPager;

    Unbinder unbinder;
    @BindView(R.id.tv_addGoodsTv)
    TextView tvAddGoodsTv;

    private View view;
    private Context mContext;
    private Activity mActivity;
    private DisplayMetrics metrics;

    /**
     * 商品列表数据碎片
     */
    private List<Fragment> fragmentList;
    private ActivityFragment activityFragment;
    private NewProductFragment newProductFragment;
    private HotFragment hotFragment;
    private OutOfStockFragment outOfStockFragment;
    private PromotionFragment promotionFragment;
    private FragmentManager fragmentManager;
    private TopTabFragmentAdapter fragmentAdapter;

    private List<String> titles;

    private int vpPosition = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fg_homepage, container, false);
        unbinder = ButterKnife.bind(this, view);

        mContext = getContext();
        mActivity = getActivity();
        metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        initIndexTitleLayout();
        setEtSearchEdit();
        initViewPager();
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


    /**
     * 设置搜索栏相关属性
     * 主要是设置Drawabel的具体，靠文字左边的显示
     */
    private void setEtSearchEdit() {
        Drawable left = getResources().getDrawable(R.drawable.ic_search_black_32);
        left.setBounds(DensityUtil.dip2px(mContext, 10), 0,
                50 + DensityUtil.dip2px(mContext, 10), 30);
        etSearchEdit.setCompoundDrawablePadding(-left.getIntrinsicWidth() / 2 + 5);
        etSearchEdit.setCompoundDrawables(left, null, null, null);

    }

    private void initViewPager() {

        titles = new ArrayList<>();
        titles.add(mContext.getResources().getString(R.string.category_hot));
        titles.add(mContext.getResources().getString(R.string.category_new_product));
        titles.add(mContext.getResources().getString(R.string.category_promotion));
        titles.add(mContext.getResources().getString(R.string.category_activity));
        titles.add(mContext.getResources().getString(R.string.category_out_of_stock));

        for (int i = 0; i < titles.size(); i++) {
            toolbarTab.addTab(toolbarTab.newTab().setText(titles.get(i)));
        }

        fragmentList = new ArrayList<>();
        hotFragment = new HotFragment();
        activityFragment = new ActivityFragment();
        newProductFragment = new NewProductFragment();
        outOfStockFragment = new OutOfStockFragment();
        promotionFragment = new PromotionFragment();

        fragmentList.add(hotFragment);
        fragmentList.add(newProductFragment);
        fragmentList.add(promotionFragment);
        fragmentList.add(activityFragment);
        fragmentList.add(outOfStockFragment);

        fragmentManager = getChildFragmentManager();

        fragmentAdapter = new TopTabFragmentAdapter(fragmentManager, fragmentList, titles);


        viewPager.setAdapter(fragmentAdapter);
        toolbarTab.setupWithViewPager(viewPager);
        toolbarTab.setTabsFromPagerAdapter(fragmentAdapter);
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(4);

    }


    @OnClick({R.id.tv_addGoodsTv, R.id.et_searchEdit})
    public void onViewCLick(View view) {
        switch (view.getId()) {
            case R.id.tv_addGoodsTv:
                //获取用户权限
                getUserPermissions();
                break;

            case R.id.et_searchEdit:
                Intent intent1 = new Intent();
                intent1.setClass(mContext, SearchActivity.class);
                startActivity(intent1);
                break;
        }

    }

    private User user;
    private String userInfoUrl;
    private SharedPreferences preferences;
    private MyHandler myHandler;

    private void getUserPermissions() {

        preferences = mContext.getSharedPreferences(USER_NAME, Context.MODE_PRIVATE);
        userInfoUrl = USER_INFO_URL + preferences.getString(USER_NAME, "");
        myHandler = new MyHandler(this);
        HttpHelper httpHelper = HttpHelper.getInstance(getContext().getApplicationContext());
        httpHelper.getRequest(userInfoUrl, null, HttpHelper.JSON_DATA_1, new NetCallBackResultBean<User>() {
            @Override
            public void onSuccess(User users) {
                user = users;
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

        private WeakReference<HomepageFragment> weakReference;

        public MyHandler(HomepageFragment fragment) {
            weakReference = new WeakReference<HomepageFragment>(fragment);

        }

        @Override
        public void handleMessage(Message msg) {
            HomepageFragment fragment = weakReference.get();
            if (fragment == null) {
                return;
            }
            switch (msg.what) {

                case 0:
                    if (fragment.user.getPermissions() >= 1) {
                        Intent intent = new Intent();
                        intent.setClass(fragment.mContext, AddGoodsActivity.class);
                        fragment.mActivity.startActivityForResult(intent , ADD_GOODS_ACTIVITY_REQUEST_CODE);
                    } else {
                        Toast.makeText(fragment.mContext, "抱歉您所在的用户组没有该权限", Toast.LENGTH_SHORT).show();
                    }
                    break;


                case 1:
                    Toast.makeText(fragment.mContext, "获取用户出错", Toast.LENGTH_SHORT).show();
                    break;

                default:

                    Toast.makeText(fragment.mContext, "未知请求", Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
