package com.example.commoditymanagerment.Fragment.GoodsCategory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.commoditymanagerment.Activity.GoodsDescribeActivity;
import com.example.commoditymanagerment.Bean.Goods;
import com.example.commoditymanagerment.Bean.GoodsGrid;
import com.example.commoditymanagerment.DrawableView.GoodsListView;
import com.example.commoditymanagerment.NetWork.HttpHelper;
import com.example.commoditymanagerment.NetWork.NetCallBackResultBean;
import com.example.commoditymanagerment.R;
import com.example.commoditymanagerment.Util.DataService;
import com.example.commoditymanagerment.Util.UrlHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.example.commoditymanagerment.Util.DataService.ROW_COUNT;
import static com.example.commoditymanagerment.Util.StaticDataUtil.GOODS_ID;

public class HotFragment extends Fragment {

    @BindView(R.id.glv_hotGoodsList)
    GoodsListView glvHotGoodsList;

    Unbinder unbinder;
    private View mView;
    private Context mContext;
    private Activity mActivity;

    //商品数据列表
    private List<Goods> goodsList;

    //服务器返回JSON类
    private GoodsGrid goodsGrid;

    //商品适配器
    private SimpleAdapter adapter;

    //商品数据容器
    private List<Map<String, Object>> goodsDataList;

    //当前页数
    private int nextPage = 1;

    //当前总类商品的总个数
    private int totalCount = 0;

    //当前商品种类
    private static final int HOT_CATEGORY = 0;

    private static final String TAG = "HotFragment";

    //网络请求地址
    private String url;

    //将网络请求完成后的界面填充放置在主线程
    private MyHanlder myHanlder = new MyHanlder(this);

    //当前数据是否加载完成
    private boolean loadFinish = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        mView = inflater.inflate(R.layout.fg_category_hot, container, false);
        mContext = getContext();
        mActivity = getActivity();
        unbinder = ButterKnife.bind(this, mView);
        return mView;
    }

    private void initData() {
        goodsDataList = new ArrayList<>();
        adapter = new SimpleAdapter(mContext, goodsDataList, R.layout.item_goods,
                new String[]{"goodsImg", "goodsName", "goodsCount", "lastModifyTime"},
                new int[]{R.id.siv_goodsImg, R.id.tv_goodsName, R.id.tv_goodsCount, R.id.tv_goodsTime});

        glvHotGoodsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GoodsListView goodsListView = (GoodsListView) parent;
                HashMap<String, Object> map = (HashMap<String, Object>) goodsListView.getItemAtPosition(position);
//                Log.d(TAG, "onItemClick: click :" + position + " id is " + map.get("goodsId"));
                Intent intent = new Intent();
                intent.putExtra(GOODS_ID, (Integer) map.get(GOODS_ID));
                intent.setClass(mContext, GoodsDescribeActivity.class);
                startActivity(intent);
            }
        });

        glvHotGoodsList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

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
                        glvHotGoodsList.setFooterViewShow();
                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    Thread.sleep(2000);
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
     * @param goodsCategory
     * @param current
     * @return
     */
    public void getData(final int goodsCategory, int current) {

        url = UrlHelper.GOODS_LIST_URL + goodsCategory + "&current=" + current + "&rowCount=" + ROW_COUNT;
        HttpHelper httpHelper = HttpHelper.getInstance(mContext.getApplicationContext());
        httpHelper.getRequest(url, null, HttpHelper.JSON_DATA_1,
                new NetCallBackResultBean<GoodsGrid>() {
                    @Override
                    public void onSuccess(GoodsGrid goods) {
                        goodsGrid = goods;
                        myHanlder.sendEmptyMessage(0);

                    }

                    @Override
                    public void onSuccess(List result) {
                        myHanlder.sendEmptyMessage(1);
                    }

                    @Override
                    public void Failed(String string) {
                        myHanlder.sendEmptyMessage(1);
                    }
                });
    }

    private static class MyHanlder extends Handler {

        private WeakReference<HotFragment> weakReference;

        public MyHanlder(HotFragment hotFragment) {
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
                        Map<String, Object> map = new HashMap<>();
                        map.put("goodsId", fragment.goodsGrid.getRows().get(i).getGoodsId());
                        map.put("goodsImg", fragment.goodsGrid.getRows().get(i).getGoodsImg());
                        map.put("goodsName", fragment.goodsGrid.getRows().get(i).getGoodsName());
                        map.put("goodsCount", fragment.goodsGrid.getRows().get(i).getGoodsCount());
                        map.put("lastModifyTime", fragment.goodsGrid.getRows().get(i).getLastModifyTime());
                        fragment.goodsDataList.add(map);
                    }

//                    fragment.adapter = new SimpleAdapter(fragment.mContext, fragment.goodsDataList, R.layout.item_goods,
//                            new String[]{"goodsImg", "goodsName", "goodsCount", "lastModifyTime"},
//                            new int[]{R.id.siv_goodsImg, R.id.tv_goodsName, R.id.tv_goodsCount, R.id.tv_goodsTime});
                    fragment.glvHotGoodsList.setAdapter(fragment.adapter);
//                    fragment.adapter.notifyDataSetChanged();

                    //隐藏底部加载栏,并将选中当前页的第一项数据
                    fragment.glvHotGoodsList.setFooterViewHide((fragment.nextPage - 1) * Integer.valueOf(ROW_COUNT) + 1);

                    //当前所请求界面+1
                    fragment.nextPage++;

                    //更新当前总类商品的总个数
                    fragment.totalCount = fragment.goodsGrid.getTotal();


                    //数据加载完成
                    fragment.loadFinish = true;
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
        nextPage =1 ;

        /**
         * 将原有数据清空
         */
        if (goodsDataList != null){
            goodsDataList.clear();
        }
        initData();

        Log.d(TAG, "onStart: ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
