package com.hf.tianjin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hf.tianjin.adapter.StationMonitorCityAdapter;
import com.hf.tianjin.adapter.StationMonitorDistrictAdapter;
import com.hf.tianjin.adapter.StationMonitorProvinceAdapter;
import com.hf.tianjin.common.CONST;
import com.hf.tianjin.dto.StationMonitorDto;
import com.hf.tianjin.manager.DBManager;
import com.hf.tianjin.stickygridheaders.StickyGridHeadersGridView;

/**
 * 站点监测搜索
 * @author shawn_sun
 *
 */
public class StationMonitorSearchActivity extends BaseActivity implements OnClickListener{
	
	private Context mContext = null;
	private LinearLayout llBack = null;
	private TextView tvTitle = null;
	private TextView tvControl = null;
	private StickyGridHeadersGridView proListView = null;
	private StationMonitorProvinceAdapter proAdapter = null;
	private List<StationMonitorDto> proList = new ArrayList<StationMonitorDto>();
	private int psection = 1;
	private HashMap<String, Integer> sectionMap1 = new HashMap<String, Integer>();
	private ListView cityListView = null;
	private StationMonitorCityAdapter cityAdapter = null;
	private List<StationMonitorDto> cityList = new ArrayList<StationMonitorDto>();
	private ListView disListView = null;
	private StationMonitorDistrictAdapter disAdapter = null;
	private List<StationMonitorDto> disList = new ArrayList<StationMonitorDto>();
	private String provinceName = null;
	private String cityName = null;
	private String districtName = null;
	private TextView tvSure = null;
	private boolean isAll = true;//是否显示全部

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.station_monitor_search);
		mContext = this;
		initProListView();
		initCityListView();
		initDisListView();
		initWidget();
	}
	
	private void initWidget() {
		llBack = (LinearLayout) findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvControl = (TextView) findViewById(R.id.tvControl);
		tvControl.setText(getString(R.string.auto_stations));
		tvControl.setVisibility(View.VISIBLE);
		tvControl.setOnClickListener(this);
		tvSure = (TextView) findViewById(R.id.tvSure);
		tvSure.setOnClickListener(this);
		
		String title = getIntent().getStringExtra(CONST.ACTIVITY_NAME);
		if (title != null) {
			tvTitle.setText(title);
		}
		
		SharedPreferences sharedPreferences = getSharedPreferences("state", Context.MODE_PRIVATE);
		isAll = sharedPreferences.getBoolean("flag", true);
		if (isAll) {
			tvControl.setText(getString(R.string.auto_stations));
			cityListView.setVisibility(View.VISIBLE);
			disListView.setVisibility(View.VISIBLE);
		}else {
			tvControl.setText(getString(R.string.all));
			cityListView.setVisibility(View.GONE);
			disListView.setVisibility(View.GONE);
		}
	}
	
	/**
	 * 查询表获取所有省份
	 * @param list
	 */
	private void queryProvince(List<StationMonitorDto> list) {
		DBManager dbManager = new DBManager(mContext);
		dbManager.openDateBase();
		SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/" + DBManager.DB_NAME, null);
		try {
			if (database != null && database.isOpen()) {
				Cursor cursor = database.rawQuery("select distinct PRO from " + DBManager.TABLE_NAME1, null);
				list.clear();
				for (int i = 0; i < cursor.getCount(); i++) {
					cursor.moveToPosition(i);
					StationMonitorDto dto = new StationMonitorDto();
					dto.provinceName = cursor.getString(cursor.getColumnIndex("PRO"));
					if (!TextUtils.isEmpty(dto.provinceName)) {
						if (dto.provinceName.contains("北京") || dto.provinceName.contains("天津") || dto.provinceName.contains("河北") || dto.provinceName.contains("山西") || dto.provinceName.contains("内蒙古")) {
							dto.partition = "华北";
						}else if (dto.provinceName.contains("上海") || dto.provinceName.contains("山东") || dto.provinceName.contains("江苏") || dto.provinceName.contains("浙江") || dto.provinceName.contains("江西") || dto.provinceName.contains("安徽") || dto.provinceName.contains("福建")) {
							dto.partition = "华东";
						}else if (dto.provinceName.contains("湖北") || dto.provinceName.contains("湖南") || dto.provinceName.contains("河南")) {
							dto.partition = "华中";
						}else if (dto.provinceName.contains("广东") || dto.provinceName.contains("广西") || dto.provinceName.contains("海南")) {
							dto.partition = "华南";
						}else if (dto.provinceName.contains("黑龙江") || dto.provinceName.contains("吉林") || dto.provinceName.contains("辽宁")) {
							dto.partition = "东北";
						}else if (dto.provinceName.contains("陕西") || dto.provinceName.contains("甘肃") || dto.provinceName.contains("宁夏") || dto.provinceName.contains("新疆") || dto.provinceName.contains("青海")) {
							dto.partition = "西北";
						}else if (dto.provinceName.contains("重庆") || dto.provinceName.contains("四川") || dto.provinceName.contains("贵州") || dto.provinceName.contains("云南") || dto.provinceName.contains("西藏")) {
							dto.partition = "西南";
						}else {
							dto.partition = "港澳台";
						}
						if (!dto.provinceName.contains(getString(R.string.not_available))) {//过滤掉名称为“暂无”的省份
							list.add(dto);
						}
					}
				}
				cursor.close();
				cursor = null;
				dbManager.closeDatabase();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < list.size(); i++) {
			StationMonitorDto sectionDto = list.get(i);
 			if (!sectionMap1.containsKey(sectionDto.partition)) {
				sectionDto.section = psection;
				sectionMap1.put(sectionDto.partition, psection);
				psection++;
			}else {
				sectionDto.section = sectionMap1.get(sectionDto.partition);
			}
		}
		
		Collections.sort(list, new Comparator<StationMonitorDto>() {
			@Override
			public int compare(StationMonitorDto a, StationMonitorDto b) {
				return String.valueOf(a.section).compareTo(String.valueOf(b.section));
			}
		});
	}
	
	private void initProListView() {
		queryProvince(proList);
		proListView = (StickyGridHeadersGridView) findViewById(R.id.proListView);
		proAdapter = new StationMonitorProvinceAdapter(mContext, proList);
		proListView.setAdapter(proAdapter);
		proListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				for (int i = 0; i < proList.size(); i++) {
					if (arg2 == i) {
						proAdapter.isSelected.put(i, true);
					}else {
						proAdapter.isSelected.put(i, false);
					}
				}
				if (proAdapter != null) {
					proAdapter.notifyDataSetChanged();
				}
				provinceName = proList.get(arg2).provinceName;
				queryCity(provinceName, cityList);
				
				for (int i = 0; i < cityList.size(); i++) {
					if (i == 0) {
						cityAdapter.isSelected.put(i, true);
					}else {
						cityAdapter.isSelected.put(i, false);
					}
				}
				if (cityAdapter != null) {
					cityAdapter.notifyDataSetChanged();
				}
				cityName = cityList.get(0).cityName;
				queryDistrict(provinceName, cityName, disList);
				
				for (int i = 0; i < disList.size(); i++) {
					if (i == 0) {
						disAdapter.isSelected.put(i, true);
					}else {
						disAdapter.isSelected.put(i, false);
					}
				}
				if (disAdapter != null) {
					disAdapter.notifyDataSetChanged();
				}
				districtName = disList.get(0).districtName;
//				Toast.makeText(mContext, provinceName+cityName+districtName, Toast.LENGTH_SHORT).show();
			}
		});
		
		provinceName = proList.get(0).provinceName;
		queryCity(provinceName, cityList);
		
		cityName = cityList.get(0).cityName;
		queryDistrict(provinceName, cityName, disList);
		
		districtName = disList.get(0).districtName;
