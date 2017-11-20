package com.hf.tianjin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hf.tianjin.adapter.WarningAdapter;
import com.hf.tianjin.adapter.WarningListAdapter1;
import com.hf.tianjin.adapter.WarningListAdapter2;
import com.hf.tianjin.adapter.WarningListAdapter3;
import com.hf.tianjin.dto.WarningDto;

/**
 * 预警列表
 * @author shawn_sun
 *
 */

public class WarningListActivity extends BaseActivity implements OnClickListener {
	
	private Context mContext = null;
	private LinearLayout llBack = null;
	private TextView tvTitle = null;
	private LinearLayout llSearch = null;
	private LinearLayout llSelect = null;
	private EditText etSearch = null;
	private ListView cityListView = null;
	private WarningAdapter cityAdapter = null;
	private List<WarningDto> warningList = new ArrayList<WarningDto>();//上个界面传过来的所有预警数据
	private List<WarningDto> showList = new ArrayList<WarningDto>();//用于存放listview上展示的数据
	private List<WarningDto> searchList = new ArrayList<WarningDto>();//用于存放搜索框搜索的数据
	private List<WarningDto> selecteList = new ArrayList<WarningDto>();//用于存放三个sppiner删选的数据
	private LinearLayout ll1, ll2, ll3;
	private TextView tv1, tv2, tv3;
	private ImageView iv1, iv2, iv3;
	private GridView gridView1 = null;
	private WarningListAdapter1 adapter1 = null;
	private List<WarningDto> list1 = new ArrayList<WarningDto>();
	private GridView gridView2 = null;
	private WarningListAdapter2 adapter2 = null;
	private List<WarningDto> list2 = new ArrayList<WarningDto>();
	private GridView gridView3 = null;
	private WarningListAdapter3 adapter3 = null;
	private List<WarningDto> list3 = new ArrayList<WarningDto>();
	private LinearLayout llContainer1, llContainer2, llContainer3;
	private String type = "999999";
	private String color = "999999";
	private String id = "999999";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.warning_list);
		mContext = this;
		initWidget();
		initListView();
		initGridView1();
		initGridView2();
		initGridView3();
	}
	
	/**
	 * 初始化控件
	 */
	@SuppressWarnings("unchecked")
	private void initWidget() {
		llBack = (LinearLayout) findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setText(getString(R.string.warning_list));
		etSearch = (EditText) findViewById(R.id.etSearch);
		etSearch.addTextChangedListener(watcher);
		llSearch = (LinearLayout) findViewById(R.id.llSearch);
		llSelect = (LinearLayout) findViewById(R.id.llSelect);
		ll1 = (LinearLayout) findViewById(R.id.ll1);
		ll1.setOnClickListener(this);
		ll2 = (LinearLayout) findViewById(R.id.ll2);
		ll2.setOnClickListener(this);
		ll3 = (LinearLayout) findViewById(R.id.ll3);
		ll3.setOnClickListener(this);
		tv1 = (TextView) findViewById(R.id.tv1);
		tv2 = (TextView) findViewById(R.id.tv2);
		tv3 = (TextView) findViewById(R.id.tv3);
		iv1 = (ImageView) findViewById(R.id.iv1);
		iv2 = (ImageView) findViewById(R.id.iv2);
		iv3 = (ImageView) findViewById(R.id.iv3);
		llContainer1 = (LinearLayout) findViewById(R.id.llContainer1);
		llContainer2 = (LinearLayout) findViewById(R.id.llContainer2);
		llContainer3 = (LinearLayout) findViewById(R.id.llContainer3);
		
		boolean isVisible = getIntent().getBooleanExtra("isVisible", false);
		if (isVisible) {
			llSearch.setVisibility(View.VISIBLE);
			llSelect.setVisibility(View.VISIBLE);
		}else {
			llSearch.setVisibility(View.GONE);
			llSelect.setVisibility(View.GONE);
		}
		
		warningList.clear();
		warningList.addAll(getIntent().getExtras().<WarningDto>getParcelableArrayList("warningList"));
		showList.clear();
		showList.addAll(warningList);
    }
	
	private TextWatcher watcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		}
		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		}
		@Override
		public void afterTextChanged(Editable arg0) {
			if (arg0.toString() == null) {
				return;
			}

			searchList.clear();
			if (!TextUtils.isEmpty(arg0.toString().trim())) {
				type = "999999";
				tv1.setText(getString(R.string.warning_class));
				for (int i = 0; i < list1.size(); i++) {
					if (i == 0) {
						adapter1.isSelected.put(i, true);
					}else {
						adapter1.isSelected.put(i, false);
					}
				}
				adapter1.notifyDataSetChanged();
				closeList(llContainer1, iv1);
				
				color = "999999";
				tv2.setText(getString(R.string.warning_level));
				for (int i = 0; i < list2.size(); i++) {
					if (i == 0) {
						adapter2.isSelected.put(i, true);
					}else {
						adapter2.isSelected.put(i, false);
					}
				}
				adapter2.notifyDataSetChanged();
				closeList(llContainer2, iv2);

				id = "999999";
				tv3.setText(getString(R.string.warning_district));
				for (int i = 0; i < list3.size(); i++) {
					if (i == 0) {
						adapter3.isSelected.put(i, true);
					}else {
						adapter3.isSelected.put(i, false);
					}
				}
				adapter3.notifyDataSetChanged();
				closeList(llContainer3, iv3);
				
				for (int i = 0; i < warningList.size(); i++) {
					WarningDto data = warningList.get(i);
					if (data.name.contains(arg0.toString().trim())) {
						searchList.add(data);
					}
				}
				showList.clear();
				showList.addAll(searchList);
				cityAdapter.notifyDataSetChanged();
			}else {
				showList.clear();
				showList.addAll(warningList);
				cityAdapter.notifyDataSetChanged();
			}
		}
	};
	
	/**
	 * 初始化listview
	 */
	private void initListView() {
		cityListView = (ListView) findViewById(R.id.cityListView);
		cityAdapter = new WarningAdapter(mContext, showList, false);
		cityListView.setAdapter(cityAdapter);
		cityListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				WarningDto data = showList.get(arg2);
				Intent intentDetail = new Intent(mContext, WarningDetailActivity.class);
				intentDetail.putExtra("data", data);
				startActivity(intentDetail);
			}
		});
	}
	
	private boolean isContainsType(String type, String selectType) {
		if (TextUtils.equals(selectType, "999999")) {
			return true;
		}
		if (type.contains(selectType)) {
			return true;
		}else {
			return false;
		}
	}
	
	private boolean isContainsColor(String color, String selectColor) {
		if (TextUtils.equals(selectColor, "999999")) {
			return true;
		}
		if (color.contains(selectColor)) {
			return true;
		}else {
			return false;
		}
	}
	
	private boolean isContainsId(String id, String selectId) {
		if (TextUtils.equals(selectId, "999999")) {
			return true;
		}
		if (id.contains(selectId)) {
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * 初始化listview
	 */
	private void initGridView1() {
		list1.clear();
		String[] array1 = getResources().getStringArray(R.array.warningType);
		for (int i = 0; i < array1.length; i++) {
			HashMap<String, Integer> map = new HashMap<String, Integer>();
			String[] value = array1[i].split(",");
			int count = 0;
			for (int j = 0; j < warningList.size(); j++) {
				WarningDto dto2 = warningList.get(j);
				String[] array = dto2.html.split("-");
				String type = array[2].substring(0, 5);
				if (TextUtils.equals(type, value[0])) {
					map.put(type, count++);
				}
			}

			WarningDto dto = new WarningDto();
			dto.name = value[1];
			dto.type = value[0];
			dto.count = count;
			list1.add(dto);
		}
		
		gridView1 = (GridView) findViewById(R.id.gridView1);
		adapter1 = new WarningListAdapter1(mContext, list1);
		gridView1.setAdapter(adapter1);
		gridView1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				WarningDto dto = list1.get(arg2);
				if (TextUtils.equals(dto.name, getString(R.string.all))) {
					tv1.setText(getString(R.string.warning_class));
					type = dto.type;
				}else {
					tv1.setText(dto.name);
					type = dto.type;
				}
				for (int i = 0; i < list1.size(); i++) {
					if (i == arg2) {
						adapter1.isSelected.put(i, true);
					}else {
						adapter1.isSelected.put(i, false);
					}
				}
				adapter1.notifyDataSetChanged();
				closeList(llContainer1, iv1);
				
				selecteList.clear();
				for (int i = 0; i < warningList.size(); i++) {
					if (isContainsType(warningList.get(i).type, type)
							&& isContainsColor(warningList.get(i).color, color)
							&& isContainsId(warningList.get(i).provinceId, id)) {
						selecteList.add(warningList.get(i));
					}
				}
				showList.clear();
				showList.addAll(selecteList);
				cityAdapter.notifyDataSetChanged();
			}
		});
	}
	
	/**
	 * 初始化listview
	 */
	private void initGridView2() {
		list2.clear();
		String[] array2 = getResources().getStringArray(R.array.warningColor);
		for (int i = 0; i < array2.length; i++) {
			HashMap<String, Integer> map = new HashMap<String, Integer>();
			String[] value = array2[i].split(",");
			int count = 0;
			for (int j = 0; j < warningList.size(); j++) {
				WarningDto dto2 = warningList.get(j);
				String[] array = dto2.html.split("-");
				String color = array[2].substring(5, 7);
				if (TextUtils.equals(color, value[0])) {
					map.put(color, count++);
				}
			}

			WarningDto dto = new WarningDto();
			dto.name = value[1];
			dto.color = value[0];
			dto.count = count;
			list2.add(dto);
		}
		
		gridView2 = (GridView) findViewById(R.id.gridView2);
		adapter2 = new WarningListAdapter2(mContext, list2);
		gridView2.setAdapter(adapter2);
		gridView2.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				WarningDto dto = list2.get(arg2);
				if (TextUtils.equals(dto.name, getString(R.string.all))) {
					tv2.setText(getString(R.string.warning_level));
					color = dto.color;
				}else {
					tv2.setText(dto.name);
					color = dto.color;
				}
				for (int i = 0; i < list2.size(); i++) {
					if (i == arg2) {
						adapter2.isSelected.put(i, true);
					}else {
						adapter2.isSelected.put(i, false);
					}
				}
				adapter2.notifyDataSetChanged();
				closeList(llContainer2, iv2);
				
				selecteList.clear();
				for (int i = 0; i < warningList.size(); i++) {
						if (isContainsType(warningList.get(i).type, type)
								&& isContainsColor(warningList.get(i).color, color)
								&& isContainsId(warningList.get(i).provinceId, id)) {
							selecteList.add(warningList.get(i));
						}
				}
				showList.clear();
				showList.addAll(selecteList);
				cityAdapter.notifyDataSetChanged();
			}
		});
	}
	
	/**
	 * 初始化listview
	 */
	private void initGridView3() {
		list3.clear();
		String[] array3 = getResources().getStringArray(R.array.warningDis);
		for (int i = 0; i < array3.length; i++) {
			HashMap<String, Integer> map = new HashMap<String, Integer>();
			String[] value = array3[i].split(",");
			int count = 0;
			for (int j = 0; j < warningList.size(); j++) {
				WarningDto dto2 = warningList.get(j);
				String[] array = dto2.html.split("-");
				String provinceId = array[0].substring(0, 2);
				if (TextUtils.equals(provinceId, value[0])) {
					map.put(provinceId, count++);
				}
			}

			WarningDto dto = new WarningDto();
			dto.name = value[1];
			dto.provinceId = value[0];
			dto.count = count;
			if (i == 0 || count != 0) {
				list3.add(dto);
			}
		}
		
		gridView3 = (GridView) findViewById(R.id.gridView3);
		adapter3 = new WarningListAdapter3(mContext, list3);
		gridView3.setAdapter(adapter3);
		gridView3.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				WarningDto dto = list3.get(arg2);
				if (TextUtils.equals(dto.name, getString(R.string.all))) {
					tv3.setText(getString(R.string.warning_district));
					id = dto.provinceId;
				}else {
					tv3.setText(dto.name);
					id = dto.provinceId;
				}
				for (int i = 0; i < list3.size(); i++) {
					if (i == arg2) {
						adapter3.isSelected.put(i, true);
					}else {
						adapter3.isSelected.put(i, false);
					}
				}
				adapter3.notifyDataSetChanged();
				closeList(llContainer3, iv3);
				
				selecteList.clear();
				for (int i = 0; i < warningList.size(); i++) {
						if (isContainsType(warningList.get(i).type, type)
								&& isContainsColor(warningList.get(i).color, color)
								&& isContainsId(warningList.get(i).provinceId, id)) {
							selecteList.add(warningList.get(i));
						}
				}
				showList.clear();
				showList.addAll(selecteList);
				cityAdapter.notifyDataSetChanged();
			}
		});
	}
	
	/**
	 * @param flag false为显示map，true为显示list
	 */
	private void startAnimation(boolean flag, final LinearLayout view) {
		//列表动画
		AnimationSet animationSet = new AnimationSet(true);
		TranslateAnimation animation = null;
		if (flag == false) {
			animation = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF,0f,
					Animation.RELATIVE_TO_SELF,0f,
					Animation.RELATIVE_TO_SELF,-1.0f,
					Animation.RELATIVE_TO_SELF,0f);
		}else {
			animation = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF,0f,
					Animation.RELATIVE_TO_SELF,0f,
					Animation.RELATIVE_TO_SELF,0f,
					Animation.RELATIVE_TO_SELF,-1.0f);
		}
		animation.setDuration(400);
		animationSet.addAnimation(animation);
		animationSet.setFillAfter(true);
		view.startAnimation(animationSet);
		animationSet.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {
			}
			@Override
			public void onAnimationRepeat(Animation arg0) {
			}
			@Override
			public void onAnimationEnd(Animation arg0) {
				view.clearAnimation();
			}
		});
	}
	
	private void bootAnimation(LinearLayout view, ImageView imageView) {
		if (view.getVisibility() == View.GONE) {
			openList(view, imageView);
		}else {
			closeList(view, imageView);
		}
	}
	
	private void openList(LinearLayout view, ImageView imageView) {
		if (view.getVisibility() == View.GONE) {
			startAnimation(false, view);
			view.setVisibility(View.VISIBLE);
			imageView.setImageResource(R.drawable.iv_arrow_black_up);
		}
	}
	
	private void closeList(LinearLayout view, ImageView imageView) {
		if (view.getVisibility() == View.VISIBLE) {
			startAnimation(true, view);
			view.setVisibility(View.GONE);
			imageView.setImageResource(R.drawable.iv_arrow_black_down);
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.llBack:
			finish();
			break;
		case R.id.ll1:
			bootAnimation(llContainer1, iv1);
			closeList(llContainer2, iv2);
			closeList(llContainer3, iv3);
			break;
		case R.id.ll2:
			bootAnimation(llContainer2, iv2);
			closeList(llContainer1, iv1);
			closeList(llContainer3, iv3);
			break;
		case R.id.ll3:
			bootAnimation(llContainer3, iv3);
			closeList(llContainer1, iv1);
			closeList(llContainer2, iv2);
			break;

		default:
			break;
		}
	}
	
}
