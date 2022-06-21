package com.example.models;

import android.util.Log;

import com.example.utils.HttpUtils;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 使用单例模式管理系统用户信息
 */
public class UserInfo {

    private static final UserInfo SINGLETON = new UserInfo();
    private UserInfo() {    }
    public static UserInfo getInstance() {
        return SINGLETON;
    }

    public String PhoneName;

    public String AndroidId;

    public String MoocName;

    public String Key;              // 用户密钥

    public String MoocId;           // 军职在线ID

    public String CreateTime;

    public String getAndroidId() {
        return AndroidId;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public String getPhoneName(){
        return PhoneName;
    }

    public String getName() {
        return MoocName;
    }

    public void setAndroidId(String androidId) {
        AndroidId = androidId;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public void setMoocName(String name) {
        MoocName = name;
    }

    public void setPhoneName(String phoneName) {
        PhoneName = phoneName;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getMoocId() {
        return MoocId;
    }

    public void setMoocId(String moocId) {
        MoocId = moocId;
    }

    /**
     * 发送用户信息到服务器
     */
    public void PostUserInfo(){
        try{
            String jsonString = new Gson().toJson(this);
            HttpUtils.post(HttpUtils.httpUpdateUrl, jsonString,new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    // Log.d("TAG", "onFailure: 失败"+ e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = response.body().string();
                    Log.d("onResponse: ",result);
                }
            });
        }catch (IOException err){
            err.printStackTrace();
        }
    }
}
