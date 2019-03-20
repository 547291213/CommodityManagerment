package com.example.commoditymanagerment.Activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.example.commoditymanagerment.Bean.Goods;
import com.example.commoditymanagerment.Bean.User;
import com.example.commoditymanagerment.DrawableView.BottomDialog;
import com.example.commoditymanagerment.DrawableView.CustomDialog;
import com.example.commoditymanagerment.DrawableView.ShapedImageView;
import com.example.commoditymanagerment.ImgUpload.UploadImg;
import com.example.commoditymanagerment.NetWork.HttpHelper;
import com.example.commoditymanagerment.NetWork.NetCallBackResultBean;
import com.example.commoditymanagerment.R;
import com.example.commoditymanagerment.Util.FileUtil;
import com.example.commoditymanagerment.Util.GoodsCategoryExchange;
import com.example.commoditymanagerment.Util.TimeUtil;
import com.example.commoditymanagerment.Util.UrlHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import static com.example.commoditymanagerment.Util.StaticDataUtil.CHOOSE_ALBUM;
import static com.example.commoditymanagerment.Util.StaticDataUtil.GET_GOODS_DATA_ERROR;
import static com.example.commoditymanagerment.Util.StaticDataUtil.GET_GOODS_DATA_SUCCESS;
import static com.example.commoditymanagerment.Util.StaticDataUtil.GET_GOODS_IMG_SUCCESS;
import static com.example.commoditymanagerment.Util.StaticDataUtil.GET_USER_PERMISSION_ERROR;
import static com.example.commoditymanagerment.Util.StaticDataUtil.GOODS_ID;
import static com.example.commoditymanagerment.Util.StaticDataUtil.OPEN_PHOTO;
import static com.example.commoditymanagerment.Util.StaticDataUtil.REQUEST_CODE_WRITE;
import static com.example.commoditymanagerment.Util.StaticDataUtil.USER_HAVE_PERMISSION;
import static com.example.commoditymanagerment.Util.StaticDataUtil.USER_NAME;
import static com.example.commoditymanagerment.Util.StaticDataUtil.USER_NOT_HAVE_PERMISSION;
import static com.example.commoditymanagerment.Util.StaticDataUtil.USER_PERMIISON_LIMIT;
import static com.example.commoditymanagerment.Util.UrlHelper.GOODS_ADD_IMG_URL;
import static com.example.commoditymanagerment.Util.UrlHelper.GOODS_ADD_URL;
import static com.example.commoditymanagerment.Util.UrlHelper.GOODS_DESCRIBE_URL;
import static com.example.commoditymanagerment.Util.UrlHelper.USER_INFO_URL;

public class GoodsDescribeActivity extends BaseActivity {

    private static final String TAG = "GoodsDescribeActivity";
    @BindView(R.id.tv_setBackText)
    TextView tvSetBackText;
    @BindView(R.id.tv_modifyTv)
    TextView tvModifyTv;
    @BindView(R.id.ll_titleLayout)
    LinearLayout llTitleLayout;
    @BindView(R.id.view_divider1)
    View viewDivider1;
    @BindView(R.id.et_goodsNameEt)
    EditText etGoodsNameEt;
    @BindView(R.id.ll_goodsNameLayout)
    LinearLayout llGoodsNameLayout;
    @BindView(R.id.view_divider2)
    View viewDivider2;
    @BindView(R.id.et_originalPriceEt)
    EditText etOriginalPriceEt;
    @BindView(R.id.ll_originalPriceLayout)
    LinearLayout llOriginalPriceLayout;
    @BindView(R.id.view_divider3)
    View viewDivider3;
    @BindView(R.id.et_presentPriceEt)
    EditText etPresentPriceEt;
    @BindView(R.id.ll_presentPriceLayout)
    LinearLayout llPresentPriceLayout;
    @BindView(R.id.view_divider4)
    View viewDivider4;
    @BindView(R.id.et_goodsCountEt)
    EditText etGoodsCountEt;
    @BindView(R.id.ll_goodsCountLayout)
    LinearLayout llGoodsCountLayout;
    @BindView(R.id.view_divider5)
    View viewDivider5;
    @BindView(R.id.et_goodsDescribeEt)
    EditText etGoodsDescribeEt;
    @BindView(R.id.ll_goodsDescribeLayout)
    LinearLayout llGoodsDescribeLayout;
    @BindView(R.id.view_divider6)
    View viewDivider6;
    @BindView(R.id.tv_goodsCategoryTv)
    TextView tvGoodsCategoryTv;
    @BindView(R.id.ll_goodsCategoryLayout)
    LinearLayout llGoodsCategoryLayout;
    @BindView(R.id.view_divider7)
    View viewDivider7;
    @BindView(R.id.tv_goodsImgTv)
    TextView tvGoodsImgTv;
    @BindView(R.id.ll_goodsImgLayout)
    LinearLayout llGoodsImgLayout;
    @BindView(R.id.siv_goodsImg)
    ShapedImageView sivGoodsImg;
    @BindView(R.id.bt_addGoodsBtn)
    Button btAddGoodsBtn;

