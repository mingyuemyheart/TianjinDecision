package com.hf.tianjin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hf.tianjin.common.CONST;

/**
 * 普通url，处理网页界面
 * @author shawn_sun
 *
 */

public class QuanyunhuiActivity extends Activity implements OnClickListener{
	
	private LinearLayout llBack = null;
	private TextView tvTitle = null;
	private WebView webView = null;
	private WebSettings webSettings = null;
	private String url = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.quanyunhui);
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
		
		String title = getIntent().getStringExtra(CONST.ACTIVITY_NAME);
		if (!TextUtils.isEmpty(title)) {
			tvTitle.setText(title);
		}else {
			tvTitle.setText(getString(R.string.detail));
		}
	}
	
	/**
	 * 初始化webview
	 */
	@SuppressLint("SetJavaScriptEnabled") 
	private void initWebView() {
		url = getIntent().getStringExtra(CONST.WEB_URL);
		if (url == null) {
			return;
		}
		
		webView = (WebView) findViewById(R.id.webView);
		webSettings = webView.getSettings();
		
		//支持javascript
		webSettings.setJavaScriptEnabled(true); 
		// 设置可以支持缩放 
		webSettings.setSupportZoom(true); 
		// 设置出现缩放工具 
		webSettings.setBuiltInZoomControls(true);
		webSettings.setDisplayZoomControls(false);
		//扩大比例的缩放
		webSettings.setUseWideViewPort(true);
		//自适应屏幕
		webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		webSettings.setLoadWithOverviewMode(false);
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		//打开DOM存储API
		webSettings.setDomStorageEnabled(true);
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
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (webView.canGoBack()) {
				webView.goBack();
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
			if (webView.canGoBack()) {
				webView.goBack();
			}else {
				finish();
			}
			break;

		default:
			break;
		}
	}
	
}
