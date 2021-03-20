package com.example.models;

/*
* 使用单例模式管理每日学习状态
 */
public class MoocStudy {
    private int signScore = 0;                  // 打卡得分
    private int studyScore = 0;                 // 学习得分
    private int shareScore = 0;                 // 分享得分

    public int readTime = 48;                   // 阅读时长
    public int readNums = 100;                   // 累计阅读文章数

    public boolean isSign = false;              // 是否签到
    public boolean startRead = false;           // 是否开始阅读
    public boolean reading = false;             // 正在阅读文章

    private static MoocStudy instance = null;

    /* 军职在线相关资源ID */
    public static String track_menu = "com.moocxuetang:id/track_menu";             // 分享按钮
    public static String ll_share = "com.moocxuetang:id/ll_share";                 // 下拉分享按钮
    public static String item_share_tv = "com.moocxuetang:id/item_share_iv";       // 分享到学友圈


    private MoocStudy(){}

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
