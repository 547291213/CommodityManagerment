package com.example.commoditymanagerment.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.commoditymanagerment.R;
import com.example.commoditymanagerment.Util.DensityUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HomepageFragment extends Fragment {

    @BindView(R.id.tv_setBackText)
    TextView tvSetBackText;
    @BindView(R.id.ll_titleLayout)
    LinearLayout llTitleLayout;
    @BindView(R.id.et_searchEdit)
    TextView etSearchEdit;
    @BindView(R.id.tv_messageEmptyView)
    TextView tvMessageEmptyView;
    @BindView(R.id.srl_messageRefreshLayout)
    SwipeRefreshLayout srlMessageRefreshLayout;
    Unbinder unbinder;
    private View view;
    private Context mContext;
    private Activity mActivity ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fg_homepage, container, false);
        unbinder = ButterKnife.bind(this, view);

        mContext = getContext() ;
        mActivity = getActivity() ;
        initIndexTitleLayout();

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
