package com.hf.tianjin.dto;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class StationMonitorDto implements Parcelable{

	public int section;
	public String stationId;//站点号
	public String name;//站点名称
	public String time;
	public String lat;
	public String lng;
	public String ballTemp;//干球温度
	public String airPressure;//气压
	public String humidity;//湿度
	public String precipitation1h;//1h降水量
	public String precipitation3h;//3h降水量
	public String precipitation6h;//6h降水量
	public String precipitation24h;//24h降水量
	public String windDir;//风向
	public String windSpeed;//风速
	public String distance;//距离
	public String pointTemp;//露点温度
	public String visibility;//能见度
	public String value;
	
	public String partition;
	public String provinceName;
	public String cityName;
	public String districtName;
	
	public String currentTemp;//当前温度
	public String current1hRain;//当前1h降水量
	public String currentHumidity;//当前湿度
	public String currentWindSpeed;//当前风速
	public String currentPressure;//当前气压
	public String currentVisible;//当前能见度
	
	public String statisHighTemp;//24h最高气温
	public String statisLowTemp;//24h最低气温
	public String statisAverTemp;//24h平巨额气温
	public String statis3hRain;//3h降水
	public String statis6hRain;//6h降水
	public String statis24hRain;//24h降水
	public String statisMaxHumidity;//24h最大湿度
	public String statisMinHumidity;//24h最小湿度
	public String statisMaxSpeed;//24h最大风速
	public String statisMaxPressure;//24h最大气压
	public String statisMinPressure;//24h最小气压
	public String statisMinVisible;//24h最小能见度
	public List<StationMonitorDto> dataList = new ArrayList<StationMonitorDto>();//24h数据list
	public float x = 0;//x轴坐标点
	public float y = 0;//y轴坐标点

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.section);
		dest.writeString(this.stationId);
		dest.writeString(this.name);
		dest.writeString(this.time);
		dest.writeString(this.lat);
		dest.writeString(this.lng);
		dest.writeString(this.ballTemp);
		dest.writeString(this.airPressure);
		dest.writeString(this.humidity);
		dest.writeString(this.precipitation1h);
		dest.writeString(this.precipitation3h);
		dest.writeString(this.precipitation6h);
		dest.writeString(this.precipitation24h);
		dest.writeString(this.windDir);
		dest.writeString(this.windSpeed);
		dest.writeString(this.distance);
		dest.writeString(this.pointTemp);
		dest.writeString(this.visibility);
		dest.writeString(this.value);
		dest.writeString(this.partition);
		dest.writeString(this.provinceName);
		dest.writeString(this.cityName);
		dest.writeString(this.districtName);
		dest.writeString(this.currentTemp);
		dest.writeString(this.current1hRain);
		dest.writeString(this.currentHumidity);
		dest.writeString(this.currentWindSpeed);
		dest.writeString(this.currentPressure);
		dest.writeString(this.currentVisible);
		dest.writeString(this.statisHighTemp);
		dest.writeString(this.statisLowTemp);
		dest.writeString(this.statisAverTemp);
		dest.writeString(this.statis3hRain);
		dest.writeString(this.statis6hRain);
		dest.writeString(this.statis24hRain);
		dest.writeString(this.statisMaxHumidity);
		dest.writeString(this.statisMinHumidity);
		dest.writeString(this.statisMaxSpeed);
		dest.writeString(this.statisMaxPressure);
		dest.writeString(this.statisMinPressure);
		dest.writeString(this.statisMinVisible);
		dest.writeTypedList(this.dataList);
		dest.writeFloat(this.x);
		dest.writeFloat(this.y);
	}

	public StationMonitorDto() {
	}

	protected StationMonitorDto(Parcel in) {
		this.section = in.readInt();
		this.stationId = in.readString();
		this.name = in.readString();
		this.time = in.readString();
		this.lat = in.readString();
		this.lng = in.readString();
		this.ballTemp = in.readString();
		this.airPressure = in.readString();
		this.humidity = in.readString();
		this.precipitation1h = in.readString();
		this.precipitation3h = in.readString();
		this.precipitation6h = in.readString();
		this.precipitation24h = in.readString();
		this.windDir = in.readString();
		this.windSpeed = in.readString();
		this.distance = in.readString();
		this.pointTemp = in.readString();
		this.visibility = in.readString();
		this.value = in.readString();
		this.partition = in.readString();
		this.provinceName = in.readString();
		this.cityName = in.readString();
		this.districtName = in.readString();
		this.currentTemp = in.readString();
		this.current1hRain = in.readString();
		this.currentHumidity = in.readString();
		this.currentWindSpeed = in.readString();
		this.currentPressure = in.readString();
		this.currentVisible = in.readString();
		this.statisHighTemp = in.readString();
		this.statisLowTemp = in.readString();
		this.statisAverTemp = in.readString();
		this.statis3hRain = in.readString();
		this.statis6hRain = in.readString();
		this.statis24hRain = in.readString();
		this.statisMaxHumidity = in.readString();
		this.statisMinHumidity = in.readString();
		this.statisMaxSpeed = in.readString();
		this.statisMaxPressure = in.readString();
		this.statisMinPressure = in.readString();
		this.statisMinVisible = in.readString();
		this.dataList = in.createTypedArrayList(StationMonitorDto.CREATOR);
		this.x = in.readFloat();
		this.y = in.readFloat();
	}

	public static final Creator<StationMonitorDto> CREATOR = new Creator<StationMonitorDto>() {
		@Override
		public StationMonitorDto createFromParcel(Parcel source) {
			return new StationMonitorDto(source);
		}

		@Override
		public StationMonitorDto[] newArray(int size) {
			return new StationMonitorDto[size];
		}
	};
}
