package com.example.utils;

import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.example.appexmple.MainActivity;

import java.io.File;

/**
 * Created by CINDY on 2017/10/1.
 * 文件和文件夹操作
 */

public class FileUtil {
    // 项目文件根目录
    public static File ROOTDIR;

    // 判断SD卡是否存在
    public static boolean sdCardExist() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    // 创建应用文件根目录
    public static void createAppDirectory(File rootStr){
        if(sdCardExist()) {
            ROOTDIR = rootStr;
            File rootDir = new File(ROOTDIR.getParent());
            if(!rootDir.exists())
                rootDir.mkdirs();
        }
    }

    // 在应用文件根目录下创建文件
    public static  void  createFileDir(String fileDir){
        String path = ROOTDIR +fileDir;
        File createPath = new File(path);
        if(!createPath.exists()) {
            createPath.mkdir();
        }
    }
}
