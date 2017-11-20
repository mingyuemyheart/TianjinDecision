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
import com.hf.tianjin.view.RainView;

public class StationDetailRainFragment extends Fragment{
	
	private TextView tvCurrent1hRain = null;
	private TextView tvStatis3hRain = null;
	private TextView tvStatis6hRain = null;
	private TextView tvStatis24hRain = null;
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
	private RainView rainView = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.station_detail_rain_fragment, null);
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
		tvCurrent1hRain = (TextView) view.findViewById(R.id.tvCurrent1hRain);
		tvStatis3hRain = (TextView) view.findViewById(R.id.tvStatis3hRain);
		tvStatis6hRain = (TextView) view.findViewById(R.id.tvStatis6hRain);
		tvStatis24hRain = (TextView) view.findViewById(R.id.tvStatis24hRain);
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
			if (!TextUtils.equals(data.current1hRain, CONST.noValue)) {
				tvCurrent1hRain.setText(data.current1hRain);
				tv1.setText(getString(R.string.unit_mm));
			}else {
				tvCurrent1hRain.setText(CONST.noValue);
			}
			
			if (!TextUtils.equals(data.statis3hRain, CONST.noValue)) {
				tvStatis3hRain.setText(data.statis3hRain);
				tv2.setText(getString(R.string.unit_mm));
			}else {
				tvStatis3hRain.setText(CONST.noValue);
			}
			
			if (!TextUtils.equals(data.statis6hRain, CONST.noValue)) {
				tvStatis6hRain.setText(data.statis6hRain);
				tv3.setText(getString(R.string.unit_mm));
			}else {
				tvStatis6hRain.setText(CONST.noValue);
			}
			
			if (!TextUtils.equals(data.statis24hRain, CONST.noValue)) {
				tvStatis24hRain.setText(data.statis24hRain);
				tv4.setText(getString(R.string.unit_mm));
			}else {
				tvStatis24hRain.setText(CONST.noValue);
			}
			
			warningList.clear();
			warningList.addAll(getArguments().<WarningDto>getParcelableArrayList("warningList"));
			rainView = new RainView(getActivity());
			rainView.setData(data.dataList, warningList);
			
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
		llContainer1.addView(rainView, (int)(CommonUtil.dip2px(getActivity(), viewWidth)), (int)(CommonUtil.dip2px(getActivity(), 250)));
	}
	
	private void showLandscape() {
		llContent.setVisibility(View.GONE);
		llContainer1.removeAllViews();
		llContainer1.addView(rainView, (int)(CommonUtil.dip2px(getActivity(), viewWidth/density)), (int)(CommonUtil.dip2px(getActivity(), 250)));
	}
	
}
