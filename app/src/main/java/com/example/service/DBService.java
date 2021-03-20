package com.example.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*
* SQLite 数据库操作
 */
public class DBService extends SQLiteOpenHelper {  //使用SQLiteOpenHelper创建数据库

    public DBService(Context context) {
        super(context, "autoread.db", null, 1);
    }

    public void onCreate(SQLiteDatabase db) {
        String sql="create table config(userid Integer primary key autoincrement,readCount Integer,readTime Integer,userKey varchar(50))";   //生成数据库表的sql
        db.execSQL(sql);  //生成表
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
