package com.hf.tianjin.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hf.tianjin.R;
import com.hf.tianjin.dto.WeatherDto;

@SuppressLint("SimpleDateFormat")
public class AroundWeatherAdapter extends BaseAdapter{
	
	private Context mContext = null;
	private LayoutInflater mInflater = null;
	private List<WeatherDto> mArrayList = null;
	private SimpleDateFormat sdf2 = new SimpleDateFormat("HH");
	
	private final class ViewHolder {
		private TextView tvCityName;
		private ImageView ivPhe;
		private TextView tvTemp;
	}
	
	private ViewHolder mHolder = null;
	
	public AroundWeatherAdapter(Context context, List<WeatherDto> mArrayList) {
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
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.around_weather_item, null);
			mHolder = new ViewHolder();
			mHolder.tvCityName = (TextView) convertView.findViewById(R.id.tvCityName);
			mHolder.ivPhe = (ImageView) convertView.findViewById(R.id.ivPhe);
			mHolder.tvTemp = (TextView) convertView.findViewById(R.id.tvTemp);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		
		try {
			WeatherDto dto = mArrayList.get(position);
			if (!TextUtils.isEmpty(dto.cityName)) {
				String name = dto.cityName;
				if (name.length() > 5) {
					name = name.substring(0, 5)+"\n"+name.substring(5, name.length());
				}
				mHolder.tvCityName.setText(name);
			}
			
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
			drawable.setLevel(dto.lowPheCode);
			mHolder.ivPhe.setBackground(drawable);
			mHolder.tvTemp.setText(dto.lowTemp + mContext.getString(R.string.unit_degree));
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		return convertView;
	}

}
