package com.example.commoditymanagerment.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.commoditymanagerment.Bean.RefreshMsg;
import com.example.commoditymanagerment.DrawableView.BottomTabFragmentAdapter;
import com.example.commoditymanagerment.DrawableView.IndexBottomLayout;
import com.example.commoditymanagerment.DrawableView.ViewPagerSlide;
import com.example.commoditymanagerment.Fragment.HomepageFragment;
import com.example.commoditymanagerment.Fragment.PersonalCenterFragment;
import com.example.commoditymanagerment.R;
import com.example.commoditymanagerment.RxBus.RxBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static com.example.commoditymanagerment.Util.StaticDataUtil.ADD_GOODS_ACTIVITY_REQUEST_CODE;
import static com.example.commoditymanagerment.Util.StaticDataUtil.GOODS_DESCRIBE_ACTIVITY_REQUEST_CODE;

public class MainActivity extends BaseActivity {


    @BindView(R.id.vp_indexFragmentPager)
    ViewPagerSlide vpIndexFragmentPager;
    @BindView(R.id.ib_indexBottomRefresh)
    IndexBottomLayout ibIndexBottomRefresh;
    @BindView(R.id.ib_indexBottomHomepage)
    IndexBottomLayout ibIndexBottomHomepage;
    @BindView(R.id.ib_indexBottomPersonalcenter)
    IndexBottomLayout ibIndexBottomPersonalcenter;
    @BindView(R.id.ll_indexbottomLayout)
    LinearLayout llIndexbottomLayout;
    @BindView(R.id.rl_indexMainLayout)
    RelativeLayout rlIndexMainLayout;

