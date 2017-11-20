package com.hf.tianjin;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.hf.tianjin.adapter.AroundWeatherAdapter;
import com.hf.tianjin.adapter.LeftAdapter;
import com.hf.tianjin.common.CONST;
import com.hf.tianjin.dto.AqiDto;
import com.hf.tianjin.dto.ChartDto;
import com.hf.tianjin.dto.CityDto;
import com.hf.tianjin.dto.WarningDto;
import com.hf.tianjin.dto.WeatherDto;
import com.hf.tianjin.fragment.IndexFragment;
import com.hf.tianjin.manager.ChartRainManager;
import com.hf.tianjin.manager.ChartRainManager.ChartRainListener;
import com.hf.tianjin.manager.DBManager;
import com.hf.tianjin.manager.DataCleanManager;
import com.hf.tianjin.manager.XiangJiManager;
import com.hf.tianjin.swipemenulistview.SwipeMenu;
import com.hf.tianjin.swipemenulistview.SwipeMenuCreator;
import com.hf.tianjin.swipemenulistview.SwipeMenuItem;
import com.hf.tianjin.swipemenulistview.SwipeMenuListView;
import com.hf.tianjin.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.hf.tianjin.utils.AutoUpdateUtil;
import com.hf.tianjin.utils.CommonUtil;
import com.hf.tianjin.utils.CustomHttpClient;
import com.hf.tianjin.utils.StatisticUtil;
import com.hf.tianjin.utils.WeatherUtil;
import com.hf.tianjin.view.CubicView;
import com.hf.tianjin.view.MainViewPager;
import com.hf.tianjin.view.MinuteFallView;
import com.hf.tianjin.view.MyHorizontalScrollView;
import com.hf.tianjin.view.MyHorizontalScrollView.ScrollListener;
import com.hf.tianjin.view.WeeklyView;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

@SuppressLint("SimpleDateFormat")
public class MainActivity extends BaseActivity implements OnClickListener, AMapLocationListener, ChartRainListener{
	
	private Context mContext = null;
	private ImageView ivMenu = null;
	private ImageView ivShare = null;
	private RelativeLayout reMain = null;
	private ScrollView scrollView = null;
	private TextView tvTitle = null;
	private TextView tvTime1 = null;
	private TextView tvTime2 = null;
	private TextView tvTime3 = null;
	private TextView tvTime4 = null;
	private TextView tvTime5 = null;
	private TextView tvQuality = null;
	private TextView tvWarning = null;
	private TextView tvRoute = null;
	private TextView tvMonitor = null;
	private ImageView ivEvent = null;
	private ImageView ivWaitWind = null;
	private LinearLayout llArea = null;
	private TextView tvArea = null;
	private ImageView ivPhe = null;
	private TextView tvTemp = null;
	private TextView tvPressre = null;
	private TextView tvHumidity = null;
	private TextView tvWind = null;
	private TextView tvVisible = null;
	private TextView tvDay1 = null;
	private ImageView ivPhe1 = null;
	private TextView tvHighTemp1 = null;
	private TextView tvLowTemp1 = null;
	private TextView tvPhe1 = null;
	private TextView tvDay2 = null;
	private ImageView ivPhe2 = null;
	private TextView tvMinetePrompt = null;
	private TextView tvHighTemp2 = null;
	private TextView tvLowTemp2 = null;
	private TextView tvPhe2 = null;
	private RelativeLayout reTitle = null;
	private RelativeLayout reBottom = null;
	private TextView tvRain = null;
	private LinearLayout llContainer3 = null;
	private RelativeLayout reMinute = null;
	private ImageView ivClose = null;
	private TextView tvFifteen = null;
	private TextView tvHourly = null;
	private MyHorizontalScrollView hScrollView1 = null;
	private HorizontalScrollView hScrollView2 = null;
	private LinearLayout llContainer1 = null;
	private LinearLayout llContainer2 = null;
	private AMapLocationClientOption mLocationOption = null;//声明mLocationOption对象
	private AMapLocationClient mLocationClient = null;//声明AMapLocationClient类对象
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
	private SimpleDateFormat sdf2 = new SimpleDateFormat("HH");
	private SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy-MM-dd");
	private int width = 0, height = 0;
	private float density = 0;
	private List<WarningDto> warningList = new ArrayList<WarningDto>();
	private long mExitTime;//记录点击完返回按钮后的long型时间
	private MainViewPager viewPager;
	private List<Fragment> fragments = new ArrayList<Fragment>();
	private ImageView[] ivTips = null;//装载点的数组
	private ViewGroup viewGroup = null;
	private GridView aroundGridView = null;
	private AroundWeatherAdapter aroundAdapter = null;
	private List<WeatherDto> aroundList = new ArrayList<WeatherDto>();//周边天气
	private ProgressBar progressBar = null;
	private List<WeatherDto> aqiList = new ArrayList<WeatherDto>();//空气指数list
	private String locationCityId = null;//定位城市id
	private String selectCityId = null;//listview选中城市
	private String lat, lng;//选中城市对应的经纬度
	private RelativeLayout reFirst = null;
	private RelativeLayout reSecond = null;
	private RelativeLayout reThird = null;
	private RelativeLayout reFourth = null;
	private RelativeLayout reFifth = null;
	private RelativeLayout reNear = null;
	private CubicView cubicView = null;
	private SwipeRefreshLayout refreshLayout = null;//下拉刷新布局
	private AqiDto aqiData = null;//空气质量
//	private String store_name = "HUAWEI";
//	private String store_name = "XIAOMI";
//	private String store_name = "TENCENT";
//	private String store_name = "BAIDU";
//	private String store_name = "360";
	private String store_name = "PGYER";
	private Timer timer = null;
	
