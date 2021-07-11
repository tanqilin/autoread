package com.example.models;

public class UserInfo {
    public String PhoneName;

    public String AndroidId;

    public String Name;

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
        return Name;
    }

    public void setAndroidId(String androidId) {
        AndroidId = androidId;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public void setName(String name) {
        Name = name;
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
}
