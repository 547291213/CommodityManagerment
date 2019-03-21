package com.example.commoditymanagerment.Activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.commoditymanagerment.Bean.Goods;
import com.example.commoditymanagerment.Bean.GoodsGrid;
import com.example.commoditymanagerment.DrawableView.GoodsAdapter;
import com.example.commoditymanagerment.DrawableView.GoodsListView;
import com.example.commoditymanagerment.DrawableView.QuickAdapter;
import com.example.commoditymanagerment.NetWork.HttpHelper;
import com.example.commoditymanagerment.NetWork.NetCallBackResultBean;
import com.example.commoditymanagerment.R;
import com.example.commoditymanagerment.SqlHelper.RecordSQLDao;
import com.example.commoditymanagerment.Util.UrlHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.commoditymanagerment.Util.DataService.ROW_COUNT;
import static com.example.commoditymanagerment.Util.StaticDataUtil.GOODS_DESCRIBE_ACTIVITY_REQUEST_CODE;
import static com.example.commoditymanagerment.Util.StaticDataUtil.GOODS_ID;
import static com.example.commoditymanagerment.Util.StaticDataUtil.USER_NAME;
import static com.example.commoditymanagerment.Util.UrlHelper.GOODS_SEARCH_LIST_URL;

public class SearchActivity extends BaseActivity {

    @BindView(R.id.bt_searchBtn)
    Button btSearchBtn;
    @BindView(R.id.et_searchEdit)
    EditText etSearchEdit;
    @BindView(R.id.ll_titleLayout)
    RelativeLayout llTitleLayout;
    @BindView(R.id.rv_searchRecyclerView)
    RecyclerView rvSearchRecyclerView;
    @BindView(R.id.tv_contact)
    TextView tvContact;
    @BindView(R.id.glv_goodsSearchList)
    GoodsListView glvGoodsSearchList;
    @BindView(R.id.tv_findNothing)
    TextView tvFindNothing;


    private QuickAdapter adapter;
    private RecordSQLDao recordSQLDao;
    private List<String> lists;
    public String userName;
    private boolean isSearchClick = false;
    private Dialog loadingDialog;
    private boolean isLoaded = false;
    private static final String TAG = "SearchActivity";


    //商品数据列表
    private List<Goods> goodsList;

    //服务器返回JSON类
    private GoodsGrid goodsGrid;

    //商品适配器
    private GoodsAdapter goodsAdapter;

    //当前页数
    private int nextPage = 1;

    //当前总类商品的总个数
    private int totalCount = 0;

    //服务器请求的URL
    private String searchDataUrl;

    private final static int ROW_COUNT = 15;

    private boolean loadFinish = false;

