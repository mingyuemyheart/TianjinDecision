package com.hf.tianjin.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.hf.tianjin.common.CONST;

/**
 * 统计类
 * @author shawn_sun
 *
 */

public class StatisticUtil {
	
	/**
	 * 统计安装信息
	 */
	public static void asyncQueryInstall(String requestUrl) {
		HttpAsyncTaskInstall task = new HttpAsyncTaskInstall();
		task.setMethod("GET");
		task.setTimeOut(CustomHttpClient.TIME_OUT);
		task.execute(requestUrl);
	}
	
	/**
	 * 异步请求方法
	 * @author dell
	 *
	 */
	public static class HttpAsyncTaskInstall extends AsyncTask<String, Void, String> {
		private String method = "GET";
		private List<NameValuePair> nvpList = new ArrayList<NameValuePair>();
		
		public HttpAsyncTaskInstall() {
			transParams();
		}
		
		/**
		 * 传参数
		 */
		private void transParams() {
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
	 * 统计登录信息
	 */
	public static void asyncQueryLogin(String requestUrl, String version, String lat, String lng) {
		HttpAsyncTaskLogin task = new HttpAsyncTaskLogin(version, lat, lng);
		task.setMethod("POST");
		task.setTimeOut(CustomHttpClient.TIME_OUT);
		task.execute(requestUrl);
	}
	
	/**
	 * 异步请求方法
	 * @author dell
	 *
	 */
	public static class HttpAsyncTaskLogin extends AsyncTask<String, Void, String> {
		private String method = "POST";
		private List<NameValuePair> nvpList = new ArrayList<NameValuePair>();
		private String version = null;
		private String lat = null;
		private String lng = null;
		
		public HttpAsyncTaskLogin(String version, String lat, String lng) {
			this.version = version;
			this.lat = lat;
			this.lng = lng;
			transParams();
		}
		
		/**
		 * 传参数
		 */
		private void transParams() {
			NameValuePair pair1 = new BasicNameValuePair("username", CONST.USERNAME);
	        NameValuePair pair2 = new BasicNameValuePair("password", CONST.PASSWORD);
	        NameValuePair pair3 = new BasicNameValuePair("appid", CONST.APPID);
	        NameValuePair pair4 = new BasicNameValuePair("device_id", "");
	        NameValuePair pair5 = new BasicNameValuePair("platform", "android");
	        NameValuePair pair6 = new BasicNameValuePair("os_version", android.os.Build.VERSION.RELEASE);
	        NameValuePair pair7 = new BasicNameValuePair("software_version", version);
	        NameValuePair pair8 = new BasicNameValuePair("mobile_type", android.os.Build.MODEL);
	        NameValuePair pair9 = new BasicNameValuePair("address", "");
	        NameValuePair pair10 = new BasicNameValuePair("lat", lat);
	        NameValuePair pair11 = new BasicNameValuePair("lng", lng);
	        
			nvpList.add(pair1);
			nvpList.add(pair2);
			nvpList.add(pair3);
			nvpList.add(pair4);
			nvpList.add(pair5);
			nvpList.add(pair6);
			nvpList.add(pair7);
			nvpList.add(pair8);
			nvpList.add(pair9);
			nvpList.add(pair10);
			nvpList.add(pair11);
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
	 * 统计页面使用信息
	 */
	@SuppressLint("SimpleDateFormat")
	public static void submitClickCount(String columnId, String eventname) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
		String addtime = sdf.format(new Date());
		String url = "http://decision-admin.tianqi.cn/Home/Count/clickCount?addtime="+addtime+"&appid="+CONST.APPID+
				"&eventid=menuClick_"+columnId+"&eventname="+eventname+"&userid="+CONST.UID+"&username="+CONST.USERNAME;
		
		HttpAsyncTask task = new HttpAsyncTask();
		task.setMethod("GET");
		task.setTimeOut(CustomHttpClient.TIME_OUT);
		task.execute(url);
	}
	private static class HttpAsyncTask extends AsyncTask<String, Void, String> {
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
	
}