    private int goodsId;
    private String url;
    private MyHandler myHandler = new MyHandler(this);
    private Goods goods;

    private SharedPreferences preferences;

    private File imageFileDir;

    private Uri imageUri;

    private String addDataUrl;

    private String addImgUrl;

    private String userInfoUrl;

    private String getImgUrl ;

    private int category;

    private FileUtil fileUtils ;


    private boolean imgIsChanged = false;

    private boolean userHavePermissions = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_goods_describe);
        ButterKnife.bind(this);
        goodsId = (int) getIntent().getIntExtra(GOODS_ID, -1);

        Log.d(TAG, "onCreate: goodsId is " + goodsId);

        preferences = getSharedPreferences(USER_NAME, MODE_PRIVATE);

        initData();

        initEvent(userHavePermissions);
    }

    /**
     * 初始默认的时候设计控件不可点击
     * 当用户点击了修改，并且用户拥有权限的时候，会重新调用该方法
     *
     * @param userHavePermissions false不可修改  true可以修改
     */
    private void initEvent(boolean userHavePermissions) {

        etGoodsNameEt.setEnabled(userHavePermissions);
        etOriginalPriceEt.setEnabled(userHavePermissions);
        etPresentPriceEt.setEnabled(userHavePermissions);
        etGoodsCountEt.setEnabled(userHavePermissions);
        etGoodsDescribeEt.setEnabled(userHavePermissions);


        tvGoodsCategoryTv.setClickable(userHavePermissions);
        tvGoodsImgTv.setClickable(userHavePermissions);

        if (userHavePermissions == true){
            btAddGoodsBtn.setVisibility(View.VISIBLE);
        }

    }

    private void initData() {

        url = GOODS_DESCRIBE_URL + goodsId;
        HttpHelper httpHelper = HttpHelper.getInstance(getApplicationContext());
        httpHelper.getRequest(url, null, HttpHelper.JSON_DATA_1, new NetCallBackResultBean<Goods>() {
            @Override
            public void onSuccess(Goods goodsData) {

                goods = goodsData;
                myHandler.sendEmptyMessage(GET_GOODS_DATA_SUCCESS);
            }

            @Override
            public void onSuccess(List result) {
                myHandler.sendEmptyMessage(GET_GOODS_DATA_ERROR);
            }

            @Override
            public void Failed(String string) {
                myHandler.sendEmptyMessage(GET_GOODS_DATA_ERROR);
            }
        });

    }

    @OnClick({R.id.tv_setBackText, R.id.bt_addGoodsBtn, R.id.tv_goodsCategoryTv, R.id.tv_goodsImgTv, R.id.tv_modifyTv})
    public void onViewClick(View view) {
        switch (view.getId()) {

            case R.id.tv_modifyTv:
                userInfoUrl = USER_INFO_URL + preferences.getString(USER_NAME, "");
                //获取用户权限
                HttpHelper httpHelper = HttpHelper.getInstance(getApplicationContext());
                httpHelper.getRequest(userInfoUrl, null, HttpHelper.JSON_DATA_1, new NetCallBackResultBean<User>() {
                    @Override
                    public void onSuccess(User user) {
                        if (user.getPermissions() >= USER_PERMIISON_LIMIT) {
                            myHandler.sendEmptyMessage(USER_HAVE_PERMISSION);

                        } else {
                            myHandler.sendEmptyMessage(USER_NOT_HAVE_PERMISSION);
                        }
                    }

                    @Override
                    public void onSuccess(List result) {
                        myHandler.sendEmptyMessage(GET_USER_PERMISSION_ERROR);
                    }

                    @Override
                    public void Failed(String string) {
                        myHandler.sendEmptyMessage(GET_USER_PERMISSION_ERROR);
                    }
                });


                break;
            case R.id.tv_setBackText:

                finish();
                break;

            case R.id.tv_goodsCategoryTv:

                final ArrayList<String> goodsCategory = new ArrayList<>();
                goodsCategory.add(getResources().getString(R.string.category_hot));
                goodsCategory.add(getResources().getString(R.string.category_new_product));
                goodsCategory.add(getResources().getString(R.string.category_promotion));
                goodsCategory.add(getResources().getString(R.string.category_activity));
                goodsCategory.add(getResources().getString(R.string.category_out_of_stock));
                OptionsPickerView<String> pickerView = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int options2, int options3, View v) {
                        tvGoodsCategoryTv.setText(goodsCategory.get(options1));
                        category = GoodsCategoryExchange.exChange(goodsCategory.get(options1));
                    }
                }).build();
                pickerView.setPicker(goodsCategory);
                pickerView.show();
                break;

            case R.id.tv_goodsImgTv:


                String item1 = "相册";
                String item2 = "拍照";
                String item3 = "取消";
                final BottomDialog bottomDialog = new BottomDialog(GoodsDescribeActivity.this, item1, item2, item3);
                bottomDialog.setBackground(Color.WHITE);
                bottomDialog.setItem1TextColor(1, Color.BLACK);
                bottomDialog.setItem1TextColor(2, Color.BLACK);
                bottomDialog.setItem1TextColor(3, Color.BLACK);
                bottomDialog.setItemClickListener(new BottomDialog.ItemClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onItem1Click(View view) {

                        Toast.makeText(GoodsDescribeActivity.this, "相册", Toast.LENGTH_SHORT).show();
                        bottomDialog.dismiss();

                        /**
                         * 相册权限
                         */
                        int check = GoodsDescribeActivity.this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        if (check != PackageManager.PERMISSION_GRANTED) {
                            //申请权限
                            requestPermissions(new String[]{Manifest.permission.WRITE_APN_SETTINGS}, REQUEST_CODE_WRITE);
                        } else {
                            //打开相册
                            openAlbum();
                        }
                    }

                    @Override
                    public void onItem2Click(View view) {

                        Toast.makeText(GoodsDescribeActivity.this, "拍照", Toast.LENGTH_SHORT).show();
                        bottomDialog.dismiss();
                        imageUri = getImageUri();
                        //启动程序
                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivityForResult(intent, OPEN_PHOTO);

                    }

                    @Override
                    public void onItem3Click(View view) {

                        Toast.makeText(GoodsDescribeActivity.this, "取消", Toast.LENGTH_SHORT).show();
                        bottomDialog.dismiss();
                    }
                });

                bottomDialog.show();
                break;

            case R.id.bt_addGoodsBtn:
                //1 数据判空以及合法性的判断
                if (TextUtils.isEmpty(etGoodsNameEt.getText().toString())) {
                    Toast.makeText(this, "商品名不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etOriginalPriceEt.getText().toString())) {
                    Toast.makeText(this, "商品原价不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etPresentPriceEt.getText().toString())) {
                    Toast.makeText(this, "商品现价不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etGoodsCountEt.getText().toString())) {
                    Toast.makeText(this, "商品数目不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!canParseInt(etOriginalPriceEt.getText().toString())) {
                    Toast.makeText(this, "商品原价类型错误", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!canParseInt(etPresentPriceEt.getText().toString())) {
                    Toast.makeText(this, "商品现价类型错误", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (!canParseInt(etGoodsCountEt.getText().toString())) {
                    Toast.makeText(this, "商品数目类型错误", Toast.LENGTH_SHORT).show();
                    return;
                }


                String lastModifyUser = preferences.getString(USER_NAME, "");

                addDataUrl = GOODS_ADD_URL + etGoodsNameEt.getText().toString() + "&goodsCount=" +
                        etGoodsCountEt.getText().toString() + "&goodsDescribe=" +
                        etGoodsDescribeEt.getText().toString() + "&goodsCategory=" +
                        category + "&lastModifyTime=" +
                        TimeUtil.getNowDate() + "&goodsImg=" +
                        imageFileDir.getName() + "&lastModifyUser=" +
                        lastModifyUser + "&originalPrice=" +
                        etOriginalPriceEt.getText().toString() + "&presentPrice=" +
                        etPresentPriceEt.getText().toString();

                addImgUrl = GOODS_ADD_IMG_URL;
                Log.d(TAG, "onViewClick: addDataUrl is " + addDataUrl);
                //上传
                inputImageFile();
                break;
        }


    }

    /**
     * 判断字符窜能否转换为int
     *
     * @param str 字符窜
     * @return true可以 ， false不可以
     */
    public boolean canParseInt(String str) {
        if (str == null) { //验证是否为空
            return false;

        }

        return str.matches("\\d+"); //使用正则表达式判断该字符串是否为数字，第一个\是转义符，\d+表示匹配1个或 //多个连续数字，"+"和"*"类似，"*"表示0个或多个

    }

    /**
     * 打开相册
     */
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_ALBUM);

    }

    /**
     * 解析从相册获取的图片
     */
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(GoodsDescribeActivity.this, uri)) {
            //如果是document类型的uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; //解析出数字格式的Id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型的uri，则用普通的处理方式
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是file类型的uri，直接获取图片即可
            imagePath = uri.getPath();
        }
        imageFileDir = new File(imagePath);

        Log.d(TAG, "handleImageOnKitKat: " + imageFileDir);
        displayImage();
    }

    /**
     * 获取图片路径
     *
     * @param uri
     * @param selection
     * @return
     */
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = GoodsDescribeActivity.this.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }

        return path;
    }


    /**
     * 获取图片Uri地址
     */
    public Uri getImageUri() {
        Uri imageUri;
        //创建文件
        imageFileDir = new File(Environment.getExternalStorageDirectory(), "/commoditymanager/img/goods_image.jpg");
        try {
            //创建目录
            imageFileDir.getParentFile().mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            /**
             * 如果图片已经存在
             * 删除已存在的图片
             */
            if (imageFileDir.exists()) {
                imageFileDir.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(GoodsDescribeActivity.this,
                    "com.example.commoditymanagerment.fileprovider", imageFileDir);
        } else {
            imageUri = Uri.fromFile(imageFileDir);
        }

        return imageUri;
    }


    /**
     * 压缩，上传，显示图片
     */
    private void displayImage() {
        if (imageFileDir != null) {

            //压缩
            imageCompressed();
            //显示
            Bitmap bitmap = BitmapFactory.decodeFile(imageFileDir.toString());
            if (bitmap != null)
                setGoodsImg(bitmap);
        } else {

            Toast.makeText(this, "显示图片出错", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 图片压缩
     * 基于luban
     */
    private void imageCompressed() {

        Luban.with(GoodsDescribeActivity.this)
                .load(imageFileDir)
                .ignoreBy(100)
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess(final File file) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (file != null) {
                                    /**
                                     * 将压缩后的图片文件赋值给源文件
                                     */
                                    imageFileDir = file;
                                } else {
                                    Toast.makeText(GoodsDescribeActivity.this, "压缩图片失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(GoodsDescribeActivity.this, "失败", Toast.LENGTH_SHORT).show();

                    }
                }).launch();
    }


    /**
     * 图片显示
     *
     * @param bitmap
     */
    private void setGoodsImg(Bitmap bitmap) {
        /**
         * 显示fini
         */
        sivGoodsImg.setImageBitmap(bitmap);
    }

    /**
     * 上传图片文件
     * 自定义加载进度条（仿Material Design）
     */
    private void inputImageFile() {
        if (imageFileDir != null) {

            final CustomDialog customDialog = new CustomDialog(this, R.style.CustomDialog);
            if (imageFileDir == null) {
                return;
            }
//            customDialog.show();

            new GoodsDescribeActivity.UpLoad(imageFileDir, this).execute(addImgUrl);

        }

    }

    public class UpLoad extends AsyncTask<String, Void, String> {
        private File file;
        private Context mContext;

        public UpLoad(File file, Context context) {
            this.file = file;
            mContext = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            return UploadImg.uploadFile(file, strings[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {

                myHandler = new GoodsDescribeActivity.MyHandler(GoodsDescribeActivity.this);
                myHandler.sendEmptyMessage(0);
            } else {
                Toast.makeText(mContext, "上传失败", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private static class MyHandler extends Handler {

        private WeakReference<GoodsDescribeActivity> weakReference;

        public MyHandler(GoodsDescribeActivity goodsDescribeActivity) {
            weakReference = new WeakReference<>(goodsDescribeActivity);
        }

        @Override
        public void handleMessage(Message msg) {

            GoodsDescribeActivity activity = weakReference.get();

            if (activity == null) {
                return;
            }

            switch (msg.what) {
                case 0:
                    Toast.makeText(activity, "上传图片成功", Toast.LENGTH_SHORT).show();
                    break;

                case GET_GOODS_DATA_SUCCESS:
                    //填装列表
                    activity.etGoodsNameEt.setText(activity.goods.getGoodsName());
                    activity.etOriginalPriceEt.setText(String.valueOf(activity.goods.getOriginalPrice()));
                    activity.etPresentPriceEt.setText(String.valueOf(activity.goods.getPresentPrice()));
                    activity.etGoodsCountEt.setText(String.valueOf(activity.goods.getGoodsCount()));
                    activity.etGoodsDescribeEt.setText(activity.goods.getGoodsDescribe());
                    activity.tvGoodsCategoryTv.setText(GoodsCategoryExchange.exChange(activity.goods.getGoodsCategory()));

                    activity.getImgUrl = UrlHelper.GOODS_GET_IMG_URL+activity.goods.getGoodsImg().toString() ;

                    Log.d(TAG, "handleMessage: getImgUrl is " + activity.getImgUrl);
                    activity.downLoad(activity.getImgUrl , activity.goods.getGoodsImg().toString());
                    break;

                case GET_GOODS_DATA_ERROR:
                    Toast.makeText(activity, "获取商品信息失败", Toast.LENGTH_SHORT).show();
                    break;

                case USER_HAVE_PERMISSION:
                    activity.userHavePermissions = true;
                    activity.initEvent(true);
                    break;

                case USER_NOT_HAVE_PERMISSION:
                    Toast.makeText(activity, "当前用户组没有权限", Toast.LENGTH_SHORT).show();
                    break;

                case GET_USER_PERMISSION_ERROR:
                    Toast.makeText(activity, "获取用户权限失败", Toast.LENGTH_SHORT).show();
                    break;

                case GET_GOODS_IMG_SUCCESS:
                    activity.sivGoodsImg.setImageBitmap(BitmapFactory.decodeFile(activity.fileUtils.getPath()+activity.goods.getGoodsImg().toString()));
                    break;

                default:
                    Toast.makeText(activity, "未知请求", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    /**
     * 从服务器下载文件
     *
     * @param path     下载文件的地址
     * @param FileName 文件名字
     */
    public void downLoad(final String path, final String FileName) {
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
                            myHandler.sendEmptyMessage(GET_GOODS_IMG_SUCCESS);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CODE_WRITE:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //打开相册
                    openAlbum();

                }
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case OPEN_PHOTO:

                if (resultCode == RESULT_OK) {
//                            图片文件路径
//                            Environment.getExternalStorageDirectory() + File.separator + "header_image.jpg"

                    displayImage();
                }
                break;

            case CHOOSE_ALBUM:
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        handleImageOnKitKat(data);
                    } else {
                        Toast.makeText(this, "版本过老，已经不再兼容", Toast.LENGTH_SHORT).show();
                    }
                }
        }
    }


}
