package com.hf.tianjin.common;

import android.app.Application;

import com.umeng.socialize.PlatformConfig;

public class MyApplication extends Application{
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
	{
		//umeng分享的平台注册
		PlatformConfig.setWeixin("wx804097a6b9936998", "101ecbf5cda175f0bda696488ac75665");
		PlatformConfig.setQQZone("1105801661", "sxRQtuRvfnwohkKQ");
	}

}
