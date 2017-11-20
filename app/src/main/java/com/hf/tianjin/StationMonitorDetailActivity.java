package com.hf.tianjin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hf.tianjin.common.CONST;
import com.hf.tianjin.dto.StationMonitorDto;
import com.hf.tianjin.dto.WarningDto;
import com.hf.tianjin.fragment.StationDetailHumidityFragment;
import com.hf.tianjin.fragment.StationDetailPressureFragment;
import com.hf.tianjin.fragment.StationDetailRainFragment;
import com.hf.tianjin.fragment.StationDetailTempFragment;
import com.hf.tianjin.fragment.StationDetailVisibilityFragment;
import com.hf.tianjin.fragment.StationDetailWindFragment;
import com.hf.tianjin.manager.DBManager;
import com.hf.tianjin.manager.RainManager;
import com.hf.tianjin.utils.CommonUtil;
import com.hf.tianjin.utils.CustomHttpClient;
import com.hf.tianjin.view.MainViewPager;
import com.hf.tianjin.view.MyDialog2;

public class StationMonitorDetailActivity extends BaseActivity implements OnClickListener{
	
	private Context mContext = null;
	private LinearLayout llBack = null;
	private TextView tvTitle = null;
	private MainViewPager viewPager;
	private List<Fragment> fragments = new ArrayList<Fragment>();
	public final static String SANX_DATA_99 = "sanx_data_99";//加密秘钥名称
	public final static String APPID = "f63d329270a44900";//机密需要用到的AppId
	private LinearLayout ll1, ll2, ll3, ll4, ll5, ll6;
	private ImageView iv1, iv2, iv3, iv4, iv5, iv6;
	private TextView tv1, tv2, tv3, tv4, tv5, tv6;
	private LinearLayout llMain = null;
	private MyDialog2 mDialog = null;
	private String warningUrl = "http://decision.tianqi.cn/alarm12379/grepalarm2.php?areaid=";//预警地址
	private List<WarningDto> warningList = new ArrayList<WarningDto>();//该站点对应的预警信息
	private ImageView ivShare = null;
	private RelativeLayout reTitle = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.station_monitor_detail);
		mContext = this;
		initDialog();
		initWidget();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			showPortrait();
		}else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			showLandscape();
		}
	}
	
	/**
	 * 初始化dialog
	 */
	private void initDialog() {
		if (mDialog == null) {
			mDialog = new MyDialog2(mContext);
		}
		mDialog.show();
	}
	private void cancelDialog() {
		if (mDialog != null) {
			mDialog.dismiss();
		}
	}
	
	private void initWidget() {
		llBack = (LinearLayout) findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		ll1 = (LinearLayout) findViewById(R.id.ll1);
		ll1.setOnClickListener(new MyOnClickListener(0));
		ll2 = (LinearLayout) findViewById(R.id.ll2);
		ll2.setOnClickListener(new MyOnClickListener(1));
		ll3 = (LinearLayout) findViewById(R.id.ll3);
		ll3.setOnClickListener(new MyOnClickListener(2));
		ll4 = (LinearLayout) findViewById(R.id.ll4);
		ll4.setOnClickListener(new MyOnClickListener(3));
		ll5 = (LinearLayout) findViewById(R.id.ll5);
		ll5.setOnClickListener(new MyOnClickListener(4));
		ll6 = (LinearLayout) findViewById(R.id.ll6);
		ll6.setOnClickListener(new MyOnClickListener(5));
		iv1 = (ImageView) findViewById(R.id.iv1);
		iv2 = (ImageView) findViewById(R.id.iv2);
		iv3 = (ImageView) findViewById(R.id.iv3);
		iv4 = (ImageView) findViewById(R.id.iv4);
		iv5 = (ImageView) findViewById(R.id.iv5);
		iv6 = (ImageView) findViewById(R.id.iv6);
		tv1 = (TextView) findViewById(R.id.tv1);
		tv2 = (TextView) findViewById(R.id.tv2);
		tv3 = (TextView) findViewById(R.id.tv3);
		tv4 = (TextView) findViewById(R.id.tv4);
		tv5 = (TextView) findViewById(R.id.tv5);
		tv6 = (TextView) findViewById(R.id.tv6);
		llMain = (LinearLayout) findViewById(R.id.llMain);
		llMain.setOnClickListener(this);
		ivShare = (ImageView) findViewById(R.id.ivShare);
		ivShare.setOnClickListener(this);
		reTitle = (RelativeLayout) findViewById(R.id.reTitle);
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		int height = dm.heightPixels;
		if (width < height) {
			showPortrait();
		}else {
			showLandscape();
		}
		
		String title = getIntent().getStringExtra(CONST.ACTIVITY_NAME);
		if (!TextUtils.isEmpty(title)) {
			tvTitle.setText(title);
		}
		
		String stationId = getIntent().getStringExtra("stationId");
		if (!TextUtils.isEmpty(stationId)) {
			String warningId = queryWarningIdByStationId(stationId);
			if (!TextUtils.isEmpty(warningId)) {
				asyncQuery(warningUrl+warningId);
			}
			
			asyncTask(stationId);
		}
	}
	
	private void showPortrait() {
		llBack.setVisibility(View.VISIBLE);
		ivShare.setVisibility(View.GONE);
		llMain.setVisibility(View.VISIBLE);
	}
	
	private void showLandscape() {
		llBack.setVisibility(View.GONE);
		ivShare.setVisibility(View.VISIBLE);
		llMain.setVisibility(View.GONE);
	}
	
	/**
	 * 根据站点id查询预警id
	 * @param stationId
	 */
	private String queryWarningIdByStationId(String stationId) {
		String warningId = null;
		DBManager dbManager = new DBManager(mContext);
		dbManager.openDateBase();
		SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/" + DBManager.DB_NAME, null);
		try {
			if (database != null && database.isOpen()) {
				Cursor cursor = database.rawQuery("select * from " + DBManager.TABLE_NAME1 + " where SID = " + stationId, null);
				for (int i = 0; i < cursor.getCount(); i++) {
					cursor.moveToPosition(i);
					warningId = cursor.getString(cursor.getColumnIndex("WARNID"));
				}
				cursor.close();
				cursor = null;
				dbManager.closeDatabase();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return warningId;
	}
	
	/**
	 * 异步请求
	 */
	private void asyncQuery(String requestUrl) {
		HttpAsyncTask task = new HttpAsyncTask();
		task.setMethod("GET");
		task.setTimeOut(CustomHttpClient.TIME_OUT);
		task.execute(requestUrl);
	}
	
	/**
	 * 异步请求方法
	 * @author dell
	 *
	 */
	private class HttpAsyncTask extends AsyncTask<String, Void, String> {
		private String method = "GET";
		private List<NameValuePair> nvpList = new ArrayList<NameValuePair>();
		
		public HttpAsyncTask() {
		}

		@Override
		protected String doInBackground(String... url) {
			String result = null;
			if (method.equalsIgnoreCase("POST")) {
				result = CustomHttpClient.post(url[0], nvpList);
			} else if (method.equalsIgnoreCase("GET")) {
				result = CustomHttpClient.get(url[0]);
			}
			return result;
		}

		@Override
		protected void onPostExecute(String requestResult) {
			super.onPostExecute(requestResult);
			if (requestResult != null) {
				try {
					JSONObject object = new JSONObject(requestResult);
					if (object != null) {
						if (!object.isNull("data")) {
							warningList.clear();
							JSONArray jsonArray = object.getJSONArray("data");
							for (int i = 0; i < jsonArray.length(); i++) {
								JSONArray tempArray = jsonArray.getJSONArray(i);
								WarningDto dto = new WarningDto();
								dto.html = tempArray.optString(1);
								String[] array = dto.html.split("-");
								String item0 = array[0];
								String item1 = array[1];
								String item2 = array[2];
								
								dto.provinceId = item0.substring(0, 2);
								dto.type = item2.substring(0, 5);
								dto.color = item2.substring(5, 7);
								dto.time = item1;
								dto.lng = tempArray.optString(2);
								dto.lat = tempArray.optString(3);
								dto.name = tempArray.optString(0);
								
								warningList.add(dto);
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		@SuppressWarnings("unused")
		private void setParams(NameValuePair nvp) {
			nvpList.add(nvp);
		}

		private void setMethod(String method) {
			this.method = method;
		}

		private void setTimeOut(int timeOut) {
			CustomHttpClient.TIME_OUT = timeOut;
		}

		/**
		 * 取消当前task
		 */
		@SuppressWarnings("unused")
		private void cancelTask() {
			CustomHttpClient.shuttdownRequest();
			this.cancel(true);
		}
	}
	
	/**
	 * 初始化viewPager
	 */
	@SuppressWarnings("unchecked")
	private void initViewPager(StationMonitorDto data) {
		Fragment fragment = null;
		for (int i = 0; i < 6; i++) {
			if (i == 0) {
				fragment = new StationDetailRainFragment();
			}else if (i == 1) {
				fragment = new StationDetailTempFragment();
			}else if (i == 2) {
				fragment = new StationDetailHumidityFragment();
			}else if (i == 3) {
				fragment = new StationDetailWindFragment();
			}else if (i == 4) {
				fragment = new StationDetailVisibilityFragment();
			}else if (i == 5) {
				fragment = new StationDetailPressureFragment();
			}
			if (fragment != null) {
				Bundle bundle = new Bundle();
				bundle.putParcelable("data", data);
				bundle.putParcelableArrayList("warningList", (ArrayList<? extends Parcelable>) warningList);
				fragment.setArguments(bundle);
				fragments.add(fragment);
			}
		}
			
		viewPager = (MainViewPager) findViewById(R.id.viewPager);
		viewPager.setSlipping(true);//设置ViewPager是否可以滑动
		viewPager.setOffscreenPageLimit(fragments.size());
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		viewPager.setAdapter(new MyPagerAdapter());
	}
	
	public class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
			if (arg0 == 0) {
				ll1.setBackgroundColor(getResources().getColor(R.color.white));
				ll2.setBackgroundColor(getResources().getColor(R.color.station_color1));
				ll3.setBackgroundColor(getResources().getColor(R.color.station_color1));
				ll4.setBackgroundColor(getResources().getColor(R.color.station_color1));
				ll5.setBackgroundColor(getResources().getColor(R.color.station_color1));
				ll6.setBackgroundColor(getResources().getColor(R.color.station_color1));
				iv1.setImageResource(R.drawable.iv_jiangshui_selected);
				iv2.setImageResource(R.drawable.iv_wendu);
				iv3.setImageResource(R.drawable.iv_shidu);
				iv4.setImageResource(R.drawable.iv_wind);
				iv5.setImageResource(R.drawable.iv_visible);
				iv6.setImageResource(R.drawable.iv_qiya);
				tv1.setTextColor(getResources().getColor(R.color.sure_color));
				tv2.setTextColor(getResources().getColor(R.color.text_color4));
				tv3.setTextColor(getResources().getColor(R.color.text_color4));
				tv4.setTextColor(getResources().getColor(R.color.text_color4));
				tv5.setTextColor(getResources().getColor(R.color.text_color4));
				tv6.setTextColor(getResources().getColor(R.color.text_color4));
			}else if (arg0 == 1) {
				ll1.setBackgroundColor(getResources().getColor(R.color.station_color1));
				ll2.setBackgroundColor(getResources().getColor(R.color.white));
				ll3.setBackgroundColor(getResources().getColor(R.color.station_color1));
				ll4.setBackgroundColor(getResources().getColor(R.color.station_color1));
				ll5.setBackgroundColor(getResources().getColor(R.color.station_color1));
				ll6.setBackgroundColor(getResources().getColor(R.color.station_color1));
				iv1.setImageResource(R.drawable.iv_jiangshui);
				iv2.setImageResource(R.drawable.iv_wendu_selected);
				iv3.setImageResource(R.drawable.iv_shidu);
				iv4.setImageResource(R.drawable.iv_wind);
				iv5.setImageResource(R.drawable.iv_visible);
				iv6.setImageResource(R.drawable.iv_qiya);
				tv1.setTextColor(getResources().getColor(R.color.text_color4));
				tv2.setTextColor(getResources().getColor(R.color.sure_color));
				tv3.setTextColor(getResources().getColor(R.color.text_color4));
				tv4.setTextColor(getResources().getColor(R.color.text_color4));
				tv5.setTextColor(getResources().getColor(R.color.text_color4));
				tv6.setTextColor(getResources().getColor(R.color.text_color4));
			}else if (arg0 == 2) {
				ll1.setBackgroundColor(getResources().getColor(R.color.station_color1));
				ll2.setBackgroundColor(getResources().getColor(R.color.station_color1));
				ll3.setBackgroundColor(getResources().getColor(R.color.white));
				ll4.setBackgroundColor(getResources().getColor(R.color.station_color1));
				ll5.setBackgroundColor(getResources().getColor(R.color.station_color1));
				ll6.setBackgroundColor(getResources().getColor(R.color.station_color1));
				iv1.setImageResource(R.drawable.iv_jiangshui);
				iv2.setImageResource(R.drawable.iv_wendu);
				iv3.setImageResource(R.drawable.iv_shidu_selected);
				iv4.setImageResource(R.drawable.iv_wind);
				iv5.setImageResource(R.drawable.iv_visible);
				iv6.setImageResource(R.drawable.iv_qiya);
				tv1.setTextColor(getResources().getColor(R.color.text_color4));
				tv2.setTextColor(getResources().getColor(R.color.text_color4));
				tv3.setTextColor(getResources().getColor(R.color.sure_color));
				tv4.setTextColor(getResources().getColor(R.color.text_color4));
				tv5.setTextColor(getResources().getColor(R.color.text_color4));
				tv6.setTextColor(getResources().getColor(R.color.text_color4));
			}else if (arg0 == 3) {
				ll1.setBackgroundColor(getResources().getColor(R.color.station_color1));
				ll2.setBackgroundColor(getResources().getColor(R.color.station_color1));
				ll3.setBackgroundColor(getResources().getColor(R.color.station_color1));
				ll4.setBackgroundColor(getResources().getColor(R.color.white));
				ll5.setBackgroundColor(getResources().getColor(R.color.station_color1));
				ll6.setBackgroundColor(getResources().getColor(R.color.station_color1));
				iv1.setImageResource(R.drawable.iv_jiangshui);
				iv2.setImageResource(R.drawable.iv_wendu);
				iv3.setImageResource(R.drawable.iv_shidu);
				iv4.setImageResource(R.drawable.iv_wind_selected);
				iv5.setImageResource(R.drawable.iv_visible);
				iv6.setImageResource(R.drawable.iv_qiya);
				tv1.setTextColor(getResources().getColor(R.color.text_color4));
				tv2.setTextColor(getResources().getColor(R.color.text_color4));
				tv3.setTextColor(getResources().getColor(R.color.text_color4));
				tv4.setTextColor(getResources().getColor(R.color.sure_color));
				tv5.setTextColor(getResources().getColor(R.color.text_color4));
				tv6.setTextColor(getResources().getColor(R.color.text_color4));
			}else if (arg0 == 4) {
				ll1.setBackgroundColor(getResources().getColor(R.color.station_color1));
				ll2.setBackgroundColor(getResources().getColor(R.color.station_color1));
				ll3.setBackgroundColor(getResources().getColor(R.color.station_color1));
				ll4.setBackgroundColor(getResources().getColor(R.color.station_color1));
				ll5.setBackgroundColor(getResources().getColor(R.color.white));
				ll6.setBackgroundColor(getResources().getColor(R.color.station_color1));
				iv1.setImageResource(R.drawable.iv_jiangshui);
				iv2.setImageResource(R.drawable.iv_wendu);
				iv3.setImageResource(R.drawable.iv_shidu);
				iv4.setImageResource(R.drawable.iv_wind);
				iv5.setImageResource(R.drawable.iv_visible_selected);
				iv6.setImageResource(R.drawable.iv_qiya);
				tv1.setTextColor(getResources().getColor(R.color.text_color4));
				tv2.setTextColor(getResources().getColor(R.color.text_color4));
				tv3.setTextColor(getResources().getColor(R.color.text_color4));
				tv4.setTextColor(getResources().getColor(R.color.text_color4));
				tv5.setTextColor(getResources().getColor(R.color.sure_color));
				tv6.setTextColor(getResources().getColor(R.color.text_color4));
			}else if (arg0 == 5) {
				ll1.setBackgroundColor(getResources().getColor(R.color.station_color1));
				ll2.setBackgroundColor(getResources().getColor(R.color.station_color1));
				ll3.setBackgroundColor(getResources().getColor(R.color.station_color1));
				ll4.setBackgroundColor(getResources().getColor(R.color.station_color1));
				ll5.setBackgroundColor(getResources().getColor(R.color.station_color1));
				ll6.setBackgroundColor(getResources().getColor(R.color.white));
				iv1.setImageResource(R.drawable.iv_jiangshui);
				iv2.setImageResource(R.drawable.iv_wendu);
				iv3.setImageResource(R.drawable.iv_shidu);
				iv4.setImageResource(R.drawable.iv_wind);
				iv5.setImageResource(R.drawable.iv_visible);
				iv6.setImageResource(R.drawable.iv_qiya_selected);
				tv1.setTextColor(getResources().getColor(R.color.text_color4));
				tv2.setTextColor(getResources().getColor(R.color.text_color4));
				tv3.setTextColor(getResources().getColor(R.color.text_color4));
				tv4.setTextColor(getResources().getColor(R.color.text_color4));
				tv5.setTextColor(getResources().getColor(R.color.text_color4));
				tv6.setTextColor(getResources().getColor(R.color.sure_color));
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	/**
	 * @ClassName: MyOnClickListener
	 * @Description: TODO头标点击监听
	 * @author Panyy
	 * @date 2013 2013年11月6日 下午2:46:08
	 *
	 */
	private class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			if (viewPager != null) {
				viewPager.setCurrentItem(index);
			}
		}
	};

	/**
	 * @ClassName: MyPagerAdapter
	 * @Description: TODO填充ViewPager的数据适配器
	 * @author Panyy
	 * @date 2013 2013年11月6日 下午2:37:47
	 *
	 */
	private class MyPagerAdapter extends PagerAdapter {
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getCount() {
			return fragments.size();
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView(fragments.get(position).getView());
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			Fragment fragment = fragments.get(position);
			if (!fragment.isAdded()) { // 如果fragment还没有added
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.add(fragment, fragment.getClass().getSimpleName());
				ft.commit();
				/**
				 * 在用FragmentTransaction.commit()方法提交FragmentTransaction对象后
				 * 会在进程的主线程中,用异步的方式来执行。
				 * 如果想要立即执行这个等待中的操作,就要调用这个方法(只能在主线程中调用)。
				 * 要注意的是,所有的回调和相关的行为都会在这个调用中被执行完成,因此要仔细确认这个方法的调用位置。
				 */
				getFragmentManager().executePendingTransactions();
			}

			if (fragment.getView().getParent() == null) {
				container.addView(fragment.getView()); // 为viewpager增加布局
			}
			return fragment.getView();
		}
	}
	
	/**
	 * 加密请求字符串
	 * @param url 基本串
	 * @param lng 经度
	 * @param lat 维度
	 * @return
	 */
	private String getUrl(String stationids) {
		String URL = "http://61.4.184.171:8080/weather/rgwst/OneDayStatistics";//
		if (!TextUtils.isEmpty(stationids) && stationids.length() == 5) {
			URL = "http://61.4.184.171:8080/weather/rgwst/newOneDayStatistics";
		}
		String sysdate = RainManager.getDate(Calendar.getInstance(), "yyyyMMddHHmmss");//系统时间
		StringBuffer buffer = new StringBuffer();
		buffer.append(URL);
		buffer.append("?");
		buffer.append("stationids=").append(stationids);
		buffer.append("&");
		buffer.append("date=").append(sysdate);
		buffer.append("&");
		buffer.append("appid=").append(APPID);
		
		String key = RainManager.getKey(SANX_DATA_99, buffer.toString());
		buffer.delete(buffer.lastIndexOf("&"), buffer.length());
		
		buffer.append("&");
		buffer.append("appid=").append(APPID.substring(0, 6));
		buffer.append("&");
		buffer.append("key=").append(key.substring(0, key.length() - 3));
		String result = buffer.toString();
		return result;
	}
	
	/**
	 * 获取最近10个站点号
	 * @param lng
	 * @param lat
	 */
	private void asyncTask(String stationids) {
		//异步请求数据
		HttpAsyncTask2 task = new HttpAsyncTask2();
		task.setMethod("GET");
		task.setTimeOut(CustomHttpClient.TIME_OUT);
		task.execute(getUrl(stationids));
	}
	
	/**
	 * 异步请求方法
	 * @author dell
	 *
	 */
	private class HttpAsyncTask2 extends AsyncTask<String, Void, String> {
		private String method = "GET";
		private List<NameValuePair> nvpList = new ArrayList<NameValuePair>();
		
		public HttpAsyncTask2() {
		}

		@Override
		protected String doInBackground(String... url) {
			String result = null;
			if (method.equalsIgnoreCase("POST")) {
				result = CustomHttpClient.post(url[0], nvpList);
			} else if (method.equalsIgnoreCase("GET")) {
				result = CustomHttpClient.get(url[0]);
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			cancelDialog();
			if (result != null) {
				try {
					JSONArray array = new JSONArray(result);
					StationMonitorDto dto = new StationMonitorDto();
					JSONObject obj = array.getJSONObject(0);
					if (!obj.isNull("present")) {
						JSONObject presentObj = obj.getJSONObject("present");
						if (!presentObj.isNull("atballtemp")) {
							String value = presentObj.getString("atballtemp");
							if (!TextUtils.isEmpty(value)) {
								if (value.length() >= 2 && value.contains(".")) {
									if (value.equals(".0")) {
										dto.currentTemp = "0";
									}else {
										if (TextUtils.equals(value.substring(value.length()-2, value.length()), ".0")) {
											dto.currentTemp = value.substring(0, value.indexOf("."));
										}else {
											dto.currentTemp = value;
										}
									}
								}
							}else {
								dto.currentTemp = CONST.noValue;
							}
						}else {
							dto.currentTemp = CONST.noValue;
						}
						
						if (!presentObj.isNull("atprecipitation1h")) {
							String value = presentObj.getString("atprecipitation1h");
							if (!TextUtils.isEmpty(value)) {
								if (value.length() >= 2 && value.contains(".")) {
									if (value.equals(".0")) {
										dto.current1hRain = "0";
									}else {
										if (TextUtils.equals(value.substring(value.length()-2, value.length()), ".0")) {
											dto.current1hRain = value.substring(0, value.indexOf("."));
										}else {
											dto.current1hRain = value;
										}
									}
								}
							}else {
								dto.current1hRain = CONST.noValue;
							}
						}else {
							dto.current1hRain = CONST.noValue;
						}
						
						if (!presentObj.isNull("athumidity")) {
							String value = presentObj.getString("athumidity");
							if (!TextUtils.isEmpty(value)) {
								if (value.length() >= 2 && value.contains(".")) {
									if (value.equals(".0")) {
										dto.currentHumidity = "0";
									}else {
										if (TextUtils.equals(value.substring(value.length()-2, value.length()), ".0")) {
											dto.currentHumidity = value.substring(0, value.indexOf("."));
										}else {
											dto.currentHumidity = value;
										}
									}
								}
							}else {
								dto.currentHumidity = CONST.noValue;
							}
						}else {
							dto.currentHumidity = CONST.noValue;
						}
						
						if (!presentObj.isNull("atwindspeed")) {
							String value = presentObj.getString("atwindspeed");
							if (!TextUtils.isDigitsOnly(value)) {
								if (value.length() >= 2 && value.contains(".")) {
									if (value.equals(".0")) {
										dto.currentWindSpeed = "0";
									}else {
										if (TextUtils.equals(value.substring(value.length()-2, value.length()), ".0")) {
											dto.currentWindSpeed = value.substring(0, value.indexOf("."));
										}else {
											dto.currentWindSpeed = value;
										}
									}
								}
							}else {
								dto.currentWindSpeed = CONST.noValue;
							}
						}else {
							dto.currentWindSpeed = CONST.noValue;
						}
						
						if (!presentObj.isNull("atairpressure")) {
							String value = presentObj.getString("atairpressure");
							if (!TextUtils.isEmpty(value)) {
								if (value.length() >= 2 && value.contains(".")) {
									if (value.equals(".0")) {
										dto.currentPressure = "0";
									}else {
										if (TextUtils.equals(value.substring(value.length()-2, value.length()), ".0")) {
											dto.currentPressure = value.substring(0, value.indexOf("."));
										}else {
											dto.currentPressure = value;
										}
									}
								}
							}else {
								dto.currentPressure = CONST.noValue;
							}
						}else {
							dto.currentPressure = CONST.noValue;
						}
						
						if (!presentObj.isNull("atvisibility")) {
							String value = presentObj.getString("atvisibility");
							if (!TextUtils.isEmpty(value)) {
								if (value.length() >= 2 && value.contains(".")) {
									if (value.equals(".0")) {
										dto.currentVisible = "0";
									}else {
										if (TextUtils.equals(value.substring(value.length()-2, value.length()), ".0")) {
											float f = Float.valueOf(value)/1000;
											BigDecimal b = new BigDecimal(f);
											float f1 = b.setScale(1,BigDecimal.ROUND_HALF_UP).floatValue();
											dto.currentVisible = String.valueOf(f1).substring(0, String.valueOf(f1).indexOf("."));
										}else {
											float f = Float.valueOf(value)/1000;
											BigDecimal b = new BigDecimal(f);
											float f1 = b.setScale(1,BigDecimal.ROUND_HALF_UP).floatValue();
											dto.currentVisible = String.valueOf(f1);
										}
									}
								}
							}else {
								dto.currentVisible = CONST.noValue;
							}
						}else {
							dto.currentVisible = CONST.noValue;
						}
					}
					
					if (!obj.isNull("statistics")) {
						JSONObject statisticsObj = obj.getJSONObject("statistics");
						if (!statisticsObj.isNull("maxtemperature")) {
							String value = statisticsObj.getString("maxtemperature");
							if (!TextUtils.isEmpty(value)) {
								if (value.length() >= 2 && value.contains(".")) {
									if (value.equals(".0")) {
										dto.statisHighTemp = "0";
									}else {
										if (TextUtils.equals(value.substring(value.length()-2, value.length()), ".0")) {
											dto.statisHighTemp = value.substring(0, value.indexOf("."));
										}else {
											dto.statisHighTemp = value;
										}
									}
								}
							}else {
								dto.statisHighTemp = CONST.noValue;
							}
						}else {
							dto.statisHighTemp = CONST.noValue;
						}
						
						if (!statisticsObj.isNull("mintemperature")) {
							String value = statisticsObj.getString("mintemperature");
							if (!TextUtils.isEmpty(value)) {
								if (value.length() >= 2 && value.contains(".")) {
									if (value.equals(".0")) {
										dto.statisLowTemp = "0";
									}else {
										if (TextUtils.equals(value.substring(value.length()-2, value.length()), ".0")) {
											dto.statisLowTemp = value.substring(0, value.indexOf("."));
										}else {
											dto.statisLowTemp = value;
										}
									}
								}
							}else {
								dto.statisLowTemp = CONST.noValue;
							}
						}else {
							dto.statisLowTemp = CONST.noValue;
						}
						
						if (!statisticsObj.isNull("mean")) {
							String value = statisticsObj.getString("mean");
							if (!TextUtils.isEmpty(value)) {
								if (value.length() >= 2 && value.contains(".")) {
									if (value.equals(".0")) {
										dto.statisAverTemp = "0";
									}else {
										if (TextUtils.equals(value.substring(value.length()-2, value.length()), ".0")) {
											dto.statisAverTemp = value.substring(0, value.indexOf("."));
										}else {
											dto.statisAverTemp = value;
										}
									}
								}
							}else {
								dto.statisAverTemp = CONST.noValue;
							}
						}else {
							dto.statisAverTemp = CONST.noValue;
						}
						
						if (!statisticsObj.isNull("rainfall3")) {
							String value = statisticsObj.getString("rainfall3");
							if (!TextUtils.isEmpty(value)) {
								if (value.length() >= 2 && value.contains(".")) {
									if (value.equals(".0")) {
										dto.statis3hRain = "0";
									}else {
										if (TextUtils.equals(value.substring(value.length()-2, value.length()), ".0")) {
											dto.statis3hRain = value.substring(0, value.indexOf("."));
										}else {
											dto.statis3hRain = value;
										}
									}
								}
							}else {
								dto.statis3hRain = CONST.noValue;
							}
						}else {
							dto.statis3hRain = CONST.noValue;
						}
						
						if (!statisticsObj.isNull("rainfall6")) {
							String value = statisticsObj.getString("rainfall6");
							if (!TextUtils.isEmpty(value)) {
								if (value.length() >= 2 && value.contains(".")) {
									if (value.equals(".0")) {
										dto.statis6hRain = "0";
									}else {
										if (TextUtils.equals(value.substring(value.length()-2, value.length()), ".0")) {
											dto.statis6hRain = value.substring(0, value.indexOf("."));
										}else {
											dto.statis6hRain = value;
										}
									}
								}
							}else {
								dto.statis6hRain = CONST.noValue;
							}
						}else {
							dto.statis6hRain = CONST.noValue;
						}
						
						if (!statisticsObj.isNull("rainfall24")) {
							String value = statisticsObj.getString("rainfall24");
							if (!TextUtils.isEmpty(value)) {
								if (value.length() >= 2 && value.contains(".")) {
									if (value.equals(".0")) {
										dto.statis24hRain = "0";
									}else {
										if (TextUtils.equals(value.substring(value.length()-2, value.length()), ".0")) {
											dto.statis24hRain = value.substring(0, value.indexOf("."));
										}else {
											dto.statis24hRain = value;
										}
									}
								}
							}else {
								dto.statis24hRain = CONST.noValue;
							}
						}else {
							dto.statis24hRain = CONST.noValue;
						}
						
						if (!statisticsObj.isNull("maxhumidity")) {
							String value = statisticsObj.getString("maxhumidity");
							if (!TextUtils.isEmpty(value)) {
								if (value.length() >= 2 && value.contains(".")) {
									if (value.equals(".0")) {
										dto.statisMaxHumidity = "0";
									}else {
										if (TextUtils.equals(value.substring(value.length()-2, value.length()), ".0")) {
											dto.statisMaxHumidity = value.substring(0, value.indexOf("."));
										}else {
											dto.statisMaxHumidity = value;
										}
									}
								}
							}else {
								dto.statisMaxHumidity = CONST.noValue;
							}
						}else {
							dto.statisMaxHumidity = CONST.noValue;
						}
						
						if (!statisticsObj.isNull("minhumidity")) {
							String value = statisticsObj.getString("minhumidity");
							if (!TextUtils.isEmpty(value)) {
								if (value.length() >= 2 && value.contains(".")) {
									if (value.equals(".0")) {
										dto.statisMinHumidity = "0";
									}else {
										if (TextUtils.equals(value.substring(value.length()-2, value.length()), ".0")) {
											dto.statisMinHumidity = value.substring(0, value.indexOf("."));
										}else {
											dto.statisMinHumidity = value;
										}
									}
								}
							}else {
								dto.statisMinHumidity = CONST.noValue;
							}
						}else {
							dto.statisMinHumidity = CONST.noValue;
						}
						
						if (!statisticsObj.isNull("maxwindspeed")) {
							String value = statisticsObj.getString("maxwindspeed");
							if (!TextUtils.isEmpty(value)) {
								if (value.length() >= 2 && value.contains(".")) {
									if (value.equals(".0")) {
										dto.statisMaxSpeed = "0";
									}else {
										if (TextUtils.equals(value.substring(value.length()-2, value.length()), ".0")) {
											dto.statisMaxSpeed = value.substring(0, value.indexOf("."));
										}else {
											dto.statisMaxSpeed = value;
										}
									}
								}
							}else {
								dto.statisMaxSpeed = CONST.noValue;
							}
						}else {
							dto.statisMaxSpeed = CONST.noValue;
						}
						
						if (!statisticsObj.isNull("maxpressure")) {
							String value = statisticsObj.getString("maxpressure");
							if (!TextUtils.isEmpty(value)) {
								if (value.length() >= 2 && value.contains(".")) {
									if (value.equals(".0")) {
										dto.statisMaxPressure = "0";
									}else {
										if (TextUtils.equals(value.substring(value.length()-2, value.length()), ".0")) {
											dto.statisMaxPressure = value.substring(0, value.indexOf("."));
										}else {
											dto.statisMaxPressure = value;
										}
									}
								}
							}else {
								dto.statisMaxPressure = CONST.noValue;
							}
						}else {
							dto.statisMaxPressure = CONST.noValue;
						}
						
						if (!statisticsObj.isNull("minpressure")) {
							String value = statisticsObj.getString("minpressure");
							if (!TextUtils.isEmpty(value)) {
								if (value.length() >= 2 && value.contains(".")) {
									if (value.equals(".0")) {
										dto.statisMinPressure = "0";
									}else {
										if (TextUtils.equals(value.substring(value.length()-2, value.length()), ".0")) {
											dto.statisMinPressure = value.substring(0, value.indexOf("."));
										}else {
											dto.statisMinPressure = value;
										}
									}
								}
							}else {
								dto.statisMinPressure = CONST.noValue;
							}
						}else {
							dto.statisMinPressure = CONST.noValue;
						}
						
						if (!statisticsObj.isNull("minvisibility")) {
							String value = statisticsObj.getString("minvisibility");
							if (!TextUtils.isEmpty(value)) {
								if (value.length() >= 2 && value.contains(".")) {
									if (value.equals(".0")) {
										dto.statisMinVisible = "0";
									}else {
										if (TextUtils.equals(value.substring(value.length()-2, value.length()), ".0")) {
											float f = Float.valueOf(value)/1000;
											BigDecimal b = new BigDecimal(f);
											float f1 = b.setScale(1,BigDecimal.ROUND_HALF_UP).floatValue();
											dto.statisMinVisible = String.valueOf(f1).substring(0, String.valueOf(f1).indexOf("."));
										}else {
											float f = Float.valueOf(value)/1000;
											BigDecimal b = new BigDecimal(f);
											float f1 = b.setScale(1,BigDecimal.ROUND_HALF_UP).floatValue();
											dto.statisMinVisible = String.valueOf(f1);
										}
									}
								}
							}else {
								dto.statisMinVisible = CONST.noValue;
							}
						}else {
							dto.statisMinVisible = CONST.noValue;
						}
					}
					
					JSONArray itemArray = obj.getJSONArray("24H");
					List<StationMonitorDto> tempList = new ArrayList<StationMonitorDto>();
					for (int i = 0; i < itemArray.length(); i++) {
						JSONObject itemObj = itemArray.getJSONObject(i);
						StationMonitorDto data = new StationMonitorDto();
						if (!itemObj.isNull("datatime")) {
							data.time = itemObj.getString("datatime");
						}
						
						if (!itemObj.isNull("balltemp")) {
							String value = itemObj.getString("balltemp");
							if (!TextUtils.isEmpty(value)) {
								if (value.length() >= 2 && value.contains(".")) {
									if (value.equals(".0")) {
										data.ballTemp = "0";
									}else {
										if (TextUtils.equals(value.substring(value.length()-2, value.length()), ".0")) {
											data.ballTemp = value.substring(0, value.indexOf("."));
										}else {
											data.ballTemp = value;
										}
									}
								}
							}else {
								data.ballTemp = CONST.noValue;
							}
						}else {
							data.ballTemp = CONST.noValue;
						}
						
						if (!itemObj.isNull("precipitation1h")) {
							String value = itemObj.getString("precipitation1h");
							if (!TextUtils.isEmpty(value)) {
								if (value.length() >= 2 && value.contains(".")) {
									if (value.equals(".0")) {
										data.precipitation1h = "0";
									}else {
										if (TextUtils.equals(value.substring(value.length()-2, value.length()), ".0")) {
											data.precipitation1h = value.substring(0, value.indexOf("."));
										}else {
											data.precipitation1h = value;
										}
									}
								}
							}else {
								data.precipitation1h = CONST.noValue;
							}
						}else {
							data.precipitation1h = CONST.noValue;
						}
						
						if (!itemObj.isNull("humidity")) {
							String value = itemObj.getString("humidity");
							if (!TextUtils.isEmpty(value)) {
								if (value.length() >= 2 && value.contains(".")) {
									if (value.equals(".0")) {
										data.humidity = "0";
									}else {
										if (TextUtils.equals(value.substring(value.length()-2, value.length()), ".0")) {
											data.humidity = value.substring(0, value.indexOf("."));
										}else {
											data.humidity = value;
										}
									}
								}
							}else {
								data.humidity = CONST.noValue;
							}
						}else {
							data.humidity = CONST.noValue;
						}
						
						if (!itemObj.isNull("windspeed")) {
							String value = itemObj.getString("windspeed");
							if (!TextUtils.isEmpty(value)) {
								if (value.length() >= 2 && value.contains(".")) {
									if (value.equals(".0")) {
										data.windSpeed = "0";
									}else {
										if (TextUtils.equals(value.substring(value.length()-2, value.length()), ".0")) {
											data.windSpeed = value.substring(0, value.indexOf("."));
										}else {
											data.windSpeed = value;
										}
									}
								}
							}else {
								data.windSpeed = CONST.noValue;
							}
						}else {
							data.windSpeed = CONST.noValue;
						}
						
						if (!itemObj.isNull("winddir")) {
							String value = itemObj.getString("winddir");
							if (!TextUtils.isEmpty(value)) {
								if (value.length() >= 2 && value.contains(".")) {
									if (value.equals(".0")) {
										data.windDir = "0";
									}else {
										if (TextUtils.equals(value.substring(value.length()-2, value.length()), ".0")) {
											data.windDir = value.substring(0, value.indexOf("."));
										}else {
											data.windDir = value;
										}
									}
								}
							}else {
								data.windDir = CONST.noValue;
							}
						}else {
							data.windDir = CONST.noValue;
						}
						
						if (!itemObj.isNull("airpressure")) {
							String value = itemObj.getString("airpressure");
							if (!TextUtils.isEmpty(value)) {
								if (value.length() >= 2 && value.contains(".")) {
									if (value.equals(".0")) {
										data.airPressure = "0";
									}else {
										if (TextUtils.equals(value.substring(value.length()-2, value.length()), ".0")) {
											data.airPressure = value.substring(0, value.indexOf("."));
										}else {
											data.airPressure = value;
										}
									}
								}
							}else {
								data.airPressure = CONST.noValue;
							}
						}else {
							data.airPressure = CONST.noValue;
						}
						
						if (!itemObj.isNull("visibility")) {
							String value = itemObj.getString("visibility");
							if (!TextUtils.isEmpty(value)) {
								if (value.length() >= 2 && value.contains(".")) {
									if (value.equals(".0")) {
										data.visibility = "0";
									}else {
										if (TextUtils.equals(value.substring(value.length()-2, value.length()), ".0")) {
											float f = Float.valueOf(value)/1000;
											BigDecimal b = new BigDecimal(f);
											float f1 = b.setScale(1,BigDecimal.ROUND_HALF_UP).floatValue();
											data.visibility = String.valueOf(f1).substring(0, String.valueOf(f1).indexOf("."));
										}else {
											float f = Float.valueOf(value)/1000;
											BigDecimal b = new BigDecimal(f);
											float f1 = b.setScale(1,BigDecimal.ROUND_HALF_UP).floatValue();
											data.visibility = String.valueOf(f1);
										}
									}
								}
							}else {
								data.visibility = CONST.noValue;
							}
						}else {
							data.visibility = CONST.noValue;
						}
						
						tempList.add(data);
					}
					dto.dataList.addAll(tempList);
					initViewPager(dto);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		@SuppressWarnings("unused")
		private void setParams(NameValuePair nvp) {
			nvpList.add(nvp);
		}

		private void setMethod(String method) {
			this.method = method;
		}

		private void setTimeOut(int timeOut) {
			CustomHttpClient.TIME_OUT = timeOut;
		}

		/**
		 * 取消当前task
		 */
		@SuppressWarnings("unused")
		private void cancelTask() {
			CustomHttpClient.shuttdownRequest();
			this.cancel(true);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.llBack:
			finish();
			break;
		case R.id.ivShare:
			Bitmap bitmap1 = CommonUtil.captureView(reTitle);
			Bitmap bitmap2 = CommonUtil.captureMyView(viewPager);
			Bitmap bitmap3 = CommonUtil.mergeBitmap(StationMonitorDetailActivity.this, bitmap1, bitmap2, false);
			CommonUtil.clearBitmap(bitmap1);
			CommonUtil.clearBitmap(bitmap2);
			Bitmap bitmap4 = BitmapFactory.decodeResource(getResources(), R.drawable.iv_share_bottom);
			Bitmap bitmap = CommonUtil.mergeBitmap(StationMonitorDetailActivity.this, bitmap3, bitmap4, false);
			CommonUtil.clearBitmap(bitmap3);
			CommonUtil.clearBitmap(bitmap4);
			CommonUtil.share(StationMonitorDetailActivity.this, bitmap);
			break;

		default:
			break;
		}
	}

}
