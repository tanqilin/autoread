package com.example.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 控制器基类
 */
public class BaseMonitor {
    public AIReadService serviceContext = null;
    public BaseMonitor(AIReadService context){
        serviceContext = context;
    }

    /**
     * 根据控件ID点击控件
     * @param recyclerView
     * @param nodeId
     * @return
     */
    public boolean onClickNodeById(AccessibilityNodeInfo recyclerView, String nodeId){
        if(recyclerView != null){
            List<AccessibilityNodeInfo> btns = recyclerView.findAccessibilityNodeInfosByViewId(nodeId);
            if(!btns.isEmpty() && btns.size() > 0){
                AccessibilityNodeInfo btn = btns.get(0);
                return onClickNode(btn);
            }
        }
        return false;
    }

    /**
     * 根据node点击指定位置
     * @param paramAccessibilityNodeInfo
     * @return
     */
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

    /**
     * 根据文本点击按钮
     * @param recyclerView
     * @param paramString
     * @return
     */
    public Boolean onClickNode(AccessibilityNodeInfo recyclerView,String paramString)
    {
        if (recyclerView == null) {
            return Boolean.FALSE;
        }

        List<AccessibilityNodeInfo> infos = recyclerView.findAccessibilityNodeInfosByText(paramString);
        if ((infos != null) && (!infos.isEmpty()))
        {
            Iterator<AccessibilityNodeInfo> iterator = infos.iterator();
            while (iterator.hasNext())
            {
                recyclerView = iterator.next();
                if (recyclerView != null)
                {
                    onClickNode(recyclerView);
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.FALSE;
    }

    /**
     * 根据节点ID查找节点
     * @param recyclerView
     * @param nodeId
     * @return
     */
    public AccessibilityNodeInfo getNodeById(AccessibilityNodeInfo recyclerView,String nodeId){
        if(recyclerView != null){
            List<AccessibilityNodeInfo> btns = recyclerView.findAccessibilityNodeInfosByViewId(nodeId);
            if(!btns.isEmpty()){
                return btns.get(0);
            }
        }
        return null;
    }

    /**
     * 根据ID查询多个节点
     * @param nodeInfo
     * @param nodeId
     * @return
     */
    public List<AccessibilityNodeInfo>  getNodeByIds(AccessibilityNodeInfo nodeInfo,String nodeId){
        if(nodeInfo != null){
            List<AccessibilityNodeInfo> btns = nodeInfo.findAccessibilityNodeInfosByViewId(nodeId);
            if(!btns.isEmpty()){
                return btns;
            }
        }
        return null;
    }

    /**
     * 使用正则表达式获取字符串中的数字
     * @param str
     * @return
     */
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

    /**
     * 模拟手势滑动
     */
    @TargetApi(24)
    public void dispatchGestureMove()
    {
        // 根据手机屏幕尺寸，决定滑动位置
        DisplayMetrics dm2 = serviceContext.getResources().getDisplayMetrics();

        int i = dm2.widthPixels;
        int j = dm2.heightPixels;
        Point localPoint = new Point((int)(i * (Math.random() * 0.5D + 0.2D)), (int)(j * (Math.random() * 0.5D + 0.4D)));


        GestureDescription.Builder localBuilder = new GestureDescription.Builder();

        Path localPath = new Path();
        localPath.moveTo(localPoint.x, localPoint.y);
        localPath.lineTo((float)(localPoint.x + Math.random() * 50.0D), (float)(localPoint.y - 470 - Math.random() * 300.0D));

        localBuilder.addStroke(new GestureDescription.StrokeDescription(localPath, 100L, 800L));
        serviceContext.dispatchGesture(localBuilder.build(), null, null);
    }

    /**
     * 弹出提示信息
     * @param msg
     */
    public void toastMsg(final String msg){
        new Thread(){
            public void run(){
                Looper.prepare();
                Toast.makeText(serviceContext.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }.start();
    }
}
