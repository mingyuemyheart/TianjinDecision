package com.hf.tianjin.dto;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class ChartDto implements Parcelable{

	public String name;//实况类型名称
	public String imgUrl;//图片地址
	public String time;//图片对应的时间
	public String tag;//图片的真实地址
	public List<ChartDto> itemList = new ArrayList<ChartDto>();//子项数组

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.name);
		dest.writeString(this.imgUrl);
		dest.writeString(this.time);
		dest.writeString(this.tag);
		dest.writeTypedList(this.itemList);
	}

	public ChartDto() {
	}

	protected ChartDto(Parcel in) {
		this.name = in.readString();
		this.imgUrl = in.readString();
		this.time = in.readString();
		this.tag = in.readString();
		this.itemList = in.createTypedArrayList(ChartDto.CREATOR);
	}

	public static final Creator<ChartDto> CREATOR = new Creator<ChartDto>() {
		@Override
		public ChartDto createFromParcel(Parcel source) {
			return new ChartDto(source);
		}

		@Override
		public ChartDto[] newArray(int size) {
			return new ChartDto[size];
		}
	};
}
