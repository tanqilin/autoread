package com.example.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.database.models.MoocConfig;
import com.example.models.MoocStudy;
import com.example.models.UserInfo;
import com.example.utils.HttpUtils;
import com.example.utils.MoocConfigUtil;
import com.example.utils.SystemUtil;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AIReadService extends BaseService {
    private static final String TAG = "";
    public static AIReadService mService;
    private static int loopCount = 6;
    private MoocStudy moocStudy = null;
    private static Random r = new Random();
    private static MoocConfig config;
    //初始话服务
    @Override
    public void onServiceConnected(){
        super.onServiceConnected();
        try {
            config = MoocConfigUtil.readAppConfig(this.openFileInput("tanqilin.xml"));
            moocStudy = MoocStudy.getInstance(config);
        } catch (FileNotFoundException e) {
            toastMsg("服务启动失败，请关闭后重启服务！");
        }
        mService = this;
    }

    //实现辅助功能
    @SuppressLint("WrongConstant")
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        super.onAccessibilityEvent(event);

        // 确定事件来自军职在线 com.moocxuetang
        if ((event.getEventType() == 32) && (event.getPackageName().equals(moocStudy.package_name)))
        {
            String eventStr = event.getClassName().toString();
            // 关闭启动页面 -> 关闭启动页面 -> 关闭今日分享
            if(eventStr.contains("SplashActivity") || eventStr.contains("CustomProgressDialog")){
                try {
                    Thread.sleep(3500);
                    super.onClickNodeById(moocStudy.page_start_time);
                    Thread.sleep(3500);
                    super.onClickNodeById(moocStudy.page_everday);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // 进入首页 -> 今日得分 -> 打卡 -> 发现 -> 文章
            if(eventStr.contains("HomeActivity")){
                try {
                    Thread.sleep(1500);
                    if(!moocStudy.isStartRead() && super.onClickNode("今日学习")) {
                        Thread.sleep(1500);

                        moocStudy.setShareScore(getStrIntNumber(getNodeById(moocStudy.share_score).getText().toString()));
                        moocStudy.setSignScore(getStrIntNumber(getNodeById(moocStudy.checkin_score).getText().toString()));
                        moocStudy.setStudyScore(getStrIntNumber(getNodeById(moocStudy.study_score).getText().toString()));

                        // 打完卡后跳转到发现页面
                        if (!moocStudy.isSign && moocStudy.getSignScore() == 0) {
                            moocStudy.isSign = autoSign(event);
                            return;
                        }

                        moocStudy.setStartRead(true);
                    }

                    // 发现 -> 文章
                    super.onClickNode("发现");
                    List<AccessibilityNodeInfo> topMenus = getNodeByIds(moocStudy.page_tab_menes);
                    if(topMenus != null && topMenus.size() > 0){
                        for (int i = 0; i < topMenus.size(); i++) {
                            // 找到文章列表
                            if(topMenus.get(i).getText().equals("文章")){
                                Thread.sleep(1500);
                                super.onClickNode(topMenus.get(i));
                                toastMsg("加载文章...");

                                // 滚动文章列表查看文章
                                Thread.sleep(3000);
                                for(int e =0 ;e < (int) (Math.random()*3 + 1);e++) {
                                    dispatchGestureMove();
                                    Thread.sleep(1000);
                                }
                                Thread.sleep(1000);
                                List<AccessibilityNodeInfo> nodes = getNodeByIds(moocStudy.page_node_list);
                                if(moocStudy.getStudyScore() < moocStudy.readNums && (nodes != null && nodes.size() > 0)) {
                                    onClickNode(nodes.get(0));
                                    moocStudy.reading = true;

                                    // 开始分享和阅读 ？
                                    Thread.sleep(2000);
                                    autoShare(event);
                                    autoReadArticle();
                                }else{
                                    moocStudy.reading = false;
                                    toastMsg("今日文章已学完");
                                    return;
                                }
                            }

                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }


        // 确定事件来之学习强国 cn.xuexi.android
        if((event.getEventType() == 32) && event.getPackageName().equals("cn.xuexi.android")){
            String eventStr = event.getClassName().toString();
            Log.i("学习：", eventStr);

            // 进入军职在线首页
            if(eventStr.contains("FrameLayout")){
                try {
                    Thread.sleep(1500);
                    onClickNodeById("cn.xuexi.android:id/ll_comm_head_score");

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 自动签到
    public boolean autoSign(AccessibilityEvent event) throws InterruptedException {
        AccessibilityNodeInfo recyclerView = getRootInActiveWindow();

        if(recyclerView != null){
            // 查找底部菜单
            List<AccessibilityNodeInfo> menus = recyclerView.findAccessibilityNodeInfosByViewId(moocStudy.bottom_container);
            if(!menus.isEmpty() && menus.size() > 0){
                AccessibilityNodeInfo meMenu = menus.get(0).getChild(4);
                if(meMenu.isEnabled()){
                    // 进入我的
                    if(onClickNode(meMenu)) {
                        // 获取当前用户名称,MoocID
                        Thread.sleep(500);
                        postUserInfo(event);
                    }

                    // 进入打卡
                    Thread.sleep(1000);
                    if(super.onClickNodeById(moocStudy.go_check_page)){
                        // 打卡
                        Thread.sleep(1500);
                        recyclerView = getRootInActiveWindow();
                        List<AccessibilityNodeInfo> signBtns = recyclerView.findAccessibilityNodeInfosByViewId(moocStudy.start_check);
                        if(!signBtns.isEmpty()){
                            AccessibilityNodeInfo signBtn = signBtns.get(0);
                            if(signBtn.isEnabled()){
                                onClickNode(signBtn);
                                Thread.sleep(1500);
                                super.onClickNodeById(moocStudy.check_ok_btn);

                                Thread.sleep(1500);
                                super.onClickNodeById(moocStudy.menber_close);
                            }
                            toastMsg("今日已打卡...");
                            // 返回首页
                            Thread.sleep(3000);
                            super.onClickNodeById(moocStudy.come_back_btn);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    // 自动分享(每天最多分享5条)
    private boolean autoShare(AccessibilityEvent event) throws InterruptedException {
        if(moocStudy.getShareScore() < 5) {
            // 点击 菜单 com.moocxuetang:id/track_menu
            Thread.sleep(2000);
            onClickNodeById(MoocStudy.track_menu);

            // 点击 分享 com.moocxuetang:id/ll_share
            Thread.sleep(1000);
            onClickNode(MoocStudy.ll_share);

            // 点击第三个 分享到学友圈
            Thread.sleep(1000);
            List<AccessibilityNodeInfo> shareMenus = getNodeByIds(MoocStudy.item_share_tv);
            if (shareMenus != null && !shareMenus.isEmpty()) {
                // 分享到学友圈
                if(onClickNode(shareMenus.get(0)))
                    moocStudy.setShareScore(moocStudy.getShareScore() + 1);
                return true;
            }
        }
        return false;
    }

    // 自动阅读
    public void autoReadArticle() {
        loopCount = moocStudy.readTime;
        while (moocStudy.reading) {
            try {
                //滚动屏幕
                int t = r.nextInt(5)+5;
                toastMsg("文章阅读中，还剩 " + loopCount + " 秒！");
                dispatchGestureMove();

                loopCount = loopCount - t;
                Thread.sleep(t*1000);

                if (loopCount < t) break;
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }

        toastMsg("阅读成功");
        moocStudy.setStudyScore(moocStudy.getStudyScore() + 1);
        super.performGlobalAction(GLOBAL_ACTION_BACK);
     }

    // 模拟手势滑动
    @TargetApi(24)
    public void dispatchGestureMove()
    {
        // 根据手机屏幕尺寸，决定滑动位置
        DisplayMetrics dm2 = getResources().getDisplayMetrics();

        int i = dm2.widthPixels;
        int j = dm2.heightPixels;
        Point localPoint = new Point((int)(i * (Math.random() * 0.5D + 0.2D)), (int)(j * (Math.random() * 0.5D + 0.4D)));


        GestureDescription.Builder localBuilder = new GestureDescription.Builder();

        Path localPath = new Path();
        localPath.moveTo(localPoint.x, localPoint.y);
        localPath.lineTo((float)(localPoint.x + Math.random() * 50.0D), (float)(localPoint.y - 470 - Math.random() * 300.0D));

        localBuilder.addStroke(new GestureDescription.StrokeDescription(localPath, 100L, 800L));
        dispatchGesture(localBuilder.build(), null, null);
    }

    // 发送请求到服务器
    private void postUserInfo(AccessibilityEvent event){
        AccessibilityNodeInfo recyclerView = getRootInActiveWindow();
        String moocname = getNodeById(moocStudy.user_name).getText().toString();
        String moocid = getNodeById(moocStudy.user_mooc_id).getText().toString();

        try {
            // 注册用户信息
            UserInfo user = new UserInfo();
            user.setAndroidId(config.getUserKey());
            user.setMoocId(moocid);
            user.setMoocName(moocname);
            String jsonString = new Gson().toJson(user);
            HttpUtils.post(HttpUtils.httpUpdateUrl, jsonString);
        }catch (Exception e){e.printStackTrace();}
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //辅助功能是否启动
    public static boolean isStart() {
        return mService != null;
    }
}

