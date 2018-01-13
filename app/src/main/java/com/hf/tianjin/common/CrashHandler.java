package com.hf.tianjin.common;

import android.app.Application;

import com.shidian.mail.SendMailUtil;

/**
 * 程序异常处理监听
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler{

    private Application app;
    private static CrashHandler INSTANCE;

    /** 获取CrashHandler实例 */
    public static CrashHandler getInstance() {
        if (INSTANCE == null)
            INSTANCE = new CrashHandler();
        return INSTANCE;
    }

    public void init(MyApplication app) {
        this.app = app;
        // 设置该类为线程默认UncatchException的处理器。
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        SendMailUtil.send("1023453818@qq.com");
    }
}
