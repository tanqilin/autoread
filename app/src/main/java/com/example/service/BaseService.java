package com.example.service;

import android.accessibilityservice.AccessibilityService;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseService extends AccessibilityService {
    private static final String TAG = "";

    public BaseService() {

    }

    // 根据Text点击指定节点
    public Boolean onClickNode(String paramString)
    {
        AccessibilityNodeInfo localAccessibilityNodeInfo = getRootInActiveWindow();
        if (localAccessibilityNodeInfo == null) {
            return Boolean.FALSE;
        }

        List<AccessibilityNodeInfo> infos = localAccessibilityNodeInfo.findAccessibilityNodeInfosByText(paramString);
        if ((infos != null) && (!infos.isEmpty()))
        {
            Iterator<AccessibilityNodeInfo> iterator = infos.iterator();
            while (iterator.hasNext())
            {
                localAccessibilityNodeInfo = iterator.next();
                if (localAccessibilityNodeInfo != null)
                {
                    onClickNode(localAccessibilityNodeInfo);
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.FALSE;
    }

    // 根据node点击指定位置
    public boolean onClickNode(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
        AccessibilityNodeInfo localAccessibilityNodeInfo = paramAccessibilityNodeInfo;
        if (paramAccessibilityNodeInfo == null) {
            return false;
        }
        while (localAccessibilityNodeInfo != null)
        {
            if (localAccessibilityNodeInfo.isClickable())
            {
                return localAccessibilityNodeInfo.performAction(16);
            }
            localAccessibilityNodeInfo = localAccessibilityNodeInfo.getParent();
        }
        return false;
    }

    // 根据节点ID点击节点
    public boolean onClickNodeById(String nodeId){
        AccessibilityNodeInfo recyclerView = getRootInActiveWindow();
        if(recyclerView != null){
            List<AccessibilityNodeInfo> btns = recyclerView.findAccessibilityNodeInfosByViewId(nodeId);
            if(!btns.isEmpty() && btns.size() > 0){
                AccessibilityNodeInfo btn = btns.get(0);
                return onClickNode(btn);
            }
        }
        return false;
    }

    // 根据节点ID查找节点
    public AccessibilityNodeInfo getNodeById(String nodeId){
        AccessibilityNodeInfo recyclerView = getRootInActiveWindow();
        if(recyclerView != null){
            List<AccessibilityNodeInfo> btns = recyclerView.findAccessibilityNodeInfosByViewId(nodeId);
            if(!btns.isEmpty()){
                return btns.get(0);
            }
        }
        return null;
    }

    public List<AccessibilityNodeInfo>  getNodeByIds(String nodeId){
        AccessibilityNodeInfo recyclerView = getRootInActiveWindow();
        if(recyclerView != null){
            List<AccessibilityNodeInfo> btns = recyclerView.findAccessibilityNodeInfosByViewId(nodeId);
            if(!btns.isEmpty()){
                return btns;
            }
        }
        return null;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

    }

    @Override
    public void onInterrupt() {

    }

    // 默认阅读方式
    public void defaultRead(AccessibilityEvent event){
        AccessibilityNodeInfo recyclerView = getRootInActiveWindow();
        if(recyclerView != null){
            // 根据ID找到文章列表容器
            List<AccessibilityNodeInfo> infos = recyclerView.findAccessibilityNodeInfosByViewId("com.moocxuetang:id/rcy_hot_more");
            if(!infos.isEmpty()) {
                AccessibilityNodeInfo contails = infos.get(0);
                // 查找容器中的文章列表
                if (contails != null) {
                    List<AccessibilityNodeInfo> childs = contails.findAccessibilityNodeInfosByViewId("com.moocxuetang:id/tvTitle");
                    Iterator<AccessibilityNodeInfo> iterator = childs.iterator();

                    while (iterator.hasNext()) {
                        // Log.i(TAG, "文章标题: "+ iterator.next().getText());
                        // 根据标题跳转
                        // super.onClickNode(iterator.next().getText().toString());
                    }
                }
            }
        }
    }

    // 获取字符串中的数字
    public int getStrIntNumber(String str){
        String regEx="[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);

        String numStr =  m.replaceAll("").trim();
        if(!numStr.isEmpty()){
           return Integer.parseInt(numStr);
        }
        return 0;
    }

    // 弹出提示信息
    public void toastMsg(final String msg){
        new Thread(){
            public void run(){
                Looper.prepare();
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }.start();
    }
}
