package com.hf.tianjin.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hf.tianjin.R;
import com.hf.tianjin.dto.StationMonitorDto;

public class StationMonitorDistrictAdapter extends BaseAdapter{
	
	private Context mContext = null;
	private LayoutInflater mInflater = null;
	private List<StationMonitorDto> mArrayList = new ArrayList<StationMonitorDto>();
	public HashMap<Integer, Boolean> isSelected = new HashMap<Integer, Boolean>();
	
	private final class ViewHolder{
		TextView tvName;
	}
	
	private ViewHolder mHolder = null;
	
	public StationMonitorDistrictAdapter(Context context, List<StationMonitorDto> mArrayList) {
		mContext = context;
		this.mArrayList = mArrayList;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		for (int i = 0; i < mArrayList.size(); i++) {
			if (i == 0) {
				isSelected.put(i, true);
			}else {
				isSelected.put(i, false);
			}
		}
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
			convertView = mInflater.inflate(R.layout.station_monitor_province_cell, null);
			mHolder = new ViewHolder();
			mHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		
		StationMonitorDto dto = mArrayList.get(position);
		mHolder.tvName.setText(dto.districtName);
		if (!isSelected.isEmpty() && isSelected.get(position) != null) {
			if (isSelected.get(position) == false) {
//				mHolder.tvName.setBackgroundColor(mContext.getResources().getColor(R.color.white));
				mHolder.tvName.setTextColor(mContext.getResources().getColor(R.color.text_color4));
			}else {
//				mHolder.tvName.setBackgroundResource(R.drawable.iv_search_bg);
				mHolder.tvName.setTextColor(mContext.getResources().getColor(R.color.selected_color));
			}
		}
		
		return convertView;
	}

}