    private HomepageFragment homepageFragment;
    private PersonalCenterFragment personalCenterFragment;
    private BottomTabFragmentAdapter bottomTabFragmentAdapter;
    private List<Fragment> fragmentList;
    private FragmentManager fragmentManager;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);



        initRxBus() ;
        initView();
        initPagerAndFragment();
    }


    /**
     * 以MainActivity界面为A , B为HomepageFragment,
     * C为HotFragment，D为NewProductFragment，E为PromotionFragment，F为ActivityFragment，G为OutOfStockFragment
     * H为CategoryFragment
     *
     * 需要实现的功能：
     * 点击主页（A）刷新按钮后，通知到当前显示的商品列表界面（C,D,E,F,G 其中一个），使其重新拉取数据，等到数据拉取完成之后，回到主页（A），修改主页布局
     *
     *
     * 实现逻辑：
     * （1） A中定义接口，B中继承和实现接口，表现为A中接口回调，通知到B，
     * （2）B中拥有{C,D,E,F,G}五个界面，且{C,D,E,F,G}五个界面拥有公共的父类H，那么用多态（动态绑定）的方式来实现调用当前具体显示界面的数据刷新操作，即：
     *     H中定义抽象方法onRefreshData，{C,D,E,F,G}中分别继承实现该方法，
     * （3）在B实现A中接口的方法中完成 ：1，用H接收当前界面{C,D,E,F,G五个之中的某一个}   2，使用H.onRefreshData()来实现具体调用 ;
     * （4）{C,D,E,F,G}中分别实现onRefreshData()方法，完成数据的刷新
     * （5）当数据刷新之后，使用RXBus（类似广播的一种机制），通知A，修改A界面。
     *
     *
     */
    private void initRxBus(){
        RxBus.getInstance().tObservable(RefreshMsg.class)
        .subscribe(new Observer<RefreshMsg>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(RefreshMsg refreshMsg) {
                //将其他两个状态设置为未选中状态
                setIbIndexBottomCheckState_UnChecked(ibIndexBottomPersonalcenter);
                setIbIndexBottomCheckState_UnChecked(ibIndexBottomRefresh);
                //将当前View设置为选中状态
                setIbIndexBottomCheckState_Checked(ibIndexBottomHomepage);
                //页面切换
//                vpIndexFragmentPager.setCurrentItem(0);
                setIbIndexBottomImage(ibIndexBottomRefresh,ibIndexBottomHomepage,ibIndexBottomPersonalcenter) ;

            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(MainActivity.this , "刷新界面失败",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void initView() {
        //初始化默认主页为选中状态
        ibIndexBottomHomepage.setmCheckState(1);
    }

    private void initPagerAndFragment() {

        fragmentList = new ArrayList<>();
        homepageFragment = new HomepageFragment();
        personalCenterFragment = new PersonalCenterFragment();
        fragmentList.add(homepageFragment);
        fragmentList.add(personalCenterFragment);
        fragmentManager = getSupportFragmentManager();

        bottomTabFragmentAdapter = new BottomTabFragmentAdapter(fragmentManager, fragmentList);

        //设置适配器
        vpIndexFragmentPager.setAdapter(bottomTabFragmentAdapter);
        //禁止左右滑动
        vpIndexFragmentPager.setSlide(false);
        //设置当前显示的页面
        vpIndexFragmentPager.setCurrentItem(0);
        //设置缓存
        vpIndexFragmentPager.setOffscreenPageLimit(1);
    }

    private RefreshListener refreshListener ;

    public void setRefreshData(RefreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }

    public interface RefreshListener{
        public void onRefreshData() ;
    }

    /**
     * 底部布局的点击事件监听
     *
     * @param view
     */
    public void bottomLayoutClick(View view) {
        switch (view.getId()) {
            case R.id.ib_indexBottomRefresh:
                //如果Refresh fragment已经是当前选中的界面，
                // 就不做任何处理
                if (ibIndexBottomRefresh.getmCheckState() == IndexBottomLayout.CHECKED) {
                    return;
                } else {
                    //将其他两个状态设置为未选中状态
                    setIbIndexBottomCheckState_UnChecked(ibIndexBottomHomepage);
                    setIbIndexBottomCheckState_UnChecked(ibIndexBottomPersonalcenter);
                    //将当前View设置为选中状态
                    setIbIndexBottomCheckState_Checked(ibIndexBottomRefresh);

                    //页面切换
                    vpIndexFragmentPager.setCurrentItem(0);
                    /**
                     * 做数据的刷新处理
                     */
                    if (refreshListener != null){
                        refreshListener.onRefreshData() ;
                    }else {
                        Log.d(TAG, "bottomLayoutClick: refresh listener is null");
                    }
                }
                break;

            case R.id.ib_indexBottomHomepage:
                //如果Homepage fragment已经是当前选中的界面，
                // 就不做任何处理
                if (ibIndexBottomHomepage.getmCheckState() == IndexBottomLayout.CHECKED) {
                    return;
                } else {
                    //将其他两个状态设置为未选中状态
                    setIbIndexBottomCheckState_UnChecked(ibIndexBottomPersonalcenter);
                    setIbIndexBottomCheckState_UnChecked(ibIndexBottomRefresh);
                    //将当前View设置为选中状态
                    setIbIndexBottomCheckState_Checked(ibIndexBottomHomepage);
                    //页面切换
                    vpIndexFragmentPager.setCurrentItem(0);


                }
                break;

            case R.id.ib_indexBottomPersonalcenter:
                //如果Message fragment已经是当前选中的界面，
                // 就不做任何处理
                if (ibIndexBottomPersonalcenter.getmCheckState() == IndexBottomLayout.CHECKED) {
                    return;
                } else {
                    //将其他两个状态设置为未选中状态
                    setIbIndexBottomCheckState_UnChecked(ibIndexBottomRefresh);
                    setIbIndexBottomCheckState_UnChecked(ibIndexBottomHomepage);
                    //将当前View设置为选中状态
                    setIbIndexBottomCheckState_Checked(ibIndexBottomPersonalcenter);
                    //页面切换
                    vpIndexFragmentPager.setCurrentItem(1);

                }
                break;
        }
        //View根据不同的状态设置显示的图片
        setIbIndexBottomImage(ibIndexBottomRefresh, ibIndexBottomHomepage, ibIndexBottomPersonalcenter);
        //为当前选中的状态设置图片缩放的动画
        ((IndexBottomLayout) view).setImageScale();
    }

    /**
     * 设置IndeBottomCheckState状态为未选中
     *
     * @param indexBottomCheckState
     */
    private void setIbIndexBottomCheckState_UnChecked(IndexBottomLayout
                                                              indexBottomCheckState) {
        indexBottomCheckState.setmCheckState(IndexBottomLayout.UNCHECKED);
    }

    /**
     * 设置IndexBottomCheckState状态为选中
     *
     * @param indexBottomCheckState_checked
     */
    private void setIbIndexBottomCheckState_Checked(IndexBottomLayout
                                                            indexBottomCheckState_checked) {
        indexBottomCheckState_checked.setmCheckState(IndexBottomLayout.CHECKED);
    }

    private void setIbIndexBottomImage(IndexBottomLayout indexBottomLayout_Refresh,
                                       IndexBottomLayout indexBottomLayout_Homepage,
                                       IndexBottomLayout indexBottomLayout_PersonalCenter) {
        if (indexBottomLayout_Refresh.getmCheckState() == IndexBottomLayout.UNCHECKED) {
            indexBottomLayout_Refresh.setImgDrawable(getResources().getDrawable(R.drawable.ic_refresh_gray_48));
        } else {
            indexBottomLayout_Refresh.setImgDrawable(getResources().getDrawable(R.drawable.ic_refresh_blue_48));
        }

        if (indexBottomLayout_Homepage.getmCheckState() == IndexBottomLayout.UNCHECKED) {
            indexBottomLayout_Homepage.setImgDrawable(getResources().getDrawable(R.drawable.ic_homepage_gray_48));
        } else {
            indexBottomLayout_Homepage.setImgDrawable(getResources().getDrawable(R.drawable.ic_homepage_blue_48));
        }

        if (indexBottomLayout_PersonalCenter.getmCheckState() == IndexBottomLayout.UNCHECKED) {
            indexBottomLayout_PersonalCenter.setImgDrawable(getResources().getDrawable(R.drawable.ic_personalcenter_gray_48));
        } else {
            indexBottomLayout_PersonalCenter.setImgDrawable(getResources().getDrawable(R.drawable.ic_personalcenter_blue_48));
        }
    }


    /**
     * 是否需要重新加载数据
     * 当存在数据添加，修改，删除，且操作成功的时候，数据需要重新加载
     */
    private boolean needToReloadData = false ;
    public boolean getNeedToReloadData(){
        return needToReloadData ;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case ADD_GOODS_ACTIVITY_REQUEST_CODE:
                if (resultCode == RESULT_OK){
                    needToReloadData = true ;
                }else {
                    needToReloadData = false ;
                }
                break;

            case GOODS_DESCRIBE_ACTIVITY_REQUEST_CODE :
                if (resultCode == RESULT_OK){
                    needToReloadData = true ;
                }else {
                    needToReloadData = false ;
                }
                break ;

            default:
                Toast.makeText(this , "未知请求" + requestCode,Toast.LENGTH_SHORT).show();
                break;
        }
    }


}
