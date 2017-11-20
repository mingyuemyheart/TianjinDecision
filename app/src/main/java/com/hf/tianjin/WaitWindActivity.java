package com.hf.tianjin;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hf.tianjin.utils.StatisticUtil;

/**
 * 等风来
 * @author shawn_sun
 *
 */

@SuppressLint("SetJavaScriptEnabled")
public class WaitWindActivity extends BaseActivity implements OnClickListener{
	
	private LinearLayout llBack = null;
	private TextView tvTitle = null;
	private WebView webView = null;
	private WebSettings webSettings = null;
	private String url = "http://www.welife100.com/Wap/Fengc/index";
	private TextView tvRefresh = null;
	private ProgressBar progressBar = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wait_wind);
		initWidget();
		initWebView();
	}
	
	/**
	 * 初始化控件
	 */
	private void initWidget() {
		llBack = (LinearLayout) findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setText("等风来");
		tvRefresh = (TextView) findViewById(R.id.tvRefresh);
		tvRefresh.setOnClickListener(this);
		tvRefresh.setVisibility(View.GONE);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		progressBar.setVisibility(View.VISIBLE);

		StatisticUtil.submitClickCount("7", "等风来");
	}

	/**
	 * 初始化webview
	 */
	private void initWebView() {
		webView = (WebView) findViewById(R.id.webView);
		webSettings = webView.getSettings();
		
		//支持javascript
		webSettings.setJavaScriptEnabled(true); 
		// 设置可以支持缩放 
		webSettings.setSupportZoom(true); 
		// 设置出现缩放工具 
		webSettings.setBuiltInZoomControls(true);
		//扩大比例的缩放
		webSettings.setUseWideViewPort(true);
		//自适应屏幕
		webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		webSettings.setLoadWithOverviewMode(true);
		String header = "Mozilla/5.0 (Linux; U; Android 2.3.6; zh-cn; GT-S5660 Build/GINGERBREAD) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1 MicroMessenger/4.5.255";
		webSettings.setUserAgentString("MicroMessenger");
		webView.loadUrl(url);
		
		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
//				if (title != null) {
//					tvTitle.setText(title);
//				}
			}
		});
		
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String itemUrl) {
				url = itemUrl;
				webView.loadUrl(url);
				return true;
			}
			
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				progressBar.setVisibility(View.GONE);
				tvRefresh.setVisibility(View.VISIBLE);
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (webView != null && webView.canGoBack()) {
				webView.goBack();
				return true;
			}else {
				finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.llBack:
			finish();
			break;
		case R.id.tvRefresh:
			progressBar.setVisibility(View.VISIBLE);
			tvRefresh.setVisibility(View.GONE);
			if (webSettings != null && webView != null) {
				webSettings.setUserAgentString("MicroMessenger");
				webView.loadUrl(url);
			}
			break;

		default:
			break;
		}
	}
	
}