	//侧拉页面
	private DrawerLayout drawerlayout = null;
	private RelativeLayout reLeft = null;
	private ImageView ivAdd = null;//添加城市
	private SwipeMenuListView cityListView = null;
	private LeftAdapter leftAdapter = null;
	private List<WarningDto> leftList = new ArrayList<WarningDto>();
	private int attentionCount = 10;//关注城市列表最大个数(包含定位城市一个)
	private LinearLayout llAboutUs = null;
	private LinearLayout llFeedBack = null;
	private LinearLayout llClearCache = null;
	private TextView tvCache = null;
	private LinearLayout llVersion = null;
	private TextView tvVersion = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (CommonUtil.isGuangGaoJi()) {
			setContentView(R.layout.activity_main2);
		}else {
			setContentView(R.layout.activity_main);
		}
		mContext = this;
		initRefreshLayout();
		initWidget();
		initLeftListView();
	}
	
	/**
	 * 初始化下拉刷新布局
	 */
	private void initRefreshLayout() {
		refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
		refreshLayout.setColorSchemeResources(CONST.color1, CONST.color2, CONST.color3, CONST.color4);
		refreshLayout.setProgressViewEndTarget(true, 300);
		refreshLayout.post(new Runnable() {
			@Override
			public void run() {
				refreshLayout.setRefreshing(true);
			}
		});
		refreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				refresh();
			}
		});
	}

	private void refresh() {
		if (TextUtils.isEmpty(selectCityId) || TextUtils.isEmpty(lng) || TextUtils.isEmpty(lat)) {
			startLocation();
		}else if (TextUtils.equals(selectCityId, locationCityId)) {
			startLocation();
		}else {
			queryAllInfo(selectCityId);
		}
		asyncTianjinEvent("http://decision-admin.tianqi.cn/Home/extra/getTJMainActivity");
	}
	
	boolean isTime = true;
	private void initWidget() {
//		AutoUpdateUtil.checkUpdate(mContext, "55", getString(R.string.app_name), true);
		ivMenu = (ImageView) findViewById(R.id.ivMenu);
		ivMenu.setOnClickListener(this);
		ivShare = (ImageView) findViewById(R.id.ivShare);
		ivShare.setOnClickListener(this);
		reMain = (RelativeLayout) findViewById(R.id.reMain);
		scrollView = (ScrollView) findViewById(R.id.scrollView);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setFocusable(true);
		tvTitle.setFocusableInTouchMode(true);
		tvTitle.requestFocus();
		tvTime1 = (TextView) findViewById(R.id.tvTime1);
		tvTime1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				if (isTime) {
//					tvTime1.setText("( "+lat+","+lng+" )");
//					isTime = false;
//				}else {
//					tvTime1.setText(tvTime3.getText().toString());
//					isTime = true;
//				}
			}
		});
		tvTime2 = (TextView) findViewById(R.id.tvTime2);
		tvTime3 = (TextView) findViewById(R.id.tvTime3);
		tvTime4 = (TextView) findViewById(R.id.tvTime4);
		tvTime5 = (TextView) findViewById(R.id.tvTime5);
		tvWarning = (TextView) findViewById(R.id.tvWarning);
		tvWarning.setOnClickListener(this);
		tvQuality = (TextView) findViewById(R.id.tvQuality);
		tvQuality.setOnClickListener(this);
		tvRoute = (TextView) findViewById(R.id.tvRoute);
		tvRoute.setOnClickListener(this);
		tvMonitor = (TextView) findViewById(R.id.tvMonitor);
		tvMonitor.setOnClickListener(this);
		ivEvent = (ImageView) findViewById(R.id.ivEvent);
		ivEvent.setOnClickListener(this);
		ivWaitWind = (ImageView) findViewById(R.id.ivWaitWind);
		ivWaitWind.setOnClickListener(this);
		ivPhe = (ImageView) findViewById(R.id.ivPhe);
		tvTemp = (TextView) findViewById(R.id.tvTemp);
		tvPressre = (TextView) findViewById(R.id.tvPressre);
		tvHumidity = (TextView) findViewById(R.id.tvHumidity);
		tvWind = (TextView) findViewById(R.id.tvWind);
		tvVisible = (TextView) findViewById(R.id.tvVisible);
		tvDay1 = (TextView) findViewById(R.id.tvDay1);
		ivPhe1 = (ImageView) findViewById(R.id.ivPhe1);
		tvHighTemp1 = (TextView) findViewById(R.id.tvHighTemp1);
		tvLowTemp1 = (TextView) findViewById(R.id.tvLowTemp1);
		tvPhe1 = (TextView) findViewById(R.id.tvPhe1);
		tvDay2 = (TextView) findViewById(R.id.tvDay2);
		ivPhe2 = (ImageView) findViewById(R.id.ivPhe2);
		tvHighTemp2 = (TextView) findViewById(R.id.tvHighTemp2);
		tvLowTemp2 = (TextView) findViewById(R.id.tvLowTemp2);
		tvMinetePrompt = (TextView) findViewById(R.id.tvMinetePrompt);
		tvPhe2 = (TextView) findViewById(R.id.tvPhe2);
		llArea = (LinearLayout) findViewById(R.id.llArea);
		llArea.setOnClickListener(this);
		tvArea = (TextView) findViewById(R.id.tvArea);
		reTitle = (RelativeLayout) findViewById(R.id.reTitle);
		reBottom = (RelativeLayout) findViewById(R.id.reBottom);
		tvRain = (TextView) findViewById(R.id.tvRain);
		reMinute = (RelativeLayout) findViewById(R.id.reMinute);
		reMinute.setOnClickListener(this);
		llContainer3 = (LinearLayout) findViewById(R.id.llContainer3);
		ivClose = (ImageView) findViewById(R.id.ivClose);
		tvFifteen = (TextView) findViewById(R.id.tvFifteen);
		tvFifteen.setOnClickListener(this);
		tvHourly = (TextView) findViewById(R.id.tvHourly);
		tvHourly.setOnClickListener(this);
		hScrollView1 = (MyHorizontalScrollView) findViewById(R.id.hScrollView1);
		hScrollView1.setScrollListener(scrollListener);
		hScrollView2 = (HorizontalScrollView) findViewById(R.id.hScrollView2);
		llContainer1 = (LinearLayout) findViewById(R.id.llContainer1);
		llContainer2 = (LinearLayout) findViewById(R.id.llContainer2);
		viewGroup = (ViewGroup) findViewById(R.id.viewGroup);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		reFirst = (RelativeLayout) findViewById(R.id.reFirst);
		reSecond = (RelativeLayout) findViewById(R.id.reSecond);
		reThird = (RelativeLayout) findViewById(R.id.reThird);
		reFourth = (RelativeLayout) findViewById(R.id.reFourth);
		reFifth = (RelativeLayout) findViewById(R.id.reFifth);
		reNear = (RelativeLayout) findViewById(R.id.reNear);
		
		//left
		drawerlayout = (DrawerLayout) findViewById(R.id.drawerlayout);
		drawerlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
		ivAdd = (ImageView) findViewById(R.id.ivAdd);
		ivAdd.setOnClickListener(this);
		reLeft = (RelativeLayout) findViewById(R.id.reLeft);
		ViewGroup.LayoutParams params = reLeft.getLayoutParams();
		params.width = width-150;
		reLeft.setLayoutParams(params);

		llAboutUs = (LinearLayout) findViewById(R.id.llAboutUs);
		llAboutUs.setOnClickListener(this);
		llFeedBack = (LinearLayout) findViewById(R.id.llFeedBack);
		llFeedBack.setOnClickListener(this);
		llClearCache = (LinearLayout) findViewById(R.id.llClearCache);
		llClearCache.setOnClickListener(this);
		tvCache = (TextView) findViewById(R.id.tvCache);
		llVersion = (LinearLayout) findViewById(R.id.llVersion);
		llVersion.setOnClickListener(this);
		tvVersion = (TextView) findViewById(R.id.tvVersion);
		tvVersion.setText(getVersion());
		
		try {
			String cache = DataCleanManager.getCacheSize(mContext);
			tvCache.setText(cache);
		} catch (Exception e) {
			e.printStackTrace();
		}

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		height = dm.heightPixels;
		density = dm.density;
		
		mRadarManager = new ChartRainManager(mContext);

		if (CommonUtil.isGuangGaoJi()) {
			if (timer == null) {
				timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						refresh();
					}
				}, 0, 1000*60*10);
			}
		}else {
			RelativeLayout.LayoutParams params1 = (LayoutParams) reTitle.getLayoutParams();
			RelativeLayout.LayoutParams params2 = (LayoutParams) reBottom.getLayoutParams();
			params2.setMargins(0, height-params1.height-params2.height, 0, 0);
			reBottom.setLayoutParams(params2);
			refresh();
		}

	}
	
	public Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == CONST.MSG_102) {
				progressBar.setVisibility(View.GONE);
				initAroundGridView();
			}
		};
	};
	
	private ScrollListener scrollListener = new ScrollListener() {
		@Override
		public void onScrollChanged(MyHorizontalScrollView hScrollView, int x, int y, int oldx, int oldy) {
			int scrollX = hScrollView.getScrollX();
			Message msg = new Message();
			msg.what = CONST.MSG_101;
			msg.arg1 = scrollX;
			cubicView.handler.sendMessage(msg);
		}
	};
	
	/**
	 * 获取版本号
	 * @return 当前应用的版本号
	 */
	private String getVersion() {
	    try {
	        PackageManager manager = getPackageManager();
	        PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
	        return info.versionName;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return "";
	    }
	}
	
	/**
	 * 初始化leftListView
	 */
	private void initLeftListView() {
		cityListView = (SwipeMenuListView) findViewById(R.id.cityListView);
		leftAdapter = new LeftAdapter(mContext, leftList);
		cityListView.setAdapter(leftAdapter);
		CommonUtil.setListViewHeightBasedOnChildren(cityListView);
		SwipeMenuCreator creator = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu) {
				switch (menu.getViewType()) {
				case 0:
					createMenu(menu, getString(R.string.delete));
					break;
				case 1:
					break;
				}
			}

			private void createMenu(SwipeMenu menu, String name1) {
				SwipeMenuItem item1 = new SwipeMenuItem(getApplicationContext());
				item1.setBackground(new ColorDrawable(getResources().getColor(R.color.red)));
				item1.setWidth((int)(CommonUtil.dip2px(mContext, 50)));
				item1.setTitle(name1);
				item1.setTitleColor(getResources().getColor(R.color.white));
				item1.setTitleSize(14);
				menu.addMenuItem(item1);
			}
		};
		cityListView.setMenuCreator(creator);
		
		cityListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public void onMenuItemClick(int position, SwipeMenu menu, int index) {
				switch (index) {
				case 0:
					//删除
					leftList.remove(position);
					if (leftAdapter != null) {
						leftAdapter.notifyDataSetChanged();
						CommonUtil.setListViewHeightBasedOnChildren(cityListView);
						saveCitysToLocal();
					}
					break;
				}
			}
		});
		cityListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				final WarningDto dto = leftList.get(arg2);
				if (dto.lng == null || dto.lat == null) {
					return;
				}
				
				if (drawerlayout.isDrawerOpen(reLeft)) {
					drawerlayout.closeDrawer(reLeft);
				}
				
				//抽屉关闭后500毫秒开始请求，避免抽屉回收卡顿
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						//当主界面选择的城市与城市列表选择的城市一致，就不再重新请求一次
						if (!TextUtils.equals(dto.cityId, selectCityId)) {
							//获取选中城市所有信息
							tvTitle.setText(dto.cityName);
							tvArea.setText(dto.cityName);
							selectCityId = dto.cityId;
							lat = dto.lat;
							lng = dto.lng;
							
							//判断是否隶属于天津市，更换背景图片
							isTianJin(dto.cityId);
							queryAllInfo(dto.cityId);
						}
					}
				}, 500);
				
			}
		});
