package com.hf.tianjin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnCameraChangeListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.hf.tianjin.adapter.WarningAdapter;
import com.hf.tianjin.common.CONST;
import com.hf.tianjin.dto.WarningDto;
import com.hf.tianjin.utils.CommonUtil;
import com.hf.tianjin.utils.CustomHttpClient;
import com.hf.tianjin.utils.StatisticUtil;
import com.hf.tianjin.view.ArcMenu;
import com.hf.tianjin.view.ArcMenu.OnMenuItemClickListener;

/**
 * 预警
 * @author shawn_sun
 *
 */

@SuppressLint("SimpleDateFormat")
public class WarningActivity extends BaseActivity implements OnClickListener, AMapLocationListener, OnMapClickListener, 
OnMarkerClickListener, InfoWindowAdapter, OnCameraChangeListener{
	
	private Context mContext = null;
	private LinearLayout llBack = null;
	private TextView tvTitle = null;
	private ProgressBar progressBar = null;
	private MapView mapView = null;//高德地图
	private AMap aMap = null;//高德地图
	private Marker selectMarker = null;
	private float zoom = 4.0f;
	private ArcMenu arcMenu = null;
	private boolean blue = true, yellow = true, orange = true, red = true;
	private String warningUrl = "http://decision.tianqi.cn/alarm12379/static.html";//预警地址
	private List<WarningDto> warningList = new ArrayList<WarningDto>();
	private List<WarningDto> blueList = new ArrayList<WarningDto>();
	private List<WarningDto> yellowList = new ArrayList<WarningDto>();
	private List<WarningDto> orangeList = new ArrayList<WarningDto>();
	private List<WarningDto> redList = new ArrayList<WarningDto>();
	private List<Marker> blueMarkers = new ArrayList<Marker>();
	private List<Marker> yellowMarkers = new ArrayList<Marker>();
	private List<Marker> orangeMarkers = new ArrayList<Marker>();
	private List<Marker> redMarkers = new ArrayList<Marker>();
	private ImageView ivLocation = null;
	private ImageView ivRefresh = null;
	private ImageView ivList = null;
	private AMapLocationClientOption mLocationOption = null;//声明mLocationOption对象
	private AMapLocationClient mLocationClient = null;//声明AMapLocationClient类对象
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.warning);
		mContext = this;
		initAmap(savedInstanceState);
		initWidget();
	}
	
	/**
	 * 初始化控件
	 */
	private void initWidget() {
		llBack = (LinearLayout) findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setText("预警地图");
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		arcMenu = (ArcMenu) findViewById(R.id.arcMenu);
		arcMenu.setOnMenuItemClickListener(arcMenuListener);
		ivLocation = (ImageView) findViewById(R.id.ivLocation);
		ivLocation.setOnClickListener(this);
		ivRefresh = (ImageView) findViewById(R.id.ivRefresh);
		ivRefresh.setOnClickListener(this);
		ivList = (ImageView) findViewById(R.id.ivList);
		ivList.setOnClickListener(this);
		
		asyncQuery(warningUrl);
		StatisticUtil.submitClickCount("4", "预警地图");
    }
	
	/**
	 * 初始化高德地图
	 */
	private void initAmap(Bundle bundle) {
		mapView = (MapView) findViewById(R.id.mapView);
		mapView.onCreate(bundle);
		if (aMap == null) {
			aMap = mapView.getMap();
		}
		
		aMap.moveCamera(CameraUpdateFactory.zoomTo(zoom));
		aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
		aMap.getUiSettings().setZoomControlsEnabled(false);
		aMap.setOnMapClickListener(this);
		aMap.setOnMarkerClickListener(this);
		aMap.setInfoWindowAdapter(this);
		aMap.setOnCameraChangeListener(this);
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
			LatLng latLng = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
			if (aMap != null) {
				aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8.0f));
			}
        }
	}
	
	/**
	 * 异步请求
	 */
	private void asyncQuery(String requestUrl) {
		progressBar.setVisibility(View.VISIBLE);
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

		@SuppressWarnings("unchecked")
		@Override
		protected void onPostExecute(String requestResult) {
			super.onPostExecute(requestResult);
			progressBar.setVisibility(View.GONE);
			warningList.clear();
			blueList.clear();
			blueMarkers.clear();
			yellowList.clear();
			yellowMarkers.clear();
			orangeList.clear();
			orangeMarkers.clear();
			redList.clear();
			redMarkers.clear();
			if (requestResult != null) {
				try {
					JSONObject object = new JSONObject(requestResult);
					if (object != null) {
						if (!object.isNull("data")) {
							JSONArray jsonArray = object.getJSONArray("data");
							for (int i = 0; i < jsonArray.length(); i++) {
								JSONArray tempArray = jsonArray.getJSONArray(i);
								WarningDto dto = new WarningDto();
								dto.html = tempArray.optString(1);
								String[] array = dto.html.split("-");
								String item0 = array[0];
								String item1 = array[1];
								String item2 = array[2];
								
								dto.item0 = item0;
								dto.provinceId = item0.substring(0, 2);
								dto.type = item2.substring(0, 5);
								dto.color = item2.substring(5, 7);
								dto.time = item1;
								dto.lng = tempArray.optString(2);
								dto.lat = tempArray.optString(3);
								dto.name = tempArray.optString(0);
								
								if (!dto.name.contains("解除")) {
									warningList.add(dto);
								}
							}
							
							for (int i = 0; i < warningList.size(); i++) {
								WarningDto dto = warningList.get(i);
								if (TextUtils.equals(dto.color, "01")) {
									blueList.add(dto);
								}else if (TextUtils.equals(dto.color, "02")) {
									yellowList.add(dto);
								}else if (TextUtils.equals(dto.color, "03")) {
									orangeList.add(dto);
								}else if(TextUtils.equals(dto.color, "04")) {
									redList.add(dto);
								}
							}
							
							aMap.clear();
							addMarkersToMap(blueList, blueMarkers);
							addMarkersToMap(yellowList, yellowMarkers);
							addMarkersToMap(orangeList, orangeMarkers);
							addMarkersToMap(redList, redMarkers);
							arcMenu.setVisibility(View.GONE);
							
							final List<WarningDto> tempList = new ArrayList<WarningDto>();
							tempList.clear();
							tempList.addAll(getIntent().getExtras().<WarningDto>getParcelableArrayList("warningList"));
							if (tempList.size() > 0) {
								//延时1秒开始地图动画
								new Handler().postDelayed(new Runnable() {
									@Override
									public void run() {
										LatLng latLng1 = new LatLng(Double.valueOf(tempList.get(0).lat), Double.valueOf(tempList.get(0).lng));
										LatLng latLng2 = new LatLng(Double.valueOf(tempList.get(tempList.size()-1).lat), Double.valueOf(tempList.get(tempList.size()-1).lng));
										try {
											LatLngBounds bounds = new LatLngBounds.Builder()
											.include(latLng1)
											.include(latLng2).build();
											aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
										} catch (ArrayIndexOutOfBoundsException e) {
											e.printStackTrace();
										}
									}
								}, 500);
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
	 * 在地图上添加marker
	 */
	private void addMarkersToMap(List<WarningDto> list, List<Marker> markerList) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (int i = 0; i < list.size(); i++) {
			WarningDto dto = list.get(i);
		    if (!TextUtils.equals(dto.item0, "000000")) {
		    	MarkerOptions optionsTemp = new MarkerOptions();
		    	optionsTemp.title(dto.lat);
		    	optionsTemp.snippet(dto.lng);
		    	optionsTemp.anchor(0.5f, 0.5f);
		    	if (!TextUtils.isEmpty(dto.lat) && !TextUtils.isEmpty(dto.lng)) {
		    		optionsTemp.position(new LatLng(Double.valueOf(dto.lat), Double.valueOf(dto.lng)));
		    	}
		    	
		    	View view = inflater.inflate(R.layout.warning_marker_view, null);
		    	ImageView ivMarker = (ImageView) view.findViewById(R.id.ivMarker);
		    	
		    	Bitmap bitmap = null;
		    	if (dto.color.equals(CONST.blue[0])) {
		    		bitmap = CommonUtil.getImageFromAssetsFile(mContext,"warning/"+dto.type+CONST.blue[1]+CONST.imageSuffix);
		    		if (bitmap == null) {
		    			bitmap = CommonUtil.getImageFromAssetsFile(mContext,"warning/"+"default"+CONST.blue[1]+CONST.imageSuffix);
					}
		    	}else if (dto.color.equals(CONST.yellow[0])) {
		    		bitmap = CommonUtil.getImageFromAssetsFile(mContext,"warning/"+dto.type+CONST.yellow[1]+CONST.imageSuffix);
		    		if (bitmap == null) {
		    			bitmap = CommonUtil.getImageFromAssetsFile(mContext,"warning/"+"default"+CONST.yellow[1]+CONST.imageSuffix);
					}
		    	}else if (dto.color.equals(CONST.orange[0])) {
		    		bitmap = CommonUtil.getImageFromAssetsFile(mContext,"warning/"+dto.type+CONST.orange[1]+CONST.imageSuffix);
		    		if (bitmap == null) {
		    			bitmap = CommonUtil.getImageFromAssetsFile(mContext,"warning/"+"default"+CONST.orange[1]+CONST.imageSuffix);
					}
		    	}else if (dto.color.equals(CONST.red[0])) {
		    		bitmap = CommonUtil.getImageFromAssetsFile(mContext,"warning/"+dto.type+CONST.red[1]+CONST.imageSuffix);
		    		if (bitmap == null) {
		    			bitmap = CommonUtil.getImageFromAssetsFile(mContext,"warning/"+"default"+CONST.red[1]+CONST.imageSuffix);
					}
		    	}
		    	ivMarker.setImageBitmap(bitmap);
		    	optionsTemp.icon(BitmapDescriptorFactory.fromView(view));
		    	
		    	Marker marker = aMap.addMarker(optionsTemp);
		    	markerList.add(marker);
			}
		}
	}
	
	@Override
	public void onMapClick(LatLng arg0) {
		if (selectMarker != null) {
			selectMarker.hideInfoWindow();
		}
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		selectMarker = marker;
		marker.showInfoWindow();
		return true;
	}
	
	@Override
	public View getInfoContents(final Marker marker) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.warning_marker_info, null);
		ListView mListView = null;
		WarningAdapter mAdapter = null;
		final List<WarningDto> infoList = new ArrayList<WarningDto>();
		
		addInfoList(warningList, marker, infoList);
		
		mListView = (ListView) view.findViewById(R.id.listView);
		mAdapter = new WarningAdapter(mContext, infoList, true);
		mListView.setAdapter(mAdapter);
		LayoutParams params = mListView.getLayoutParams();
		if (infoList.size() == 1) {
			params.height = (int) CommonUtil.dip2px(mContext, 50);
		}else if (infoList.size() == 2) {
			params.height = (int) CommonUtil.dip2px(mContext, 100);
		}else if (infoList.size() > 2){
			params.height = (int) CommonUtil.dip2px(mContext, 150);
		}
		mListView.setLayoutParams(params);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				intentDetail(infoList.get(arg2));
			}
		});
		return view;
	}
	
	private void intentDetail(WarningDto data) {
		Intent intentDetail = new Intent(mContext, WarningDetailActivity.class);
		intentDetail.putExtra("data", data);
		startActivity(intentDetail);
	}
	
	private void addInfoList(List<WarningDto> list, Marker marker, List<WarningDto> infoList) {
		for (int i = 0; i < list.size(); i++) {
			WarningDto dto = list.get(i);
			if (TextUtils.equals(marker.getTitle(), dto.lat) && TextUtils.equals(marker.getSnippet(), dto.lng)) {
				infoList.add(dto);
			}
		}
	}
	
	@Override
	public View getInfoWindow(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * @param flag false为显示map，true为显示list
	 */
	
	private void removeMarkers(List<Marker> markers) {
		for (int i = 0; i < markers.size(); i++) {
			markers.get(i).remove();
		}
		markers.clear();
	}
	
	private OnMenuItemClickListener arcMenuListener = new OnMenuItemClickListener() {
		@Override
		public void onClick(View view, int pos) {
			if (pos == 0) {
				if (blue) {
					blue = false;
					((ImageView)view).setImageResource(R.drawable.iv_arc_blue_press);
					removeMarkers(blueMarkers);
				}else {
					blue = true;
					((ImageView)view).setImageResource(R.drawable.iv_arc_blue);
					addMarkersToMap(blueList, blueMarkers);
				}
			}else if (pos == 1) {
				if (yellow) {
					yellow = false;
					((ImageView)view).setImageResource(R.drawable.iv_arc_yellow_press);
					removeMarkers(yellowMarkers);
				}else {
					yellow = true;
					((ImageView)view).setImageResource(R.drawable.iv_arc_yellow);
					addMarkersToMap(yellowList, yellowMarkers);
				}
			}else if (pos == 2) {
				if (orange) {
					orange = false;
					((ImageView)view).setImageResource(R.drawable.iv_arc_orange_press);
					removeMarkers(orangeMarkers);
				}else {
					orange = true;
					((ImageView)view).setImageResource(R.drawable.iv_arc_orange);
					addMarkersToMap(orangeList, orangeMarkers);
				}
			}else if (pos == 3) {
				if (red) {
					red = false;
					((ImageView)view).setImageResource(R.drawable.iv_arc_red_press);
					removeMarkers(redMarkers);
				}else {
					red = true;
					((ImageView)view).setImageResource(R.drawable.iv_arc_red);
					addMarkersToMap(redList, redMarkers);
				}
			}
		}
	};
	
	@Override
	public void onCameraChange(CameraPosition arg0) {
	}
	@Override
	public void onCameraChangeFinish(CameraPosition arg0) {
//		DisplayMetrics dm = new DisplayMetrics();
//		getWindowManager().getDefaultDisplay().getMetrics(dm);
//		Point leftPoint = new Point(0, dm.heightPixels);
//		Point rightPoint = new Point(dm.widthPixels, 0);
//		LatLng leftlatlng = aMap.getProjection().fromScreenLocation(leftPoint);
//		LatLng rightLatlng = aMap.getProjection().fromScreenLocation(rightPoint);
		
//		if (arg0.zoom == zoom) {
//			return;
//		}else {
//			handler.removeMessages(100);
//			Message msg = handler.obtainMessage();
//			msg.what = 100;
//			msg.obj = arg0;
//			handler.sendMessageDelayed(msg, 1000);
//		}
	}
	
//	private Handler handler = new Handler() {
//		public void handleMessage(Message msg) {
//			switch (msg.what) {
//			case 100:
//				CameraPosition arg0 = (CameraPosition) msg.obj;
//				aMap.clear();
//				zoom = arg0.zoom;
//				blueList.clear();
//				blueMarkers.clear();
//				yellowList.clear();
//				yellowMarkers.clear();
//				orangeList.clear();
//				orangeMarkers.clear();
//				redList.clear();
//				redMarkers.clear();
//				
//				if (zoom <= 6.0f) {
//					for (int i = 0; i < proList.size(); i++) {
//						WarningDto dto = proList.get(i);
//						if (TextUtils.equals(dto.color, "01")) {
//							blueList.add(dto);
//						}else if (TextUtils.equals(dto.color, "02")) {
//							yellowList.add(dto);
//						}else if (TextUtils.equals(dto.color, "03")) {
//							orangeList.add(dto);
//						}else if (TextUtils.equals(dto.color, "04")) {
//							redList.add(dto);
//						}
//					}
//				}else if (zoom > 6.0f && zoom <= 8.0f) {
//					for (int i = 0; i < cityList.size(); i++) {
//						WarningDto dto = cityList.get(i);
//						if (TextUtils.equals(dto.color, "01")) {
//							blueList.add(dto);
//						}else if (TextUtils.equals(dto.color, "02")) {
//							yellowList.add(dto);
//						}else if (TextUtils.equals(dto.color, "03")) {
//							orangeList.add(dto);
//						}else if (TextUtils.equals(dto.color, "04")) {
//							redList.add(dto);
//						}
//					}
//				}else if (zoom > 8.0f) {
//					for (int i = 0; i < disList.size(); i++) {
//						WarningDto dto = disList.get(i);
//						if (TextUtils.equals(dto.color, "01")) {
//							blueList.add(dto);
//						}else if (TextUtils.equals(dto.color, "02")) {
//							yellowList.add(dto);
//						}else if (TextUtils.equals(dto.color, "03")) {
//							orangeList.add(dto);
//						}else if (TextUtils.equals(dto.color, "04")) {
//							redList.add(dto);
//						}
//					}
//				}
//				
//				if (blue) {
//					addMarkersToMap(blueList, blueMarkers);
//				}
//				if (yellow) {
//					addMarkersToMap(yellowList, yellowMarkers);
//				}
//				if (orange) {
//					addMarkersToMap(orangeList, orangeMarkers);
//				}
//				if (red) {
//					addMarkersToMap(redList, redMarkers);
//				}
//				break;
//
//			default:
//				break;
//			}
//		};
//	};
	
	@SuppressWarnings("unchecked")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.llBack:
			finish();
			break;
		case R.id.ivLocation:
			startLocation();
			break;
		case R.id.ivRefresh:
			asyncQuery(warningUrl);
			break;
		case R.id.ivList:
			Intent intent = new Intent(mContext, WarningListActivity.class);
			intent.putExtra("isVisible", true);
			Bundle bundle = new Bundle();
			bundle.putParcelableArrayList("warningList", (ArrayList<? extends Parcelable>) warningList);
			intent.putExtras(bundle);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

}
