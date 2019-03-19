package com.example.commoditymanagerment.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

import com.example.commoditymanagerment.Activity.AddGoodsActivity;
import com.example.commoditymanagerment.Activity.SearchActivity;
import com.example.commoditymanagerment.DrawableView.InnerViewPager;
import com.example.commoditymanagerment.DrawableView.TopTabFragmentAdapter;
import com.example.commoditymanagerment.Fragment.GoodsCategory.ActivityFragment;
import com.example.commoditymanagerment.Fragment.GoodsCategory.HotFragment;
import com.example.commoditymanagerment.Fragment.GoodsCategory.NewProductFragment;
import com.example.commoditymanagerment.Fragment.GoodsCategory.OutOfStockFragment;
import com.example.commoditymanagerment.Fragment.GoodsCategory.PromotionFragment;
import com.example.commoditymanagerment.R;
import com.example.commoditymanagerment.Util.DensityUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

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
                Intent intent = new Intent();
                intent.setClass(mContext, AddGoodsActivity.class);
                startActivity(intent);
                break;

            case R.id.et_searchEdit:
                Intent intent1 = new Intent() ;
                intent1.setClass(mContext , SearchActivity.class) ;
                startActivity(intent1);
                break;
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
