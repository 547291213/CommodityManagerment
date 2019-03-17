package com.example.commoditymanagerment.DrawableView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.ArrayRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.commoditymanagerment.R;

public class IndexBottomLayout extends LinearLayout {


    private Context mContext;

    //选中状态
    public final static int CHECKED = 0;  //选中
    public final static int UNCHECKED = 1; //未选中

    private int imgSrc;
    private int imgWidth;
    private int imgHeight;
    private String title;

    //主布局view
    private View mView;

    //显示控件
    private ImageView imageView;
    private TextView textView;

    //选中状态默认为1（非选中）  0（选中状态）
    private int mCheckState = 1;


    public IndexBottomLayout(Context context) {
        this(context, null);
    }

    public IndexBottomLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndexBottomLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.IndexBottomLayout);
        imgSrc = array.getResourceId(R.styleable.IndexBottomLayout_iconSrc, 0);
        imgHeight = array.getResourceId(R.styleable.IndexBottomLayout_iconHeight, 60);
        imgWidth = array.getResourceId(R.styleable.IndexBottomLayout_iconWidth, 60);
        title = array.getNonResourceString(R.styleable.IndexBottomLayout_title);
        //释放资源
        array.recycle();

        //设置布局为垂直布局
        setOrientation(VERTICAL);


        //初始化
        initView();
    }


    private void initView() {

        mView = LayoutInflater.from(mContext).inflate(R.layout.indexbottom_view, null, false);
        imageView = mView.findViewById(R.id.iv_imgSrc);
        imageView.setImageResource(imgSrc);

        Log.d("MainActivity", "initView: title is " + title);
        textView = mView.findViewById(R.id.tv_bottomTitle);
        textView.setText(title);

        //设置布局  params
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        mView.setLayoutParams(lp);

        //添加布局
        addView(mView);

    }

    /**
     * 设置图片的缩放动画
     */
    public void setImageScale() {
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.index_bottom_image_scale);
        animation.setInterpolator(new LinearInterpolator());
        imageView.setAnimation(animation);
        imageView.startAnimation(animation);

    }

    /**
     * 在调用处传入Drawable，来更改BigBitmap显示的内容
     *
     * @param drawable
     */
    public void setImgDrawable(Drawable drawable) {
        imageView.setImageDrawable(drawable);
    }

    public void setmCheckState(int mCheckState) {
        this.mCheckState = mCheckState;
    }

    public int getmCheckState() {
        return mCheckState;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int w = resolveSize(getMeasuredWidth(), widthMeasureSpec);
        final int h = resolveSize(getMeasuredHeight(), heightMeasureSpec);

        setMeasuredDimension(w, h);

    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        int childLeft;
        int childTop = 0;
        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (child.getVisibility() != View.GONE) {
                final int childWidth = child.getMeasuredWidth();
                final int childHeight = child.getMeasuredHeight();
                //水平居中
                childLeft = (getWidth() - childWidth) / 2;
                //加上topMargin
                childTop += lp.topMargin;
                child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
                //下一个View的top是当前View的top + childHeight + lp.bottomMargin
                childTop += childHeight + lp.bottomMargin;
            }
        }
    }
}
