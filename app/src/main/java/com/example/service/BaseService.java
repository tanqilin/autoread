package com.example.service;

import android.accessibilityservice.AccessibilityService;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
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
        toastMsg("服务已停止！！");
        Log.i("","服务已停止！！");
        super.performGlobalAction(GLOBAL_ACTION_BACK);
    }

    // 获取字符串中的数字
    public int getStrIntNumber(String str){
        try {
            String regEx = "[^0-9]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(str);

            String numStr = m.replaceAll("").trim();
            if (!numStr.isEmpty()) {
                return Integer.parseInt(numStr);
            }
        }catch(Exception e){
            return 0;
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