//		cityListView.setOnItemLongClickListener(new OnItemLongClickListener() {
//			@Override
//			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//				// TODO Auto-generated method stub
//				Toast.makeText(mContext, "长按", Toast.LENGTH_SHORT).show();
//				return true;
//			}
//		});
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
			llArea.setVisibility(View.VISIBLE);
			try {
				long zao6 = sdf2.parse("05").getTime();
				long wan8 = sdf2.parse("18").getTime();
				long current = sdf2.parse(sdf2.format(new Date())).getTime();
				if (current >= zao6 && current < wan8) {
					reMain.setBackgroundResource(R.drawable.bg_tianjin_day);
				}else {
					reMain.setBackgroundResource(R.drawable.bg_tianjin_night);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else {
			CONST.ISTIANJIN = false;
			ivEvent.setVisibility(View.GONE);
			llArea.setVisibility(View.INVISIBLE);
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

	@SuppressWarnings("deprecation")
	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (amapLocation != null && amapLocation.getErrorCode() == 0) {
			lat = String.valueOf(amapLocation.getLatitude());
			lng = String.valueOf(amapLocation.getLongitude());
			String name = amapLocation.getAoiName();
        	if (TextUtils.isEmpty(name)) {
        		name = amapLocation.getRoad();
			}
        	tvTitle.setText(name);
        	tvArea.setText(amapLocation.getDistrict());
        	getCityId(amapLocation.getLongitude(), amapLocation.getLatitude());
        	
        	new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					final SharedPreferences sp = getSharedPreferences("VERSION", Context.MODE_PRIVATE);
					String spStoreName = sp.getString("store_name", "");
					if (!TextUtils.isEmpty(spStoreName)) {
						store_name = spStoreName;
					}
					new Thread(new Runnable() {
						@Override
						public void run() {
							//统计安装次数
							String version = sp.getString("version", "");
							if (!TextUtils.equals(version, getVersion())) {
								StatisticUtil.asyncQueryInstall("http://decision-admin.tianqi.cn/Home/Count/installCount?addtime="+sdf1.format(new Date())+"&appid="+CONST.APPID +
										"&mobile_type="+android.os.Build.MODEL.replace(" ", "")+"&newver="+getVersion()+"&oldver="+version+"&os_version="+
										android.os.Build.VERSION.RELEASE+"&platform_type=android"+"&store_name="+store_name);
							}
							Editor editor = sp.edit();
							editor.putString("version", getVersion());
							editor.putString("store_name", store_name);
							editor.commit();
							StatisticUtil.asyncQueryLogin("http://decision-admin.tianqi.cn/home/Work/login", getVersion(), lat, lng);
							StatisticUtil.submitClickCount("1", "首页");
							StatisticUtil.submitClickCount("2", "设置");
						}
					}).start();
				}
			}, 5000);
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
							if (!TextUtils.isEmpty(id) && id.length() >= 9) {
								id = id.substring(0, 9);
							}
							locationCityId = id;
							selectCityId = locationCityId;
							
							//判断是否隶属于天津市，更换背景图片
							isTianJin(locationCityId);
							
							WarningDto locationDto = new WarningDto();
							locationDto.cityId = locationCityId;
							locationDto.lng = String.valueOf(lng);
							locationDto.lat = String.valueOf(lat);
							locationDto.cityName = tvTitle.getText().toString();
							locationDto.cityName2 = tvArea.getText().toString();
							SharedPreferences sharedPreferences = getSharedPreferences("CITYIDS", Context.MODE_PRIVATE);
							String cityInfo = sharedPreferences.getString("cityInfo", null);
							leftList.clear();
							if (!TextUtils.isEmpty(cityInfo)) {//如果本次保存的关注列表不为空
								if (cityInfo.contains(locationCityId)) {//包含定位城市
									String[] info = cityInfo.split(";");
									for (int m = 0; m < info.length; m++) {
										String[] ids = info[m].split(",");
										WarningDto dto = new WarningDto();
										dto.cityId = ids[0];
										dto.cityName = ids[1];
										dto.warningId = ids[2];
										dto.lng = ids[3];
										dto.lat = ids[4];
										if (TextUtils.equals(ids[0], locationCityId)) {
											leftList.add(0, dto);
										}else {
											leftList.add(dto);
										}
									}
									for (int i = 0; i < leftList.size(); i++) {
										getLeftWeatherInfo(leftList.get(i).cityId, leftList.get(i));
									}
								}else {//不包含定位城市
									leftList.add(0, locationDto);
									getLeftWeatherInfo(locationCityId, locationDto);
									
									String[] info = cityInfo.split(";");
									for (int m = 0; m < info.length; m++) {
										String[] ids = info[m].split(",");
										WarningDto dto = new WarningDto();
										dto.cityId = ids[0];
										dto.cityName = ids[1];
										dto.warningId = ids[2];
										dto.lng = ids[3];
										dto.lat = ids[4];
										leftList.add(dto);
										getLeftWeatherInfo(dto.cityId, dto);
									}
								}
							}else {//本次保存的关注列表为空
								leftList.add(0, locationDto);
								getLeftWeatherInfo(locationCityId, locationDto);
							}
							
							//获取定位城市所有信息
							queryAllInfo(locationCityId);
							
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
	 * 获取侧滑页面的天气信息：天气显现、温度
	 * @param cityId
	 * @param dto
	 */
	private void getLeftWeatherInfo(String cityId, final WarningDto dto) {
		if (TextUtils.isEmpty(cityId)) {
			return;
		}
		WeatherAPI.getWeather2(mContext, cityId, Language.ZH_CN, new AsyncResponseHandler() {
			@Override
			public void onComplete(Weather content) {
				super.onComplete(content);
				if (content != null) {
					//实况信息
					JSONObject object = content.getWeatherFactInfo();
					try {
						if (!object.isNull("l1")) {
							dto.temp = WeatherUtil.lastValue(object.getString("l1"));
						}
						if (!object.isNull("l5")) {
							dto.pheCode = WeatherUtil.lastValue(object.getString("l5"));
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					if (leftAdapter != null) {
						leftAdapter.notifyDataSetChanged();
						CommonUtil.setListViewHeightBasedOnChildren(cityListView);
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
		ivShare.setVisibility(View.GONE);
		scrollView.setVisibility(View.INVISIBLE);
		if (!refreshLayout.isRefreshing()) {
			refreshLayout.setRefreshing(true);
		}

		WeatherAPI.getWeather2(mContext, cityId, Language.ZH_CN, new AsyncResponseHandler() {
			@SuppressWarnings("deprecation")
			@Override
			public void onComplete(Weather content) {
				super.onComplete(content);
				if (content != null) {
					JSONObject city = content.getCityInfo();
					double lat = 0, lng = 0;
					try {
						if (!city.isNull("c14")) {
							lat = Double.valueOf(city.getString("c14"));

						}
						if (!city.isNull("c13")) {
							lng = Double.valueOf(city.getString("c13"));
						}

						//获取aqi
						long timestamp = new Date().getTime();
						String start2 = sdf1.format(timestamp);
						String end2 = sdf1.format(timestamp+1000*60*60*24*7);
						asyncQueryAqi(XiangJiManager.getXJDayFore(lng, lat, start2, end2, timestamp), cityId);

						//获取分钟级预报信息
						queryMinute(lng, lat);

						//获取周边城市天气信息
//		queryAroundWeathers(cityId);
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

		//获取预警信息
		queryWarningByCityId(cityId);

		//获取生活指数信息
		initIndexViewPager(cityId);

	}
	
	/**
	 * 异步请求七天aqi
	 */
	private void asyncQueryAqi(String requestUrl, String cityId) {
		HttpAsyncTaskAqi task = new HttpAsyncTaskAqi(cityId);
		task.setMethod("GET");
		task.setTimeOut(CustomHttpClient.TIME_OUT);
		task.execute(requestUrl);
	}
	
	/**
	 * 异步请求方法
	 * @author dell
	 *
	 */
	private class HttpAsyncTaskAqi extends AsyncTask<String, Void, String> {
		private String method = "GET";
		private List<NameValuePair> nvpList = new ArrayList<NameValuePair>();
		private String cityId = null;
		
		public HttpAsyncTaskAqi(String cityId) {
			this.cityId = cityId;
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
					if (!obj.isNull("series")) {
						aqiList.clear();
						JSONArray array = obj.getJSONArray("series");
						for (int i = 0; i < array.length(); i++) {
							WeatherDto data = new WeatherDto();
							data.aqi = String.valueOf(array.get(i));
							aqiList.add(data);
						}
					}
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
			}

			if (!TextUtils.isEmpty(cityId) && TextUtils.equals(cityId.substring(0, 5), CONST.TIANJINCITYID)) {
				CONST.ISTIANJIN = true;
				asyncQueryTianjin("http://211.99.240.5:8080/datafusion/GetRhdata?ID="+cityId);
				getWeatherInfo(cityId);
			}else {
				CONST.ISTIANJIN = false;
				getWeatherInfo(cityId);
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

		@SuppressWarnings("deprecation")
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
							List<WeatherDto> weeklyList = new ArrayList<WeatherDto>();
							for (int i = 0; i < f1.length(); i++) {
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
								
								if (i == 0) {
									tvDay1.setText(getString(R.string.today));
									Drawable drawable = getResources().getDrawable(R.drawable.phenomenon_drawable);
									try {
										long zao6 = sdf2.parse("06").getTime();
										long wan8 = sdf2.parse("18").getTime();
										long current = sdf2.parse(sdf2.format(new Date())).getTime();
										if (current >= zao6 && current < wan8) {
											drawable = getResources().getDrawable(R.drawable.phenomenon_drawable);
											drawable.setLevel(dto.highPheCode);
											tvPhe1.setText(getString(WeatherUtil.getWeatherId(dto.highPheCode)));
										}else {
											drawable = getResources().getDrawable(R.drawable.phenomenon_drawable_night);
											drawable.setLevel(dto.lowPheCode);
											tvPhe1.setText(getString(WeatherUtil.getWeatherId(dto.lowPheCode)));
										}
									} catch (ParseException e) {
										e.printStackTrace();
									}
									if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
										ivPhe1.setBackground(drawable);
									}else {
										ivPhe1.setBackgroundDrawable(drawable);
									}
									
									tvLowTemp1.setText(dto.lowTemp + getString(R.string.unit_degree));
									tvHighTemp1.setText(dto.highTemp + getString(R.string.unit_degree));
								}
								if (i == 1) {
									tvDay2.setText(getString(R.string.tommorrow));
									Drawable drawable = getResources().getDrawable(R.drawable.phenomenon_drawable);
									try {
										long zao6 = sdf2.parse("06").getTime();
										long wan8 = sdf2.parse("18").getTime();
										long current = sdf2.parse(sdf2.format(new Date())).getTime();
										if (current >= zao6 && current < wan8) {
											drawable = getResources().getDrawable(R.drawable.phenomenon_drawable);
											drawable.setLevel(dto.highPheCode);
											tvPhe2.setText(getString(WeatherUtil.getWeatherId(dto.highPheCode)));
										}else {
											drawable = getResources().getDrawable(R.drawable.phenomenon_drawable_night);
											drawable.setLevel(dto.lowPheCode);
											tvPhe2.setText(getString(WeatherUtil.getWeatherId(dto.lowPheCode)));
										}
									} catch (ParseException e) {
										e.printStackTrace();
									}
									if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
										ivPhe2.setBackground(drawable);
									}else {
										ivPhe2.setBackgroundDrawable(drawable);
									}
									
									tvLowTemp2.setText(dto.lowTemp + getString(R.string.unit_degree));
									tvHighTemp2.setText(dto.highTemp + getString(R.string.unit_degree));
								}
							}
							
							WeeklyView weeklyView = new WeeklyView(mContext);
							weeklyView.setData(weeklyList);
							llContainer2.removeAllViews();
							llContainer2.addView(weeklyView, (int)(CommonUtil.dip2px(mContext, width*2/density)), (int)(CommonUtil.dip2px(mContext, 400)));
						}
					}
					
					//逐小时预报信息
					if (!obj.isNull("jh")) {
						JSONArray jh = obj.getJSONArray("jh");
						List<WeatherDto> hourlyList = new ArrayList<WeatherDto>();
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
						
						cubicView = new CubicView(mContext);
						cubicView.setData(hourlyList, width);
						llContainer1.removeAllViews();
						llContainer1.addView(cubicView, (int)(CommonUtil.dip2px(mContext, width*2/density)), (int)(CommonUtil.dip2px(mContext, 200)));
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
				refreshLayout.setRefreshing(false);
				if (content != null) {
					//实况信息
					JSONObject object = content.getWeatherFactInfo();
					try {
						if (!object.isNull("l7")) {
							String time = object.getString("l7");
							if (time != null) {
								time = sdf4.format(new Date())+ " "+time;
								tvTime1.setText(time + getString(R.string.publish));
								tvTime3.setText(time + getString(R.string.publish));
								tvTime4.setText(time + getString(R.string.publish));
								tvTime5.setText(time + getString(R.string.publish));
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
					
					//空气质量
					try {
						JSONObject obj = content.getAirQualityInfo();
						aqiData = new AqiDto();
						if (!obj.isNull("k3")) {
							String num = WeatherUtil.lastValue(obj.getString("k3"));
							if (!TextUtils.isEmpty(num)) {
								if (!TextUtils.isEmpty(num)) {
									aqiData.aqi = num;
									tvQuality.setText(getString(R.string.weather_quality) + "：" +
											WeatherUtil.getAqi(mContext, Integer.valueOf(num)));
								}
							}
						}
						
						if (!obj.isNull("k4")) {
							aqiData.date = WeatherUtil.lastValue(obj.getString("k4"));
						}
						
						if (!obj.isNull("k1")) {
							aqiData.pm2_5 = WeatherUtil.lastValue(obj.getString("k1"));
						}
						
						if (!obj.isNull("k9")) {
							aqiData.pm10 = WeatherUtil.lastValue(obj.getString("k9"));
						}
						
						if (!obj.isNull("k7")) {
							aqiData.NO2 = WeatherUtil.lastValue(obj.getString("k7"));
						}
						
						if (!obj.isNull("k5")) {
							aqiData.SO2 = WeatherUtil.lastValue(obj.getString("k5"));
						}
						
						if (!obj.isNull("k13")) {
							aqiData.O3 = WeatherUtil.lastValue(obj.getString("k13"));
						}
						
						if (!obj.isNull("k11")) {
							aqiData.CO = WeatherUtil.lastValue(obj.getString("k11"));
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					if (!TextUtils.isEmpty(cityId) && TextUtils.equals(cityId.substring(0, 5), CONST.TIANJINCITYID)) {
						CONST.ISTIANJIN = true;
					}else {
						CONST.ISTIANJIN = false;
						//一周预报信息
						try {
							List<WeatherDto> weeklyList = new ArrayList<WeatherDto>();
							//这里只去一周预报，默认为15天，所以遍历7次
							for (int i = 1; i <= 15; i++) {
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
								
								if (i == 1) {
									tvDay1.setText(getString(R.string.today));
									Drawable drawable = getResources().getDrawable(R.drawable.phenomenon_drawable);
									try {
										long zao6 = sdf2.parse("06").getTime();
										long wan8 = sdf2.parse("18").getTime();
										long current = sdf2.parse(sdf2.format(new Date())).getTime();
										if (current >= zao6 && current < wan8) {
											drawable = getResources().getDrawable(R.drawable.phenomenon_drawable);
											drawable.setLevel(dto.highPheCode);
											tvPhe1.setText(getString(WeatherUtil.getWeatherId(dto.highPheCode)));
										}else {
											drawable = getResources().getDrawable(R.drawable.phenomenon_drawable_night);
											drawable.setLevel(dto.lowPheCode);
											tvPhe1.setText(getString(WeatherUtil.getWeatherId(dto.lowPheCode)));
										}
									} catch (ParseException e) {
										e.printStackTrace();
									}
									if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
										ivPhe1.setBackground(drawable);
									}else {
										ivPhe1.setBackgroundDrawable(drawable);
									}
									
									tvLowTemp1.setText(dto.lowTemp + getString(R.string.unit_degree));
									tvHighTemp1.setText(dto.highTemp + getString(R.string.unit_degree));
								}
								if (i == 2) {
									tvDay2.setText(getString(R.string.tommorrow));
									Drawable drawable = getResources().getDrawable(R.drawable.phenomenon_drawable);
									try {
										long zao6 = sdf2.parse("06").getTime();
										long wan8 = sdf2.parse("18").getTime();
										long current = sdf2.parse(sdf2.format(new Date())).getTime();
										if (current >= zao6 && current < wan8) {
											drawable = getResources().getDrawable(R.drawable.phenomenon_drawable);
											drawable.setLevel(dto.highPheCode);
											tvPhe2.setText(getString(WeatherUtil.getWeatherId(dto.highPheCode)));
										}else {
											drawable = getResources().getDrawable(R.drawable.phenomenon_drawable_night);
											drawable.setLevel(dto.lowPheCode);
											tvPhe2.setText(getString(WeatherUtil.getWeatherId(dto.lowPheCode)));
										}
									} catch (ParseException e) {
										e.printStackTrace();
									}
									if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
										ivPhe2.setBackground(drawable);
									}else {
										ivPhe2.setBackgroundDrawable(drawable);
									}
									
									tvLowTemp2.setText(dto.lowTemp + getString(R.string.unit_degree));
									tvHighTemp2.setText(dto.highTemp + getString(R.string.unit_degree));
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
							llContainer2.addView(weeklyView, (int)(CommonUtil.dip2px(mContext, width*2/density)), (int)(CommonUtil.dip2px(mContext, 400)));
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
							cubicView = new CubicView(mContext);
							cubicView.setData(hourlyList, width);
							llContainer1.removeAllViews();
							llContainer1.addView(cubicView, (int)(CommonUtil.dip2px(mContext, width*2/density)), (int)(CommonUtil.dip2px(mContext, 200)));
						} catch (JSONException e) {
							e.printStackTrace();
						} catch (NullPointerException e) {
							e.printStackTrace();
						}
					}

					if (CommonUtil.isGuangGaoJi()) {
						ivShare.setVisibility(View.GONE);
					}else {
						ivShare.setVisibility(View.VISIBLE);
					}
					scrollView.setVisibility(View.VISIBLE);
//					scrollView.scrollTo(0, 0);
				}
			}
			
			@Override
			public void onError(Throwable error, String content) {
				super.onError(error, content);
				refreshLayout.setRefreshing(false);
			}
		});
	}
	
	/**
	 * 获取预警id
	 */
	private void queryWarningByCityId(String cityId) {
		DBManager dbManager = new DBManager(mContext);
		dbManager.openDateBase();
		dbManager.closeDatabase();
		SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/" + DBManager.DB_NAME, null);
		Cursor cursor = null;
		cursor = database.rawQuery("select * from " + DBManager.TABLE_NAME3 + " where cid = " + "\"" + cityId + "\"",null);
		String warningId = "";
		String pro = "";
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToPosition(i);
			warningId = cursor.getString(cursor.getColumnIndex("wid"));
			pro = cursor.getString(cursor.getColumnIndex("pro"));
		}
		if (!TextUtils.isEmpty(warningId)) {
			asyncQueryWarning("http://decision.tianqi.cn/alarm12379/grepalarm2.php?areaid="+warningId.substring(0, 2), pro);
		}
	}
	
	/**
	 * 获取预警信息
	 */
	private void asyncQueryWarning(String requestUrl, String pro) {
		HttpAsyncTask task = new HttpAsyncTask(pro);
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
		private String pro = "";
		
		public HttpAsyncTask(String pro) {
			this.pro = pro;
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
			warningList.clear();
			if (requestResult != null) {
				try {
					JSONObject object = new JSONObject(requestResult);
					if (object != null) {
						String count = null;
						if (!object.isNull("count")) {
							count = object.getString("count");
							if (!TextUtils.equals(count, "0")) {
								if (!object.isNull("data")) {
									JSONArray jsonArray = object.getJSONArray("data");
									int size = jsonArray.length();
									for (int i = 0; i < size; i++) {
										JSONArray tempArray = jsonArray.getJSONArray(i);
										WarningDto data = new WarningDto();
										data.html = tempArray.optString(1);
										String[] array = data.html.split("-");
										String item0 = array[0];
										String item1 = array[1];
										String item2 = array[2];
										data.item0 = item0;
										data.provinceId = item0.substring(0, 2);
										data.type = item2.substring(0, 5);
										data.color = item2.substring(5, 7);
										data.time = item1;
										data.lng = tempArray.optString(2);
										data.lat = tempArray.optString(3);
										data.name = tempArray.optString(0);
										if (!data.name.contains("解除")) {
											warningList.add(data);
										}
									}
									
									if (warningList.size() > 0) {
										tvWarning.setText(pro + getString(R.string.gong)+getString(R.string.publish)+warningList.size()+getString(R.string.tiao) + getString(R.string.warning));
										tvWarning.setVisibility(View.VISIBLE);
									}else {
										tvWarning.setText(getString(R.string.no_warning));
										tvWarning.setVisibility(View.VISIBLE);
									}
								}
							}else {
								tvWarning.setText(getString(R.string.no_warning));
								tvWarning.setVisibility(View.VISIBLE);
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}else {
				tvWarning.setText(getString(R.string.no_warning));
				tvWarning.setVisibility(View.GONE);
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
	 * 异步加载一小时内降雨、或降雪信息
	 * @param lng
	 * @param lat
	 */
	private void queryMinute(double lng, double lat) {
		String url = "http://api.caiyunapp.com/v2/HyTVV5YAkoxlQ3Zd/"+lng+","+lat+"/forecast";
		HttpAsyncTaskMinute task = new HttpAsyncTaskMinute();
		task.setMethod("GET");
		task.setTimeOut(CustomHttpClient.TIME_OUT);
		task.execute(url);
	}
	
	/**
	 * 异步请求方法
	 * @author dell
	 *
	 */
	private class HttpAsyncTaskMinute extends AsyncTask<String, Void, String> {
		private String method = "GET";
		private List<NameValuePair> nvpList = new ArrayList<NameValuePair>();
		
		public HttpAsyncTaskMinute() {
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
						if (!object.isNull("server_time")) {
							long t = object.getLong("server_time");
							Date date = new Date(t*1000);
							tvTime2.setText(sdf3.format(date)+getString(R.string.publish));
						}
						if (!object.isNull("result")) {
							JSONObject obj = object.getJSONObject("result");
							if (!obj.isNull("minutely")) {
								JSONObject objMin = obj.getJSONObject("minutely");
								if (!objMin.isNull("description")) {
									String rain = objMin.getString("description");
									if (!TextUtils.isEmpty(rain)) {
										tvRain.setText(rain.replace(getString(R.string.little_caiyun), ""));
										if (tvRain.getText().toString().contains("雪")) {
											tvMinetePrompt.setText(getString(R.string.minute_snowfall));
										}else {
											tvMinetePrompt.setText(getString(R.string.minute_fall));
										}
										
										if (reSecond.getVisibility() == View.VISIBLE) {
											tvRain.setVisibility(View.VISIBLE);
										}else {
											tvRain.setVisibility(View.GONE);
										}
									}else {
										tvRain.setVisibility(View.GONE);
									}
								}
								if (!objMin.isNull("precipitation")) {
									JSONArray array = objMin.getJSONArray("precipitation");
									int size = array.length();
									List<WeatherDto> minuteList = new ArrayList<WeatherDto>();
									for (int i = 0; i < size; i++) {
										WeatherDto dto = new WeatherDto();
										dto.minuteFall = (float) array.getDouble(i);
//										dto.minuteFall = new Random().nextFloat();
										minuteList.add(dto);
									}
									
									MinuteFallView minuteFallView = new MinuteFallView(mContext);
									minuteFallView.setData(minuteList, tvRain.getText().toString());
									llContainer3.removeAllViews();
									llContainer3.addView(minuteFallView, (int)(CommonUtil.dip2px(mContext, width/density)), (int)(CommonUtil.dip2px(mContext, 150)));
								}
							}
						}
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
	 * 初始化生活指数viewPager
	 */
	private void initIndexViewPager(String cityId) {
		fragments.clear();
		int size = 5;
		for (int i = 0; i < size; i++) {
			Fragment fragment = new IndexFragment();
			Bundle bundle = new Bundle();
			bundle.putInt("index", i);
			bundle.putString("cityId", cityId);
			fragment.setArguments(bundle);
			fragments.add(fragment);
		}
		
		ivTips = new ImageView[size];
		viewGroup.removeAllViews();
		for (int i = 0; i < size; i++) {
			ImageView imageView = new ImageView(mContext);
			imageView.setLayoutParams(new LayoutParams(5, 5));  
			ivTips[i] = imageView;  
			if(i == 0){  
				ivTips[i].setBackgroundResource(R.drawable.point_white);  
			}else{  
				ivTips[i].setBackgroundResource(R.drawable.point_gray);  
			}  
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));  
			layoutParams.leftMargin = 15;  
			layoutParams.rightMargin = 15;  
			viewGroup.addView(imageView, layoutParams);  
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
			for (int i = 0; i < fragments.size(); i++) {
				if(i == arg0){  
					ivTips[i].setBackgroundResource(R.drawable.point_white);  
				}else{  
					ivTips[i].setBackgroundResource(R.drawable.point_gray);  
				} 
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
			viewPager.setCurrentItem(index);
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
//				ft.commit();
				ft.commitAllowingStateLoss();
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
	 * 初始化周边天气gridview
	 */
	private void initAroundGridView() {
		aroundGridView = (GridView) findViewById(R.id.aroundGridView);
		aroundAdapter = new AroundWeatherAdapter(mContext, aroundList);
		aroundGridView.setAdapter(aroundAdapter);
		
		android.view.ViewGroup.LayoutParams params = aroundGridView.getLayoutParams();
		int size = aroundList.size();
		if (size > 0 && size <= 2) {
			params.height = (int) CommonUtil.dip2px(mContext, 60);
		}else if (size > 2 && size <= 4) {
			params.height = (int) CommonUtil.dip2px(mContext, 120);
		}else if (size > 4) {
			params.height = (int) CommonUtil.dip2px(mContext, 180);
		}
		aroundGridView.setLayoutParams(params);
	}
	
	/**
	 * 查询周边天气信息
	 */
	private void queryAroundWeathers(final String cityId) {
		if (TextUtils.isEmpty(cityId)) {
			return;
		}
		progressBar.setVisibility(View.VISIBLE);
		new AsyncTask<Object, Object, Object>() {
			@Override
			protected Object doInBackground(Object... params) {
				if (TextUtils.isEmpty(cityId) || cityId.length() < 9) {
					return null;
				}
				try {
					InputStreamReader isr = new InputStreamReader(getResources().getAssets().open("around/surround.txt"));
					BufferedReader reader = new BufferedReader(isr);
					String line = null;
					while (!TextUtils.isEmpty((line = reader.readLine()))) {
						if (line.startsWith(cityId.substring(0, 9))) {
							break;
						}
					}
					isr.close();
					reader.close();
					isr = null;
					reader = null;
					if (TextUtils.isEmpty(line)) {
						line = cityId;
					}
					String[] ids = line.split(",");
					int len = ids.length >= 6 ? 6 : ids.length;
					List<String> cityIds = new ArrayList<String>();
					for (int i = 0; i < len; i ++) {
						if (!TextUtils.isEmpty(ids[i])) {
							cityIds.add(ids[i]);
						}
					}
					WeatherAPI.getWeathers2(mContext, cityIds, Language.ZH_CN, new AsyncResponseHandler() {
						@Override
						public void onComplete(List<Weather> content) {
							super.onComplete(content);
							try {
								int size = content.size();
								aroundList.clear();
								for (int i = 0; i < size; i++) {
									WeatherDto dto = new WeatherDto();
									Weather weather = content.get(i);
									JSONObject city = weather.getCityInfo();
									if (city == null) {
										continue;
									}
									dto.cityName = city.getString("c3");
									
									//实况信息
									JSONObject object = weather.getWeatherFactInfo();
									try {
										if (!object.isNull("l5")) {
											String weatherCode = WeatherUtil.lastValue(object.getString("l5"));
											if (!TextUtils.isEmpty(weatherCode)) {
												dto.lowPheCode = Integer.valueOf(weatherCode);
											}
										}
										if (!object.isNull("l1")) {
											String factTemp = WeatherUtil.lastValue(object.getString("l1"));
											if (!TextUtils.isEmpty(factTemp)) {
												dto.lowTemp = Integer.valueOf(factTemp);
											}
										}
										aroundList.add(dto);
									} catch (JSONException e) {
										e.printStackTrace();
									}
									
								}
								
								Message msg = new Message();
								msg.what = CONST.MSG_102;
								handler.sendMessage(msg);
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
						
						@Override
						public void onError(Throwable error, String content) {
							super.onError(error, content);
						}
					});
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}
		}.execute();
	}
	
	/**
	 * 保存关注的城市列表数据到本地
	 */
	private void saveCitysToLocal() {
		String cityInfo = "";
		for (int i = 1; i < leftList.size(); i++) {//从1开始是为了过滤掉定位城市
			cityInfo += (leftList.get(i).cityId+","+leftList.get(i).cityName+","+leftList.get(i).warningId+","+leftList.get(i).lng+","+leftList.get(i).lat+";");
		}
		
		//保存所有的城市id
		SharedPreferences sharedPreferences = getSharedPreferences("CITYIDS", Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putString("cityInfo", cityInfo);
		editor.commit();
		Log.e("cityInfo", cityInfo);
	}
	
	/**
	 * 删除对话框
	 * @param message 标题
	 * @param content 内容
	 * @param flag 0删除本地存储，1删除缓存
	 */
	private void deleteDialog(final boolean flag, String message, String content, final TextView textView) {
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.delete_dialog, null);
		TextView tvMessage = (TextView) view.findViewById(R.id.tvMessage);
		TextView tvContent = (TextView) view.findViewById(R.id.tvContent);
		LinearLayout llNegative = (LinearLayout) view.findViewById(R.id.llNegative);
		LinearLayout llPositive = (LinearLayout) view.findViewById(R.id.llPositive);
		
		final Dialog dialog = new Dialog(mContext, R.style.CustomProgressDialog);
		dialog.setContentView(view);
		dialog.show();
		
		tvMessage.setText(message);
		tvContent.setText(content);
		tvContent.setVisibility(View.VISIBLE);
		llNegative.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		
		llPositive.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (flag) {
					DataCleanManager.clearCache(mContext);
					try {
						String cache = DataCleanManager.getCacheSize(mContext);
						if (cache != null) {
							textView.setText(cache);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else {
//					ChannelsManager.clearData(mContext);//清除保存在本地的频道数据
					DataCleanManager.clearLocalSave(mContext);
					try {
						String data = DataCleanManager.getLocalSaveSize(mContext);
						if (data != null) {
							textView.setText(data);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				dialog.dismiss();
			}
		});
	}
	
	/**
	 * 请求新添加城市信息
	 * @param intent
	 */
	private void queryAddCityInfo(Intent intent) {
		if (intent != null) {
			CityDto data = intent.getExtras().getParcelable("data");
			boolean isAdded = intent.getExtras().getBoolean("isAdded");
			
			tvTitle.setText(data.disName);
			tvArea.setText(data.disName);
			selectCityId = data.cityId;
			
			//判断是否隶属于天津市，更换背景图片
			isTianJin(data.cityId);
			
			WarningDto dto = new WarningDto();
			dto.cityId = data.cityId;
			dto.cityName = data.disName;
			dto.warningId = data.warningId;
			if (isAdded == false) {
				leftList.add(dto);
			}

			getLeftWeatherInfo(data.cityId, dto);
			queryAllInfo(data.cityId);
			saveCitysToLocal();

		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (drawerlayout.isDrawerOpen(reLeft)) {
				drawerlayout.closeDrawer(reLeft);
			}else {
				if ((System.currentTimeMillis() - mExitTime) > 2000) {
					Toast.makeText(mContext, getString(R.string.confirm_exit)+getString(R.string.app_name), Toast.LENGTH_SHORT).show();
					mExitTime = System.currentTimeMillis();
				} else {
					finish();
				}
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivMenu:
			if (drawerlayout.isDrawerOpen(reLeft)) {
				drawerlayout.closeDrawer(reLeft);
			}else {
				drawerlayout.openDrawer(reLeft);
			}
			break;
		case R.id.ivShare:
//			if (scrollView.getVisibility() == View.VISIBLE) {
//				Bitmap bitmap1 = null;
//				Bitmap bitmap2 = null;
//				//判断是否隶属于天津市，更换背景图片
//				if (TextUtils.equals(selectCityId.substring(0, 5), CONST.TIANJINCITYID)) {
//					CONST.ISTIANJIN = true;
//					refreshLayout.setColorSchemeResources(CONST.color11, CONST.color22, CONST.color33, CONST.color44);
//					try {
//						long zao6 = sdf2.parse("05").getTime();
//						long wan8 = sdf2.parse("18").getTime();
//						long current = sdf2.parse(sdf2.format(new Date())).getTime();
//						if (current >= zao6 && current < wan8) {
//							bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.bg_tianjin_day);
//							bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.bg_tianjin_day_color);
//						}else {
//							bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.bg_tianjin_night);
//							bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.bg_tianjin_day_color);
//						}
//					} catch (ParseException e) {
//						e.printStackTrace();
//					}
//				}else {
//					CONST.ISTIANJIN = false;
//					refreshLayout.setColorSchemeResources(CONST.color1, CONST.color2, CONST.color3, CONST.color4);
//					try {
//						long zao6 = sdf2.parse("05").getTime();
//						long wan8 = sdf2.parse("18").getTime();
//						long current = sdf2.parse(sdf2.format(new Date())).getTime();
//						if (current >= zao6 && current < wan8) {
//							bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.bg_else_city_day);
//							bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.bg_else_city_day_color);
//						}else {
//							bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.bg_else_city_night);
//							bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.bg_else_city_night_color);
//						}
//					} catch (ParseException e) {
//						e.printStackTrace();
//					}
//				}
//				Bitmap bitmap3 = CommonUtil.mergeBitmap(MainActivity.this, bitmap1, bitmap2, false);
//				CommonUtil.clearBitmap(bitmap1);
//				CommonUtil.clearBitmap(bitmap2);
//				Bitmap bitmap4 = CommonUtil.captureView(reTitle);
//				Bitmap bitmap5 = CommonUtil.captureView(reFirst);
//				Bitmap bitmap6 = CommonUtil.mergeBitmap(MainActivity.this, bitmap4, bitmap5, false);
//				CommonUtil.clearBitmap(bitmap4);
//				CommonUtil.clearBitmap(bitmap5);
//				
//				Bitmap bitmap7 = null;
//				Bitmap bitmap8 = null;
//				Bitmap bitmap9 = null;
//				Bitmap bitmap10 = null;
//				Bitmap bitmap11 = null;
//				Bitmap bitmap12 = null;
//				if (reSecond.getVisibility() == View.VISIBLE) {
//					bitmap7 = CommonUtil.captureView(reSecond);
//					bitmap8 = CommonUtil.mergeBitmap(MainActivity.this, bitmap6, bitmap7, false);
//					CommonUtil.clearBitmap(bitmap6);
//					CommonUtil.clearBitmap(bitmap7);
//					bitmap9 = CommonUtil.captureView(llContainer3);
//					bitmap10 = CommonUtil.mergeBitmap(MainActivity.this, bitmap8, bitmap9, false);
//					CommonUtil.clearBitmap(bitmap8);
//					CommonUtil.clearBitmap(bitmap9);
//					bitmap11 = CommonUtil.captureView(reThird);
//					bitmap12 = CommonUtil.mergeBitmap(MainActivity.this, bitmap10, bitmap11, false);
//					CommonUtil.clearBitmap(bitmap10);
//					CommonUtil.clearBitmap(bitmap11);
//				}else {
//					bitmap11 = CommonUtil.captureView(reThird);
//					bitmap12 = CommonUtil.mergeBitmap(MainActivity.this, bitmap6, bitmap11, false);
//					CommonUtil.clearBitmap(bitmap6);
//					CommonUtil.clearBitmap(bitmap11);
//				}
//				
//				Bitmap bitmap13 = CommonUtil.captureView(llContainer2);
//				if (hScrollView2.getVisibility() == View.VISIBLE) {
//					bitmap13 = CommonUtil.captureView(llContainer2);
//				}else {
//					bitmap13 = CommonUtil.captureView(llContainer1);
//				}
//				Bitmap bitmap14 = CommonUtil.mergeBitmap(MainActivity.this, bitmap12, bitmap13, false);
//				CommonUtil.clearBitmap(bitmap12);
//				CommonUtil.clearBitmap(bitmap13);
//				Bitmap bitmap15 = CommonUtil.captureView(reFourth);
//				Bitmap bitmap16 = CommonUtil.mergeBitmap(MainActivity.this, bitmap14, bitmap15, false);
//				CommonUtil.clearBitmap(bitmap14);
//				CommonUtil.clearBitmap(bitmap15);
//				Bitmap bitmap17 = CommonUtil.captureView(viewPager);
//				Bitmap bitmap18 = CommonUtil.mergeBitmap(MainActivity.this, bitmap16, bitmap17, false);
//				CommonUtil.clearBitmap(bitmap16);
//				CommonUtil.clearBitmap(bitmap17);
//				Bitmap bitmap19 = CommonUtil.captureView(viewGroup);
//				Bitmap bitmap20 = CommonUtil.mergeBitmap(MainActivity.this, bitmap18, bitmap19, false);
//				CommonUtil.clearBitmap(bitmap18);
//				CommonUtil.clearBitmap(bitmap19);
//				Bitmap bitmap21 = CommonUtil.captureView(reFifth);
//				Bitmap bitmap22 = CommonUtil.mergeBitmap(MainActivity.this, bitmap20, bitmap21, false);
//				CommonUtil.clearBitmap(bitmap20);
//				CommonUtil.clearBitmap(bitmap21);
//				Bitmap bitmap23 = CommonUtil.captureView(reNear);
//				Bitmap bitmap24 = CommonUtil.mergeBitmap(MainActivity.this, bitmap22, bitmap23, false);
//				CommonUtil.clearBitmap(bitmap22);
//				CommonUtil.clearBitmap(bitmap23);
//				bitmap3 = Bitmap.createScaledBitmap(bitmap3, bitmap3.getWidth(), bitmap24.getHeight(), true);
//				Bitmap bitmap25 = CommonUtil.mergeBitmap(MainActivity.this, bitmap3, bitmap24, true);
//				CommonUtil.clearBitmap(bitmap3);
//				CommonUtil.clearBitmap(bitmap24);
//				Bitmap bitmap26 = BitmapFactory.decodeResource(getResources(), R.drawable.iv_share_bottom);
//				Bitmap bitmap = CommonUtil.mergeBitmap(MainActivity.this, bitmap25, bitmap26, false);
//				CommonUtil.clearBitmap(bitmap25);
//				CommonUtil.clearBitmap(bitmap26);
//				if (bitmap != null) {
//					File file = new File(CONST.filePath);
//					try {
//						FileOutputStream os = new FileOutputStream(file);
//						bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
//						os.flush();
//						os.close();
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//					CommonUtil.clearBitmap(bitmap);
//					
//					if (file.exists()) {
//						BitmapFactory.Options options = new BitmapFactory.Options();
//						options.inSampleSize = 2;
//						bitmap = BitmapFactory.decodeFile(CONST.filePath, options);
//						if (bitmap != null) {
//							try {
//								FileOutputStream os = new FileOutputStream(file);
//								bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
//								os.flush();
//								os.close();
//							} catch (Exception e) {
//								e.printStackTrace();
//							}
//							CommonUtil.clearBitmap(bitmap);
//						}
//						CommonUtil.share(MainActivity.this, file);
//					}
//				}
//			}
			
			Bitmap bitmap1 = CommonUtil.captureView(reTitle);
			Bitmap bitmap2 = CommonUtil.captureView(reFirst);
			Bitmap bitmap3 = CommonUtil.mergeBitmap(mContext, bitmap1, bitmap2, false);
			CommonUtil.clearBitmap(bitmap1);
			CommonUtil.clearBitmap(bitmap2);
			Bitmap bitmap4 = BitmapFactory.decodeResource(getResources(), R.drawable.iv_share_bottom);
			Bitmap bitmap5 = CommonUtil.mergeBitmap(mContext, bitmap3, bitmap4, false);
			CommonUtil.clearBitmap(bitmap3);
			CommonUtil.clearBitmap(bitmap4);
			Bitmap bitmap6 = null;
			//判断是否隶属于天津市，更换背景图片
			if (!TextUtils.isEmpty(selectCityId) && TextUtils.equals(selectCityId.substring(0, 5), CONST.TIANJINCITYID)) {
				CONST.ISTIANJIN = true;
				try {
					long zao6 = sdf2.parse("05").getTime();
					long wan8 = sdf2.parse("18").getTime();
					long current = sdf2.parse(sdf2.format(new Date())).getTime();
					if (current >= zao6 && current < wan8) {
						bitmap6 = BitmapFactory.decodeResource(getResources(), R.drawable.bg_tianjin_day);
					}else {
						bitmap6 = BitmapFactory.decodeResource(getResources(), R.drawable.bg_tianjin_night);
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}else {
				CONST.ISTIANJIN = false;
				try {
					long zao6 = sdf2.parse("05").getTime();
					long wan8 = sdf2.parse("18").getTime();
					long current = sdf2.parse(sdf2.format(new Date())).getTime();
					if (current >= zao6 && current < wan8) {
						bitmap6 = BitmapFactory.decodeResource(getResources(), R.drawable.bg_else_city_day);
					}else {
						bitmap6 = BitmapFactory.decodeResource(getResources(), R.drawable.bg_else_city_night);
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			Bitmap bitmap = CommonUtil.mergeBitmap(mContext, bitmap6, bitmap5, true);
			CommonUtil.clearBitmap(bitmap5);
			CommonUtil.clearBitmap(bitmap6);
			CommonUtil.share(MainActivity.this, bitmap);
			break;
		case R.id.tvQuality:
			Intent intentQ = new Intent(mContext, QualityActivity.class);
			intentQ.putExtra("lat", lat);
			intentQ.putExtra("lng", lng);
			intentQ.putExtra("cityId", selectCityId);
			intentQ.putExtra("cityName", tvTitle.getText().toString());
			intentQ.putExtra("aqiData", aqiData);
			startActivity(intentQ);
			break;
		case R.id.tvWarning:
			int size = warningList.size();
			if (size > 0) {
				Intent intent = new Intent(mContext, WarningActivity.class);
				intent.putExtra("cityId", selectCityId);
				intent.putExtra("lat", lat);
				intent.putExtra("lng", lng);
				Bundle bundle = new Bundle();
				bundle.putParcelableArrayList("warningList", (ArrayList<? extends Parcelable>) warningList);
				intent.putExtras(bundle);
				startActivity(intent);
			}
			break;
		case R.id.tvRoute:
			startActivity(new Intent(mContext, RouteWeatherActivity.class));
			break;
		case R.id.tvMonitor:
			startActivity(new Intent(mContext, StationMonitorActivity.class));
			break;
		case R.id.ivEvent:
			Intent intent1 = new Intent(mContext, QuanyunhuiActivity.class);
			intent1.putExtra(CONST.ACTIVITY_NAME, eventName);
			intent1.putExtra(CONST.WEB_URL, eventUrl);
			startActivity(intent1);
			break;
		case R.id.ivWaitWind:
			startActivity(new Intent(mContext, WaitWindActivity.class));
			break;
		case R.id.llArea:
			Intent intentArea = new Intent(mContext, AreaActivity.class);
			intentArea.putExtra("locationCityId", locationCityId);
			intentArea.putExtra("attentionCount", attentionCount);
			intentArea.putExtra("size", leftList.size());
			startActivityForResult(intentArea, 1);
			break;
		case R.id.reMinute:
			if (reSecond.getVisibility() == View.VISIBLE) {
				tvRain.setVisibility(View.GONE);
				reSecond.setVisibility(View.GONE);
				ivClose.setImageResource(R.drawable.iv_open);
			}else {
				tvRain.setVisibility(View.VISIBLE);
				reSecond.setVisibility(View.VISIBLE);
				ivClose.setImageResource(R.drawable.iv_close);
			}
			break;
		case R.id.tvFifteen:
			tvFifteen.setTextColor(getResources().getColor(R.color.white));
			tvHourly.setTextColor(0x90ffffff);
			hScrollView1.setVisibility(View.GONE);
			hScrollView2.setVisibility(View.VISIBLE);
			break;
		case R.id.tvHourly:
			tvFifteen.setTextColor(0x90ffffff);
			tvHourly.setTextColor(getResources().getColor(R.color.white));
			hScrollView1.setVisibility(View.VISIBLE);
			hScrollView2.setVisibility(View.GONE);
			break;
			
			//侧拉页面
		case R.id.ivAdd:
			if (leftList.size() >= attentionCount) {
				Toast.makeText(mContext, getString(R.string.most_add_city), Toast.LENGTH_SHORT).show();
				return;
			}else {
				Intent intent = new Intent(mContext, CityActivity.class);
				intent.putExtra("locationCityId", locationCityId);
				startActivityForResult(intent, 0);
			}
			break;
		case R.id.llAboutUs:
			startActivity(new Intent(mContext, AboutUsActivity.class));
			break;
		case R.id.llFeedBack:
			startActivity(new Intent(mContext, FeedbackActivity.class));
			break;
		case R.id.llClearCache:
			deleteDialog(true, getString(R.string.delete_cache), getString(R.string.sure_delete_cache), tvCache);
			break;
		case R.id.llVersion:
			AutoUpdateUtil.checkUpdate(mContext, "55", getString(R.string.app_name), false);
			break;

		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 0:
			case 1:
				queryAddCityInfo(data);
				break;

			default:
				break;
			}
		}
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
