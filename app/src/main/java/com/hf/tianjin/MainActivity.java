package com.hf.tianjin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.hf.tianjin.common.CONST;
import com.hf.tianjin.dto.ChartDto;
import com.hf.tianjin.dto.WarningDto;
import com.hf.tianjin.dto.WeatherDto;
import com.hf.tianjin.manager.ChartRainManager;
import com.hf.tianjin.manager.ChartRainManager.ChartRainListener;
import com.hf.tianjin.manager.DataCleanManager;
import com.hf.tianjin.utils.CommonUtil;
import com.hf.tianjin.utils.CustomHttpClient;
import com.hf.tianjin.utils.OkHttpUtil;
import com.hf.tianjin.utils.WeatherUtil;
import com.hf.tianjin.view.CubicView;
import com.hf.tianjin.view.WeeklyView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.com.weather.api.WeatherAPI;
import cn.com.weather.beans.Weather;
import cn.com.weather.constants.Constants.Language;
import cn.com.weather.listener.AsyncResponseHandler;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends BaseActivity implements AMapLocationListener, ChartRainListener{
	
	private Context mContext = null;
	private RelativeLayout reMain = null;
	private ScrollView scrollView = null;
	private TextView tvTitle = null;
	private TextView tvTime1 = null;
	private TextView tvWarning = null;
	private ImageView ivEvent = null;
	private ImageView ivPhe = null;
	private TextView tvTemp = null;
	private TextView tvPressre = null;
	private TextView tvHumidity = null;
	private TextView tvWind = null;
	private TextView tvVisible = null;
	private LinearLayout llContainer1 = null;
	private LinearLayout llContainer2 = null;
	private AMapLocationClientOption mLocationOption = null;//声明mLocationOption对象
	private AMapLocationClient mLocationClient = null;//声明AMapLocationClient类对象
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
	private SimpleDateFormat sdf2 = new SimpleDateFormat("HH");
	private SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy-MM-dd");
	private int width = 0;
	private List<WarningDto> warningList = new ArrayList<>();
	private long mExitTime;//记录点击完返回按钮后的long型时间
	private List<WeatherDto> aqiList = new ArrayList<>();//空气指数list
	private Timer timer = null;
	private ViewPager viewPager = null;
	private List<ImageView> imgList = new ArrayList<>();
	private SwipeRefreshLayout refreshLayout = null;//下拉刷新布局
	private ImageLoader imageLoader = ImageLoader.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		mContext = this;
		initRefreshLayout();
		initWidget();
	}

	/**
	 * 初始化下拉刷新布局
	 */
	private void initRefreshLayout() {
		refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
		refreshLayout.setColorSchemeResources(CONST.color1, CONST.color2, CONST.color3, CONST.color4);
		refreshLayout.setProgressViewEndTarget(true, 300);
		refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				refresh();
			}
		});
	}
	
	/**
	 * 初始化viewPager
	 */
	private void initViewPager() {
		index_plus = 0;
		imgList.clear();
		imageLoader.clearMemoryCache();
		String url = "http://60.29.105.39:7777/sk_qg/";
		for (int i = 0; i < 27; i++) {
			final ImageView imageView = new ImageView(mContext);
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//			FinalBitmap finalBitmap = FinalBitmap.create(mContext);
//			finalBitmap.display(imageView, url+i+".jpg", null, 0);
			//显示图片的配置
			DisplayImageOptions options = new DisplayImageOptions.Builder()
					.cacheInMemory(true)
					.cacheOnDisk(true)
					.bitmapConfig(Bitmap.Config.RGB_565)
					.build();
			imageLoader.displayImage(url+i+".jpg", imageView, options);
			imgList.add(imageView);
		}

		viewPager = (ViewPager) findViewById(R.id.viewPager);
		viewPager.setAdapter(new PagerAdapter() {
			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				container.addView(imgList.get(position));
				return imgList.get(position);
			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager) container).removeView(imgList.get(position));
			}

			@Override
			public boolean isViewFromObject(View view, Object object) {
				return view == object;
			}

			@Override
			public int getCount() {
				return imgList.size();
			}
		});
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				imgHandler.sendEmptyMessageDelayed(AUTO_PLUS, PHOTO_CHANGE_TIME);
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});

		if (imgList.size() > 1) {
			imgHandler.sendEmptyMessageDelayed(AUTO_PLUS, PHOTO_CHANGE_TIME);
		}
	}

	private final int AUTO_REFRESH = 10002;
	private final int AUTO_PLUS = 1001;
	private static final int PHOTO_CHANGE_TIME = 4000;//定时变量
	private int index_plus = 0;
	private Handler imgHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case AUTO_PLUS:
					viewPager.setCurrentItem(index_plus++);//收到消息后设置当前要显示的图片
					if (index_plus >= imgList.size()) {
						index_plus = 0;
					}
					break;
				case AUTO_REFRESH:
					refresh();
					break;
				default:
					break;
			}
		};
	};

	private void initWidget() {
		reMain = (RelativeLayout) findViewById(R.id.reMain);
		scrollView = (ScrollView) findViewById(R.id.scrollView);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setFocusable(true);
		tvTitle.setFocusableInTouchMode(true);
		tvTitle.requestFocus();
		tvTime1 = (TextView) findViewById(R.id.tvTime1);
		tvWarning = (TextView) findViewById(R.id.tvWarning);
		ivEvent = (ImageView) findViewById(R.id.ivEvent);
		ivPhe = (ImageView) findViewById(R.id.ivPhe);
		tvTemp = (TextView) findViewById(R.id.tvTemp);
		tvPressre = (TextView) findViewById(R.id.tvPressre);
		tvHumidity = (TextView) findViewById(R.id.tvHumidity);
		tvWind = (TextView) findViewById(R.id.tvWind);
		tvVisible = (TextView) findViewById(R.id.tvVisible);
		llContainer1 = (LinearLayout) findViewById(R.id.llContainer1);
		llContainer2 = (LinearLayout) findViewById(R.id.llContainer2);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;

		mRadarManager = new ChartRainManager(mContext);

		refresh();
		if (timer == null) {
			timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					Message msg = new Message();
					msg.what = AUTO_REFRESH;
					imgHandler.sendMessage(msg);
				}
			}, 0, 1000*60*10);
		}

	}

	private void refresh() {
		refreshLayout.post(new Runnable() {
			@Override
			public void run() {
				refreshLayout.setRefreshing(true);
			}
		});
		DataCleanManager.clearCache(mContext);
		initViewPager();
//		if (CommonUtil.isLocationOpen(mContext)) {
//			startLocation();
//		}else {
			tvTitle.setText("津南区气象局");
			String id = "101031000";
			//判断是否隶属于天津市，更换背景图片
			isTianJin(id);
			//获取定位城市所有信息
			queryAllInfo(id);
//		}
		asyncTianjinEvent("http://decision-admin.tianqi.cn/Home/extra/getTJMainActivity");
	}
	
	/**
	 * 判断是否隶属于天津市，更换背景图片
	 * @param cityId
	 */
	private void isTianJin(String cityId) {
		if (!TextUtils.isEmpty(cityId) && TextUtils.equals(cityId.substring(0, 5), CONST.TIANJINCITYID)) {
			CONST.ISTIANJIN = true;
			if (chartList.size() > 0) {
				ivEvent.setVisibility(View.VISIBLE);
			}
			try {
				long zao6 = sdf2.parse("05").getTime();
				long wan8 = sdf2.parse("18").getTime();
				long current = sdf2.parse(sdf2.format(new Date())).getTime();
				if (current >= zao6 && current < wan8) {
					reMain.setBackgroundResource(R.drawable.bg_else_city_day);
				}else {
					reMain.setBackgroundResource(R.drawable.bg_else_city_night);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else {
			CONST.ISTIANJIN = false;
			ivEvent.setVisibility(View.GONE);
			try {
				long zao6 = sdf2.parse("05").getTime();
				long wan8 = sdf2.parse("18").getTime();
				long current = sdf2.parse(sdf2.format(new Date())).getTime();
				if (current >= zao6 && current < wan8) {
					reMain.setBackgroundResource(R.drawable.bg_else_city_day);
				}else {
					reMain.setBackgroundResource(R.drawable.bg_else_city_night);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
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
        	tvTitle.setText(amapLocation.getPoiName());
        	getCityId(amapLocation.getLongitude(), amapLocation.getLatitude());
        }
	}
	
	/**
	 * 获取天气数据
	 */
	private void getCityId(final double lng, final double lat) {
		WeatherAPI.getGeo(mContext,String.valueOf(lng), String.valueOf(lat), new AsyncResponseHandler(){
			@Override
			public void onComplete(JSONObject content) {
				super.onComplete(content);
				if (!content.isNull("geo")) {
					try {
						JSONObject geoObj = content.getJSONObject("geo");
						if (!geoObj.isNull("id")) {
							String id = geoObj.getString("id");

							//判断是否隶属于天津市，更换背景图片
							isTianJin(id);
							
							//获取定位城市所有信息
							queryAllInfo(id);
							
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			
			@Override
			public void onError(Throwable error, String content) {
				super.onError(error, content);
			}
		});
	}
	
	/**
	 * 获取某一个城市所有信息
	 * @param cityId
	 */
	private void queryAllInfo(final String cityId) {
		if (!TextUtils.isEmpty(cityId) && TextUtils.equals(cityId.substring(0, 5), CONST.TIANJINCITYID)) {
			CONST.ISTIANJIN = true;
//			asyncQueryTianjin("http://211.99.240.5:8080/datafusion/GetRhdata?ID="+cityId);
			getWeatherInfo(cityId);
		}else {
			CONST.ISTIANJIN = false;
			getWeatherInfo(cityId);
		}

	}
	
	/**
	 * 请求天津预报数据
	 */
	private void asyncQueryTianjin(String requestUrl) {
		HttpAsyncTaskTianjin task = new HttpAsyncTaskTianjin();
		task.setMethod("GET");
		task.setTimeOut(CustomHttpClient.TIME_OUT);
		task.execute(requestUrl);
	}
	
	/**
	 * 异步请求方法
	 * @author dell
	 *
	 */
	private class HttpAsyncTaskTianjin extends AsyncTask<String, Void, String> {
		private String method = "GET";
		private List<NameValuePair> nvpList = new ArrayList<NameValuePair>();
		
		public HttpAsyncTaskTianjin() {
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
					JSONObject obj = new JSONObject(requestResult);
					
					//时间日期
					List<WeatherDto> timeList = new ArrayList<WeatherDto>();
					timeList.clear();
					if (!obj.isNull("t")) {
						JSONArray array = obj.getJSONArray("t");
						for (int i = 0; i < array.length(); i++) {
							JSONObject itemObj = array.getJSONObject(i);
							WeatherDto dto = new WeatherDto();
							if (!itemObj.isNull("t4")) {
								dto.week = itemObj.getString("t4");//星期几
							}
							if (!itemObj.isNull("t1")) {
								dto.date = itemObj.getString("t1");//日期
							}
							timeList.add(dto);
						}
					}
					
					//一周预报信息
					if (!obj.isNull("f")) {
						JSONObject f = obj.getJSONObject("f");
						if (!f.isNull("f1")) {
							JSONArray f1 = f.getJSONArray("f1");
							List<WeatherDto> weeklyList = new ArrayList<>();
							for (int i = 0; i < 7; i++) {
								JSONObject weeklyObj = f1.getJSONObject(i);
								WeatherDto dto = new WeatherDto();
								//晚上
								dto.lowPheCode = Integer.valueOf(weeklyObj.getString("fb"));
								dto.lowPhe = getString(WeatherUtil.getWeatherId(Integer.valueOf(weeklyObj.getString("fb"))));
								dto.lowTemp = Integer.valueOf(weeklyObj.getString("fd"));
								dto.windDir = Integer.valueOf(weeklyObj.getString("ff"));
								dto.windForce = Integer.valueOf(weeklyObj.getString("fh"));
								//白天
								dto.highPheCode = Integer.valueOf(weeklyObj.getString("fa"));
								dto.highPhe = getString(WeatherUtil.getWeatherId(Integer.valueOf(weeklyObj.getString("fa"))));
								dto.highTemp = Integer.valueOf(weeklyObj.getString("fc"));
								dto.windDir = Integer.valueOf(weeklyObj.getString("fe"));
								dto.windForce = Integer.valueOf(weeklyObj.getString("fg"));
								
								try {
									if (timeList.get(i) != null) {
										if (timeList.get(i).date != null) {
											dto.date = timeList.get(i).date;//日期
										}
										if (timeList.get(i).week != null) {
											dto.week = timeList.get(i).week;//星期几
										}
									}
								} catch (IndexOutOfBoundsException e) {
									e.printStackTrace();
								}
								
								if (i <= aqiList.size()-1) {
									String aqi = aqiList.get(i).aqi;
									if (!TextUtils.isEmpty(aqi)) {
										dto.aqi = aqi;
									}
								}
								weeklyList.add(dto);
							}
							
							WeeklyView weeklyView = new WeeklyView(mContext);
							weeklyView.setData(weeklyList);
							llContainer2.removeAllViews();
							llContainer2.addView(weeklyView, width, (int)(CommonUtil.dip2px(mContext, 600)));
						}
					}
					
					//逐小时预报信息
					if (!obj.isNull("jh")) {
						JSONArray jh = obj.getJSONArray("jh");
						List<WeatherDto> hourlyList = new ArrayList<>();
						for (int i = 0; i < jh.length(); i++) {
							JSONObject itemObj = jh.getJSONObject(i);
							WeatherDto dto = new WeatherDto();
							dto.hourlyCode = Integer.valueOf(itemObj.getString("ja"));
							dto.hourlyTemp = Integer.valueOf(itemObj.getString("jb"));
							dto.hourlyTime = itemObj.getString("jf");
							dto.hourlyWindDirCode = Integer.valueOf(itemObj.getString("jc"));
							dto.hourlyWindForceCode = Integer.valueOf(itemObj.getString("jd"));
							hourlyList.add(dto);
						}

						CubicView cubicView = new CubicView(mContext);
						cubicView.setData(hourlyList, width);
						llContainer1.removeAllViews();
						llContainer1.addView(cubicView, width, (int)(CommonUtil.dip2px(mContext, 300)));
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
	
	/**
	 * 获取实况信息、预报信息
	 */
	private void getWeatherInfo(final String cityId) {
		if (TextUtils.isEmpty(cityId)) {
			return;
		}
		WeatherAPI.getWeather2(mContext, cityId, Language.ZH_CN, new AsyncResponseHandler() {
			@SuppressWarnings("deprecation")
			@Override
			public void onComplete(Weather content) {
				super.onComplete(content);
				if (content != null) {
					//实况信息
					JSONObject object = content.getWeatherFactInfo();
					try {
						if (!object.isNull("l7")) {
							String time = object.getString("l7");
							if (time != null) {
								time = sdf4.format(new Date())+ " "+time;
								tvTime1.setText(time + getString(R.string.publish));
							}
						}
						if (!object.isNull("l5")) {
							String weatherCode = WeatherUtil.lastValue(object.getString("l5"));
							Drawable drawable = getResources().getDrawable(R.drawable.phenomenon_drawable);
							try {
								long zao6 = sdf2.parse("06").getTime();
								long wan8 = sdf2.parse("18").getTime();
								long current = sdf2.parse(sdf2.format(new Date())).getTime();
								if (current >= zao6 && current < wan8) {
									drawable = getResources().getDrawable(R.drawable.phenomenon_drawable);
								}else {
									drawable = getResources().getDrawable(R.drawable.phenomenon_drawable_night);
								}
							} catch (ParseException e) {
								e.printStackTrace();
							}
							drawable.setLevel(Integer.valueOf(weatherCode));
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
								ivPhe.setBackground(drawable);
							}else {
								ivPhe.setBackgroundDrawable(drawable);
							}
						}
						if (!object.isNull("l1")) {
							String factTemp = WeatherUtil.lastValue(object.getString("l1"));
							tvTemp.setText(factTemp);
						}
						if (!object.isNull("l2")) {
							String humidity = WeatherUtil.lastValue(object.getString("l2"));
							if (TextUtils.isEmpty(humidity) || TextUtils.equals(humidity, "null")) {
								tvHumidity.setText("湿度"+" "+"--");
							}else {
								tvHumidity.setText("湿度"+" "+humidity + getString(R.string.unit_percent));
							}
						}
						if (!object.isNull("l4")) {
							String windDir = WeatherUtil.lastValue(object.getString("l4"));
							if (!object.isNull("l3")) {
								String windForce = WeatherUtil.lastValue(object.getString("l3"));
								if (TextUtils.isEmpty(windDir) || TextUtils.equals(windDir, "null")
										|| TextUtils.isEmpty(windForce) || TextUtils.equals(windForce, "null")) {
									tvWind.setText("--");
								}else {
									tvWind.setText(getString(WeatherUtil.getWindDirection(Integer.valueOf(windDir)))
											+ " " + WeatherUtil.getFactWindForce(Integer.valueOf(windForce)));
								}
							}
						}
						if (!object.isNull("l10")) {
							String pressure = WeatherUtil.lastValue(object.getString("l10"));
							if (TextUtils.isEmpty(pressure) || TextUtils.equals(pressure, "null")) {
								tvPressre.setText("气压"+" "+"--");
							}else {
								tvPressre.setText("气压"+" "+pressure + getString(R.string.unit_hPa));
							}
						}
						if (!object.isNull("l9")) {
							String visible = WeatherUtil.lastValue(object.getString("l9"));
							if (TextUtils.isEmpty(visible) || TextUtils.equals(visible, "null")) {
								tvVisible.setText("能见度"+" "+"--");
							}else {
								float value = Float.valueOf(visible);
								if (value > 1000) {
									BigDecimal bd = new BigDecimal(value/1000);
									bd.setScale(1, BigDecimal.ROUND_HALF_UP);
									value = bd.floatValue();
									tvVisible.setText("能见度"+" "+value + getString(R.string.unit_km));
								}else {
									tvVisible.setText("能见度"+" "+visible + getString(R.string.unit_m));
								}
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
//					if (!TextUtils.isEmpty(cityId) && TextUtils.equals(cityId.substring(0, 5), CONST.TIANJINCITYID)) {
//						CONST.ISTIANJIN = true;
//					}else {
//						CONST.ISTIANJIN = false;
						//一周预报信息
						try {
							List<WeatherDto> weeklyList = new ArrayList<>();
							//这里只去一周预报，默认为15天，所以遍历7次
							for (int i = 1; i <= 7; i++) {
								WeatherDto dto = new WeatherDto();
								
								JSONArray weeklyArray = content.getWeatherForecastInfo(i);
								JSONObject weeklyObj = weeklyArray.getJSONObject(0);
								
								//晚上
								dto.lowPheCode = Integer.valueOf(weeklyObj.getString("fb"));
								dto.lowPhe = getString(WeatherUtil.getWeatherId(Integer.valueOf(weeklyObj.getString("fb"))));
								dto.lowTemp = Integer.valueOf(weeklyObj.getString("fd"));
								dto.windDir = Integer.valueOf(weeklyObj.getString("ff"));
								dto.windForce = Integer.valueOf(weeklyObj.getString("fh"));
								
								//白天数据缺失时，就使用晚上数据
								if (TextUtils.isEmpty(weeklyObj.getString("fa"))) {
									JSONObject obj1 = content.getWeatherForecastInfo(2).getJSONObject(0);
									//白天
									dto.highPheCode = Integer.valueOf(obj1.getString("fa"));
									dto.highPhe = getString(WeatherUtil.getWeatherId(Integer.valueOf(obj1.getString("fa"))));
									dto.highTemp = Integer.valueOf(obj1.getString("fc"));
//									dto.windDir = Integer.valueOf(obj1.getString("fe"));
//									dto.windForce = Integer.valueOf(obj1.getString("fg"));
								}else {
									//白天
									dto.highPheCode = Integer.valueOf(weeklyObj.getString("fa"));
									dto.highPhe = getString(WeatherUtil.getWeatherId(Integer.valueOf(weeklyObj.getString("fa"))));
									dto.highTemp = Integer.valueOf(weeklyObj.getString("fc"));
									dto.windDir = Integer.valueOf(weeklyObj.getString("fe"));
									dto.windForce = Integer.valueOf(weeklyObj.getString("fg"));
								}
								
								JSONArray timeArray =  content.getTimeInfo(i);
								JSONObject timeObj = timeArray.getJSONObject(0);
								dto.week = timeObj.getString("t4");//星期几
								dto.date = timeObj.getString("t1");//日期
								
								if (i <= aqiList.size()) {
									String aqi = aqiList.get(i-1).aqi;
									if (!TextUtils.isEmpty(aqi)) {
										dto.aqi = aqi;
									}
								}
								
								weeklyList.add(dto);
							}
							
							//一周预报曲线
							WeeklyView weeklyView = new WeeklyView(mContext);
							weeklyView.setData(weeklyList);
							llContainer2.removeAllViews();
							llContainer2.addView(weeklyView, width, (int)(CommonUtil.dip2px(mContext, 600)));
						} catch (JSONException e) {
							e.printStackTrace();
						} catch (NullPointerException e) {
							e.printStackTrace();
						}
						
						//逐小时预报信息
						JSONArray hourlyArray = content.getHourlyFineForecast2();
						try {
							int size = hourlyArray.length();
							List<WeatherDto> hourlyList = new ArrayList<WeatherDto>();
							for (int i = 0; i < size; i++) {
								JSONObject itemObj = hourlyArray.getJSONObject(i);
								WeatherDto dto = new WeatherDto();
								dto.hourlyCode = Integer.valueOf(itemObj.getString("ja"));
								dto.hourlyTemp = Integer.valueOf(itemObj.getString("jb"));
								dto.hourlyTime = itemObj.getString("jf");
								dto.hourlyWindDirCode = Integer.valueOf(itemObj.getString("jc"));
								dto.hourlyWindForceCode = Integer.valueOf(itemObj.getString("jd"));
								hourlyList.add(dto);
							}

							//逐小时预报信息
							CubicView cubicView = new CubicView(mContext);
							cubicView.setData(hourlyList, width);
							llContainer1.removeAllViews();
							llContainer1.addView(cubicView, width, (int)(CommonUtil.dip2px(mContext, 300)));
						} catch (JSONException e) {
							e.printStackTrace();
						} catch (NullPointerException e) {
							e.printStackTrace();
						}
//					}

					scrollView.setVisibility(View.VISIBLE);
//					scrollView.scrollTo(0, 0);
					refreshLayout.setRefreshing(false);
				}
			}
			
			@Override
			public void onError(Throwable error, String content) {
				super.onError(error, content);
			}
		});

		OkhttpWarning("http://decision-admin.tianqi.cn/Home/extra/getwarns?order=1&areaid=12");
	}

	/**
	 * 获取预警信息
	 */
	private void OkhttpWarning(String requestUrl) {
		OkHttpUtil.enqueue(new Request.Builder().url(requestUrl).build(), new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {

			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				if (!response.isSuccessful()) {
					return;
				}
				final String result = response.body().string();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						warningList.clear();
						if (result != null) {
							try {
								JSONObject object = new JSONObject(result);
								if (!object.isNull("count")) {
									String count = object.getString("count");
									if (!TextUtils.equals(count, "0")) {
										if (!object.isNull("data")) {
											JSONArray jsonArray = object.getJSONArray("data");
											for (int i = jsonArray.length()-1; i >= 0; i--) {
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
												if (!dto.name.contains("解除") && (TextUtils.equals(dto.item0, "120000") || TextUtils.equals(dto.item0, "120112"))) {
													warningList.add(dto);
												}
											}

											int size = warningList.size();
											if (size > 0) {
												String name = warningList.get(0).name;
												if (name.contains("发布")) {
													String[] nameArray = name.split("发布");
													if (!TextUtils.isEmpty(nameArray[1])) {
														if (nameArray[1].contains("[") && nameArray[1].contains("]")) {
															tvWarning.setText(nameArray[1].substring(0, nameArray[1].indexOf("[")));
														}else if (nameArray[1].contains("/")){
															tvWarning.setText(nameArray[1].substring(0, nameArray[1].indexOf("/")));
														}
													}
												}else {
													tvWarning.setText(name);
												}
											}else {
												tvWarning.setText(getString(R.string.no_warning));
											}
											tvWarning.setVisibility(View.VISIBLE);
										}
									}else {
										tvWarning.setText(getString(R.string.no_warning));
										tvWarning.setVisibility(View.VISIBLE);
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				});

			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(mContext, getString(R.string.confirm_exit)+getString(R.string.app_name), Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} else {
				finish();
			}
		}
		return false;
	}



	private ChartRainManager mRadarManager = null;
	private ChartRainThread mRadarThread = null;
	private static final int HANDLER_SHOW_RADAR = 1;
	private static final int HANDLER_PROGRESS = 2;
	private List<ChartDto> chartList = new ArrayList<ChartDto>();
	private String eventName = "";
	private String eventUrl = "";
	/**
	 * 获取天津重大活动
	 */
	private void asyncTianjinEvent(String requestUrl) {
		HttpAsyncTaskEvent task = new HttpAsyncTaskEvent();
		task.setMethod("GET");
		task.setTimeOut(CustomHttpClient.TIME_OUT);
		task.execute(requestUrl);
	}
	
	/**
	 * 异步请求方法
	 * @author dell
	 *
	 */
	private class HttpAsyncTaskEvent extends AsyncTask<String, Void, String> {
		private String method = "GET";
		private List<NameValuePair> nvpList = new ArrayList<NameValuePair>();
		
		public HttpAsyncTaskEvent() {
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
						if (!object.isNull("imgs")) {
							chartList.clear();
							JSONArray array = object.getJSONArray("imgs");
							for (int i = 0; i < array.length(); i++) {
								ChartDto dto = new ChartDto();
								dto.imgUrl = array.getString(i);
								chartList.add(dto);
							}
						}
						if (!object.isNull("name")) {
							eventName = object.getString("name");
						}
						if (!object.isNull("url")) {
							eventUrl = object.getString("url");
						}
						startDownLoadImgs(chartList);
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

	private void startDownLoadImgs(List<ChartDto> list) {
		if (mRadarThread != null) {
			mRadarThread.cancel();
			mRadarThread = null;
		}
		if (list.size() > 0) {
			mRadarManager.loadImagesAsyn(list, this);
		}
	}

	@Override
	public void onResult(int result, List<ChartDto> images) {
		if (result == ChartRainListener.RESULT_SUCCESSED) {
			if (mRadarThread != null) {
				mRadarThread.cancel();
				mRadarThread = null;
			}
			if (images.size() > 0) {
				mRadarThread = new ChartRainThread(images);
				mRadarThread.start();
			}
		}
	}
	
	private class ChartRainThread extends Thread {
		static final int STATE_PLAYING = 1;
		static final int STATE_CANCEL = 3;
		private List<ChartDto> images;
		private int state;
		private int index;
		private int count;
		
		public ChartRainThread(List<ChartDto> images) {
			this.images = images;
			this.count = images.size();
			this.index = 0;
			this.state = STATE_PLAYING;
		}
		
		@Override
		public void run() {
			super.run();
			this.state = STATE_PLAYING;
			while (true) {
				if (state == STATE_CANCEL) {
					break;
				}
				sendRadar();
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		private void sendRadar() {
			if (index >= count || index < 0) {
				index = 0;
			}else {
				ChartDto radar = images.get(index);
				Message message = mHandler.obtainMessage();
				message.what = HANDLER_SHOW_RADAR;
				message.obj = radar;
				message.arg1 = count - 1;
				message.arg2 = index ++;
				mHandler.sendMessage(message);
			}
			
		}
		
		public void cancel() {
			this.state = STATE_CANCEL;
		}
	}

	@Override
	public void onProgress(String url, int progress) {
		Message msg = new Message();
		msg.obj = progress;
		msg.what = HANDLER_PROGRESS;
		mHandler.sendMessage(msg);
	}
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch (what) {
			case HANDLER_SHOW_RADAR: 
				if (msg.obj != null) {
					ChartDto radar = (ChartDto) msg.obj;
					if (radar != null) {
						Bitmap bitmap = BitmapFactory.decodeFile(radar.imgUrl);
						if (bitmap != null) {
							ivEvent.setImageBitmap(bitmap);
						}
					}
				}
				break;
			default:
				break;
			}
			
		};
	};
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mRadarManager != null) {
			mRadarManager.onDestory();
		}
		if (mRadarThread != null) {
			mRadarThread.cancel();
			mRadarThread = null;
		}
	}
	
}
