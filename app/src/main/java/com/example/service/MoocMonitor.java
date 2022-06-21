package com.example.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Build;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.database.models.MoocConfig;
import com.example.models.MoocStudy;
import com.example.models.SubjectInfo;
import com.example.models.UserInfo;
import com.example.utils.MoocConfigUtil;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 军职在线操控器
 */
public class MoocMonitor extends BaseMonitor{
    private MoocStudy moocStudy = null;
    private static Random r = new Random();
    private static MoocConfig config;
    private String subjectInfo = "";        /* 题目信息 */

    /**
     * 获取Mooc PakageName
     * @return
     */
    public String getPakageName(){
        return moocStudy.package_name;
    }

    /**
     * 构造函数
     * @param context service context
     * @throws FileNotFoundException
     */
    public MoocMonitor(AIReadService context) {
        super(context);
        try {
            config = MoocConfigUtil.readAppConfig(super.serviceContext.openFileInput("tanqilin.xml"));
            moocStudy = MoocStudy.getInstance(config);
        }catch (FileNotFoundException e){}
    }

    /**
     * 跳过启动页/关闭一些弹窗
     * @param nodeInfo
     */
    public void enterStartUI(AccessibilityNodeInfo nodeInfo,AccessibilityEvent event){
        Log.d("EnterStartUI（启动页）: ",event.getEventType() + "  className：" + nodeInfo.getClassName());
        if(event.getClassName().toString().equals("android.widget.ImageView") ||
            event.getClassName().toString().equals("android.widget.FrameLayout")){
            this.onClickNodeById(nodeInfo,moocStudy.page_start_time);
            this.onClickNodeById(nodeInfo,moocStudy.page_everday);
        }
        this.onClickNodeById(nodeInfo,moocStudy.check_ok_btn);
        this.onClickNodeById(nodeInfo,moocStudy.menber_close);
    }

