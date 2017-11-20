package com.hf.tianjin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hf.tianjin.dto.AqiDto;
import com.hf.tianjin.manager.RainManager;
import com.hf.tianjin.manager.XiangJiManager;
import com.hf.tianjin.utils.CommonUtil;
import com.hf.tianjin.utils.CustomHttpClient;
import com.hf.tianjin.utils.StatisticUtil;
import com.hf.tianjin.utils.WeatherUtil;
import com.hf.tianjin.view.AqiQualityView;

/**
 * 空气质量
 * @author shawn_sun
 *
 */

@SuppressLint("SimpleDateFormat")
public class QualityActivity extends BaseActivity implements OnClickListener{
	
	private Context mContext = null;
	private LinearLayout llBack = null;
	private TextView tvTitle = null;
	private TextView tvName = null;
	private TextView tvTime = null;
	private TextView tvAqiCount = null;
	private TextView tvAqi = null;
	private TextView tvPrompt = null;
	private TextView tvRank = null;
	private TextView tvPm2_5 = null;
	private TextView tvPm10 = null;
	private TextView tvNO2 = null;
	private TextView tvSO2 = null;
	private TextView tvO3 = null;
	private TextView tvCO = null;
	private int width = 0;
	private LinearLayout llContainer = null;
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHH");
	private SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMddHHmm");
	private SimpleDateFormat sdf4 = new SimpleDateFormat("MM月dd日HH时");
	public final static String SANX_DATA_99 = "sanx_data_99";//加密秘钥名称
	public final static String APPID = "f63d329270a44900";//机密需要用到的AppId
	private String cityId = null, lat = null, lng = null;
	private ProgressBar progressBar = null;
	private RelativeLayout reMain = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.quality);
		mContext = this;
		initWidget();
	}
	
	private void initWidget() {
		llBack = (LinearLayout) findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setText(getString(R.string.aqi_quality));
		tvName = (TextView) findViewById(R.id.tvName);
		tvTime = (TextView) findViewById(R.id.tvTime);
		tvAqiCount = (TextView) findViewById(R.id.tvAqiCount);
		tvAqi = (TextView) findViewById(R.id.tvAqi);
		tvPrompt = (TextView) findViewById(R.id.tvPrompt);
		tvRank = (TextView) findViewById(R.id.tvRank);
		tvPm2_5 = (TextView) findViewById(R.id.tvPm2_5);
		tvPm10 = (TextView) findViewById(R.id.tvPm10);
		tvNO2 = (TextView) findViewById(R.id.tvNO2);
		tvSO2 = (TextView) findViewById(R.id.tvSO2);
		tvO3 = (TextView) findViewById(R.id.tvO3);
		tvCO = (TextView) findViewById(R.id.tvCO);
		llContainer = (LinearLayout) findViewById(R.id.llContainer);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		reMain = (RelativeLayout) findViewById(R.id.reMain);
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		
		setIntentAqiData();
    	
    	String title = getIntent().getStringExtra("cityName");
    	if (!TextUtils.isEmpty(title)) {
			tvName.setText(title);
		}
    	
    	lat = getIntent().getStringExtra("lat");
    	lng = getIntent().getStringExtra("lng");
    	cityId = getIntent().getStringExtra("cityId");
    	asyncTaskRank(cityId);
    	queryXiangJiAqi(lat, lng);
    	
		StatisticUtil.submitClickCount("3", "空气质量");
	}
	
	/**
	 * 获取上个界面传过来的aqi数据并赋值
	 */
	private void setIntentAqiData() {
		AqiDto data = getIntent().getExtras().getParcelable("aqiData");
    	if (data != null) {
			if (!TextUtils.isEmpty(data.date)) {
				try {
					tvTime.setText("环境监测总站"+sdf4.format(sdf3.parse(data.date))+"更新");
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		
			if (!TextUtils.isEmpty(data.aqi)) {
				tvAqiCount.setText(data.aqi);
				tvAqi.setText(WeatherUtil.getAqi(mContext, Integer.valueOf(data.aqi)));
				tvAqi.setBackgroundResource(getCornerBackground(Integer.valueOf(data.aqi)));
				tvPrompt.setText(getPrompt(Integer.valueOf(data.aqi)));
			}
		
			if (!TextUtils.isEmpty(data.pm2_5)) {
				tvPm2_5.setText(data.pm2_5);
			}
		
			if (!TextUtils.isEmpty(data.pm10)) {
				tvPm10.setText(data.pm10);
			}
		
			if (!TextUtils.isEmpty(data.NO2)) {
				tvNO2.setText(data.NO2);
			}
		
			if (!TextUtils.isEmpty(data.SO2)) {
				tvSO2.setText(data.SO2);
			}
		
			if (!TextUtils.isEmpty(data.O3)) {
				tvO3.setText(data.O3);
			}
		
			if (!TextUtils.isEmpty(data.CO)) {
				tvCO.setText(data.CO);
			}
		}
	}
	
	/**
	 * 根据aqi数据获取相对应的背景图标
	 * @param value
	 * @return
	 */
	private int getCornerBackground(int value) {
		int drawable = -1;
		if (value >= 0 && value < 50) {
			drawable = R.drawable.corner_aqi_one;
		}else if (value >= 50 && value < 100) {
			drawable = R.drawable.corner_aqi_two;
		}else if (value >= 100 && value < 150) {
			drawable = R.drawable.corner_aqi_three;
		}else if (value >= 150 && value < 200) {
			drawable = R.drawable.corner_aqi_four;
		}else if (value >= 200 && value < 300) {
			drawable = R.drawable.corner_aqi_five;
		}else if (value >= 300) {
			drawable = R.drawable.corner_aqi_six;
		}
		return drawable;
	}
	
	/**
	 * 根据aqi值获取aqi的提示信息
	 * @param value
	 * @return
	 */
	private String getPrompt(int value) {
		String aqi = null;
		if (value >= 0 && value < 50) {
			aqi = getString(R.string.aqi1_text);
		}else if (value >= 50 && value < 100) {
			aqi = getString(R.string.aqi2_text);
		}else if (value >= 100 && value < 150) {
			aqi = getString(R.string.aqi3_text);
		}else if (value >= 150 && value < 200) {
			aqi = getString(R.string.aqi4_text);
		}else if (value >= 200 && value < 300) {
			aqi = getString(R.string.aqi5_text);
		}else if (value >= 300) {
			aqi = getString(R.string.aqi6_text);
		}
		return aqi;
	}
	
	/**
	 * 加密请求字符串
	 * @param url 基本串
	 * @param lng 经度
	 * @param lat 维度
	 * @return
	 */
	private String getSecretUrl(String areaids) {
		String URL = "http://scapi.weather.com.cn/weather/onareaids";//空气污染
		String sysdate = RainManager.getDate(Calendar.getInstance(), "yyyyMMddHHmm");//系统时间
		StringBuffer buffer = new StringBuffer();
		buffer.append(URL);
		buffer.append("?");
		buffer.append("date=").append(sysdate);
		buffer.append("&");
		buffer.append("areaids=").append(areaids);
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
	 * 获取aqi排行
	 */
	private void asyncTaskRank(String areaids) {
		//异步请求数据
		if (TextUtils.isEmpty(areaids)) {
			return;
		}
		HttpAsyncTaskRank task = new HttpAsyncTaskRank();
		task.setMethod("GET");
		task.setTimeOut(CustomHttpClient.TIME_OUT);
		task.execute(getSecretUrl(areaids));
	}
	
	/**
	 * 异步请求方法
	 * @author dell
	 *
	 */
	private class HttpAsyncTaskRank extends AsyncTask<String, Void, String> {
		private String method = "GET";
		private List<NameValuePair> nvpList = new ArrayList<NameValuePair>();
		
		public HttpAsyncTaskRank() {
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
			if (result != null) {
				try {
					JSONArray array = new JSONArray(result);
					if (array.length() > 0) {
						JSONObject obj = array.getJSONObject(0);
						if (!obj.isNull("rank")) {
							int rank = obj.getInt("rank");
							tvRank.setText("全国AQI排名"+rank);
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
	 * 请求象辑aqi
	 */
	private void queryXiangJiAqi(String lat, String lng) {
		Date date = new Date();
    	long timestamp = date.getTime();
    	String start1 = sdf1.format(timestamp);
    	String end1 = sdf1.format(timestamp+1000*60*60*24);
    	if (!TextUtils.isEmpty(lat) && !TextUtils.isEmpty(lng)) {
    		asyncQueryXiangJi(XiangJiManager.getXJHourFore(Double.valueOf(lng), Double.valueOf(lat), start1, end1, timestamp));
		}
	}
	
	/**
	 * 异步请求
	 */
	private void asyncQueryXiangJi(String requestUrl) {
		HttpAsyncTaskXiangJi task = new HttpAsyncTaskXiangJi();
		task.setMethod("GET");
		task.setTimeOut(CustomHttpClient.TIME_OUT);
		task.execute(requestUrl);
	}
	
	/**
	 * 异步请求方法
	 * @author dell
	 *
	 */
	private class HttpAsyncTaskXiangJi extends AsyncTask<String, Void, String> {
		private String method = "GET";
		private List<NameValuePair> nvpList = new ArrayList<NameValuePair>();
		
		public HttpAsyncTaskXiangJi() {
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
					JSONObject obj = new JSONObject(requestResult.toString());
					String aqiDate = null;
					if (!obj.isNull("reqTime")) {
						aqiDate = obj.getString("reqTime");
					}
					
					List<AqiDto> aqiList = new ArrayList<AqiDto>();
					if (!obj.isNull("series")) {
						JSONArray array = obj.getJSONArray("series");
						for (int i = 0; i < array.length(); i++) {
							AqiDto dto = new AqiDto();
							dto.aqi = String.valueOf(array.get(i));
							if (!TextUtils.isEmpty(tvAqiCount.getText().toString()) && i == 0) {
								dto.aqi = tvAqiCount.getText().toString();
							}
							aqiList.add(dto);
						}
					}
					
					if (aqiList.size() == 0) {
						return;
					}else {
						if (!TextUtils.isEmpty(aqiList.get(0).aqi)) {
//							int maxAqi = Integer.valueOf(aqiList.get(0).aqi);
//							for (int i = 0; i < aqiList.size(); i++) {
//								if (!TextUtils.isEmpty(aqiList.get(i).aqi)) {
//									if (maxAqi <= Integer.valueOf(aqiList.get(i).aqi)) {
//										maxAqi = Integer.valueOf(aqiList.get(i).aqi);
//									}
//								}
//							}
//							maxAqi = maxAqi + (50 - maxAqi%50);
							
							AqiQualityView aqiView = new AqiQualityView(mContext);
							aqiView.setData(aqiList, aqiDate);
							int viewHeight = (int)(CommonUtil.dip2px(mContext, 220));
//							if (maxAqi <= 100) {
//								viewHeight = (int)(CommonUtil.dip2px(mContext, 150));
//							}else if (maxAqi > 100 && maxAqi <= 150) {
//								viewHeight = (int)(CommonUtil.dip2px(mContext, 200));
//							}else if (maxAqi > 150) {
//								viewHeight = (int)(CommonUtil.dip2px(mContext, 250));
//							}
							llContainer.removeAllViews();
							llContainer.addView(aqiView, width*2, viewHeight);
						}

						progressBar.setVisibility(View.GONE);
						reMain.setVisibility(View.VISIBLE);
					}
				} catch (JSONException e1) {
					e1.printStackTrace();
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

		default:
			break;
		}
	}
	
}