//		Toast.makeText(mContext, provinceName+cityName+districtName, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * 查询表获取某个省份的所有市级数据
	 * @param list
	 */
	private void queryCity(String provinceName, List<StationMonitorDto> list) {
		DBManager dbManager = new DBManager(mContext);
		dbManager.openDateBase();
		SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/" + DBManager.DB_NAME, null);
		try {
			if (database != null && database.isOpen()) {
				Cursor cursor = database.rawQuery("select distinct CITY from " + DBManager.TABLE_NAME1 + " where PRO = " + "\""+provinceName+"\"", null);
				list.clear();
				for (int i = 0; i < cursor.getCount(); i++) {
					cursor.moveToPosition(i);
					StationMonitorDto dto = new StationMonitorDto();
					dto.cityName = cursor.getString(cursor.getColumnIndex("CITY"));
					list.add(dto);
				}
				cursor.close();
				cursor = null;
				dbManager.closeDatabase();
			}
			
			if (cityAdapter != null) {
				cityAdapter.notifyDataSetChanged();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initCityListView() {
		cityListView = (ListView) findViewById(R.id.cityListView);
		cityAdapter = new StationMonitorCityAdapter(mContext, cityList);
		cityListView.setAdapter(cityAdapter);
		cityListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				for (int i = 0; i < cityList.size(); i++) {
					if (arg2 == i) {
						cityAdapter.isSelected.put(i, true);
					}else {
						cityAdapter.isSelected.put(i, false);
					}
				}
				if (cityAdapter != null) {
					cityAdapter.notifyDataSetChanged();
				}
				cityName = cityList.get(arg2).cityName;
				queryDistrict(provinceName, cityName, disList);
				
				for (int i = 0; i < disList.size(); i++) {
					if (i == 0) {
						disAdapter.isSelected.put(i, true);
					}else {
						disAdapter.isSelected.put(i, false);
					}
				}
				if (disAdapter != null) {
					disAdapter.notifyDataSetChanged();
				}
				districtName = disList.get(0).districtName;
//				Toast.makeText(mContext, provinceName+cityName+districtName, Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	/**
	 * 查询表获取某个市的所有区县级数据
	 * @param list
	 */
	private void queryDistrict(String provinceName, String cityName, List<StationMonitorDto> list) {
		DBManager dbManager = new DBManager(mContext);
		dbManager.openDateBase();
		SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/" + DBManager.DB_NAME, null);
		try {
			if (database != null && database.isOpen()) {
				Cursor cursor = database.rawQuery("select distinct DIST from " + DBManager.TABLE_NAME1 + " where PRO = " + "\""+provinceName+"\""+ " and CITY = " + "\""+cityName+"\"", null);
				list.clear();
				for (int i = 0; i < cursor.getCount(); i++) {
					cursor.moveToPosition(i);
					StationMonitorDto dto = new StationMonitorDto();
					dto.districtName = cursor.getString(cursor.getColumnIndex("DIST"));
					list.add(dto);
				}
				cursor.close();
				cursor = null;
				dbManager.closeDatabase();
			}
			
			if (disAdapter != null) {
				disAdapter.notifyDataSetChanged();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initDisListView() {
		disListView = (ListView) findViewById(R.id.disListView);
		disAdapter = new StationMonitorDistrictAdapter(mContext, disList);
		disListView.setAdapter(disAdapter);
		disListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				for (int i = 0; i < disList.size(); i++) {
					if (arg2 == i) {
						disAdapter.isSelected.put(i, true);
					}else {
						disAdapter.isSelected.put(i, false);
					}
				}
				if (disAdapter != null) {
					disAdapter.notifyDataSetChanged();
				}
				
				districtName = disList.get(arg2).districtName;
//				Toast.makeText(mContext, provinceName+cityName+districtName, Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.llBack:
			finish();
			break;
		case R.id.tvSure:
			Intent intent = new Intent();
			if (isAll) {
				intent.putExtra("provinceName", provinceName);
				intent.putExtra("cityName", cityName);
				intent.putExtra("districtName", districtName);
//			Toast.makeText(mContext, districtName, Toast.LENGTH_SHORT).show();
			}else {
				intent.putExtra("auto_stations", "auto_stations");
				intent.putExtra("provinceName", provinceName);
			}
			SharedPreferences sp2 = getSharedPreferences("state", Context.MODE_PRIVATE);
			Editor editor2 = sp2.edit();
			editor2.putBoolean("flag", isAll);
			editor2.commit();
			setResult(RESULT_OK, intent);
			finish();
			break;
		case R.id.tvControl:
			if (isAll) {
				tvControl.setText(getString(R.string.all));
				cityListView.setVisibility(View.GONE);
				disListView.setVisibility(View.GONE);
				isAll = false;
			}else {
				tvControl.setText(getString(R.string.auto_stations));
				cityListView.setVisibility(View.VISIBLE);
				disListView.setVisibility(View.VISIBLE);
				isAll = true;
			}
			break;

		default:
			break;
		}
	}
	
}
