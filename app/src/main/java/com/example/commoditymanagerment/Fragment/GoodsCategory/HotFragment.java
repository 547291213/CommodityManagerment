package com.example.commoditymanagerment.Fragment.GoodsCategory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.commoditymanagerment.Activity.GoodsDescribeActivity;
import com.example.commoditymanagerment.Activity.MainActivity;
import com.example.commoditymanagerment.Bean.Goods;
import com.example.commoditymanagerment.Bean.GoodsGrid;
import com.example.commoditymanagerment.Bean.RefreshMsg;
import com.example.commoditymanagerment.DrawableView.GoodsAdapter;
import com.example.commoditymanagerment.DrawableView.GoodsListView;
import com.example.commoditymanagerment.NetWork.HttpHelper;
import com.example.commoditymanagerment.NetWork.NetCallBackResultBean;
import com.example.commoditymanagerment.R;
import com.example.commoditymanagerment.RxBus.RxBus;
import com.example.commoditymanagerment.Util.UrlHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.example.commoditymanagerment.Util.DataService.ROW_COUNT;
import static com.example.commoditymanagerment.Util.StaticDataUtil.CATEGORY_HOT;
import static com.example.commoditymanagerment.Util.StaticDataUtil.GOODS_DESCRIBE_ACTIVITY_REQUEST_CODE;
import static com.example.commoditymanagerment.Util.StaticDataUtil.GOODS_ID;
import static java.lang.Thread.sleep;

public class HotFragment extends CategoryFragment {

    @BindView(R.id.glv_hotGoodsList)
    GoodsListView glvHotGoodsList;

    Unbinder unbinder;
    @BindView(R.id.srl_refrshLayout)
    SwipeRefreshLayout srlRefrshLayout;
    private View mView;
    private Context mContext;
    private Activity mActivity;

    //商品数据列表
    private List<Goods> goodsList;

    //服务器返回JSON类
    private GoodsGrid goodsGrid;

    //商品适配器
    private GoodsAdapter adapter;

    //当前页数
    private int nextPage = 1;

    //当前总类商品的总个数
    private int totalCount = 0;

    //当前商品种类
    private static final int HOT_CATEGORY = 0;

    private static final String TAG = "HotFragment";

    //网络请求地址
    private String goodsListUrl;


    //将网络请求完成后的界面填充放置在主线程
    private MyHandler myHandler = new MyHandler(this);

    //当前数据是否加载完成
    private boolean loadFinish = false;

    //设置ListView的滑动状态。默认为静止
//    private int scrollerState = SCROLL_STATE_IDLE;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        mView = inflater.inflate(R.layout.fg_category_hot, container, false);
        mContext = getContext();
        mActivity = getActivity();
        unbinder = ButterKnife.bind(this, mView);

        //创建的碎片的时候执行以此初始化操作
        initData();

