package com.hf.tianjin.dto;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;


public class AqiDto implements Parcelable{

	public float x = 0;//x轴坐标点
	public float y = 0;//y轴坐标点
	public String date = null;//时间
	public String aqi = null;//空气质量
	public String pm2_5 = null;
	public String pm10 = null;
	public String NO2 = null;
	public String SO2 = null;
	public String O3 = null;
	public String CO = null;
	public List<AqiDto> aqiList = new ArrayList<AqiDto>();

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeFloat(this.x);
		dest.writeFloat(this.y);
		dest.writeString(this.date);
		dest.writeString(this.aqi);
		dest.writeString(this.pm2_5);
		dest.writeString(this.pm10);
		dest.writeString(this.NO2);
		dest.writeString(this.SO2);
		dest.writeString(this.O3);
		dest.writeString(this.CO);
		dest.writeTypedList(this.aqiList);
	}

	public AqiDto() {
	}

	protected AqiDto(Parcel in) {
		this.x = in.readFloat();
		this.y = in.readFloat();
		this.date = in.readString();
		this.aqi = in.readString();
		this.pm2_5 = in.readString();
		this.pm10 = in.readString();
		this.NO2 = in.readString();
		this.SO2 = in.readString();
		this.O3 = in.readString();
		this.CO = in.readString();
		this.aqiList = in.createTypedArrayList(AqiDto.CREATOR);
	}

	public static final Creator<AqiDto> CREATOR = new Creator<AqiDto>() {
		@Override
		public AqiDto createFromParcel(Parcel source) {
			return new AqiDto(source);
		}

		@Override
		public AqiDto[] newArray(int size) {
			return new AqiDto[size];
		}
	};
}
