package com.hf.tianjin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;

public class WelcomeActivity extends BaseActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		startIntentMain();
	}
	
	private void startIntentMain() {
		Handler handler = new Handler();
		handler.postDelayed(new MainRunnable(), 2000);
	}
	
	private class MainRunnable implements Runnable{
		@Override
		public void run() {
			startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
			finish();
		}
	}
	
	@Override
	public boolean onKeyDown(int KeyCode, KeyEvent event){
		if (KeyCode == KeyEvent.KEYCODE_BACK){
			return true;
		}
		return super.onKeyDown(KeyCode, event);
	}
	
}
