package com.hf.tianjin.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hf.tianjin.MainActivity;

/**
 * 开机自动启动
 */

public class BootBroadcastReceiver extends BroadcastReceiver {

    //重写onReceive方法
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Intent i = new Intent(context, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }

}
