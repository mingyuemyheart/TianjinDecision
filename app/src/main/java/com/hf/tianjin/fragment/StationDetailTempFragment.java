package com.hf.tianjin.fragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hf.tianjin.R;
import com.hf.tianjin.common.CONST;
import com.hf.tianjin.dto.StationMonitorDto;
import com.hf.tianjin.dto.WarningDto;
import com.hf.tianjin.utils.CommonUtil;
import com.hf.tianjin.view.TemperatureView;

public class StationDetailTempFragment extends Fragment{
	
	private TextView tvCurrentTemp = null;//当前温度
	private TextView tvHighTemp = null;//最高气温
	private TextView tvLowTemp = null;//最低气温
	private TextView tvAverTemp = null;//平均气温
	private TextView tv1, tv2, tv3, tv4;
	private LinearLayout llContainer1 = null;
	private LinearLayout llContent = null;
	private StationMonitorDto data = null;
	private List<WarningDto> warningList = new ArrayList<WarningDto>();
	private int viewWidth = 0;
	private int width = 0;
	private int height = 0;
	private float density = 0;
	private boolean isPortrait = true;//判断默认进来是否为竖屏
	private TemperatureView temperatureView = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.station_detail_temp_fragment, null);
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initWidget(view);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (isPortrait) {//默认竖屏进来
			if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
				viewWidth = width;
				showPortrait();
			}else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
				viewWidth = height;
				showLandscape();
			}
		}else {//默认横屏进来
			if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
				viewWidth = height;
				showPortrait();
			}else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
				viewWidth = width;
				showLandscape();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void initWidget(View view) {
		tvCurrentTemp = (TextView) view.findViewById(R.id.tvCurrentTemp);
		tvHighTemp = (TextView) view.findViewById(R.id.tvHighTemp);
		tvLowTemp = (TextView) view.findViewById(R.id.tvLowTemp);
		tvAverTemp = (TextView) view.findViewById(R.id.tvAverTemp);
		tv1 = (TextView) view.findViewById(R.id.tv1);
		tv2 = (TextView) view.findViewById(R.id.tv2);
		tv3 = (TextView) view.findViewById(R.id.tv3);
		tv4 = (TextView) view.findViewById(R.id.tv4);
		llContainer1 = (LinearLayout) view.findViewById(R.id.llContainer1);
		llContent = (LinearLayout) view.findViewById(R.id.llContent);
		
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		height = dm.heightPixels;
		density = dm.density;

		data = getArguments().getParcelable("data");
		if (data != null) {
			if (!TextUtils.equals(data.currentTemp, CONST.noValue)) {
				tvCurrentTemp.setText(data.currentTemp);
				tv1.setText(getString(R.string.unit_degree));
			}else {
				tvCurrentTemp.setText(CONST.noValue);
			}
			
			if (!TextUtils.equals(data.statisHighTemp, CONST.noValue)) {
				tvHighTemp.setText(data.statisHighTemp);
				tv2.setText(getString(R.string.unit_degree));
			}else {
				tvHighTemp.setText(CONST.noValue);
			}
			
			if (!TextUtils.equals(data.statisLowTemp, CONST.noValue)) {
				tvLowTemp.setText(data.statisLowTemp);
				tv3.setText(getString(R.string.unit_degree));
			}else {
				tvLowTemp.setText(CONST.noValue);
			}
			
			if (!TextUtils.equals(data.statisAverTemp, CONST.noValue)) {
				tvAverTemp.setText(data.statisAverTemp);
				tv4.setText(getString(R.string.unit_degree));
			}else {
				tvAverTemp.setText(CONST.noValue);
			}
			
			warningList.clear();
			warningList.addAll(getArguments().<WarningDto>getParcelableArrayList("warningList"));
			temperatureView = new TemperatureView(getActivity());
			temperatureView.setData(data.dataList, warningList);
			
			if (width < height) {
				isPortrait = true;
				viewWidth = width;
				showPortrait();
			}else {
				isPortrait = false;
				viewWidth = width;
				showLandscape();
			}
			
		}
	}
	
	private void showPortrait() {
		llContent.setVisibility(View.VISIBLE);
		llContainer1.removeAllViews();
		llContainer1.addView(temperatureView, (int)(CommonUtil.dip2px(getActivity(), viewWidth)), (int)(CommonUtil.dip2px(getActivity(), 250)));
	}
	
	private void showLandscape() {
		llContent.setVisibility(View.GONE);
		llContainer1.removeAllViews();
		llContainer1.addView(temperatureView, (int)(CommonUtil.dip2px(getActivity(), viewWidth/density)), (int)(CommonUtil.dip2px(getActivity(), 250)));
	}
	
}
