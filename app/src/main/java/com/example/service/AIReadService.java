package com.example.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.GestureDescription;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.appexmple.MainActivity;
import com.example.appexmple.R;
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

/**
 * 自动阅读服务
 */
public class AIReadService extends BaseService {
    private static final String TAG = "";
    private static final int NOTIFICATIONS_ID = 1;
    public static AIReadService mService;
    private MoocMonitor moocMonitor = null;
    public NotificationManager mNotificationManager = null;
    //初始话服务
    @Override
    public void onServiceConnected(){
        super.onServiceConnected();
        moocMonitor = new MoocMonitor(this);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // 设置服务信息
        AccessibilityServiceInfo serviceInfo = new AccessibilityServiceInfo();
        serviceInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        serviceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        serviceInfo.packageNames = new String[]{"com.moocxuetang", "cn.xuexi.android"};// 监控的app
        serviceInfo.notificationTimeout = 100;
        // 获取 webview 中的内容
        serviceInfo.flags = serviceInfo.flags | AccessibilityServiceInfo.FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY;
        setServiceInfo(serviceInfo);
        mService = this;
    }

    /**
     * 实现辅助功能
     * @param event
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("WrongConstant")
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        super.onAccessibilityEvent(event);

        try {
            // 确定事件来自军职在线
            String eventStr = event.getClassName().toString();
            if (event.getPackageName().equals(moocMonitor.getPakageName())) {
                switch (event.getEventType()) {
                    case AccessibilityEvent.TYPE_VIEW_CLICKED:

                        break;
                    case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
                        moocMonitor.LongPress2Answer(getRootInActiveWindow(), event);
                        break;
                    case AccessibilityEvent.CONTENT_CHANGE_TYPE_PANE_DISAPPEARED:
                        if (eventStr.contains("HomeActivity"))
                            moocMonitor.enterStudyUI(getRootInActiveWindow(), event);
                        if (eventStr.contains("CheckInActivity"))
                            moocMonitor.enterCheckInUI(getRootInActiveWindow(), event);
                        if (eventStr.contains("VerifyCodeWebActivity"))
                            moocMonitor.enterArticleUI(getRootInActiveWindow(), event);
                        break;
                    case AccessibilityEvent.TYPE_VIEW_SCROLLED:         // 当View滑动时发送此事件
                    case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:// 窗口状态改变
                        // 关闭启动页面 -> 关闭启动页面 -> 关闭今日分享
                        moocMonitor.enterStartUI(getRootInActiveWindow(), event);
                        break;
                }
            }

            // 确定事件来之学习强国 cn.xuexi.android
            if ((event.getEventType() == 32) && event.getPackageName().equals("cn.xuexi.android")) {
                Log.i("学习：", eventStr);

                // 进入军职在线首页
                if (eventStr.contains("FrameLayout")) {
                    try {
                        Thread.sleep(1500);
                        onClickNodeById("cn.xuexi.android:id/ll_comm_head_score");

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        catch (Exception err){
            err.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 辅助功能是否启动
     * @return 服务状态
     */
    public static boolean isStart() {
        return mService != null;
    }

    /**
     * 显示题目信息
     * @param title
     * @param text
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void ShowNotice(String title,String text){
        try {
            NotificationCompat.Builder builder;
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int notificationId = new Random().nextInt(); //通知栏id
            String channelId = String.valueOf(new Random().nextInt()); //自己生成的用于通知栏的channelId，高版本必备
            NotificationChannel mChannel = new NotificationChannel(channelId, "name", NotificationManager.IMPORTANCE_HIGH);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            manager.createNotificationChannel(mChannel);
            builder = new NotificationCompat.Builder(getApplicationContext(), channelId);
            builder.setContentTitle(title)
                    .setWhen(System.currentTimeMillis()) //设置通知时间戳
                    .setSmallIcon(R.mipmap.ic_main)
                    .setContentText(text)
                    .setDefaults(NotificationCompat.DEFAULT_VIBRATE | NotificationCompat.DEFAULT_LIGHTS)  //设置将从系统默认值继承哪些通知属性 基本就是用来设置是否通知音效或者震动
                    .setAutoCancel(true)
                    .setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT))   //点击通知后的跳转
                    .setTicker(text) //收到通知后从顶部弹出精简版通知
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400}); //自定义震动的频率
            manager.notify(notificationId, builder.build());
        }catch (Exception err){
            toastMsg(text);
        }
    }
}