    /**
     * 进入主主页面
     * @param nodeInfo
     * @param event
     */
    public void enterStudyUI(AccessibilityNodeInfo nodeInfo,AccessibilityEvent event) {
        String className = event.getClassName().toString();
        Log.d("EnterCheckInUI（主页面）: ",event.getEventType() + "  className：" + className);
        if(className.contains("HomeActivity")){
            try {
                Thread.sleep(500);
                /* 如果还没开始阅读，则跳转到今日学习页面获取学习信息 */
                if (!moocStudy.startRead) {
                    if(onClickNode(nodeInfo, "今日学习")){
                        Thread.sleep(1000);
                        moocStudy.setShareScore(getStrIntNumber(getNodeById(nodeInfo, moocStudy.share_score).getText().toString()));
                        moocStudy.setSignScore(getStrIntNumber(getNodeById(nodeInfo, moocStudy.checkin_score).getText().toString()));
                        moocStudy.setStudyScore(getStrIntNumber(getNodeById(nodeInfo, moocStudy.study_score).getText().toString()));

                        /* 未签到，或者签到分数为 0 跳转到签到页面*/
                        if (!moocStudy.isSign && moocStudy.getSignScore() == 0) {
                            if (onClickNode(nodeInfo, "我的")) {
                                Thread.sleep(1000);
                                String moocname = getNodeById(nodeInfo, moocStudy.user_name).getText().toString();
                                String moocid = getNodeById(nodeInfo, moocStudy.user_mooc_id).getText().toString();

                                /* 关联用户信息 */
                                UserInfo.getInstance().setAndroidId(config.getUserKey());
                                UserInfo.getInstance().setMoocId(moocid);
                                UserInfo.getInstance().setMoocName(moocname);
                                UserInfo.getInstance().PostUserInfo();

                                /* 点击进入签到页面 */
                                onClickNodeById(nodeInfo, moocStudy.go_check_page);
                            }
                        }

                        /* 签到成功后开始学习 */
                        moocStudy.startRead = true;
                    }
                }

                onClickNode(nodeInfo, "发现");
                List<AccessibilityNodeInfo> topMenus = getNodeByIds(nodeInfo,moocStudy.page_tab_menes);
                if(topMenus != null && topMenus.size() > 0) {
                    for (int i = 0; i < topMenus.size(); i++) {
                        // 找到文章列表
                        if (topMenus.get(i).getText().equals("文章")) {
                            onClickNode(topMenus.get(i));
                            toastMsg("加载文章...");

                            // 滚动文章列表查看文章
                            Thread.sleep(3000);
                            for(int e =0 ;e < (int) (Math.random()*3 + 1);e++) {
                                dispatchGestureMove();
                                Thread.sleep(1000);
                            }

                            Thread.sleep(1000);
                            nodeInfo = serviceContext.getRootInActiveWindow();
                            List<AccessibilityNodeInfo> nodes = getNodeByIds(nodeInfo,moocStudy.page_node_list);
                            if(moocStudy.getStudyScore() < moocStudy.readNums && (nodes != null && nodes.size() > 0)) {
                                onClickNode(nodes.get(0));
                                moocStudy.reading = true;

                                /* 自动分享 */
                                Thread.sleep(1500);
                                autoShare(nodeInfo,event);
                            }else{
                                toastMsg("今日文章已学完");
                                return;
                            }
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 进入签到页面
     * @param nodeInfo
     * @param event
     */
    public void enterCheckInUI(AccessibilityNodeInfo nodeInfo,AccessibilityEvent event){
        String className = event.getClassName().toString();
        Log.d("EnterCheckInUI（签到页面）: ",event.getEventType() + "  className：" + className);
        if(className.contains("CheckInActivity"))
        {
            try {
                Thread.sleep(500);
                /* 打卡 */
                if(onClickNodeById(nodeInfo,moocStudy.start_check)){
                    toastMsg("很不错、今天签到成功！");
                }else{
                    toastMsg("很不错、今天已签到！");
                }

                nodeInfo = serviceContext.getRootInActiveWindow();

                Thread.sleep(1000);
                onClickNodeById(nodeInfo,moocStudy.check_ok_btn);

                Thread.sleep(1000);
                onClickNodeById(nodeInfo,moocStudy.menber_close);

                // 返回首页
                moocStudy.isSign = true;
                Thread.sleep(500);
                onClickNodeById(nodeInfo,moocStudy.come_back_btn);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 进入文章阅读页面
     * @param nodeInfo
     * @param event
     */
    @SuppressLint("LongLogTag")
    public void enterArticleUI(AccessibilityNodeInfo nodeInfo, AccessibilityEvent event){
        String className = event.getClassName().toString();
        Log.d("EnterArticleUI（文章阅读页面）: ",event.getEventType() + "  className：" + className);
        if(className.contains("VerifyCodeWebActivity"))
        {
            int loopCount = moocStudy.readTime;
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
            moocStudy.reading = false;
            moocStudy.setStudyScore(moocStudy.getStudyScore() + 1);
            serviceContext.performGlobalAction(serviceContext.GLOBAL_ACTION_BACK);
        }
    }

    /**
     * 自动分享
     * @param event
     * @return
     * @throws InterruptedException
     */
    private void autoShare(AccessibilityNodeInfo nodeInfo,AccessibilityEvent event) {
        try {
            if (moocStudy.getShareScore() < 5) {
                // 点击 菜单 com.moocxuetang:id/track_menu
                Thread.sleep(500);
                onClickNodeById(nodeInfo, moocStudy.track_menu);

                // 点击 分享 com.moocxuetang:id/ll_share
                Thread.sleep(500);
                onClickNode(nodeInfo, moocStudy.ll_share);

                // 点击第三个 分享到学友圈
                Thread.sleep(500);
                List<AccessibilityNodeInfo> shareMenus = getNodeByIds(nodeInfo, moocStudy.item_share_tv);
                if (shareMenus != null && !shareMenus.isEmpty()) {
                    // 分享到学友圈
                    if(onClickNode(shareMenus.get(0)))
                        moocStudy.setShareScore(moocStudy.getShareScore() + 1);
                }
            }
        }catch (InterruptedException e){
            toastMsg("分享失败！");
        }
    }

    /**
     * 长按答题
     * @param nodeInfo
     * @param event
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void LongPress2Answer(AccessibilityNodeInfo nodeInfo, AccessibilityEvent event){
        String className = event.getClassName().toString();
        if(className.contains("WebView")){
            subjectInfo = "";
            if(nodeInfo !=null && nodeInfo.getChildCount() >0 ) {
                for (int i = 0; i < nodeInfo.getChildCount(); i++) {
                    AccessibilityNodeInfo child = nodeInfo.getChild(i);
                    if (child.getChildCount() > 0) {
                        AccessibilityNodeInfo childWeb = child.getChild(0);
                        if ("android.webkit.WebView".equals(childWeb.getClassName())) {
                           GetWebViewText(childWeb);
                        }
                    }
                }
            }

            if(subjectInfo.isEmpty() || subjectInfo.length() < 5 || subjectInfo == "|"){
                toastMsg("没有获取到题目信息，请退出后重新进入军职在线");
            }else{
                if(subjectInfo.contains("得分")){
                    serviceContext.ShowNotice("操作无效","此题你已经答过啦！");
                    return;
                }
                SubjectInfo subject = new SubjectInfo();
                subject.setSubject(subjectInfo);
                subject.postSubjectInfo(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        toastMsg("请求失败，请重新启动“自动阅读APP”");
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        String jsonStr = response.body().string();
                        SubjectInfo account = new Gson().fromJson(jsonStr,SubjectInfo.class);
                        toastMsg("此题答案为：" + account.getCorrect());
                        serviceContext.ShowNotice("此题答案为",account.getCorrect());
                    }
                });
            }
        }
    }

    /**
     * 获取WebView中的文字
     * @param node
     */
    private void GetWebViewText(AccessibilityNodeInfo node){
        String content = "";
        List aList = Arrays.asList("A","B","C","D","E","F","G","H");
        if (null != node && node.getChildCount() > 0) {
            for (int j = 0; j < node.getChildCount(); j++) {

                AccessibilityNodeInfo child = node.getChild(j);
                if (child == null) continue;

                if("android.view.View".equals(child.getClassName()) ||
                    "android.widget.TextView".equals(child.getClassName())){
                    CharSequence text = child.getText();
                    if(text != null && text.toString() != "" &&
                            !aList.contains(text.toString()))  subjectInfo +="|" + text.toString();
                }
                // 递归解析
                GetWebViewText(child);
            }
        }
    }
}
