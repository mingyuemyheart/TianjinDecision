package com.hf.tianjin;

/**
 * 选择区域
 */

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hf.tianjin.adapter.AreaAdapter;
import com.hf.tianjin.dto.CityDto;

public class AreaActivity extends BaseActivity implements OnClickListener{
	
	private Context mContext = null;
	private LinearLayout llBack = null;
	private TextView tvTitle = null;
	private GridView nGridView = null;
	private AreaAdapter nAdapter = null;
	private List<CityDto> nList = new ArrayList<CityDto>();
	private String locationId = null;//定位城市的id
	private int size = 0;//关注城市个数
	private int attentionCount = 0;//关注城市最大个数
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_area);
		mContext = this;
		initWidget();
		initNGridView();
	}
	
	/**
	 * 初始化控件
	 */
	private void initWidget() {
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setText(getString(R.string.select_area));
		llBack = (LinearLayout) findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		
		locationId = getIntent().getStringExtra("locationCityId");
		size = getIntent().getIntExtra("size", 0);
		attentionCount = getIntent().getIntExtra("attentionCount", 0);
	}
	
	/**
	 * 获取全国热门城市
	 * @return
	 */
	private void getNationHotCity() {
		nList.clear();
		String[] array = getResources().getStringArray(R.array.tianjinArea);
		for (int i = 0; i < array.length; i++) {
			String[] itemArray = array[i].split(",");
			CityDto dto = new CityDto();
			dto.cityId = itemArray[0];
			dto.disName = itemArray[1];
			dto.warningId = itemArray[2];
			nList.add(dto);
		}
		
		getLocationCity(locationId, nList);
		getSelectedCitys(nList);
	}
	
	/**
	 * 初始化全国热门
	 */
	private void initNGridView() {
		getNationHotCity();
		nGridView = (GridView) findViewById(R.id.nGridView);
		nAdapter = new AreaAdapter(mContext, nList);
		nGridView.setAdapter(nAdapter);
		nGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				CityDto dto = nList.get(arg2);
				intentAttention(dto, true);
//				if (size == attentionCount) {
//					intentAttention(dto.cityName, dto.cityId, dto.warningId, true);
//				}else {
//					if (dto.isLocation || dto.isSelected) {
//						intentAttention(dto.cityName, dto.cityId, dto.warningId, true);
//					}else {
//						intentAttention(dto.cityName, dto.cityId, dto.warningId, true);
//					}
//				}
			}
		});
	}
	
	/**
	 * 获取定位城市id
	 * @param locationId
	 * @param list
	 */
	private void getLocationCity(String locationId, List<CityDto> list) {
		if (TextUtils.isEmpty(locationId)) {
			return;
		}
		for (int i = 0; i < list.size(); i++) {
			if (TextUtils.equals(locationId, list.get(i).cityId)) {
				list.get(i).isLocation = true;
			}
		}
	}
	
	/**
	 * //获取所有关注城市的id
	 * @param list
	 */
	private void getSelectedCitys(List<CityDto> list) {
		SharedPreferences sharedPreferences = getSharedPreferences("CITYIDS", Context.MODE_PRIVATE);
		String cityInfo = sharedPreferences.getString("cityInfo", null);
		if (!TextUtils.isEmpty(cityInfo)) {
			String[] info = cityInfo.split(";");
			for (int m = 0; m < info.length; m++) {
				String[] ids = info[m].split(",");
				for (int j = 0; j < list.size(); j++) {
					if (TextUtils.equals(ids[0], list.get(j).cityId)) {
						list.get(j).isSelected = true;
					}
				}
			}
		}
	}
	
	/**
	 * 迁移到关注列表页面
	 * @param isAdded 该城市是否为已添加城市
	 */
	private void intentAttention(CityDto data, boolean isAdded) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putParcelable("data", data);
		bundle.putBoolean("isAdded", isAdded);
		intent.putExtras(bundle);
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.llBack:
			finish();
			break;

		default:
			break;
		}
	}
}
