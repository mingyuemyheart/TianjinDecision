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
import com.hf.tianjin.view.VisibleView;

public class StationDetailVisibilityFragment extends Fragment{
	
	private TextView tvCurrentVisible = null;//当前能见度
	private TextView tvMinVisible = null;//最小能见度
	private TextView tv1, tv2;
	private LinearLayout llContainer1 = null;
	private LinearLayout llContent = null;
	private StationMonitorDto data = null;
	private List<WarningDto> warningList = new ArrayList<WarningDto>();
	private int viewWidth = 0;
	private int width = 0;
	private int height = 0;
	private float density = 0;
	private boolean isPortrait = true;//判断默认进来是否为竖屏
	private VisibleView visibleView = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.station_detail_visiblity_fragment, null);
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
		tvCurrentVisible = (TextView) view.findViewById(R.id.tvCurrentVisible);
		tvMinVisible = (TextView) view.findViewById(R.id.tvMinVisible);
		tv1 = (TextView) view.findViewById(R.id.tv1);
		tv2 = (TextView) view.findViewById(R.id.tv2);
		llContainer1 = (LinearLayout) view.findViewById(R.id.llContainer1);
		llContent = (LinearLayout) view.findViewById(R.id.llContent);
		
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		height = dm.heightPixels;
		density = dm.density;

		data = getArguments().getParcelable("data");
		if (data != null) {
			if (!TextUtils.equals(data.currentVisible, CONST.noValue)) {
				tvCurrentVisible.setText(data.currentVisible);
				tv1.setText(getString(R.string.unit_km));
			}else {
				tvCurrentVisible.setText(CONST.noValue);
			}
			
			if (!TextUtils.equals(data.statisMinVisible, CONST.noValue)) {
				tvMinVisible.setText(data.statisMinVisible);
				tv2.setText(getString(R.string.unit_km));
			}else {
				tvMinVisible.setText(CONST.noValue);
			}
			
			warningList.clear();
			warningList.addAll(getArguments().<WarningDto>getParcelableArrayList("warningList"));
			visibleView = new VisibleView(getActivity());
			visibleView.setData(data.dataList, warningList);
			
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
		llContainer1.addView(visibleView, (int)(CommonUtil.dip2px(getActivity(), viewWidth)), (int)(CommonUtil.dip2px(getActivity(), 250)));
	}
	
	private void showLandscape() {
		llContent.setVisibility(View.GONE);
		llContainer1.removeAllViews();
		llContainer1.addView(visibleView, (int)(CommonUtil.dip2px(getActivity(), viewWidth/density)), (int)(CommonUtil.dip2px(getActivity(), 250)));
	}
	
}
