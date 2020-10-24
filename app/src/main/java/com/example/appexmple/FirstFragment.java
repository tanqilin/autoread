package com.example.appexmple;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.service.AIReadService;

public class FirstFragment extends Fragment implements View.OnClickListener{

    LinearLayout btnOnServer,btnOnApp,btnOnQiangguo;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnOnServer = (LinearLayout) view.findViewById(R.id.open_server);
        btnOnApp = (LinearLayout) view.findViewById(R.id.open_app_mooc);
        btnOnQiangguo = (LinearLayout) view.findViewById(R.id.open_app_qiangguo);
        btnOnQiangguo.setOnClickListener(this);
        btnOnServer.setOnClickListener(this);
        btnOnApp.setOnClickListener(this);
    }

    //按钮点击事件响应
    @Override
    public void onClick(View v) {
        Intent intent = null;
        PackageManager packageManager = getActivity().getPackageManager();

        switch (v.getId()){
            case R.id.open_server:
                if(!AIReadService.isStart()){
                    try {
                        getActivity().startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                    }catch (Exception exc){
                        getActivity().startActivity(new Intent(Settings.ACTION_SETTINGS));
                        exc.printStackTrace();
                    }
                }else{
                    Toast.makeText(getContext(), "服务已在运行中...", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.open_app_mooc:
                //启动名：com.moocxuetang.ui.SplashActivity(军职在线)
                intent= packageManager.getLaunchIntentForPackage("com.moocxuetang");
                if(intent==null){
                    Toast.makeText(getContext(), "当前手机未安装此应用！", Toast.LENGTH_LONG).show();
                }else{
                    startActivity(intent);
                }
                break;
            case R.id.open_app_qiangguo:
                //启动名：cn.xuexi.android(学习强国)
                intent= packageManager.getLaunchIntentForPackage("cn.xuexi.android");
                if(intent==null){
                    Toast.makeText(getContext(), "当前手机未安装此应用！", Toast.LENGTH_LONG).show();
                }else{
                    startActivity(intent);
                }
                break;
            default:break;
        }
    }
}