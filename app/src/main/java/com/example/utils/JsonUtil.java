package com.example.utils;

import android.util.Log;

import com.example.models.AppInfo;

import org.json.JSONArray;
import org.json.JSONObject;

public class JsonUtil {
    /**
     * 解析 AppInfo Json字符串
     * @param jsonData
     */
    public static AppInfo str2Obj(String jsonData){
        AppInfo info = new AppInfo();
        try{
            JSONObject jobj= new JSONObject(jsonData);
            info.setVersion(jobj.getInt("version"));
            info.setVersionName(jobj.getString("versionName"));
        }catch(Exception e){
            e.printStackTrace();
        }
        return info;
    }
}
