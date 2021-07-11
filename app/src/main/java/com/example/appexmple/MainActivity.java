package com.example.appexmple;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.models.MoocStudy;
import com.example.models.UserInfo;
import com.example.utils.HttpUtils;
import com.example.utils.MoocConfigUtil;
import com.example.utils.SystemUtil;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;

import okhttp3.Response;

/*
 * 程序主窗口
 */
public class MainActivity extends AppCompatActivity {
    private long firstTime = 0;
    private NavController controller;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /// 创建程序所需的相关文件
        initAppConfig();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 关联toolbar跟 Navigation
        controller = Navigation.findNavController(this,R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(toolbar,controller);
    }

    @Override
    public boolean onSupportNavigateUp() {
        if(controller != null)
            return controller.navigateUp();
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.action_setting:
                Intent intent = new Intent(this , SettingsActivity.class);
                startActivity(intent);
                // controller.navigate(R.id.SettingFragment);
                break;
            case R.id.action_study:
                controller.navigate(R.id.HelpFragment);
                break;
            case R.id.action_newapp:
                AlertDialog showNew = new AlertDialog.Builder(this)
                        .setView(R.layout.dialog_getupdate)
                        .create();
                showNew.show();
                break;
            case R.id.action_about:
                AlertDialog alertDialog1 = new AlertDialog.Builder(this)
                        .setTitle("关于我") //标题
                        .setMessage("作者：谭其林\n邮箱：1135574399@qq.com\n声明：此程序只供学习使用\n\n1.微信搜索公众号“谭其林”获取最新版本！\n2.使用中如有交流意见可以直接发送消息给公众号！") //内容
                        .setIcon(R.drawable.tanqilin)//图标
                        .create();
                alertDialog1.show();
                break;
            default:break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 创建应用程序配置环境
     */
    private void initAppConfig(){
        try {
            String[] files = this.fileList();
            if(files.length == 0 ){
                MoocConfigUtil.initAppConfig(openFileOutput("tanqilin.xml", MODE_PRIVATE));

                // 发送用户信息
                UserInfo user = new UserInfo();
                user.setAndroidId(SystemUtil.getAndroidId(getApplicationContext()));
                user.setPhoneName(SystemUtil.getSystemModel());
                String jsonString = new Gson().toJson(user);
                HttpUtils.post(HttpUtils.httpUrl, jsonString);
            }
        }catch (Exception e){}
    }

    /**
     * 获取当前APP版本
     * @param mContext
     * @return
     */
    public int getVersionCode(Context mContext) {
        int versionCode = 0;
        try {
            //获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = mContext.getPackageManager().
                    getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    @Override
    public void onBackPressed() {
        long secondTime = System.currentTimeMillis();
        if (secondTime - firstTime > 1000) {
            firstTime = secondTime;
            if(controller != null) controller.navigateUp();
        } else {
            System.exit(0);
        }
    }
}