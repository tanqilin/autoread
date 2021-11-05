package com.example.utils;

import android.util.Log;
import android.util.Xml;

import com.example.database.models.MoocConfig;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/*
* 军职在线配置文件
 */
public class MoocConfigUtil {

    /**
     * 写入配置信息
     * @param fileStr 配置文件
     */
    public static void initAppConfig(FileOutputStream fileStr,String key){
        XmlSerializer serializer = Xml.newSerializer();

        try {
            serializer.setOutput(fileStr, "utf-8");
            serializer.startDocument("utf-8", true);
            //创建默认配置信息
            serializer.startTag(null,"configs");
            serializer.attribute(null, "id", key);

            serializer.startTag(null,"mooc_config");
            serializer.startTag(null,"count");
            serializer.text("40");
            serializer.endTag(null,"count");

            serializer.startTag(null,"time");
            serializer.text("50");
            serializer.endTag(null,"time");

            serializer.endTag(null,"mooc_config");
            serializer.endTag(null,"configs");
            serializer.endDocument(); //结束文档,并将内容写入文件
            fileStr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取配置信息
     * @param fileStr
     */
    public static MoocConfig readAppConfig(FileInputStream fileStr){
        MoocConfig config = null;

        Log.i("",fileStr.toString());
        XmlPullParser pullParser = Xml.newPullParser();
        try {
            config = new MoocConfig();
            pullParser.setInput(fileStr, "utf-8");
            int eventType = pullParser.getEventType();

            while(eventType!=pullParser.END_DOCUMENT)
            {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if("configs".equals(pullParser.getName())){
                            config.setUserKey(pullParser.getAttributeValue(null, "id"));       //获取编号,并初始化内容
                            config.setReadCount(40);
                            config.setReadTime(50);

                        }else if("count".equals(pullParser.getName())){
                            config.setReadCount(Integer.parseInt(pullParser.nextText()));
                        }else if("time".equals(pullParser.getName())){
                            config.setReadTime(Integer.parseInt(pullParser.nextText()));
                        }
                        break;
                    case XmlPullParser.END_TAG: break;
                }

                eventType = pullParser.next();
            }

            fileStr.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return config;
    }

    /**
     * 修改配置信息
     * @param fileStr
     * @param config
     * @return
     */
    public static boolean updateAppConfig(FileOutputStream fileStr,MoocConfig config){
        XmlSerializer serializer = Xml.newSerializer();
        try {
            serializer.setOutput(fileStr, "utf-8");
            serializer.startDocument("utf-8", true);
            //创建默认配置信息
            serializer.startTag(null,"configs");
            serializer.attribute(null, "id", config.getUserKey());

            serializer.startTag(null,"mooc_config");
            serializer.startTag(null,"count");
            serializer.text(config.getReadCount()+"");
            serializer.endTag(null,"count");

            serializer.startTag(null,"time");
            serializer.text(config.getReadTime()+"");
            serializer.endTag(null,"time");

            serializer.endTag(null,"mooc_config");
            serializer.endTag(null,"configs");
            serializer.endDocument(); //结束文档,并将内容写入文件

            fileStr.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
