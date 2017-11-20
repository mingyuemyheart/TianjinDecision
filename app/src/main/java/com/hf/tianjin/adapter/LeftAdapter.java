package com.hf.tianjin.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hf.tianjin.R;
import com.hf.tianjin.dto.WarningDto;
import com.hf.tianjin.utils.CommonUtil;

@SuppressLint("SimpleDateFormat")
public class LeftAdapter extends BaseAdapter {
	
	private Context mContext = null;
	private LayoutInflater mInflater = null;
	private List<WarningDto> mArrayList = null;
	private SimpleDateFormat sdf2 = new SimpleDateFormat("HH");
	
	private final class ViewHolder {
		ImageView ivLocation;
		TextView cityName;
		TextView cityName2;
		TextView tvTemp;
		ImageView ivPhe;
		LinearLayout front;
		LinearLayout back;
	}
	
	private ViewHolder mHolder = null;
	
	public LeftAdapter(Context context, List<WarningDto> mArrayList) {
		mContext = context;
		this.mArrayList = mArrayList;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return mArrayList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public int getViewTypeCount() {
		// menu type count
		return 1;
	}
	
	@Override
	public int getItemViewType(int position) {
		if (position == 0) {
			return 1;
		}
		return 0;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.left_cell, null);
			mHolder = new ViewHolder();
			mHolder.ivLocation = (ImageView) convertView.findViewById(R.id.ivLocation);
			mHolder.cityName = (TextView) convertView.findViewById(R.id.cityName);
			mHolder.cityName2 = (TextView) convertView.findViewById(R.id.cityName2);
			mHolder.tvTemp = (TextView) convertView.findViewById(R.id.tvTemp);
			mHolder.ivPhe = (ImageView) convertView.findViewById(R.id.ivPhe);
			mHolder.front = (LinearLayout) convertView.findViewById(R.id.front);
			mHolder.back = (LinearLayout) convertView.findViewById(R.id.back);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		
		WarningDto dto = mArrayList.get(position);
		mHolder.cityName.setText(dto.cityName);
		
		LayoutParams params = (LayoutParams) mHolder.front.getLayoutParams();
		LayoutParams params2 = (LayoutParams) mHolder.back.getLayoutParams();
		if (!TextUtils.isEmpty(dto.cityName2)) {
			mHolder.cityName2.setText(dto.cityName2);
			mHolder.cityName2.setVisibility(View.VISIBLE);
			params.height = (int) CommonUtil.dip2px(mContext, 50);
			params2.height = (int) CommonUtil.dip2px(mContext, 50);
		}else {
			mHolder.cityName2.setVisibility(View.GONE);
			params.height = (int) CommonUtil.dip2px(mContext, 40);
			params2.height = (int) CommonUtil.dip2px(mContext, 40);
		}
		mHolder.front.setLayoutParams(params);
		mHolder.back.setLayoutParams(params);
		
		if (!TextUtils.isEmpty(dto.temp)) {
			mHolder.tvTemp.setText(dto.temp+mContext.getString(R.string.unit_degree));
		}
		
		if (!TextUtils.isEmpty(dto.pheCode)) {
			Drawable drawable = mContext.getResources().getDrawable(R.drawable.phenomenon_drawable);
			try {
				long zao8 = sdf2.parse("08").getTime();
				long wan8 = sdf2.parse("20").getTime();
				long current = sdf2.parse(sdf2.format(new Date())).getTime();
				if (current >= zao8 && current < wan8) {
					drawable = mContext.getResources().getDrawable(R.drawable.phenomenon_drawable);
				}else {
					drawable = mContext.getResources().getDrawable(R.drawable.phenomenon_drawable_night);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			drawable.setLevel(Integer.valueOf(dto.pheCode));
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {  
			    mHolder.ivPhe.setBackground(drawable);
			} else {  
			    mHolder.ivPhe.setBackgroundDrawable(drawable);
			}  
			
//			tvPhe.setText(getString(WeatherUtil.getWeatherId(Integer.valueOf(weatherCode))));
		}
		
		if (position == 0) {
			mHolder.ivLocation.setVisibility(View.VISIBLE);
		}else {
			mHolder.ivLocation.setVisibility(View.INVISIBLE);
		}
		
		return convertView;
	}

}
