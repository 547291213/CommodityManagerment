package com.example.commoditymanagerment.SqlHelper;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class RecordSQLiteOpenHelper extends SQLiteOpenHelper {
    private static String name = "record.db";
    private static Integer version = 1;

    public RecordSQLiteOpenHelper(@Nullable Context context) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //打开数据库，建立了一个叫records的表，里面有一列name来存储历史记录,id 指定用户Id：
        db.execSQL("create table records(id integer primary key autoincrement,name varchar(200),username text ,createtime text )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion){
            db.execSQL("drop table if exists records ");
            onCreate(db);
        }
    }
}
