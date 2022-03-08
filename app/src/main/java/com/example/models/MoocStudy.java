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
    public static String package_name = "com.moocxuetang";                          // 包名
    public static String page_start_time = "com.moocxuetang:id/tvSkip";              // 启动页倒计时
    public static String page_everday = "com.moocxuetang:id/ibClose";         // 每日分享

    public static String share_score = "com.moocxuetang:id/tvShareScore";           // 分享得分
    public static String checkin_score = "com.moocxuetang:id/tvCheckinScore";       // 签到得分
    public static String study_score = "com.moocxuetang:id/tvStudyScore";           // 学习得分

    public static String page_tab_menes = "com.moocxuetang:id/tvTabTitle";          // 发现页顶部菜单列表
    public static String page_node_list = "com.moocxuetang:id/clOne";               // 文章列表

    public static String bottom_container = "com.moocxuetang:id/llBottomContainer"; // 主页面底部菜单列表
    public static String user_name = "com.moocxuetang:id/tvUserName";               // 我的页面，用户名称
    public static String user_mooc_id = "com.moocxuetang:id/tvID";                  // 我的页面用户ID

    public static String go_check_page = "com.moocxuetang:id/tvCheckIn";            //进入打卡页面
    public static String start_check = "com.moocxuetang:id/tvStart";                // 打卡
    public static String check_ok_btn = "com.moocxuetang:id/tvOk";                  // 打卡成功确定按钮
    public static String menber_close = "com.moocxuetang:id/iv_member_close";       // 关闭打卡成功确认后弹出的学友圈
    public static String come_back_btn = "com.moocxuetang:id/ib_back";              // 打卡成功后左上角返回主页面按钮

    public static String track_menu = "com.moocxuetang:id/ibRightIcon";             // 分享按钮
    public static String ll_share = "分享";                                           // 下拉分享按钮
    public static String item_share_tv = "com.moocxuetang:id/tvShareSchoolCircle";       // 分享到学友圈
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
