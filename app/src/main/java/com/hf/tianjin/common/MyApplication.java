package com.hf.tianjin.common;

import android.app.Application;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.shidian.mail.SendMailUtil;
import com.umeng.socialize.PlatformConfig;

public class MyApplication extends Application{

	public static String location = "";
	
	@Override
	public void onCreate() {
		super.onCreate();

		CrashHandler ch = CrashHandler.getInstance();
		ch.init(this);

		//初始化Imageloader
		ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(getApplicationContext());
		ImageLoader.getInstance().init(configuration);
	}
	{
		//umeng分享的平台注册
		PlatformConfig.setWeixin("wx804097a6b9936998", "101ecbf5cda175f0bda696488ac75665");
		PlatformConfig.setQQZone("1105801661", "sxRQtuRvfnwohkKQ");
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		SendMailUtil.send("1023453818@qq.com", MyApplication.location);
	}
}
