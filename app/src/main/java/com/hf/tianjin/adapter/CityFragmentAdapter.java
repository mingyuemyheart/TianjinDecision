package com.hf.tianjin.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hf.tianjin.R;
import com.hf.tianjin.dto.CityDto;

public class CityFragmentAdapter extends BaseAdapter{
	
	private Context mContext = null;
	private LayoutInflater mInflater = null;
	private List<CityDto> mArrayList = new ArrayList<CityDto>();
	
	private final class ViewHolder{
		TextView tvName;
		ImageView imageView;
	}
	
	private ViewHolder mHolder = null;
	
	public CityFragmentAdapter(Context context, List<CityDto> mArrayList) {
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
			convertView = mInflater.inflate(R.layout.city_item, null);
			mHolder = new ViewHolder();
			mHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
			mHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		
		CityDto dto = mArrayList.get(position);
		mHolder.tvName.setText(dto.disName);
		if (dto.isLocation) {
			mHolder.imageView.setVisibility(View.VISIBLE);
			mHolder.imageView.setImageResource(R.drawable.iv_location_red);
		}else {
			if (dto.isSelected) {
				mHolder.imageView.setVisibility(View.VISIBLE);
				mHolder.imageView.setImageResource(R.drawable.iv_city_selected);
			}else {
				mHolder.imageView.setVisibility(View.INVISIBLE);
			}
		}
		
		return convertView;
	}

}
