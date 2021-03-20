package com.example.database;

import android.content.Context;

import com.example.service.DBService;

public class ConfigTableService {
    private DBService db;
    public  ConfigTableService(Context context){ //构造方法实例化DBService
        db= new DBService(context);
    }


}
