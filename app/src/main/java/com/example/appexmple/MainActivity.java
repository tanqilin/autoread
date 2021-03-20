package com.example.appexmple;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.models.MoocStudy;
import com.example.utils.MoocConfigUtil;

import java.io.File;
import java.io.IOException;

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
            }

//            if (!(new File(String.valueOf(this.getExternalFilesDir("tanqilin.xml")))).exists())
//            {
//                Log.i("1111",String.valueOf(this.getExternalFilesDir("tanqilin.xml")));
//
//                MoocConfigUtil.initAppConfig(openFileOutput("tanqilin.xml", MODE_PRIVATE));
//            }
        }catch (Exception e){}
    }

    @Override
    public void onBackPressed() {
        long secondTime = System.currentTimeMillis();
        if (secondTime - firstTime > 1000) {
            //Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            firstTime = secondTime;
            if(controller != null) controller.navigateUp();
        } else {
            System.exit(0);
        }
    }
}