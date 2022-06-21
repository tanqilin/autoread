package com.example.appexmple;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * 定义一个广播接收器
 */
public class MyBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "MyBroadcastReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: receive in MyBroadcastReceiver");
        Toast.makeText(context,"receive in MyBroadcastReceiver",Toast.LENGTH_SHORT).show();
    }
}
