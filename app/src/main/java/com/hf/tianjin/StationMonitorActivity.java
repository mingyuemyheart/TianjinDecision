package com.hf.tianjin;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnCameraChangeListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMapScreenShotListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.hf.tianjin.common.CONST;
import com.hf.tianjin.dto.StationMonitorDto;
import com.hf.tianjin.manager.DBManager;
import com.hf.tianjin.manager.RainManager;
import com.hf.tianjin.manager.StationManager;
import com.hf.tianjin.utils.CommonUtil;
import com.hf.tianjin.utils.CustomHttpClient;
import com.hf.tianjin.utils.StatisticUtil;
import com.hf.tianjin.view.MyDialog2;

/**
 * 站点监测
 * @author shawn_sun
 *
 */

@SuppressLint("SimpleDateFormat")
public class StationMonitorActivity extends BaseActivity implements OnClickListener, AMapLocationListener, OnMarkerClickListener, 
OnMapClickListener, OnGeocodeSearchListener, OnCameraChangeListener, OnMapScreenShotListener{
	
	private Context mContext = null;
	private LinearLayout llBack = null;
	private TextView tvTitle = null;
	private MapView mMapView = null;
	private AMap aMap = null;
	private List<StationMonitorDto> stationList = new ArrayList<StationMonitorDto>();
	private TextView tvName = null;
	private TextView tvStationId = null;
	private TextView tvTemp = null;
	private TextView tvDistance = null;
	private TextView tvJiangshui = null;
	private TextView tvShidu = null;
	private TextView tvWind = null;
	private TextView tvLoudian = null;
	private TextView tvVisible = null;
	private TextView tvPressrue = null;
	private TextView tvCheckStation = null;
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:00");
	private SimpleDateFormat sdf2 = new SimpleDateFormat("HH:00");
	private long pastTime = 60*60*1000;
	private RelativeLayout reContent = null;
	private ImageView ivDelete = null;
	public final static String SANX_DATA_99 = "sanx_data_99";//加密秘钥名称
	public final static String APPID = "f63d329270a44900";//机密需要用到的AppId
	private MyDialog2 mDialog = null;
	private float zoom = 4.0f;
	private AMapLocationClientOption mLocationOption = null;//声明mLocationOption对象
	private AMapLocationClient mLocationClient = null;//声明AMapLocationClient类对象
	private String stationName = null;
	private String stationId = null;
	private TextView tvTemp2 = null;
	private TextView tvRain2 = null;
	private TextView tvHumidity2 = null;
	private TextView tvVisibility2 = null;
	private TextView tvPressure2 = null;
	private TextView tvWindSpeed2 = null;
	private ImageView ivOpen = null;
	private LinearLayout llContainer = null;
	private ImageView ivTemp = null;
	private ImageView ivRain = null;
	private ImageView ivHumidity = null;
	private ImageView ivVisibility = null;
	private ImageView ivPressure = null;
	private ImageView ivWindSpeed = null;
	private List<Marker> markerList = new ArrayList<Marker>();//10个站点的marker
	private ImageView ivMapSearch = null;//省市县筛选站点
	private double locationLat = 0;
	private double locationLng = 0;
	private GeocodeSearch geocoderSearch = null;
	private String precipitation1hJson = null;
	private String precipitation3hJson = null;
	private String precipitation6hJson = null;
	private String precipitation24hJson = null;
	private String balltempJson = null;
	private String humidityJson = null;
	private String visibilityJson = null;
	private String windspeedJson = null;
	private String airpressureJson = null;
	private List<Polygon> polygons = new ArrayList<Polygon>();
	private Marker locationMarker = null;//定位的marker点
	private Marker selecteMarker = null;//点击地图添加的marker点
	private ImageView ivLocation = null;//定位按钮
	private TextView tvLayerName = null;//图层名称
	private int value = 1;//默认为降水
	private ImageView ivLegend = null;
	private ImageView ivLegendPrompt = null;
	private boolean isLegendVisible = false;
	private HorizontalScrollView hScrollView = null;
	private LinearLayout llRain = null;
	private TextView tv1, tv2, tv3, tv4;
	private ImageView ivRank = null;//进入排序界面
	private RelativeLayout reShare = null;
	private ImageView ivShare = null;
	private LinearLayout llScrollView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.station_monitor );
		mContext = this;
		initMap(savedInstanceState);
		initWidget();
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
	
	/**
	 * 初始化地图
	 */
	private void initMap(Bundle bundle) {
		mMapView = (MapView) findViewById(R.id.map);
		mMapView.onCreate(bundle);
		if (aMap == null) {
			aMap = mMapView.getMap();
		}
		aMap.moveCamera(CameraUpdateFactory.zoomTo(zoom));
		aMap.getUiSettings().setZoomControlsEnabled(false);
		aMap.setOnMarkerClickListener(this);
		aMap.setOnMapClickListener(this);
		aMap.setOnCameraChangeListener(this);
		
		asyncGetMapUrl("http://61.4.184.171:8080/weather/rgwst/JsonCatalogue?map=china");//获取中国地区五中天气现象数据
	}
	
	/**
	 * 获取五种天气要素的json地址
	 * @param url
	 */
	private void asyncGetMapUrl(String url) {
		//异步请求数据
		HttpAsyncTaskUrl task = new HttpAsyncTaskUrl();
		task.setMethod("GET");
		task.setTimeOut(CustomHttpClient.TIME_OUT);
		task.execute(url);
	}
	
	/**
	 * 异步请求方法
	 * @author dell
	 *
	 */
	private class HttpAsyncTaskUrl extends AsyncTask<String, Void, String> {
		private String method = "GET";
		private List<NameValuePair> nvpList = new ArrayList<NameValuePair>();
		
		public HttpAsyncTaskUrl() {
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
					if (array.length() == 0) {
						hScrollView.setVisibility(View.GONE);
						return;
					}
					JSONObject obj = array.getJSONObject(0);
					precipitation1hJson  = obj.getString("precipitation1h");
					precipitation3hJson  = obj.getString("rainfall3");
					precipitation6hJson  = obj.getString("rainfall6");
					precipitation24hJson  = obj.getString("rainfall24");
					balltempJson  = obj.getString("balltemp");
					humidityJson = obj.getString("humidity");
					visibilityJson  = obj.getString("visibility");
					windspeedJson  = obj.getString("windspeed");
					airpressureJson  = obj.getString("airpressure");
					if (!TextUtils.isEmpty(precipitation1hJson)) {
						asyncGetMapData(precipitation1hJson);
					}
					if (!TextUtils.isEmpty(precipitation3hJson)) {
						StationManager.asyncGetMapData3H(precipitation3hJson);
					}
					if (!TextUtils.isEmpty(precipitation6hJson)) {
						StationManager.asyncGetMapData6H(precipitation6hJson);
					}
					if (!TextUtils.isEmpty(precipitation24hJson)) {
						StationManager.asyncGetMapData24H(precipitation24hJson);
					}
					if (!TextUtils.isEmpty(balltempJson)) {
						StationManager.asyncGetMapData2(balltempJson);
					}
					if (!TextUtils.isEmpty(humidityJson)) {
						StationManager.asyncGetMapData3(humidityJson);
					}
					if (!TextUtils.isEmpty(visibilityJson)) {
						StationManager.asyncGetMapData4(visibilityJson);
					}
					if (!TextUtils.isEmpty(airpressureJson)) {
						StationManager.asyncGetMapData5(airpressureJson);
					}
					if (!TextUtils.isEmpty(windspeedJson)) {
						StationManager.asyncGetMapData6(windspeedJson);
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
	 * 获取五中天气要素数据接口
	 * @param url
	 */
	private void asyncGetMapData(String url) {
		//异步请求数据
		HttpAsyncTaskMap task = new HttpAsyncTaskMap();
		task.setMethod("GET");
		task.setTimeOut(CustomHttpClient.TIME_OUT);
		task.execute(url);
	}
	
	/**
	 * 异步请求方法
	 * @author dell
	 *
	 */
	private class HttpAsyncTaskMap extends AsyncTask<String, Void, String> {
		private String method = "GET";
		private List<NameValuePair> nvpList = new ArrayList<NameValuePair>();
		
		public HttpAsyncTaskMap() {
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
				StationManager.precipitation1hResult = result;
				asynParseMapData(result, getString(R.string.layer_rain1));
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
     * 异步解析五中天气现象数据并绘制在地图上
     */
	private void asynParseMapData(String result, String type) {
		AsynLoadTask task = new AsynLoadTask(result, type);  
        task.execute();
	}
	
	private class AsynLoadTask extends AsyncTask<Void, Void, Void> {
		
		private String result = null;
		private String type = null;
		
		private AsynLoadTask(String result, String type) {
			this.result = result;
			this.type = type;
		}

		@Override
		protected void onPreExecute() {
			//开始执行
		}
		
		@Override
		protected void onProgressUpdate(Void... values) {
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			//执行完毕
		}

		@Override
		protected Void doInBackground(Void... params) {
			drawDataToMap(result, aMap, type);
			return null;
		}
	}
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				String time = (String) msg.obj;
				if (time != null) {
					tvLayerName.setText(time);
				}
				break;

			default:
				break;
			}
		};
	};
	
	/**
	 * 回执区域
	 * @param context
	 * @param aMap
	 */
	private void drawDataToMap(String result, AMap aMap, String type) {
		if (TextUtils.isEmpty(result) || aMap == null) {
			return;
		}
		
		for (int i = 0; i < polygons.size(); i++) {
			polygons.get(i).remove();
		}
		
		try {
			JSONObject obj = new JSONObject(result);
			if (!obj.isNull("t")) {
				long time1 = obj.getLong("t") - pastTime;
				long time2 = obj.getLong("t");
				String time = "( "+sdf1.format(time1)+"--"+sdf2.format(time2)+" )";
				Message msg = new Message();
				msg.what = 0;
				msg.obj = type+"\n"+time;
				handler.sendMessage(msg);
			}
			JSONArray array = obj.getJSONArray("l");
			int length = array.length();
//			if (length > 200) {
//				length = 200;
//			}
			for (int i = 0; i < length; i++) {
				JSONObject itemObj = array.getJSONObject(i);
				JSONArray c = itemObj.getJSONArray("c");
				int r = c.getInt(0);
				int g = c.getInt(1);
				int b = c.getInt(2);
				int a = c.getInt(3)*255;
				
				String p = itemObj.getString("p");
				if (!TextUtils.isEmpty(p)) {
					String[] points = p.split(";");
					PolygonOptions polylineOption = new PolygonOptions();
					polylineOption.fillColor(Color.argb(a, r, g, b));
					polylineOption.strokeColor(Color.TRANSPARENT);
					for (int j = 0; j < points.length; j++) {
						String[] value = points[j].split(",");
						double lat = Double.valueOf(value[1]);
						double lng = Double.valueOf(value[0]);
						polylineOption.add(new LatLng(lat, lng));
					}
					Polygon polygon = aMap.addPolygon(polylineOption);
					polygons.add(polygon);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 初始化控件
	 */
	private void initWidget() {
		llBack = (LinearLayout) findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setText(getString(R.string.fact_monitor));
		tvName = (TextView) findViewById(R.id.tvName);
		tvStationId = (TextView) findViewById(R.id.tvStationId);
		tvTemp = (TextView) findViewById(R.id.tvTemp);
		tvDistance = (TextView) findViewById(R.id.tvDistance);
		tvJiangshui = (TextView) findViewById(R.id.tvJiangshui);
		tvWind = (TextView) findViewById(R.id.tvWind);
		tvShidu = (TextView) findViewById(R.id.tvShidu);
		tvLoudian = (TextView) findViewById(R.id.tvLoudian);
		tvVisible = (TextView) findViewById(R.id.tvVisible);
		tvPressrue = (TextView) findViewById(R.id.tvPressrue);
		tvCheckStation = (TextView) findViewById(R.id.tvCheckStation);
		tvCheckStation.setOnClickListener(this);
		reContent = (RelativeLayout) findViewById(R.id.reContent);
		reContent.setOnClickListener(this);
		ivDelete = (ImageView) findViewById(R.id.ivDelete);
		ivDelete.setOnClickListener(this);
		ivLocation = (ImageView) findViewById(R.id.ivLocation);
		ivLocation.setOnClickListener(this);
		tvLayerName = (TextView) findViewById(R.id.tvLayerName);
		ivLegend = (ImageView) findViewById(R.id.ivLegend);
		ivLegendPrompt = (ImageView) findViewById(R.id.ivLegendPrompt);
		ivLegendPrompt.setOnClickListener(this);
		hScrollView = (HorizontalScrollView) findViewById(R.id.hScrollView);
		llRain = (LinearLayout) findViewById(R.id.llRain);
		reShare = (RelativeLayout) findViewById(R.id.reShare);
		ivShare = (ImageView) findViewById(R.id.ivShare);
		ivShare.setOnClickListener(this);
		llScrollView = (LinearLayout) findViewById(R.id.llScrollView);
		tv1 = (TextView) findViewById(R.id.tv1);
		tv1.setOnClickListener(this);
		tv2 = (TextView) findViewById(R.id.tv2);
		tv2.setOnClickListener(this);
		tv3 = (TextView) findViewById(R.id.tv3);
		tv3.setOnClickListener(this);
		tv4 = (TextView) findViewById(R.id.tv4);
		tv4.setOnClickListener(this);
		ivRank = (ImageView) findViewById(R.id.ivRank);
		ivRank.setOnClickListener(this);
		
		tvTemp2 = (TextView) findViewById(R.id.tvTemp2);
		tvTemp2.setOnClickListener(this);
		tvRain2 = (TextView) findViewById(R.id.tvRain2);
		tvRain2.setOnClickListener(this);
		tvHumidity2 = (TextView) findViewById(R.id.tvHumidity2);
		tvHumidity2.setOnClickListener(this);
		tvVisibility2 = (TextView) findViewById(R.id.tvVisibility2);
		tvVisibility2.setOnClickListener(this);
		tvPressure2 = (TextView) findViewById(R.id.tvPressure2);
		tvPressure2.setOnClickListener(this);
		tvWindSpeed2 = (TextView) findViewById(R.id.tvWindSpeed2);
		tvWindSpeed2.setOnClickListener(this);
		
		ivOpen = (ImageView) findViewById(R.id.ivOpen);
		ivOpen.setOnClickListener(this);
		llContainer = (LinearLayout) findViewById(R.id.llContainer);
		ivTemp = (ImageView) findViewById(R.id.ivTemp);
		ivTemp.setOnClickListener(this);
		ivRain = (ImageView) findViewById(R.id.ivRain);
		ivRain.setOnClickListener(this);
		ivHumidity = (ImageView) findViewById(R.id.ivHumidity);
		ivHumidity.setOnClickListener(this);
		ivVisibility = (ImageView) findViewById(R.id.ivVisibility);
		ivVisibility.setOnClickListener(this);
		ivPressure = (ImageView) findViewById(R.id.ivPressure);
		ivPressure.setOnClickListener(this);
		ivWindSpeed = (ImageView) findViewById(R.id.ivWindSpeed);
		ivWindSpeed.setOnClickListener(this);
		ivMapSearch = (ImageView) findViewById(R.id.ivMapSearch);
		ivMapSearch.setOnClickListener(this);
		
		geocoderSearch = new GeocodeSearch(mContext);
		geocoderSearch.setOnGeocodeSearchListener(this);
		
		startLocation();
		StatisticUtil.submitClickCount("6", "监测");
	}
	
	/**
	 * 开始定位
	 */
	private void startLocation() {
        mLocationOption = new AMapLocationClientOption();//初始化定位参数
        mLocationClient = new AMapLocationClient(mContext);//初始化定位
        mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setNeedAddress(true);//设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setOnceLocation(true);//设置是否只定位一次,默认为false
        mLocationOption.setWifiActiveScan(true);//设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setMockEnable(false);//设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setInterval(2000);//设置定位间隔,单位毫秒,默认为2000ms
        mLocationClient.setLocationOption(mLocationOption);//给定位客户端对象设置定位参数
        mLocationClient.setLocationListener(this);
        mLocationClient.startLocation();//启动定位
	}

	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (amapLocation != null && amapLocation.getErrorCode() == 0) {
			locationLat = amapLocation.getLatitude();
			locationLng = amapLocation.getLongitude();
			ivLocation.setVisibility(View.VISIBLE);
			LatLng latLng = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
			MarkerOptions options = new MarkerOptions();
			options.anchor(0.5f, 0.5f);
			Bitmap bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeResource(getResources(), R.drawable.iv_map_location2), 
					(int)(CommonUtil.dip2px(mContext, 20)), (int)(CommonUtil.dip2px(mContext, 20)));
			if (bitmap != null) {
				options.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
			}else {
				options.icon(BitmapDescriptorFactory.fromResource(R.drawable.iv_map_location2));
			}
			options.position(latLng);
			locationMarker = aMap.addMarker(options);
        }
	}
	
	/**
	 * 加密请求字符串
	 * @param url 基本串
	 * @param lng 经度
	 * @param lat 维度
	 * @return
	 */
	private String getStaionIdUrl(double lng, double lat) {
		String URL = "http://61.4.184.171:8080/weather/rgwst/nearStation";//
		String sysdate = RainManager.getDate(Calendar.getInstance(), "yyyyMMddHHmmss");//系统时间
		StringBuffer buffer = new StringBuffer();
		buffer.append(URL);
		buffer.append("?");
		buffer.append("lon=").append(lng);
		buffer.append("&");
		buffer.append("lat=").append(lat);
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
	private void asyncTask(double lng, double lat) {
		//异步请求数据
		HttpAsyncTask task = new HttpAsyncTask();
		task.setMethod("GET");
		task.setTimeOut(CustomHttpClient.TIME_OUT);
		task.execute(getStaionIdUrl(lng, lat));
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
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result != null) {
				try {
					JSONArray array = new JSONArray(result);
					String ids = "";
					for (int i = 0; i < array.length(); i++) {
						StationMonitorDto dto = new StationMonitorDto();
						JSONObject obj = array.getJSONObject(i);
						if (!obj.isNull("stationid")) {
							String stationId = obj.getString("stationid");
							if (!TextUtils.isEmpty(stationId)) {
								ids += stationId+",";
								dto.stationId = stationId;
							}
						}
						
						if (!obj.isNull("dis")) {
							String value = obj.getString("dis");
							if (value.length() >= 2 && value.contains(".")) {
								if (value.equals(".0")) {
									dto.distance = "0";
								}else {
									if (TextUtils.equals(value.substring(value.length()-2, value.length()), ".0")) {
										dto.distance = value.substring(0, value.indexOf("."));
									}else {
										dto.distance = value;
									}
								}
							}
						}else {
							dto.distance = CONST.noValue;
						}
						
						stationList.add(dto);
					}
					if (!TextUtils.isEmpty(ids)) {
						asyncTaskContent(ids.substring(0, ids.length()-1), false);
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
	 * 根据站点id查询站点信息
	 * @param stationId
	 * @param data
	 */
	private void queryInfoByStationId(String stationId, StationMonitorDto data) {
		DBManager dbManager = new DBManager(mContext);
		dbManager.openDateBase();
		SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/" + DBManager.DB_NAME, null);
		try {
			if (database != null && database.isOpen()) {
				Cursor cursor = database.rawQuery("select * from " + DBManager.TABLE_NAME1 + " where SID = " + stationId, null);
				for (int i = 0; i < cursor.getCount(); i++) {
					cursor.moveToPosition(i);
					data.lat = cursor.getString(cursor.getColumnIndex("LAT"));
					data.lng = cursor.getString(cursor.getColumnIndex("LON"));
					
					String pro = cursor.getString(cursor.getColumnIndex("PRO"));
					String city = cursor.getString(cursor.getColumnIndex("CITY"));
					String dis = cursor.getString(cursor.getColumnIndex("DIST"));
					String addr = cursor.getString(cursor.getColumnIndex("ADDR"));
					
					if (addr.contains(pro) && addr.contains(city) && addr.contains(dis)) {
						data.name = addr;
					}else if (addr.contains(city) && addr.contains(dis)) {
						data.name = pro+addr;
					}else if (addr.contains(dis)) {
						data.name = pro+city+addr;
					}else {
						if (pro.contains(city) || dis.contains(city)) {
							data.name = pro+dis+addr;
						}else {
							data.name = pro+city+dis+addr;
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
	}
	
	/**
	 * 加密请求字符串
	 * @param url 基本串
	 * @param lng 经度
	 * @param lat 维度
	 * @return
	 */
	private String getStationContentUrl(String stationIds) {
		String URL = "http://61.4.184.171:8080/weather/rgwst/NewestData";
		String sysdate = RainManager.getDate(Calendar.getInstance(), "yyyyMMddHHmmss");//系统时间
		StringBuffer buffer = new StringBuffer();
		buffer.append(URL);
		buffer.append("?");
		buffer.append("stationids=").append(stationIds);
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
	 * 获取最近10个站点的信息
	 * @param stationIds
	 * @param isLatLng 是否已经拥有经纬度
	 */
	private void asyncTaskContent(String stationIds, boolean isLatLng) {
		//异步请求数据
		HttpAsyncTaskContent task = new HttpAsyncTaskContent(isLatLng);
		task.setMethod("GET");
		task.setTimeOut(CustomHttpClient.TIME_OUT);
		task.execute(getStationContentUrl(stationIds));
	}
	
	/**
	 * 异步请求方法
	 * @author dell
	 *
	 */
	private class HttpAsyncTaskContent extends AsyncTask<String, Void, String> {
		private String method = "GET";
		private List<NameValuePair> nvpList = new ArrayList<NameValuePair>();
		private boolean isLatLng;
		
		public HttpAsyncTaskContent(boolean isLatLng) {
			this.isLatLng = isLatLng;
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
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);
						StationMonitorDto dto = stationList.get(i);
						
						if (isLatLng == false) {
							queryInfoByStationId(dto.stationId, dto);
						}
						
						if (!obj.isNull("datatime")) {
							dto.time = obj.getString("datatime");
						}
						
						if (!obj.isNull("airpressure")) {
							String value = obj.getString("airpressure");
							if (!TextUtils.isEmpty(value)) {
								if (value.length() >= 2 && value.contains(".")) {
									if (value.equals(".0")) {
										dto.airPressure = "0";
									}else {
										if (TextUtils.equals(value.substring(value.length()-2, value.length()), ".0")) {
											dto.airPressure = value.substring(0, value.indexOf("."));
										}else {
											dto.airPressure = value;
										}
									}
								}
							}else {
								dto.airPressure = CONST.noValue;
							}
						}else {
							dto.airPressure = CONST.noValue;
						}
						
						if (!obj.isNull("balltemp")) {
							String value = obj.getString("balltemp");
							if (!TextUtils.isEmpty(value)) {
								if (value.length() >= 2 && value.contains(".")) {
									if (value.equals(".0")) {
										dto.ballTemp = "0";
									}else {
										if (TextUtils.equals(value.substring(value.length()-2, value.length()), ".0")) {
											dto.ballTemp = value.substring(0, value.indexOf("."));
										}else {
											dto.ballTemp = value;
										}
									}
								}
							}else {
								dto.ballTemp = CONST.noValue;
							}
						}else {
							dto.ballTemp = CONST.noValue;
						}
						
						if (!obj.isNull("humidity")) {
							String value = obj.getString("humidity");
							if (!TextUtils.isEmpty(value)) {
								if (value.length() >= 2 && value.contains(".")) {
									if (value.equals(".0")) {
										dto.humidity = "0";
									}else {
										if (TextUtils.equals(value.substring(value.length()-2, value.length()), ".0")) {
											dto.humidity = value.substring(0, value.indexOf("."));
										}else {
											dto.humidity = value;
										}
									}
								}
							}else {
								dto.humidity = CONST.noValue;
							}
						}else {
							dto.humidity = CONST.noValue;
						}
						
						if (!obj.isNull("precipitation1h")) {
							String value = obj.getString("precipitation1h");
							if (!TextUtils.isEmpty(value)) {
								if (value.length() >= 2 && value.contains(".")) {
									if (value.equals(".0")) {
										dto.precipitation1h = "0";
									}else {
										if (TextUtils.equals(value.substring(value.length()-2, value.length()), ".0")) {
											dto.precipitation1h = value.substring(0, value.indexOf("."));
										}else {
											dto.precipitation1h = value;
										}
									}
								}
							}else {
								dto.precipitation1h = CONST.noValue;
							}
						}else {
							dto.precipitation1h = CONST.noValue;
						}
						
						if (!obj.isNull("rainfall3")) {
							String value = obj.getString("rainfall3");
							if (!TextUtils.isEmpty(value)) {
								if (value.length() >= 2 && value.contains(".")) {
									if (value.equals(".0")) {
										dto.precipitation3h = "0";
									}else {
										if (TextUtils.equals(value.substring(value.length()-2, value.length()), ".0")) {
											dto.precipitation3h = value.substring(0, value.indexOf("."));
										}else {
											dto.precipitation3h = value;
										}
									}
								}
							}else {
								dto.precipitation3h = CONST.noValue;
							}
						}else {
							dto.precipitation3h = CONST.noValue;
						}
						
						if (!obj.isNull("rainfall6")) {
							String value = obj.getString("rainfall6");
							if (!TextUtils.isEmpty(value)) {
								if (value.length() >= 2 && value.contains(".")) {
									if (value.equals(".0")) {
										dto.precipitation6h = "0";
									}else {
										if (TextUtils.equals(value.substring(value.length()-2, value.length()), ".0")) {
											dto.precipitation6h = value.substring(0, value.indexOf("."));
										}else {
											dto.precipitation6h = value;
										}
									}
								}
							}else {
								dto.precipitation6h = CONST.noValue;
							}
						}else {
							dto.precipitation6h = CONST.noValue;
						}
						
						if (!obj.isNull("rainfall24")) {
							String value = obj.getString("rainfall24");
							if (!TextUtils.isEmpty(value)) {
								if (value.length() >= 2 && value.contains(".")) {
									if (value.equals(".0")) {
										dto.precipitation24h = "0";
									}else {
										if (TextUtils.equals(value.substring(value.length()-2, value.length()), ".0")) {
											dto.precipitation24h = value.substring(0, value.indexOf("."));
										}else {
											dto.precipitation24h = value;
										}
									}
								}
							}else {
								dto.precipitation24h = CONST.noValue;
							}
						}else {
							dto.precipitation24h = CONST.noValue;
						}
						
						if (!obj.isNull("winddir")) {
							String dir = null;
							String value = obj.getString("winddir");
							if (!TextUtils.isEmpty(value)) {
								if (value.length() >= 2 && value.contains(".")) {
									if (value.equals(".0")) {
										dir = "0";
									}else {
										if (TextUtils.equals(value.substring(value.length()-2, value.length()), ".0")) {
											dir = value.substring(0, value.indexOf("."));
										}else {
											dir = value;
										}
									}
								}
								
								float fx = Float.valueOf(dir);
								String wind_dir = null;
								if(fx == 0 || fx == 360){
									wind_dir = mContext.getString(R.string.chart_north);
								}else if(fx > 0 && fx < 90){
									wind_dir = mContext.getString(R.string.chart_north_east);
								}else if(fx == 90){
									wind_dir = mContext.getString(R.string.chart_east);
								}else if(fx > 90 && fx< 180){
									wind_dir = mContext.getString(R.string.chart_south_east);
								}else if(fx == 180){
									wind_dir = mContext.getString(R.string.chart_south);
								}else if(fx > 180 && fx < 270){
									wind_dir = mContext.getString(R.string.chart_south_west);
								}else if(fx == 270){
									wind_dir = mContext.getString(R.string.chart_west);
								}else if(fx > 270){
									wind_dir = mContext.getString(R.string.chart_north_west);
								}
								dto.windDir = wind_dir;
							}else {
								dto.windDir = CONST.noValue;
							}
						}else {
							dto.windDir = CONST.noValue;
						}
						
						if (!obj.isNull("windspeed")) {
							String value = obj.getString("windspeed");
							if (!TextUtils.isEmpty(value)) {
								if (value.length() >= 2 && value.contains(".")) {
									if (value.equals(".0")) {
										dto.windSpeed = "0";
									}else {
										if (TextUtils.equals(value.substring(value.length()-2, value.length()), ".0")) {
											dto.windSpeed = value.substring(0, value.indexOf("."));
										}else {
											dto.windSpeed = value;
										}
									}
								}
							}else {
								dto.windSpeed = CONST.noValue;
							}
						}else {
							dto.windSpeed = CONST.noValue;
						}
						
						if (!obj.isNull("pointtemp")) {
							String value = obj.getString("pointtemp");
							if (!TextUtils.isEmpty(value)) {
								if (value.length() >= 2 && value.contains(".")) {
									if (value.equals(".0")) {
										dto.pointTemp = "0";
									}else {
										if (TextUtils.equals(value.substring(value.length()-2, value.length()), ".0")) {
											dto.pointTemp = value.substring(0, value.indexOf("."));
										}else {
											dto.pointTemp = value;
										}
									}
								}
							}else {
								dto.pointTemp = CONST.noValue;
							}
						}else {
							dto.pointTemp = CONST.noValue;
						}
						
						if (!obj.isNull("visibility")) {
							String value = obj.getString("visibility");
							if (!TextUtils.isEmpty(value)) {
								if (value.length() >= 2 && value.contains(".")) {
									if (value.equals(".0")) {
										dto.visibility = "0";
									}else {
										if (TextUtils.equals(value.substring(value.length()-2, value.length()), ".0")) {
											float f = Float.valueOf(value)/1000;
											BigDecimal b = new BigDecimal(f);
											float f1 = b.setScale(1,BigDecimal.ROUND_HALF_UP).floatValue();
											dto.visibility = String.valueOf(f1).substring(0, String.valueOf(f1).indexOf("."));
										}else {
											float f = Float.valueOf(value)/1000;
											BigDecimal b = new BigDecimal(f);
											float f1 = b.setScale(1,BigDecimal.ROUND_HALF_UP).floatValue();
											dto.visibility = String.valueOf(f1);
										}
									}
								}
							}else {
								dto.visibility = CONST.noValue;
							}
						}else {
							dto.visibility = CONST.noValue;
						}
						
						if (i == 0) {
							String snippet = dto.name;
							if (snippet.contains(getString(R.string.not_available))) {//把“暂无”监测站更换为高德地图通过经纬度获取名称
								searchAddrByLatLng(Double.valueOf(dto.lat), Double.valueOf(dto.lng));
							}else {
								tvName.setText(snippet+getString(R.string.monitor_station));
								stationName = tvName.getText().toString();
							}
							stationId = dto.stationId;
							tvStationId.setText(getString(R.string.station_id)+": "+stationId);
					        tvDistance.setText(getString(R.string.distance_station)+dto.distance+getString(R.string.unit_km));
							
					        if (TextUtils.equals(dto.ballTemp, CONST.noValue)) {
					        	tvTemp.setText(CONST.noValue);
							}else {
								tvTemp.setText(dto.ballTemp);
							}
					        
							if (TextUtils.equals(dto.precipitation1h, CONST.noValue)) {
								tvJiangshui.setText(CONST.noValue);
							}else {
								tvJiangshui.setText(dto.precipitation1h+getString(R.string.unit_mm));
							}
							
							if (TextUtils.equals(dto.humidity, CONST.noValue)) {
								tvShidu.setText(CONST.noValue);
							}else {
								tvShidu.setText(dto.humidity+getString(R.string.unit_percent));
							}
							
							if (TextUtils.equals(dto.windDir, CONST.noValue) && TextUtils.equals(dto.windDir, CONST.noValue)) {
								tvWind.setText(CONST.noValue);
							}else {
								tvWind.setText(dto.windDir+" "+dto.windSpeed+getString(R.string.unit_speed));
							}
							
							if (TextUtils.equals(dto.pointTemp, CONST.noValue)) {
								tvLoudian.setText(CONST.noValue);
							}else {
								tvLoudian.setText(dto.pointTemp+getString(R.string.unit_degree));
							}
							
							if (TextUtils.equals(dto.visibility, CONST.noValue)) {
								tvVisible.setText(CONST.noValue);
							}else {
								tvVisible.setText(dto.visibility+getString(R.string.unit_km));
							}
							
							if (TextUtils.equals(dto.airPressure, CONST.noValue)) {
								tvPressrue.setText(CONST.noValue);
							}else {
								tvPressrue.setText(dto.airPressure+getString(R.string.unit_hPa));
							}
							
							reContent.setVisibility(View.VISIBLE);
						}
						
					}

					ivOpen.setVisibility(View.VISIBLE);
					addMarker(stationList, value, true);
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (ArrayIndexOutOfBoundsException e) {
					e.printStackTrace();
				} catch (IndexOutOfBoundsException e) {
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
	 * 加密请求字符串
	 * @param url 基本串
	 * @param lng 经度
	 * @param lat 维度
	 * @return
	 */
	private String getStationContentUrlPost(String stationIds) {
		String URL = "http://61.4.184.171:8080/weather/rgwst/NewestDataNew";
		String sysdate = RainManager.getDate(Calendar.getInstance(), "yyyyMMddHHmmss");//系统时间
		StringBuffer buffer = new StringBuffer();
		buffer.append(URL);
		buffer.append("?");
		buffer.append("stationids=").append(stationIds);
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
	 * 获取最近10个站点的信息
	 * @param stationIds
	 * @param isLatLng 是否已经拥有经纬度
	 */
	private void asyncTaskContentPost(String stationIds, boolean isLatLng) {
		//异步请求数据
		HttpAsyncTaskContentPost task = new HttpAsyncTaskContentPost(isLatLng, stationIds);
		task.setMethod("POST");
		task.setTimeOut(CustomHttpClient.TIME_OUT);
		String[] ids = stationIds.split(",");
		task.execute(getStationContentUrlPost(ids[0]));
	}
	
	/**
	 * 异步请求方法
	 * @author dell
	 *
	 */
	private class HttpAsyncTaskContentPost extends AsyncTask<String, Void, String> {
		private String method = "POST";
		private List<NameValuePair> nvpList = new ArrayList<NameValuePair>();
		private boolean isLatLng;
		private String stationIds = null;
		
		public HttpAsyncTaskContentPost(boolean isLatLng, String stationIds) {
			this.isLatLng = isLatLng;
			this.stationIds = stationIds;
			transParams();
		}
		
		/**
		 * 传参数
		 */
		private void transParams() {
			NameValuePair pair1 = new BasicNameValuePair("ids", stationIds);
			nvpList.add(pair1);
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
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);
						StationMonitorDto dto = stationList.get(i);
						
						if (isLatLng == false) {
							queryInfoByStationId(dto.stationId, dto);
						}
						
						if (!obj.isNull("datatime")) {
							dto.time = obj.getString("datatime");
						}
						
						if (!obj.isNull("airpressure")) {
							String value = obj.getString("airpressure");
							if (!TextUtils.isEmpty(value)) {
								if (value.length() >= 2 && value.contains(".")) {
									if (value.equals(".0")) {
										dto.airPressure = "0";
									}else {
										if (TextUtils.equals(value.substring(value.length()-2, value.length()), ".0")) {
											dto.airPressure = value.substring(0, value.indexOf("."));
										}else {
											dto.airPressure = value;
										}
									}
								}
							}else {
								dto.airPressure = CONST.noValue;
							}
						}else {
							dto.airPressure = CONST.noValue;
						}
						
						if (!obj.isNull("balltemp")) {
							String value = obj.getString("balltemp");
							if (!TextUtils.isEmpty(value)) {
								if (value.length() >= 2 && value.contains(".")) {
									if (value.equals(".0")) {
										dto.ballTemp = "0";
									}else {
										if (TextUtils.equals(value.substring(value.length()-2, value.length()), ".0")) {
											dto.ballTemp = value.substring(0, value.indexOf("."));
										}else {
											dto.ballTemp = value;
										}
									}
								}
							}else {
								dto.ballTemp = CONST.noValue;
							}
						}else {
							dto.ballTemp = CONST.noValue;
						}
						
						if (!obj.isNull("humidity")) {
							String value = obj.getString("humidity");
							if (!TextUtils.isEmpty(value)) {
								if (value.length() >= 2 && value.contains(".")) {
									if (value.equals(".0")) {
										dto.humidity = "0";
									}else {
										if (TextUtils.equals(value.substring(value.length()-2, value.length()), ".0")) {
											dto.humidity = value.substring(0, value.indexOf("."));
										}else {
											dto.humidity = value;
										}
									}
								}
							}else {
								dto.humidity = CONST.noValue;
							}
						}else {
							dto.humidity = CONST.noValue;
						}
						
						if (!obj.isNull("precipitation1h")) {
							String value = obj.getString("precipitation1h");
							if (!TextUtils.isEmpty(value)) {
								if (value.length() >= 2 && value.contains(".")) {
									if (value.equals(".0")) {
										dto.precipitation1h = "0";
									}else {
										if (TextUtils.equals(value.substring(value.length()-2, value.length()), ".0")) {
											dto.precipitation1h = value.substring(0, value.indexOf("."));
										}else {
											dto.precipitation1h = value;
										}
									}
								}
							}else {
								dto.precipitation1h = CONST.noValue;
							}
						}else {
							dto.precipitation1h = CONST.noValue;
						}
						
						if (!obj.isNull("winddir")) {
							String dir = null;
							String value = obj.getString("winddir");
							if (!TextUtils.isEmpty(value)) {
								if (value.length() >= 2 && value.contains(".")) {
									if (value.equals(".0")) {
										dir = "0";
									}else {
										if (TextUtils.equals(value.substring(value.length()-2, value.length()), ".0")) {
											dir = value.substring(0, value.indexOf("."));
										}else {
											dir = value;
										}
									}
								}
								
								float fx = Float.valueOf(dir);
								String wind_dir = null;
								if(fx == 0 || fx == 360){
									wind_dir = mContext.getString(R.string.chart_north);
								}else if(fx > 0 && fx < 90){
									wind_dir = mContext.getString(R.string.chart_north_east);
								}else if(fx == 90){
									wind_dir = mContext.getString(R.string.chart_east);
								}else if(fx > 90 && fx< 180){
									wind_dir = mContext.getString(R.string.chart_south_east);
								}else if(fx == 180){
									wind_dir = mContext.getString(R.string.chart_south);
								}else if(fx > 180 && fx < 270){
									wind_dir = mContext.getString(R.string.chart_south_west);
								}else if(fx == 270){
									wind_dir = mContext.getString(R.string.chart_west);
								}else if(fx > 270){
									wind_dir = mContext.getString(R.string.chart_north_west);
								}
								dto.windDir = wind_dir;
							}else {
								dto.windDir = CONST.noValue;
							}
						}else {
							dto.windDir = CONST.noValue;
						}
						
						if (!obj.isNull("windspeed")) {
							String value = obj.getString("windspeed");
							if (!TextUtils.isEmpty(value)) {
								if (value.length() >= 2 && value.contains(".")) {
									if (value.equals(".0")) {
										dto.windSpeed = "0";
									}else {
										if (TextUtils.equals(value.substring(value.length()-2, value.length()), ".0")) {
											dto.windSpeed = value.substring(0, value.indexOf("."));
										}else {
											dto.windSpeed = value;
										}
									}
								}
							}else {
								dto.windSpeed = CONST.noValue;
							}
						}else {
							dto.windSpeed = CONST.noValue;
						}
						
						if (!obj.isNull("pointtemp")) {
							String value = obj.getString("pointtemp");
							if (!TextUtils.isEmpty(value)) {
								if (value.length() >= 2 && value.contains(".")) {
									if (value.equals(".0")) {
										dto.pointTemp = "0";
									}else {
										if (TextUtils.equals(value.substring(value.length()-2, value.length()), ".0")) {
											dto.pointTemp = value.substring(0, value.indexOf("."));
										}else {
											dto.pointTemp = value;
										}
									}
								}
							}else {
								dto.pointTemp = CONST.noValue;
							}
						}else {
							dto.pointTemp = CONST.noValue;
						}
						
						if (!obj.isNull("visibility")) {
							String value = obj.getString("visibility");
							if (!TextUtils.isEmpty(value)) {
								if (value.length() >= 2 && value.contains(".")) {
									if (value.equals(".0")) {
										dto.visibility = "0";
									}else {
										if (TextUtils.equals(value.substring(value.length()-2, value.length()), ".0")) {
											float f = Float.valueOf(value)/1000;
											BigDecimal b = new BigDecimal(f);
											float f1 = b.setScale(1,BigDecimal.ROUND_HALF_UP).floatValue();
											dto.visibility = String.valueOf(f1).substring(0, String.valueOf(f1).indexOf("."));
										}else {
											float f = Float.valueOf(value)/1000;
											BigDecimal b = new BigDecimal(f);
											float f1 = b.setScale(1,BigDecimal.ROUND_HALF_UP).floatValue();
											dto.visibility = String.valueOf(f1);
										}
									}
								}
							}else {
								dto.visibility = CONST.noValue;
							}
						}else {
							dto.visibility = CONST.noValue;
						}
						
						if (i == 0) {
							String snippet = dto.name;
							if (snippet.contains(getString(R.string.not_available))) {//把“暂无”监测站更换为高德地图通过经纬度获取名称
								searchAddrByLatLng(Double.valueOf(dto.lat), Double.valueOf(dto.lng));
							}else {
								tvName.setText(snippet+getString(R.string.monitor_station));
								stationName = tvName.getText().toString();
							}
							stationId = dto.stationId;
							tvStationId.setText(getString(R.string.station_id)+": "+stationId);
					        tvDistance.setText(getString(R.string.distance_station)+dto.distance+getString(R.string.unit_km));
							
					        if (TextUtils.equals(dto.ballTemp, CONST.noValue)) {
					        	tvTemp.setText(CONST.noValue);
							}else {
								tvTemp.setText(dto.ballTemp);
							}
					        
							if (TextUtils.equals(dto.precipitation1h, CONST.noValue)) {
								tvJiangshui.setText(CONST.noValue);
							}else {
								tvJiangshui.setText(dto.precipitation1h+getString(R.string.unit_mm));
							}
							
							if (TextUtils.equals(dto.humidity, CONST.noValue)) {
								tvShidu.setText(CONST.noValue);
							}else {
								tvShidu.setText(dto.humidity+getString(R.string.unit_percent));
							}
							
							if (TextUtils.equals(dto.windDir, CONST.noValue) && TextUtils.equals(dto.windDir, CONST.noValue)) {
								tvWind.setText(CONST.noValue);
							}else {
								tvWind.setText(dto.windDir+" "+dto.windSpeed+getString(R.string.unit_speed));
							}
							
							if (TextUtils.equals(dto.pointTemp, CONST.noValue)) {
								tvLoudian.setText(CONST.noValue);
							}else {
								tvLoudian.setText(dto.pointTemp+getString(R.string.unit_degree));
							}
							
							if (TextUtils.equals(dto.visibility, CONST.noValue)) {
								tvVisible.setText(CONST.noValue);
							}else {
								tvVisible.setText(dto.visibility+getString(R.string.unit_km));
							}
							
							if (TextUtils.equals(dto.airPressure, CONST.noValue)) {
								tvPressrue.setText(CONST.noValue);
							}else {
								tvPressrue.setText(dto.airPressure+getString(R.string.unit_hPa));
							}
							
							reContent.setVisibility(View.VISIBLE);
						}
						
						
					}

					ivOpen.setVisibility(View.VISIBLE);
					addMarker(stationList, value, true);
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
	 * 移除选择点的marker
	 * 移除map上已经存在的站点marker
	 */
	private void removeStationMarkers() {
		if (selecteMarker != null) {
			selecteMarker.remove();
		}
		for (int i = 0; i < markerList.size(); i++) {
			markerList.get(i).remove();
		}
		markerList.clear();
	}
	
	/**
	 * 添加marker
	 * @param list
	 * @param value
	 * @param isReAdded 是否重新添加marker
	 */
	private void addMarker(final List<StationMonitorDto> list, int value, boolean isReAdded) {
		if (selecteMarker != null) {
			selecteMarker.remove();
		}
		
		if (value == 1 || value == 13 || value == 16 || value == 124) {
			ivRain.setImageResource(R.drawable.iv_map_rain_selected);
			ivTemp.setImageResource(R.drawable.iv_map_temp);
			ivHumidity.setImageResource(R.drawable.iv_map_humidity);
			ivVisibility.setImageResource(R.drawable.iv_map_visible);
			ivPressure.setImageResource(R.drawable.iv_map_pressure);
			ivWindSpeed.setImageResource(R.drawable.iv_map_winddir);
		}else if (value == 2) {
			ivRain.setImageResource(R.drawable.iv_map_rain);
			ivTemp.setImageResource(R.drawable.iv_map_temp_selected);
			ivHumidity.setImageResource(R.drawable.iv_map_humidity);
			ivVisibility.setImageResource(R.drawable.iv_map_visible);
			ivPressure.setImageResource(R.drawable.iv_map_pressure);
			ivWindSpeed.setImageResource(R.drawable.iv_map_winddir);
		}else if (value == 3) {
			ivRain.setImageResource(R.drawable.iv_map_rain);
			ivTemp.setImageResource(R.drawable.iv_map_temp);
			ivHumidity.setImageResource(R.drawable.iv_map_humidity_selected);
			ivVisibility.setImageResource(R.drawable.iv_map_visible);
			ivPressure.setImageResource(R.drawable.iv_map_pressure);
			ivWindSpeed.setImageResource(R.drawable.iv_map_winddir);
		}else if (value == 4) {
			ivRain.setImageResource(R.drawable.iv_map_rain);
			ivTemp.setImageResource(R.drawable.iv_map_temp);
			ivHumidity.setImageResource(R.drawable.iv_map_humidity);
			ivVisibility.setImageResource(R.drawable.iv_map_visible_selected);
			ivPressure.setImageResource(R.drawable.iv_map_pressure);
			ivWindSpeed.setImageResource(R.drawable.iv_map_winddir);
		}else if (value == 5) {
			ivRain.setImageResource(R.drawable.iv_map_rain);
			ivTemp.setImageResource(R.drawable.iv_map_temp);
			ivHumidity.setImageResource(R.drawable.iv_map_humidity);
			ivVisibility.setImageResource(R.drawable.iv_map_visible);
			ivPressure.setImageResource(R.drawable.iv_map_pressure_selected);
			ivWindSpeed.setImageResource(R.drawable.iv_map_winddir);
		}else if (value == 6) {
			ivRain.setImageResource(R.drawable.iv_map_rain);
			ivTemp.setImageResource(R.drawable.iv_map_temp);
			ivHumidity.setImageResource(R.drawable.iv_map_humidity);
			ivVisibility.setImageResource(R.drawable.iv_map_visible);
			ivPressure.setImageResource(R.drawable.iv_map_pressure);
			ivWindSpeed.setImageResource(R.drawable.iv_map_winddir_selected);
		}
		
		if (list.isEmpty() || list.size() == 0) {
			return;
		}
		
		if (markerList.size() > 0 && isReAdded == false) {
			for (int i = 0; i < markerList.size(); i++) {
				Marker marker = markerList.get(i);
				StationMonitorDto dto = stationList.get(i);
				if (marker != null) {
					View markerView = null;
					if (value == 1) {
						markerView = getTextBitmap(dto.precipitation1h);
					}else if (value == 13) {
						markerView = getTextBitmap(dto.precipitation3h);
					}else if (value == 16) {
						markerView = getTextBitmap(dto.precipitation6h);
					}else if (value == 124) {
						markerView = getTextBitmap(dto.precipitation24h);
					}else if (value == 2) {
						markerView = getTextBitmap(dto.ballTemp);
					}else if (value == 3) {
						markerView = getTextBitmap(dto.humidity);
					}else if (value == 4) {
						markerView = getTextBitmap(dto.visibility);
					}else if (value == 5) {
						markerView = getTextBitmap(dto.airPressure);
					}else if (value == 6) {
						markerView = getTextBitmap(dto.windSpeed);
					}
					if (markerView != null) {
						marker.setIcon(BitmapDescriptorFactory.fromView(markerView));
					}
				}
			}
		}else {
			for (int i = 0; i < list.size(); i++) {
				StationMonitorDto dto = list.get(i);
				if (!TextUtils.isEmpty(dto.lat) && !TextUtils.isEmpty(dto.lng)) {
					double lat = Double.valueOf(dto.lat);
					double lng = Double.valueOf(dto.lng);
					MarkerOptions options = new MarkerOptions();
					options.title(dto.stationId);
					options.snippet(dto.name);
					options.anchor(0.5f, 0.5f);
					options.position(new LatLng(lat, lng));
					View markerView = null;
					if (value == 1) {
						markerView = getTextBitmap(dto.precipitation1h);
					}else if (value == 13) {
						markerView = getTextBitmap(dto.precipitation3h);
					}else if (value == 16) {
						markerView = getTextBitmap(dto.precipitation6h);
					}else if (value == 124) {
						markerView = getTextBitmap(dto.precipitation24h);
					}else if (value == 2) {
						markerView = getTextBitmap(dto.ballTemp);
					}else if (value == 3) {
						markerView = getTextBitmap(dto.humidity);
					}else if (value == 4) {
						markerView = getTextBitmap(dto.visibility);
					}else if (value == 5) {
						markerView = getTextBitmap(dto.airPressure);
					}else if (value == 6) {
						markerView = getTextBitmap(dto.windSpeed);
					}
					if (markerView != null) {
						options.icon(BitmapDescriptorFactory.fromView(markerView));
						Marker m = aMap.addMarker(options);
						markerList.add(m);
					}
				}
			}
			
			//延时1秒开始地图动画
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					if (markerList.size() > 0) {
						LatLngBounds bounds = new LatLngBounds.Builder()
						.include(markerList.get(0).getPosition())
						.include(markerList.get(markerList.size()-1).getPosition()).build();
						aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
					}
				}
			}, 500);
		}
	}
	
	/**
	 * 给marker添加文字
	 * @param name 城市名称
	 * @return
	 */
	private View getTextBitmap(String name) {  
		if (TextUtils.isEmpty(name)) {
			return null;
		}
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.station_monitor_item, null);
		if (view == null) {
			return null;
		}
		TextView tvName = (TextView) view.findViewById(R.id.tvName);
		tvName.setText(name);
		return view;
	}
	
	@Override
	public void onMapClick(LatLng arg0) {
		stationList.clear();
		removeStationMarkers();
		asyncTask(arg0.longitude, arg0.latitude);
		
		MarkerOptions options = new MarkerOptions();
		options.anchor(0.5f, 0.5f);
		options.icon(BitmapDescriptorFactory.fromResource(R.drawable.iv_map_click_map));
		options.position(arg0);
		selecteMarker = aMap.addMarker(options);
	}
	
	/**
	 * 通过经纬度获取地理位置信息
	 * @param lat
	 * @param lng
	 */
	private void searchAddrByLatLng(double lat, double lng) {
		//latLonPoint参数表示一个Latlng，第二参数表示范围多少米，GeocodeSearch.AMAP表示是国测局坐标系还是GPS原生坐标系   
		RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(lat, lng), 200, GeocodeSearch.AMAP); 
    	geocoderSearch.getFromLocationAsyn(query); 
	}
	
	@Override
	public void onGeocodeSearched(GeocodeResult arg0, int arg1) {
		// TODO Auto-generated method stub
	}
	@Override
	public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
		if (rCode == 0) {
			if (result != null && result.getRegeocodeAddress() != null && result.getRegeocodeAddress().getFormatAddress() != null) {
				String pro = result.getRegeocodeAddress().getProvince();
				String city = result.getRegeocodeAddress().getCity();
				String dis = result.getRegeocodeAddress().getDistrict();
				String addr = result.getRegeocodeAddress().getStreetNumber().getStreet();
				if (pro.contains(city) || dis.contains(city)) {
					tvName.setText(pro+dis+addr+getString(R.string.monitor_station));
					stationName = tvName.getText().toString();
				}else {
					tvName.setText(pro+city+dis+addr+getString(R.string.monitor_station));
					stationName = tvName.getText().toString();
				}
			} 
		} 
	}
	
//	private String setStationName(String snippet) {
//		if (snippet.length() > 10 && snippet.length() <= 20) {
//			snippet = snippet.substring(0, 10)+"\n"+snippet.substring(10, snippet.length());
//		}else if (snippet.length() > 20) {
//			snippet = snippet.substring(0, 10)+"\n"+snippet.substring(10, 20)+"\n"+snippet.substring(20, snippet.length());
//		}
//		return snippet+getString(R.string.monitor_station);
//	}
	
	@Override
	public boolean onMarkerClick(Marker marker) {
		if (marker == locationMarker) {
			return false;
		}
		
		if (!TextUtils.isEmpty(marker.getTitle())) {
			for (int i = 0; i < stationList.size(); i++) {
				StationMonitorDto data = stationList.get(i);
				if (TextUtils.equals(marker.getTitle(), data.stationId)) {
					String snippet = marker.getSnippet();
					if (snippet.contains(getString(R.string.not_available))) {//把“暂无”监测站更换为高德地图通过经纬度获取名称
						searchAddrByLatLng(marker.getPosition().latitude, marker.getPosition().longitude);
					}else {
						tvName.setText(snippet+getString(R.string.monitor_station));
						stationName = tvName.getText().toString();
					}
					stationId = marker.getTitle();
					tvStationId.setText(getString(R.string.station_id)+": "+stationId);
			        tvDistance.setText(getString(R.string.distance_station)+data.distance+getString(R.string.unit_km));
					
			        if (TextUtils.equals(data.ballTemp, CONST.noValue)) {
			        	tvTemp.setText(CONST.noValue);
					}else {
						tvTemp.setText(data.ballTemp);
					}
			        
					if (TextUtils.equals(data.precipitation1h, CONST.noValue)) {
						tvJiangshui.setText(CONST.noValue);
					}else {
						tvJiangshui.setText(data.precipitation1h+getString(R.string.unit_mm));
					}
					
					if (TextUtils.equals(data.humidity, CONST.noValue)) {
						tvShidu.setText(CONST.noValue);
					}else {
						tvShidu.setText(data.humidity+getString(R.string.unit_percent));
					}
					
					if (TextUtils.equals(data.windDir, CONST.noValue) && TextUtils.equals(data.windDir, CONST.noValue)) {
						tvWind.setText(CONST.noValue);
					}else {
						tvWind.setText(data.windDir+" "+data.windSpeed+getString(R.string.unit_speed));
					}
					
					if (TextUtils.equals(data.pointTemp, CONST.noValue)) {
						tvLoudian.setText(CONST.noValue);
					}else {
						tvLoudian.setText(data.pointTemp+getString(R.string.unit_degree));
					}
					
					if (TextUtils.equals(data.visibility, CONST.noValue)) {
						tvVisible.setText(CONST.noValue);
					}else {
						tvVisible.setText(data.visibility+getString(R.string.unit_km));
					}
					
					if (TextUtils.equals(data.airPressure, CONST.noValue)) {
						tvPressrue.setText(CONST.noValue);
					}else {
						tvPressrue.setText(data.airPressure+getString(R.string.unit_hPa));
					}
					
					break;
				}
			}
			
			Animation animation = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF, 0, 
					Animation.RELATIVE_TO_SELF, 0, 
					Animation.RELATIVE_TO_SELF, 1f, 
					Animation.RELATIVE_TO_SELF, 0);
			animation.setDuration(300);
			if (reContent.getVisibility() == View.GONE) {
				reContent.setAnimation(animation);
				reContent.setVisibility(View.VISIBLE);
			}
		}
		return true;
	}
	
	@Override
	public void onCameraChange(CameraPosition arg0) {
		closeDetailWindow();
	}

	@Override
	public void onCameraChangeFinish(CameraPosition arg0) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 关闭详情窗口
	 */
	private void closeDetailWindow() {
		Animation animation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0, 
				Animation.RELATIVE_TO_SELF, 0, 
				Animation.RELATIVE_TO_SELF, 0, 
				Animation.RELATIVE_TO_SELF, 1f);
		animation.setDuration(300);
		if (reContent.getVisibility() == View.VISIBLE) {
			reContent.setAnimation(animation);
			reContent.setVisibility(View.GONE);
		}
	}
	
	/**
	 * 右侧控制条动画
	 * @param flag
	 * @param layout
	 */
	private void rightControlAnimation(final boolean flag, final LinearLayout layout) {
		AnimationSet animationSet = new AnimationSet(true);
		TranslateAnimation animation = null;
		if (flag == false) {
			animation = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF, 0, 
					Animation.RELATIVE_TO_SELF, 0, 
					Animation.RELATIVE_TO_SELF, -1.0f, 
					Animation.RELATIVE_TO_SELF, 0f);
		}else {
			animation = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF,0f,
					Animation.RELATIVE_TO_SELF,0f,
					Animation.RELATIVE_TO_SELF,0,
					Animation.RELATIVE_TO_SELF,-1.0f);
		}
		animation.setDuration(400);
		animationSet.addAnimation(animation);
		animationSet.setFillAfter(true);
		layout.startAnimation(animationSet);
		animationSet.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {
			}
			@Override
			public void onAnimationRepeat(Animation arg0) {
			}
			@Override
			public void onAnimationEnd(Animation arg0) {
				layout.clearAnimation();
			}
		});
	}
	
	/**
	 * 根据城市名称、区县名称查询对应的站点数据
	 * @param name
	 * @param columnName 表的列名称
	 * @param data
	 */
	private void queryCityOrDistrict(String proName, String cityName, String disName, List<StationMonitorDto> list) {
		DBManager dbManager = new DBManager(mContext);
		dbManager.openDateBase();
		SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/" + DBManager.DB_NAME, null);
		try {
			if (database != null && database.isOpen()) {
				Cursor cursor = database.rawQuery(
						"select * from " + DBManager.TABLE_NAME1 + " where PRO = " + "\""+proName+"\"" + " and CITY = " + "\""+cityName+"\"" + " and DIST = " + "\""+disName+"\"", null);
				list.clear();
				for (int i = 0; i < cursor.getCount(); i++) {
					cursor.moveToPosition(i);
					StationMonitorDto dto = new StationMonitorDto();
					dto.stationId = cursor.getString(cursor.getColumnIndex("SID"));
					dto.lat = cursor.getString(cursor.getColumnIndex("LAT"));
					dto.lng = cursor.getString(cursor.getColumnIndex("LON"));
					
					String pro = cursor.getString(cursor.getColumnIndex("PRO"));
					String city = cursor.getString(cursor.getColumnIndex("CITY"));
					String dis = cursor.getString(cursor.getColumnIndex("DIST"));
					String addr = cursor.getString(cursor.getColumnIndex("ADDR"));
					
					if (addr.contains(pro) && addr.contains(city) && addr.contains(dis)) {
						dto.name = addr;
					}else if (addr.contains(city) && addr.contains(dis)) {
						dto.name = pro+addr;
					}else if (addr.contains(dis)) {
						dto.name = pro+city+addr;
					}else {
						if (pro.contains(city) || dis.contains(city)) {
							dto.name = pro+dis+addr;
						}else {
							dto.name = pro+city+dis+addr;
						}
					}
					
					list.add(dto);
				}
				cursor.close();
				cursor = null;
				dbManager.closeDatabase();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void queryCityOrDistrict(List<StationMonitorDto> list, String provinceName) {
		DBManager dbManager = new DBManager(mContext);
		dbManager.openDateBase();
		SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/" + DBManager.DB_NAME, null);
		try {
			if (database != null && database.isOpen()) {
				Cursor cursor = database.rawQuery(
						"select * from " + DBManager.TABLE_NAME1 + " where CAST(SID as decimal) < 100000 and PRO = " + "\""+provinceName+"\"", null);
				list.clear();
				for (int i = 0; i < cursor.getCount(); i++) {
					cursor.moveToPosition(i);
					StationMonitorDto dto = new StationMonitorDto();
					dto.stationId = cursor.getString(cursor.getColumnIndex("SID"));
					dto.lat = cursor.getString(cursor.getColumnIndex("LAT"));
					dto.lng = cursor.getString(cursor.getColumnIndex("LON"));
					
					String pro = cursor.getString(cursor.getColumnIndex("PRO"));
					String city = cursor.getString(cursor.getColumnIndex("CITY"));
					String dis = cursor.getString(cursor.getColumnIndex("DIST"));
					String addr = cursor.getString(cursor.getColumnIndex("ADDR"));
					
					if (addr.contains(pro) && addr.contains(city) && addr.contains(dis)) {
						dto.name = addr;
					}else if (addr.contains(city) && addr.contains(dis)) {
						dto.name = pro+addr;
					}else if (addr.contains(dis)) {
						dto.name = pro+city+addr;
					}else {
						if (pro.contains(city) || dis.contains(city)) {
							dto.name = pro+dis+addr;
						}else {
							dto.name = pro+city+dis+addr;
						}
					}
					
					list.add(dto);
				}
				cursor.close();
				cursor = null;
				dbManager.closeDatabase();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 计算两点之间距离
	 * @param longitude1
	 * @param latitude1
	 * @param longitude2
	 * @param latitude2
	 * @return
	 */
	public static String getDistance(double longitude1, double latitude1, double longitude2, double latitude2) {
		double EARTH_RADIUS = 6378137;
		double Lat1 = rad(latitude1);
		double Lat2 = rad(latitude2);
		double a = Lat1 - Lat2;
		double b = rad(longitude1) - rad(longitude2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(Lat1) * Math.cos(Lat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		BigDecimal bd = new BigDecimal(s/1000);
		double d = bd.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
		String distance = d+"";
		
		String value = distance;
		if (value.length() >= 2 && value.contains(".")) {
			if (value.equals(".0")) {
				distance = "0";
			}else {
				if (TextUtils.equals(value.substring(value.length()-2, value.length()), ".0")) {
					distance = value.substring(0, value.indexOf("."));
				}else {
					distance = value;
				}
			}
		}
			
		return distance;
	}

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}
	
	private void openMenuAnimation(LinearLayout llContainer) {
		if (llContainer.getVisibility() == View.INVISIBLE) {
			rightControlAnimation(false, llContainer);
			llContainer.setVisibility(View.VISIBLE);
			ivOpen.setImageResource(R.drawable.iv_map_close);
		}else {
			rightControlAnimation(true, llContainer);
			llContainer.setVisibility(View.INVISIBLE);
			ivOpen.setImageResource(R.drawable.iv_map_open);
		}
	}
	
	@Override
	public void onMapScreenShot(final Bitmap bitmap1) {//bitmap1为地图截屏
		Bitmap bitmap2 = CommonUtil.captureView(reShare);
		Bitmap bitmap3 = CommonUtil.mergeBitmap(StationMonitorActivity.this, bitmap1, bitmap2, true);
		CommonUtil.clearBitmap(bitmap1);
		CommonUtil.clearBitmap(bitmap1);
		Bitmap bitmap4 = BitmapFactory.decodeResource(getResources(), R.drawable.iv_share_bottom);
		Bitmap bitmap5 = CommonUtil.mergeBitmap(mContext, bitmap3, bitmap4, false);
		CommonUtil.clearBitmap(bitmap3);
		CommonUtil.clearBitmap(bitmap4);
		Bitmap bitmap6 = CommonUtil.captureView(llScrollView);
		Bitmap bitmap = CommonUtil.mergeBitmap(mContext, bitmap6, bitmap5, false);
		CommonUtil.clearBitmap(bitmap5);
		CommonUtil.clearBitmap(bitmap6);
		CommonUtil.share(StationMonitorActivity.this, bitmap);
	}

	@Override
	public void onMapScreenShot(Bitmap arg0, int arg1) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.llBack:
			finish();
			break;
		case R.id.ivShare:
			aMap.getMapScreenShot(StationMonitorActivity.this);
			break;
		case R.id.tv1:
			if (llRain.getVisibility() == View.GONE) {
				llRain.setVisibility(View.VISIBLE);
			}else {
				llRain.setVisibility(View.GONE);
			}
			ivLegend.setImageResource(R.drawable.iv_legend_rain);
			pastTime = 60*60*1000;
			if (!TextUtils.isEmpty(StationManager.precipitation1hResult)) {
				asynParseMapData(StationManager.precipitation1hResult, getString(R.string.layer_rain1));
			}
			tv1.setTextColor(0xff2d5a9d);
			tv2.setTextColor(getResources().getColor(R.color.text_color4));
			tv3.setTextColor(getResources().getColor(R.color.text_color4));
			tv4.setTextColor(getResources().getColor(R.color.text_color4));
			tvRain2.setTextColor(0xff2d5a9d);
			tvRain2.setBackgroundResource(R.drawable.bg_layer_button);
			tvTemp2.setTextColor(getResources().getColor(R.color.text_color4));
			tvTemp2.setBackgroundColor(getResources().getColor(R.color.transparent));
			tvHumidity2.setTextColor(getResources().getColor(R.color.text_color4));
			tvHumidity2.setBackgroundColor(getResources().getColor(R.color.transparent));
			tvVisibility2.setTextColor(getResources().getColor(R.color.text_color4));
			tvVisibility2.setBackgroundColor(getResources().getColor(R.color.transparent));
			tvPressure2.setTextColor(getResources().getColor(R.color.text_color4));
			tvPressure2.setBackgroundColor(getResources().getColor(R.color.transparent));
			tvWindSpeed2.setTextColor(getResources().getColor(R.color.text_color4));
			tvWindSpeed2.setBackgroundColor(getResources().getColor(R.color.transparent));
			
			value = 1;
			addMarker(stationList, value, false);
			break;
		case R.id.tv2:
			if (llRain.getVisibility() == View.GONE) {
				llRain.setVisibility(View.VISIBLE);
			}else {
				llRain.setVisibility(View.GONE);
			}
			ivLegend.setImageResource(R.drawable.iv_legend_rain);
			pastTime = 3*60*60*1000;
			if (!TextUtils.isEmpty(StationManager.precipitation3hResult)) {
				asynParseMapData(StationManager.precipitation3hResult, getString(R.string.layer_rain3));
			}
			tv1.setTextColor(getResources().getColor(R.color.text_color4));
			tv2.setTextColor(0xff2d5a9d);
			tv3.setTextColor(getResources().getColor(R.color.text_color4));
			tv4.setTextColor(getResources().getColor(R.color.text_color4));
			tvRain2.setTextColor(0xff2d5a9d);
			tvRain2.setBackgroundResource(R.drawable.bg_layer_button);
			tvTemp2.setTextColor(getResources().getColor(R.color.text_color4));
			tvTemp2.setBackgroundColor(getResources().getColor(R.color.transparent));
			tvHumidity2.setTextColor(getResources().getColor(R.color.text_color4));
			tvHumidity2.setBackgroundColor(getResources().getColor(R.color.transparent));
			tvVisibility2.setTextColor(getResources().getColor(R.color.text_color4));
			tvVisibility2.setBackgroundColor(getResources().getColor(R.color.transparent));
			tvPressure2.setTextColor(getResources().getColor(R.color.text_color4));
			tvPressure2.setBackgroundColor(getResources().getColor(R.color.transparent));
			tvWindSpeed2.setTextColor(getResources().getColor(R.color.text_color4));
			tvWindSpeed2.setBackgroundColor(getResources().getColor(R.color.transparent));
			
			value = 13;
			addMarker(stationList, value, false);
			break;
		case R.id.tv3:
			if (llRain.getVisibility() == View.GONE) {
				llRain.setVisibility(View.VISIBLE);
			}else {
				llRain.setVisibility(View.GONE);
			}
			ivLegend.setImageResource(R.drawable.iv_legend_rain6);
			pastTime = 6*60*60*1000;
			if (!TextUtils.isEmpty(StationManager.precipitation6hResult)) {
				asynParseMapData(StationManager.precipitation6hResult, getString(R.string.layer_rain6));
			}
			tv1.setTextColor(getResources().getColor(R.color.text_color4));
			tv2.setTextColor(getResources().getColor(R.color.text_color4));
			tv3.setTextColor(0xff2d5a9d);
			tv4.setTextColor(getResources().getColor(R.color.text_color4));
			tvRain2.setTextColor(0xff2d5a9d);
			tvRain2.setBackgroundResource(R.drawable.bg_layer_button);
			tvTemp2.setTextColor(getResources().getColor(R.color.text_color4));
			tvTemp2.setBackgroundColor(getResources().getColor(R.color.transparent));
			tvHumidity2.setTextColor(getResources().getColor(R.color.text_color4));
			tvHumidity2.setBackgroundColor(getResources().getColor(R.color.transparent));
			tvVisibility2.setTextColor(getResources().getColor(R.color.text_color4));
			tvVisibility2.setBackgroundColor(getResources().getColor(R.color.transparent));
			tvPressure2.setTextColor(getResources().getColor(R.color.text_color4));
			tvPressure2.setBackgroundColor(getResources().getColor(R.color.transparent));
			tvWindSpeed2.setTextColor(getResources().getColor(R.color.text_color4));
			tvWindSpeed2.setBackgroundColor(getResources().getColor(R.color.transparent));
			
			value = 16;
			addMarker(stationList, value, false);
			break;
		case R.id.tv4:
			if (llRain.getVisibility() == View.GONE) {
				llRain.setVisibility(View.VISIBLE);
			}else {
				llRain.setVisibility(View.GONE);
			}
			ivLegend.setImageResource(R.drawable.iv_legend_rain24);
			pastTime = 24*60*60*1000;
			if (!TextUtils.isEmpty(StationManager.precipitation24hResult)) {
				asynParseMapData(StationManager.precipitation24hResult, getString(R.string.layer_rain24));
			}
			tv1.setTextColor(getResources().getColor(R.color.text_color4));
			tv2.setTextColor(getResources().getColor(R.color.text_color4));
			tv3.setTextColor(getResources().getColor(R.color.text_color4));
			tv4.setTextColor(0xff2d5a9d);
			tvRain2.setTextColor(0xff2d5a9d);
			tvRain2.setBackgroundResource(R.drawable.bg_layer_button);
			tvTemp2.setTextColor(getResources().getColor(R.color.text_color4));
			tvTemp2.setBackgroundColor(getResources().getColor(R.color.transparent));
			tvHumidity2.setTextColor(getResources().getColor(R.color.text_color4));
			tvHumidity2.setBackgroundColor(getResources().getColor(R.color.transparent));
			tvVisibility2.setTextColor(getResources().getColor(R.color.text_color4));
			tvVisibility2.setBackgroundColor(getResources().getColor(R.color.transparent));
			tvPressure2.setTextColor(getResources().getColor(R.color.text_color4));
			tvPressure2.setBackgroundColor(getResources().getColor(R.color.transparent));
			tvWindSpeed2.setTextColor(getResources().getColor(R.color.text_color4));
			tvWindSpeed2.setBackgroundColor(getResources().getColor(R.color.transparent));
			
			value = 124;
			addMarker(stationList, value, false);
			break;
		case R.id.tvRain2:
			if (llRain.getVisibility() == View.GONE) {
				llRain.setVisibility(View.VISIBLE);
			}else {
				llRain.setVisibility(View.GONE);
			}
//			ivLegend.setImageResource(R.drawable.iv_legend_rain);
//			pastTime = 60*60*1000;
//			if (!TextUtils.isEmpty(StationManager.precipitation1hResult)) {
//				asynParseMapData(StationManager.precipitation1hResult, getString(R.string.layer_rain1));
//			}
//			tv1.setTextColor(0xff2d5a9d);
//			tv2.setTextColor(getResources().getColor(R.color.text_color4));
//			tv3.setTextColor(getResources().getColor(R.color.text_color4));
//			tv4.setTextColor(getResources().getColor(R.color.text_color4));
//			tvRain2.setTextColor(0xff2d5a9d);
//			tvRain2.setBackgroundResource(R.drawable.bg_layer_button);
//			tvTemp2.setTextColor(getResources().getColor(R.color.text_color4));
//			tvTemp2.setBackgroundColor(getResources().getColor(R.color.transparent));
//			tvHumidity2.setTextColor(getResources().getColor(R.color.text_color4));
//			tvHumidity2.setBackgroundColor(getResources().getColor(R.color.transparent));
//			tvVisibility2.setTextColor(getResources().getColor(R.color.text_color4));
//			tvVisibility2.setBackgroundColor(getResources().getColor(R.color.transparent));
//			tvPressure2.setTextColor(getResources().getColor(R.color.text_color4));
//			tvPressure2.setBackgroundColor(getResources().getColor(R.color.transparent));
//			tvWindSpeed2.setTextColor(getResources().getColor(R.color.text_color4));
//			tvWindSpeed2.setBackgroundColor(getResources().getColor(R.color.transparent));
//			
//			value = 1;
//			addMarker(stationList, value, false);
			break;
		case R.id.tvTemp2:
			llRain.setVisibility(View.GONE);
			ivLegend.setImageResource(R.drawable.iv_legend_temp);
			pastTime = 60*60*1000;
			if (!TextUtils.isEmpty(StationManager.balltempResult)) {
				asynParseMapData(StationManager.balltempResult, getString(R.string.layer_temp));
			}
			tvRain2.setTextColor(getResources().getColor(R.color.text_color4));
			tvRain2.setBackgroundColor(getResources().getColor(R.color.transparent));
			tvTemp2.setTextColor(0xff2d5a9d);
			tvTemp2.setBackgroundResource(R.drawable.bg_layer_button);
			tvHumidity2.setTextColor(getResources().getColor(R.color.text_color4));
			tvHumidity2.setBackgroundColor(getResources().getColor(R.color.transparent));
			tvVisibility2.setTextColor(getResources().getColor(R.color.text_color4));
			tvVisibility2.setBackgroundColor(getResources().getColor(R.color.transparent));
			tvPressure2.setTextColor(getResources().getColor(R.color.text_color4));
			tvPressure2.setBackgroundColor(getResources().getColor(R.color.transparent));
			tvWindSpeed2.setTextColor(getResources().getColor(R.color.text_color4));
			tvWindSpeed2.setBackgroundColor(getResources().getColor(R.color.transparent));
			
			value = 2;
			addMarker(stationList, value, false);
			break;
		case R.id.tvHumidity2:
			llRain.setVisibility(View.GONE);
			ivLegend.setImageResource(R.drawable.iv_legend_humidity);
			pastTime = 60*60*1000;
			if (!TextUtils.isEmpty(StationManager.humidityResult)) {
				asynParseMapData(StationManager.humidityResult, getString(R.string.layer_humidity));
			}
			tvRain2.setTextColor(getResources().getColor(R.color.text_color4));
			tvRain2.setBackgroundColor(getResources().getColor(R.color.transparent));
			tvTemp2.setTextColor(getResources().getColor(R.color.text_color4));
			tvTemp2.setBackgroundColor(getResources().getColor(R.color.transparent));
			tvHumidity2.setTextColor(0xff2d5a9d);
			tvHumidity2.setBackgroundResource(R.drawable.bg_layer_button);
			tvVisibility2.setTextColor(getResources().getColor(R.color.text_color4));
			tvVisibility2.setBackgroundColor(getResources().getColor(R.color.transparent));
			tvPressure2.setTextColor(getResources().getColor(R.color.text_color4));
			tvPressure2.setBackgroundColor(getResources().getColor(R.color.transparent));
			tvWindSpeed2.setTextColor(getResources().getColor(R.color.text_color4));
			tvWindSpeed2.setBackgroundColor(getResources().getColor(R.color.transparent));
			
			value = 3;
			addMarker(stationList, value, false);
			break;
		case R.id.tvVisibility2:
			llRain.setVisibility(View.GONE);
			ivLegend.setImageResource(R.drawable.iv_legend_visible);
			pastTime = 60*60*1000;
			if (!TextUtils.isEmpty(StationManager.visibilityResult)) {
				asynParseMapData(StationManager.visibilityResult, getString(R.string.layer_visible));
			}
			tvRain2.setTextColor(getResources().getColor(R.color.text_color4));
			tvRain2.setBackgroundColor(getResources().getColor(R.color.transparent));
			tvTemp2.setTextColor(getResources().getColor(R.color.text_color4));
			tvTemp2.setBackgroundColor(getResources().getColor(R.color.transparent));
			tvHumidity2.setTextColor(getResources().getColor(R.color.text_color4));
			tvHumidity2.setBackgroundColor(getResources().getColor(R.color.transparent));
			tvVisibility2.setTextColor(0xff2d5a9d);
			tvVisibility2.setBackgroundResource(R.drawable.bg_layer_button);
			tvPressure2.setTextColor(getResources().getColor(R.color.text_color4));
			tvPressure2.setBackgroundColor(getResources().getColor(R.color.transparent));
			tvWindSpeed2.setTextColor(getResources().getColor(R.color.text_color4));
			tvWindSpeed2.setBackgroundColor(getResources().getColor(R.color.transparent));
			
			value = 4;
			addMarker(stationList, value, false);
			break;
		case R.id.tvPressure2:
			llRain.setVisibility(View.GONE);
			ivLegend.setImageResource(R.drawable.iv_legend_pressure);
			pastTime = 60*60*1000;
			if (!TextUtils.isEmpty(StationManager.airpressureResult)) {
				asynParseMapData(StationManager.airpressureResult, getString(R.string.layer_pressure));
			}
			tvRain2.setTextColor(getResources().getColor(R.color.text_color4));
			tvRain2.setBackgroundColor(getResources().getColor(R.color.transparent));
			tvTemp2.setTextColor(getResources().getColor(R.color.text_color4));
			tvTemp2.setBackgroundColor(getResources().getColor(R.color.transparent));
			tvHumidity2.setTextColor(getResources().getColor(R.color.text_color4));
			tvHumidity2.setBackgroundColor(getResources().getColor(R.color.transparent));
			tvVisibility2.setTextColor(getResources().getColor(R.color.text_color4));
			tvVisibility2.setBackgroundColor(getResources().getColor(R.color.transparent));
			tvPressure2.setTextColor(0xff2d5a9d);
			tvPressure2.setBackgroundResource(R.drawable.bg_layer_button);
			tvWindSpeed2.setTextColor(getResources().getColor(R.color.text_color4));
			tvWindSpeed2.setBackgroundColor(getResources().getColor(R.color.transparent));
			
			value = 5;
			addMarker(stationList, value, false);
			break;
		case R.id.tvWindSpeed2:
			llRain.setVisibility(View.GONE);
			ivLegend.setImageResource(R.drawable.iv_legend_wind);
			pastTime = 60*60*1000;
			if (!TextUtils.isEmpty(StationManager.windspeedResult)) {
				asynParseMapData(StationManager.windspeedResult, getString(R.string.layer_wind));
			}
			tvRain2.setTextColor(getResources().getColor(R.color.text_color4));
			tvRain2.setBackgroundColor(getResources().getColor(R.color.transparent));
			tvTemp2.setTextColor(getResources().getColor(R.color.text_color4));
			tvTemp2.setBackgroundColor(getResources().getColor(R.color.transparent));
			tvHumidity2.setTextColor(getResources().getColor(R.color.text_color4));
			tvHumidity2.setBackgroundColor(getResources().getColor(R.color.transparent));
			tvVisibility2.setTextColor(getResources().getColor(R.color.text_color4));
			tvVisibility2.setBackgroundColor(getResources().getColor(R.color.transparent));
			tvPressure2.setTextColor(getResources().getColor(R.color.text_color4));
			tvPressure2.setBackgroundColor(getResources().getColor(R.color.transparent));
			tvWindSpeed2.setTextColor(0xff2d5a9d);
			tvWindSpeed2.setBackgroundResource(R.drawable.bg_layer_button);
			
			value = 6;
			addMarker(stationList, value, false);
			break;
			
		case R.id.ivOpen:
			openMenuAnimation(llContainer);
			break;
		case R.id.ivRain:
			addMarker(stationList, 1, false);
			break;
		case R.id.ivTemp:
			addMarker(stationList, 2, false);
			break;
		case R.id.ivHumidity:
			addMarker(stationList, 3, false);
			break;
		case R.id.ivVisibility:
			addMarker(stationList, 4, false);
			break;
		case R.id.ivPressure:
			addMarker(stationList, 5, false);
			break;
		case R.id.ivWindSpeed:
			addMarker(stationList, 6, false);
			break;
		case R.id.tvCheckStation:
			Intent intent = new Intent(mContext, StationMonitorDetailActivity.class);
			intent.putExtra(CONST.ACTIVITY_NAME, stationName);
			intent.putExtra("stationId", stationId);
			startActivity(intent);
			break;
		case R.id.ivMapSearch:
			llContainer.setVisibility(View.INVISIBLE);
			reContent.setVisibility(View.GONE);
			
			Intent intentMap = new Intent(mContext, StationMonitorSearchActivity.class);
			intentMap.putExtra(CONST.ACTIVITY_NAME, getString(R.string.selecte_area));
			startActivityForResult(intentMap, 0);
			break;
		case R.id.ivDelete:
			closeDetailWindow();
			break;
		case R.id.ivLocation:
			aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locationLat, locationLng), zoom));
			break;
		case R.id.ivLegendPrompt:
			if (isLegendVisible == false) {
				ivLegend.setVisibility(View.VISIBLE);
				isLegendVisible = true;
			}else {
				ivLegend.setVisibility(View.INVISIBLE);
				isLegendVisible = false;
			}
			break;
		case R.id.ivRank:
			startActivity(new Intent(mContext, StationMonitorRankActivity.class));
			break;

		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 0:
				stationList.clear();
				removeStationMarkers();
				
				Bundle bundle = data.getExtras();
				if (bundle != null) {
					String auto_stations = bundle.getString("auto_stations");
					if (!TextUtils.isEmpty(auto_stations)) {
						String provinceName = bundle.getString("provinceName");
						if (!TextUtils.isEmpty(provinceName)) {
							queryCityOrDistrict(stationList, provinceName);
						}
					}else {
						String provinceName = bundle.getString("provinceName");
						String cityName = bundle.getString("cityName");
						String districtName = bundle.getString("districtName");
						if (!TextUtils.isEmpty(provinceName) && !TextUtils.isEmpty(cityName) && !TextUtils.isEmpty(districtName)) {
							queryCityOrDistrict(provinceName, cityName, districtName, stationList);
						}
					}
					
					String ids = "";
					for (int i = 0; i < stationList.size(); i++) {
						StationMonitorDto dto = stationList.get(i);
						if (!TextUtils.isEmpty(dto.stationId)) {
							ids += dto.stationId+",";
						}
						dto.distance = getDistance(locationLng, locationLat, Double.valueOf(dto.lng), Double.valueOf(dto.lat));
					}
					
					if (!TextUtils.isEmpty(auto_stations)) {
						asyncTaskContentPost(ids.substring(0, ids.length()-1), true);
					}else {
						if (!TextUtils.isEmpty(ids)) {
							asyncTaskContent(ids.substring(0, ids.length()-1), true);
						}
					}
				}
				break;

			default:
				break;
			}
		}
	}

}
