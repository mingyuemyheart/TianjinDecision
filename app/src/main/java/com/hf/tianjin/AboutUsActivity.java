package com.hf.tianjin;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hf.tianjin.common.CONST;

/**
 * 关于我们
 * @author shawn_sun
 *
 */

public class AboutUsActivity extends BaseActivity implements OnClickListener{
	
	private Context mContext = null;
	private LinearLayout llBack = null;
	private TextView tvTitle = null;
	private TextView tvServer1 = null;
	private TextView tvServer2 = null;
	private TextView tvServer3 = null;
	private TextView tvEmail = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_us);
		mContext = this;
		initWidget();
	}
	
	private void initWidget() {
		llBack = (LinearLayout) findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setText(getString(R.string.setting_about));
		tvServer1 = (TextView) findViewById(R.id.tvServer1);
		tvServer1.setOnClickListener(this);
		tvServer2 = (TextView) findViewById(R.id.tvServer2);
		tvServer2.setOnClickListener(this);
		tvServer3 = (TextView) findViewById(R.id.tvServer3);
		tvServer3.setOnClickListener(this);
		tvEmail = (TextView) findViewById(R.id.tvEmail);
		tvEmail.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.llBack:
			finish();
			break;
		case R.id.tvServer1:
			Intent intent1 = new Intent(mContext, WebViewActivity.class);
			intent1.putExtra(CONST.ACTIVITY_NAME, getString(R.string.addr_server1));
			intent1.putExtra(CONST.WEB_URL, "http://www.weather.tj.cn");
			startActivity(intent1);
			break;
		case R.id.tvServer2:
			Intent intent2 = new Intent(mContext, WebViewActivity.class);
			intent2.putExtra(CONST.ACTIVITY_NAME, getString(R.string.addr_server2));
			intent2.putExtra(CONST.WEB_URL, "http://www.tjsqqx.cn");
			startActivity(intent2);
			break;
		case R.id.tvServer3:
			Intent intent3 = new Intent(mContext, WebViewActivity.class);
			intent3.putExtra(CONST.ACTIVITY_NAME, getString(R.string.addr_server3));
			intent3.putExtra(CONST.WEB_URL, "http://tj.weather.com.cn/");
			startActivity(intent3);
			break;
		case R.id.tvEmail:
			Intent intent4 = new Intent(Intent.ACTION_SENDTO);
			intent4.setData(Uri.parse("mailto:"+tvEmail.getText().toString()));
			startActivity(intent4);
			break;

		default:
			break;
		}
	}
	
}
