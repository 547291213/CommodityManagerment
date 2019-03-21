package com.example.commoditymanagerment.DrawableView;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.effect.EffectUpdateListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.commoditymanagerment.Bean.Goods;
import com.example.commoditymanagerment.Fragment.GoodsCategory.CategoryFragment;
import com.example.commoditymanagerment.R;
import com.example.commoditymanagerment.Util.FileUtil;
import com.example.commoditymanagerment.Util.UrlHelper;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static com.example.commoditymanagerment.Util.StaticDataUtil.GET_GOODS_IMG_SUCCESS;

public class GoodsAdapter extends BaseAdapter {


    private List<Goods> goodsList;
    private Context mContext;
    private LayoutInflater layoutInflater;
    private String goodsImgUrl;
    private static final String TAG = "GoodsAdapter";

    public GoodsAdapter(List<Goods> goods, Context context) {

        goodsList = goods;
        mContext = context;
        layoutInflater = LayoutInflater.from(mContext);
    }


    @Override
    public int getCount() {
        return goodsList.size();
    }

    @Override
    public Object getItem(int position) {
        return goodsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;
        final Goods goods = goodsList.get(position);
        if (convertView == null) {

            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_goods, null, false);
            viewHolder.shapedImageView = convertView.findViewById(R.id.siv_goodsImg);
            viewHolder.goodsName = convertView.findViewById(R.id.tv_goodsName);
            viewHolder.goodsCount = convertView.findViewById(R.id.tv_goodsCount);
            viewHolder.goodsTime = convertView.findViewById(R.id.tv_goodsTime);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


//        if (fragment.getScrollerState() != SCROLL_STATE_FLING){
        goodsImgUrl = UrlHelper.GOODS_GET_IMG_URL + goodsList.get(position).getGoodsImg().toString();
        Glide.with(mContext).load(goodsImgUrl).into(viewHolder.shapedImageView);
//            downLoad(goodsImgUrl, goodsList.get(position).getGoodsImg().toString(), viewHolder.shapedImageView);
//        }else {
//            viewHolder.shapedImageView.setImageResource(R.drawable.ic_pic_not_found);
//            Glide.with(mContext).load(R.drawable.ic_pic_not_found).into(viewHolder.shapedImageView) ;
//        }

        viewHolder.goodsName.setText(goodsList.get(position).getGoodsName());
        viewHolder.goodsCount.setText(String.valueOf(goodsList.get(position).getGoodsCount()));
        viewHolder.goodsTime.setText(goodsList.get(position).getLastModifyTime());
        return convertView;
    }


    private class ViewHolder {
        ShapedImageView shapedImageView;
        TextView goodsName;
        TextView goodsCount;
        TextView goodsTime;
    }


    private FileUtil fileUtils;

    private MyHandler myHandler = new MyHandler(this);

    /**
     * 从服务器下载文件
     *
     * @param path     下载文件的地址
     * @param FileName 文件名字
     */
    @Deprecated
    public void downLoad(final String path, final String FileName, final ShapedImageView shapedImageView) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(path);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setReadTimeout(5000);
                    con.setConnectTimeout(5000);
                    con.setRequestProperty("Charset", "UTF-8");
                    con.setRequestMethod("GET");
                    if (con.getResponseCode() == 200) {
                        InputStream is = con.getInputStream();//获取输入流
                        FileOutputStream fileOutputStream = null;//文件输出流
                        if (is != null) {
                            fileUtils = new FileUtil();
                            fileOutputStream = new FileOutputStream(fileUtils.createFile(FileName));//指定文件保存路径，代码看下一步
                            byte[] buf = new byte[1024];
                            int ch;
                            while ((ch = is.read(buf)) != -1) {
                                fileOutputStream.write(buf, 0, ch);//将获取到的流写入文件中
                            }
                        }
                        if (fileOutputStream != null) {
                            fileOutputStream.flush();
                            fileOutputStream.close();
                            //加载图片
                            Message message = Message.obtain();

                            Bundle bundle = new Bundle();
                            bundle.putString("imgPath", fileUtils.getPath() + FileName);
                            message.obj = shapedImageView;
                            message.setData(bundle);
                            message.what = GET_GOODS_IMG_SUCCESS;
                            myHandler.sendMessage(message);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Deprecated
    private static class MyHandler extends Handler {

        private WeakReference<GoodsAdapter> weakReference;

        public MyHandler(GoodsAdapter adapter) {
            weakReference = new WeakReference<>(adapter);

        }

        @Override
        public void handleMessage(Message msg) {
            GoodsAdapter adapter = weakReference.get();
            if (adapter == null) {
                return;
            }

            switch (msg.what) {
                case GET_GOODS_IMG_SUCCESS:
                    Log.d(TAG, "handleMessage: SUCCESS");
                    Glide.with(adapter.mContext).load(BitmapFactory.decodeFile(msg.getData().getString("imgPath"))).into((ShapedImageView) msg.obj);
                    break;

                default:
                    Toast.makeText(adapter.mContext, "未知请求类型", Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    }

}
