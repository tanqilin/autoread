package com.example.appexmple;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.models.MoocStudy;
import com.example.utils.FileUtil;

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

        initAppConfig();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        MoocStudy.getInstance();

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
                controller.navigate(R.id.SettingFragment);
                break;
            case R.id.action_study:
                controller.navigate(R.id.HelpFragment);
                break;
            case R.id.action_about:
                AlertDialog alertDialog1 = new AlertDialog.Builder(this)
                        .setTitle("关于我") //标题
                        .setMessage("作者：谭其林\n邮箱：1135574399@qq.com\n声明：此程序只供学习使用\n\n使用中如有交流意见请发送到此邮箱！") //内容
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
        FileUtil.createAppDirectory(this.getExternalFilesDir(""));
        FileUtil.createFileDir("/config");
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