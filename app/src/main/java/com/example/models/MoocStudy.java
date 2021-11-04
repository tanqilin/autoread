package com.example.models;

import android.content.Context;

import com.example.database.models.MoocConfig;
import com.example.utils.MoocConfigUtil;

/*
* 使用单例模式管理每日学习状态
 */
public class MoocStudy {
    private int signScore = 0;                  // 打卡得分
    private int studyScore = 0;                 // 学习得分
    private int shareScore = 0;                 // 分享得分

    public int readTime = 48;                   // 阅读时长
    public int readNums = 40;                   // 累计阅读文章数

    public boolean isSign = false;              // 是否签到
    public boolean startRead = false;           // 是否开始阅读
    public boolean reading = false;             // 正在阅读文章

    private static MoocConfig config = null;
    private static MoocStudy instance = null;

    /* 军职在线相关资源ID */
    public static String track_menu = "com.moocxuetang:id/track_menu";             // 分享按钮
    public static String ll_share = "com.moocxuetang:id/ll_share";                 // 下拉分享按钮
    public static String item_share_tv = "com.moocxuetang:id/item_share_iv";       // 分享到学友圈
    public static String TOP_CHILD_MENU = "com.moocxuetang:id/title";               // 文章菜单下的二级菜单列表

    private MoocStudy(){}

    /* 传入配置文件初始化学习模式 */
    public static MoocStudy getInstance(MoocConfig config){
        if (instance == null) {
            instance = new MoocStudy();

            /* 从配置文件中读取参数 */
            if(config != null) {
                instance.readNums = config.getReadCount();
                instance.readTime = config.getReadTime();
            }
        }
        return instance;
    }

    /* 不传入配置文件使用默认方式初始化学习模式 */
    public static MoocStudy getInstance(){
        if (instance == null) {
            instance = new MoocStudy();
        }
        return instance;
    }

    /* 设置变量值 */
    public void setSignScore(int signScore) {
        this.signScore = signScore;
    }

    public void setStudyScore(int studyScore) {
        this.studyScore = studyScore;
    }

    public void setShareScore(int shareScore) {
        this.shareScore = shareScore;
    }

    public void setStartRead(boolean startRead) {
        this.startRead = startRead;
    }

    /* 获取变量值 */
    public int getStudyScore() {
        return studyScore;
    }

    public int getShareScore() {
        return shareScore;
    }

    public int getSignScore() {
        return signScore;
    }

    public boolean isStartRead() {
        return startRead;
    }
}
