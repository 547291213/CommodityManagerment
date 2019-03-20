package com.example.commoditymanagerment.ImgUpload;


import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.File;

public class UpLoad extends AsyncTask<String,Void,String> {
    @Override
    protected String doInBackground(String... strings) {
        return null;
    }
//    private File file;
//    private Context mContext ;
//    public UpLoad(File file , Context context){
//        this.file = file;
//        mContext = context ;
//    }
//    @Override
//    protected String doInBackground(String... strings) {
//        return UploadImg.uploadFile(file,strings[0]);
//    }
//
//    @Override
//    protected void onPostExecute(String s) {
//        super.onPostExecute(s);
//        if(s != null){
//            Toast.makeText(mContext,"上传成功",Toast.LENGTH_SHORT).show();
//
//        }else{
//            Toast.makeText(mContext,"上传失败", Toast.LENGTH_SHORT).show();
//        }
//    }
}