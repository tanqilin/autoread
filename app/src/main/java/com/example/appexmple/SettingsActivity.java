package com.example.appexmple;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.example.database.models.MoocConfig;
import com.example.utils.MoocConfigUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

// 软件设置页面
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    // 监听 ActionBar 中的按钮
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 设置页面交互逻辑
     */
    public static class SettingsFragment extends PreferenceFragmentCompat{
        private EditTextPreference textKey;
        private ListPreference listCount,listTime;
        private MoocConfig config = null;
        private Context mContext = null;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            textKey = findPreference("signature");
            listCount = findPreference("readCount");
            listTime = findPreference("readTime");
            mContext = getContext();

            /* 读取配置信息 */
            try {
                config = MoocConfigUtil.readAppConfig(mContext.openFileInput("tanqilin.xml"));

                textKey.setText(config.getUserKey());
                listCount.setValue(config.getReadCount()+"");
                listTime.setValue(config.getReadTime()+"");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            /* 跟新配置文件 */
            textKey.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
//                    try {
//                        config.setUserKey(newValue.toString());
//                        MoocConfigUtil.updateAppConfig(mContext.openFileOutput("tanqilin.xml", MODE_PRIVATE),config);
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
                    return true;
                }
            });

            listCount.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    try {
                        config.setReadCount(Integer.parseInt(newValue.toString()));
                        MoocConfigUtil.updateAppConfig(mContext.openFileOutput("tanqilin.xml", MODE_PRIVATE),config);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            });

            listTime.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    try {
                        config.setReadTime(Integer.parseInt(newValue.toString()));
                        MoocConfigUtil.updateAppConfig(mContext.openFileOutput("tanqilin.xml", MODE_PRIVATE),config);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            });
        }
    }
}