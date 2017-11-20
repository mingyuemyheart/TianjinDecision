package com.hf.tianjin.common;

import java.io.File;

import android.os.Environment;

import com.hf.tianjin.R;

public class CONST {
	
	public static String USERNAME = "tjweather";
	public static String PASSWORD = "tjweather";
	public static String APPID = "24";
	public static String UID = "2522";
	public static String TIANJINCITYID = "10103";//天津城市id
	public static final String imageSuffix = ".png";//图标后缀名
	public static boolean ISTIANJIN = false;//定位城市是否属于天津辖区内
	public static String noValue = "--";
	public static final String ACTIVITY_NAME = "activity_name";//界面名称
	public static final String WEB_URL = "web_Url";//网页地址的标示
	public static final int MSG_101 = 101;
	public static final int MSG_102 = 102;
	public static String filePath = Environment.getExternalStorageDirectory() + File.separator + "screenshot.png";
	
	//预警颜色对应规则
	public static String[] blue = {"01", "_blue"};
	public static String[] yellow = {"02", "_yellow"};
	public static String[] orange = {"03", "_orange"};
	public static String[] red = {"04", "_red"};
	
	//下拉刷新progresBar四种颜色，其它城市
	public static final int color1 = R.color.title_bg_else;
	public static final int color2 = R.color.title_bg_else;
	public static final int color3 = R.color.title_bg_else;
	public static final int color4 = R.color.title_bg_else;
	
	public class CityInfo {
//		public static final String cityName = "cityName";//城市名称
//		public static final String areaName = "areaName";//区域名称
		public static final String cityId = "cityId";//城市id
		public static final String publishTime = "publishTime";//天气数据发布时间
		public static final String pheCode = "pheCode";//实况天气现象编号
		public static final String factTemp = "factTemp";//实况温度
		public static final String factPressure = "factPressure";//实况气压
		public static final String factHumidity = "factHumidity";//实况湿度
		public static final String factWindDir = "factWindDir";//实况风向编号
		public static final String factWindForce = "factWindForce";//实况风力编号
		public static final String factVisible = "factVisible";//实况能见度
		public static final String todayPheCode = "todayPheCode";//今天天气现象
		public static final String todayHighTemp = "todayHighTemp";//今天最高温度
		public static final String todayLowTemp = "todayLowTemp";//今天最低温度
		public static final String tomorrowPheCode = "tomorrowPheCode";//明天天气现象
		public static final String tomorrowHighTemp = "tomorrowHighTemp";//明天最高温度
		public static final String tomorrowLowTemp = "tomorrowLowTemp";//明天最低温度
		public static final String hourlyCode = "hourlyCode";//逐小时天气编号
		public static final String hourlyTemp = "hourlyTemp";//逐小时温度
		public static final String hourlyTime = "hourlyTime";//逐小时时间
		public static final String hourlyWindDirCode = "hourlyWindDirCode";//逐小时风向编号
		public static final String hourlyWindForceCode = "hourlyWindForceCode";//逐小时风力编号
		public static final String weeklyLowPheCode = "weeklyLowPheCode";//周晚上天气现象编号
		public static final String weeklyLowPhe = "weeklyLowPhe";//周晚上天气现象
		public static final String weeklyLowTemp = "weeklyLowTemp";//周晚上温度
		public static final String weeklyHighPheCode = "weeklyHighPheCode";//周白天天气现象编号
		public static final String weeklyHighPhe = "weeklyHighPhe";//周白天天气现象
		public static final String weeklyHighTemp = "weeklyHighTemp";//周白天温度
		public static final String weeklyWindDir = "weeklyWindDir";//周风向编号
		public static final String weeklyWindForce = "weeklyWindForce";//周风速编号
		public static final String weeklyWeek = "weeklyWeek";//周几
		public static final String weeklyDate = "weeklyDate";//周日期
		public static final String weeklyAqi = "weeklyAqi";//周空气质量
		public static final String quality = "quality";//空气质量
		public static final String aqiDate = "aqiDate";//空气质量日期
		public static final String aqiPm2_5 = "aqiPm2_5";
		public static final String aqiPm10 = "aqiPm10";
		public static final String aqiNO2 = "aqiNO2";
		public static final String aqiSO2 = "aqiSO2";
		public static final String aqiO3 = "aqiO3";
		public static final String aqiCO = "aqiCO";
		public static final String aqiValue = "aqiValue";//aqilist的值
		public static final String minuteTime = "minuteTime";//分钟级降水发布时间
		public static final String description = "description";//分钟级降水描述
		public static final String minuteFall = "minuteFall";//分钟级降水量
		public static final String aroundCityName = "aroundCityName";//周边天气城市名称
		public static final String aroundHighPheCode = "aroundHighPheCode";//周边天气高温编号
		public static final String aroundLowPheCode = "aroundLowPheCode";//周边天气低温编号
		public static final String aroundHighTemp = "aroundHighTemp";//周边天气高温温度
		public static final String aroundLowTemp = "aroundLowTemp";//周边天气低温温度
		public static final String warningHtml = "warningHtml";//预警信息
		public static final String warningLat = "warningLat";//预警信息
		public static final String warningLng = "warningLng";//预警信息
		public static final String warningName = "warningName";//预警信息
	}
	
}