        //设置为不能刷新
        srlRefrshLayout.setEnabled(false);
        //设置刷新旋转样式
        srlRefrshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);

        return mView;
    }


    private void initData() {

        //初始化列表数据
        goodsList = new ArrayList<>();

        glvHotGoodsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GoodsListView goodsListView = (GoodsListView) parent;
                Goods goods = (Goods) goodsListView.getItemAtPosition(position);
                Intent intent = new Intent();
                intent.putExtra(GOODS_ID, (Integer) goods.getGoodsId());
                intent.setClass(mContext, GoodsDescribeActivity.class);
                mActivity.startActivityForResult(intent, GOODS_DESCRIBE_ACTIVITY_REQUEST_CODE);
            }
        });

        glvHotGoodsList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int state) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                //得到listView的最后一项的显示id
                int lastItemPosition = glvHotGoodsList.getLastVisiblePosition();
                //判断用户是否滑动到最后一项，因为索引值从零开始所以要加上1
                if ((lastItemPosition + 1) == totalItemCount && loadFinish == true) {

                    Log.d(TAG, "onScroll: " + lastItemPosition + "   " + totalItemCount + "   "
                            + totalCount + "  nextPage=" + nextPage);
                    //设置状态为 数据尚在加载

                    loadFinish = false;
                    //如果数据尚未加载完成
                    //访问网络请求成功之后，nextPage会自增，
                    //实际nextPage指向的是下一页，所以这里需要-1
                    if ((nextPage - 1) * Integer.valueOf(ROW_COUNT) < totalCount) {
                        /**
                         * 如果是由主界面发起的数据刷新，则不显示footerView
                         */
                        if (refreshDataByActivity != true){
                            glvHotGoodsList.setFooterViewShow();
                        }
                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    sleep(2000);
                                    getData(HOT_CATEGORY, nextPage);

                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }

                }
            }
        });

        getData(HOT_CATEGORY, nextPage);
    }


    /**
     * okHttp 网络访问是异步进行，在子线程中实现，
     * 如果这里直接返回列表数据，返回为null
     *
     * @param goodsCategory 商品种类
     * @param current       当前页面
     * @return
     */
    public void getData(final int goodsCategory, int current) {

        goodsListUrl = UrlHelper.GOODS_LIST_URL + goodsCategory + "&current=" + current + "&rowCount=" + ROW_COUNT;
        HttpHelper httpHelper = HttpHelper.getInstance(mContext.getApplicationContext());
        httpHelper.getRequest(goodsListUrl, null, HttpHelper.JSON_DATA_1,
                new NetCallBackResultBean<GoodsGrid>() {
                    @Override
                    public void onSuccess(GoodsGrid goods) {
                        goodsGrid = goods;
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

        private WeakReference<HotFragment> weakReference;

        public MyHandler(HotFragment hotFragment) {
            weakReference = new WeakReference<>(hotFragment);

        }

        @Override
        public void handleMessage(Message msg) {
            HotFragment fragment = weakReference.get();
            if (fragment == null) {
                return;
            }

            switch (msg.what) {
                case 0:
                    /**
                     * 列表界面的显示
                     */
                    for (int i = 0; i < fragment.goodsGrid.getRows().size(); i++) {
                        Goods goods = fragment.goodsGrid.getRows().get(i);
                        fragment.goodsList.add(goods);
                    }

                    //初始化和设置适配器
                    fragment.adapter = new GoodsAdapter(fragment.goodsList, fragment.getContext());
                    fragment.glvHotGoodsList.setAdapter(fragment.adapter);

                    //隐藏底部加载栏,并将选中当前页的第一项数据
                    fragment.glvHotGoodsList.setFooterViewHide((fragment.nextPage - 1) * Integer.valueOf(ROW_COUNT) + 1);

                    //当前所请求界面+1
                    fragment.nextPage++;

                    //更新当前总类商品的总个数
                    fragment.totalCount = fragment.goodsGrid.getTotal();

                    //数据加载完成
                    fragment.loadFinish = true;

                    if (fragment.refreshDataByActivity) {
                        //设为默认值
                        fragment.refreshDataByActivity = false;
                        //设置状态为刷新完成
                        fragment.srlRefrshLayout.setRefreshing(false);
                        //发送广播通知刷新
                        RxBus.getInstance().post(new RefreshMsg(HOT_CATEGORY));
                        Toast.makeText(fragment.mContext, "刷新成功", Toast.LENGTH_SHORT).show();
                    }
                    break;

                default:
                    Toast.makeText(fragment.mContext, "获取数据失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        //数据初始化
        if (((MainActivity) getActivity()).getNeedToReloadData()) {
            nextPage = 1;
            /**
             * 将原有数据清空
             */
            if (goodsList != null) {
                goodsList.clear();
            }
            adapter.notifyDataSetChanged();
//            initData();
        } else {

        }
    }


    private boolean refreshDataByActivity = false;

    @Override
    public void onRefreshData() {
        refreshDataByActivity = true;
        refreshData();
    }

    public void refreshData() {
        srlRefrshLayout.setRefreshing(true);
        //数据初始化
        nextPage = 1;
        /**
         * 将原有数据清空
         */
        if (goodsList != null) {
            goodsList.clear();
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        if (glvHotGoodsList != null) {
            glvHotGoodsList.setFooterViewHide(0);
        }
//        getData(HOT_CATEGORY, nextPage);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