    private String searchedData;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_goods);
        ButterKnife.bind(this);

        preferences = getSharedPreferences(USER_NAME, MODE_PRIVATE);
        userName = preferences.getString(USER_NAME, "");


        //初始化数据
        init();
        initData();

    }


    /**
     *
     */
    private void init() {
        //数据库Dao类初始化
        recordSQLDao = new RecordSQLDao(this);
        //列表初始化
        lists = new ArrayList<>();
        lists = recordSQLDao.queryData("", userName);
        adapter = new QuickAdapter<String>(lists) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.item_search;
            }

            @Override
            public void convert(VH vh, String data, final int position) {
                //设置显示的数据
                vh.setText(R.id.tv_searchItemView, lists.get(position));
                if (position == 0 && isSearchClick) {
                    addAnimation(vh.getView(R.id.iv_closeImageView));
                    addAnimation(vh.getView(R.id.tv_searchItemView));
                    isSearchClick = false;
                }

                vh.getView(R.id.tv_searchItemView).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        etSearchEdit.setText(((TextView) view).getText().toString());
                    }
                });
                //设置点击close图片删除
                vh.getView(R.id.iv_closeImageView).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adapter.setList(lists);
                        recordSQLDao.delete(lists.get(position), userName);
                        lists.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                });
            }

        };
        adapter.setList(lists);
        //定义瀑布流管理器，第一个参数是列数，第二个是方向。
        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        rvSearchRecyclerView.setLayoutManager(layoutManager);
        rvSearchRecyclerView.setItemAnimator(new DefaultItemAnimator());
        rvSearchRecyclerView.setAdapter(adapter);
        rvSearchRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //这行主要解决了当加载更多数据时，底部需要重绘，否则布局可能衔接不上。
                layoutManager.invalidateSpanAssignments();
            }
        });


    }

    /**
     * 缩放动画
     *
     * @param view 动画实现的view
     */
    private void addAnimation(View view) {
        float[] vaules = new float[]{0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.3f, 1.25f, 1.2f, 1.15f, 1.1f, 1.0f};
        AnimatorSet set = new AnimatorSet();
        set.setDuration(200);
        set.playTogether(ObjectAnimator.ofFloat(view, "scaleX", vaules),
                ObjectAnimator.ofFloat(view, "scaleY", vaules));
        set.start();
    }


    private void initData() {

        //初始化列表数据
        goodsList = new ArrayList<>();

        glvGoodsSearchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GoodsListView goodsListView = (GoodsListView) parent;
                Goods goods = (Goods) goodsListView.getItemAtPosition(position);
                Intent intent = new Intent();
                intent.putExtra(GOODS_ID, (Integer) goods.getGoodsId());
                Log.d(TAG, "onItemClick: goodsId" + (Integer) goods.getGoodsId());
                intent.setClass(SearchActivity.this, GoodsDescribeActivity.class);
                startActivityForResult(intent, GOODS_DESCRIBE_ACTIVITY_REQUEST_CODE);
            }
        });

        glvGoodsSearchList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int state) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                //得到listView的最后一项的显示id
                int lastItemPosition = glvGoodsSearchList.getLastVisiblePosition();
                //判断用户是否滑动到最后一项，因为索引值从零开始所以要加上1
                if ((lastItemPosition + 1) == totalItemCount && loadFinish == true && !TextUtils.isEmpty(searchedData)) {

                    Log.d(TAG, "onScroll: " + lastItemPosition + "   " + totalItemCount + "   "
                            + totalCount + "  nextPage=" + nextPage);
                    //设置状态为 数据尚在加载

                    loadFinish = false;
                    //如果数据尚未加载完成
                    //访问网络请求成功之后，nextPage会自增，
                    //实际nextPage指向的是下一页，所以这里需要-1
                    if ((nextPage - 1) * Integer.valueOf(ROW_COUNT) < totalCount) {
                        glvGoodsSearchList.setFooterViewShow();
                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    Thread.sleep(2000);
                                    getData(searchedData, nextPage);

                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }

                }
            }
        });

    }


    /**
     * okHttp 网络访问是异步进行，在子线程中实现，
     * 如果这里直接返回列表数据，返回为null
     *
     * @param goodsCategory 商品种类
     * @param current       当前页面
     * @return
     */
    private MyHandler myHandler = new MyHandler(this);

    public void getData(final String data, int current) {
        searchDataUrl = GOODS_SEARCH_LIST_URL + data + "&current=" + current + "&rowCount=" + ROW_COUNT;
        HttpHelper httpHelper = HttpHelper.getInstance(getApplicationContext());
        httpHelper.getRequest(searchDataUrl, null, HttpHelper.JSON_DATA_1,
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

        private WeakReference<SearchActivity> weakReference;

        public MyHandler(SearchActivity SearchActivity) {
            weakReference = new WeakReference<>(SearchActivity);

        }

        @Override
        public void handleMessage(Message msg) {
            SearchActivity activity = weakReference.get();
            if (activity == null) {
                return;
            }

            switch (msg.what) {
                case 0:
                    if (activity.goodsGrid.getRows().size() <= 0 && activity.nextPage == 1) {
                        //做没有找到相关数据的处理
                        activity.tvContact.setVisibility(View.GONE);
                        activity.tvFindNothing.setVisibility(View.VISIBLE);
                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                        spannableStringBuilder.append("没有找到");
                        SpannableStringBuilder colorFilter = new SpannableStringBuilder(activity.searchedData);
                        colorFilter.setSpan(new ForegroundColorSpan(Color.parseColor("#2DD0CF")), 0, activity.searchedData.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        spannableStringBuilder.append(colorFilter);
                        spannableStringBuilder.append("相关的信息");
                        activity.tvFindNothing.setText(spannableStringBuilder);
                    } else {
                        activity.tvFindNothing.setVisibility(View.GONE);
                        activity.tvContact.setVisibility(View.VISIBLE);
                    }

                    /**
                     * 列表界面的显示
                     */
                    for (int i = 0; i < activity.goodsGrid.getRows().size(); i++) {
                        Goods goods = activity.goodsGrid.getRows().get(i);
                        activity.goodsList.add(goods);
                    }
                    //初始化和设置适配器
                    activity.goodsAdapter = new GoodsAdapter(activity.goodsList, activity);
                    activity.glvGoodsSearchList.setAdapter(activity.goodsAdapter);

                    //隐藏底部加载栏,并将选中当前页的第一项数据
                    activity.glvGoodsSearchList.setFooterViewHide((activity.nextPage - 1) * Integer.valueOf(ROW_COUNT) + 1);

                    //当前所请求界面+1
                    activity.nextPage++;

                    //更新当前总类商品的总个数
                    activity.totalCount = activity.goodsGrid.getTotal();

                    //数据加载完成
                    activity.loadFinish = true;
                    break;

                default:
                    Toast.makeText(activity, "获取数据失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }


    @OnClick(R.id.bt_searchBtn)
    public void onSearchBtnClicked() {
        if (!TextUtils.isEmpty(etSearchEdit.getText().toString())) {
            //数据初始化
            nextPage = 1;
            /**
             * 将原有数据清空
             */
            if (goodsList != null) {
                goodsList.clear();
            }
            if (goodsAdapter != null) {
                goodsAdapter.notifyDataSetChanged();
            }

            //设置当前搜索的数据
            searchedData = etSearchEdit.getText().toString();

            //向服务器对数据进行查找
            getData(searchedData, nextPage);

            //写入本地数据库
            processHistoryAndDatabase();


        } else {
            Toast.makeText(this, "请输入需要查找的数据", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 处理历史数据和数据库相关的操作
     */
    private void processHistoryAndDatabase() {
        boolean hasData = recordSQLDao.hasData(etSearchEdit.getText().toString().trim(), userName);
        if (!hasData) {
            recordSQLDao.insertData(etSearchEdit.getText().toString().trim(), userName);
            lists = recordSQLDao.queryData("", userName);
            adapter.setList(lists);
            adapter.notifyDataSetChanged();
            isSearchClick = true;

        }
        //清空输入栏的数据
        etSearchEdit.setText("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case GOODS_DESCRIBE_ACTIVITY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    nextPage = 1;
                    /**
                     * 将原有数据清空
                     */
                    if (goodsList != null) {
                        goodsList.clear();
                    }
                    goodsAdapter.notifyDataSetChanged();
                }
                break;

            default:
                Toast.makeText(this, "未知请求值", Toast.LENGTH_SHORT).show();
                break;

        }

    }
}
