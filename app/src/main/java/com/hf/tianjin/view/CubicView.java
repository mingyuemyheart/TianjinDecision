package com.hf.tianjin.view;

/**
 * 绘制平滑曲线
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.hf.tianjin.R;
import com.hf.tianjin.common.CONST;
import com.hf.tianjin.dto.WeatherDto;
import com.hf.tianjin.utils.CommonUtil;
import com.hf.tianjin.utils.WeatherUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CubicView extends View{
	
	private Context mContext = null;
	private SimpleDateFormat sdf0 = new SimpleDateFormat("yyyyMMddHHmm");
	private SimpleDateFormat sdf2 = new SimpleDateFormat("HH");
	private List<WeatherDto> tempList = new ArrayList<>();
	private int maxTemp = 0;//最高温度
	private int minTemp = 0;//最低温度
	private int averTemp = 0;
	private Paint lineP = null;//画线画笔
	private Paint textP = null;//写字画笔
	private int hourlyCode = 0;
	private int scrollX = 0;
	private int index = 0;
	private int hScrollViewWidth = 0;
	private Bitmap bitmap, bitmap2;
	private int totalDivider = 0;
	private int itemDivider = 1;
	
	public CubicView(Context context) {
		super(context);
		mContext = context;
		init();
	}
	
	public CubicView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}
	
	public CubicView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = context;
		init();
	}
	
	private void init() {
		lineP = new Paint();
		lineP.setStyle(Paint.Style.STROKE);
		lineP.setStrokeCap(Paint.Cap.ROUND);
		lineP.setAntiAlias(true);
		
		textP = new Paint();
		textP.setAntiAlias(true);

		bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeResource(getResources(), R.drawable.bg_hourly),
				(int)(CommonUtil.dip2px(mContext, 100)), (int)(CommonUtil.dip2px(mContext, 50)));
		bitmap2 = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeResource(getResources(), R.drawable.bg_hourly2),
				(int)(CommonUtil.dip2px(mContext, 100)), (int)(CommonUtil.dip2px(mContext, 50)));
	}
	
	/**
	 * 对cubicView进行赋值
	 */
	public void setData(List<WeatherDto> dataList, int hScrollViewWidth) {
		this.hScrollViewWidth = hScrollViewWidth;
		if (!dataList.isEmpty()) {
			tempList.clear();
			tempList.addAll(dataList);
			
			maxTemp = tempList.get(0).hourlyTemp;
			minTemp = tempList.get(0).hourlyTemp;
			for (int i = 0; i < tempList.size(); i++) {
				if (maxTemp <= tempList.get(i).hourlyTemp) {
					maxTemp = tempList.get(i).hourlyTemp;
				}
				if (minTemp >= tempList.get(i).hourlyTemp) {
					minTemp = tempList.get(i).hourlyTemp;
				}
			}

			if (maxTemp > 0 && minTemp > 0) {
				totalDivider = maxTemp-minTemp;
			}else if (maxTemp >= 0 && minTemp <= 0) {
				totalDivider = maxTemp-minTemp;
			}else if (maxTemp < 0 && minTemp < 0) {
				totalDivider = maxTemp-minTemp;
			}
			if (totalDivider <= 5) {
				itemDivider = 1;
			}else if (totalDivider > 5 && totalDivider <= 15) {
				itemDivider = 2;
			}else if (totalDivider > 15 && totalDivider <= 25) {
				itemDivider = 3;
			}else if (totalDivider > 25 && totalDivider <= 40) {
				itemDivider = 4;
			}else {
				itemDivider = 5;
			}
			maxTemp = maxTemp+itemDivider*3/2;
			minTemp = minTemp-itemDivider;
			averTemp = (maxTemp+minTemp)/2;

			hourlyCode = tempList.get(0).hourlyCode;
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (tempList.isEmpty()) {
			return;
		}
		
		float w = canvas.getWidth();
		float h = canvas.getHeight();
		canvas.drawColor(Color.TRANSPARENT);
		float chartW = w-CommonUtil.dip2px(mContext, 120);
		float chartH = h-CommonUtil.dip2px(mContext, 150);
		float leftMargin = CommonUtil.dip2px(mContext, 80);
		float rightMargin = CommonUtil.dip2px(mContext, 20);
		float topMargin = CommonUtil.dip2px(mContext, 0);
		float bottomMargin = CommonUtil.dip2px(mContext, 5);
		
		int size = tempList.size();
		//获取曲线上每个温度点的坐标
		for (int i = 0; i < size; i++) {
			WeatherDto dto = tempList.get(i);
			dto.x = (chartW/(size-1))*i + leftMargin;

			float temp = dto.hourlyTemp;
			dto.y = chartH*Math.abs(maxTemp-temp)/totalDivider;
			Log.e("temp", temp+"---"+dto.y);
			tempList.set(i, dto);
		}

		//绘制刻度线
		for (int i = minTemp; i <= maxTemp; i+=itemDivider) {
			float dividerY = chartH*Math.abs(maxTemp-i)/totalDivider;
			lineP.setColor(0x30ffffff);
			lineP.setStrokeWidth(CommonUtil.dip2px(mContext, 3));
			canvas.drawLine(CommonUtil.dip2px(mContext, 60), dividerY, w-rightMargin, dividerY, lineP);
			textP.setColor(mContext.getResources().getColor(R.color.white));
			textP.setTextSize(getResources().getDimensionPixelSize(R.dimen.level_6));
			canvas.drawText(String.valueOf(i)+"℃", CommonUtil.dip2px(mContext, 5), dividerY, textP);
		}
		
		for (int i = 0; i < size-1; i++) {
			float x1 = tempList.get(i).x;
			float y1 = tempList.get(i).y;
			float x2 = tempList.get(i+1).x;
			float y2 = tempList.get(i+1).y;
			
			float wt = (x1 + x2) / 2;
			
			float x3 = wt;
			float y3 = y1;
			float x4 = wt;
			float y4 = y2;
			
			Path pathLow = new Path();
			pathLow.moveTo(x1, y1);
			pathLow.cubicTo(x3, y3, x4, y4, x2, y2);
			lineP.setColor(getResources().getColor(R.color.white));
			lineP.setStrokeWidth(CommonUtil.dip2px(mContext, 5));
			canvas.drawPath(pathLow, lineP);
		}
		
		float halfX = (tempList.get(1).x - tempList.get(0).x)/2;
		for (int i = 0; i < tempList.size(); i++) {
			WeatherDto dto = tempList.get(i);
			
			//绘制曲线上每个时间点marker
			lineP.setColor(getResources().getColor(R.color.white));
			lineP.setStrokeWidth(CommonUtil.dip2px(mContext, 15));
			canvas.drawPoint(dto.x, dto.y, lineP);
			
			if (index != i) {
				try {
					long zao8 = sdf2.parse("06").getTime();
					long wan8 = sdf2.parse("18").getTime();
					long current = sdf2.parse(sdf2.format(sdf0.parse(dto.hourlyTime))).getTime();
					Bitmap b = null;
					if (current >= zao8 && current < wan8) {
						b = WeatherUtil.getBitmap(mContext, dto.hourlyCode);
					}else {
						b = WeatherUtil.getNightBitmap(mContext, dto.hourlyCode);
					}
					if (i == 0) {
						hourlyCode = dto.hourlyCode;
						Bitmap newBit = ThumbnailUtils.extractThumbnail(b, (int)(CommonUtil.dip2px(mContext, 40)), (int)(CommonUtil.dip2px(mContext, 40)));
						canvas.drawBitmap(newBit, dto.x-newBit.getWidth()/2, dto.y-CommonUtil.dip2px(mContext, 35f), textP);
					}else {
						if (hourlyCode != dto.hourlyCode) {
							hourlyCode = dto.hourlyCode;
							Bitmap newBit = ThumbnailUtils.extractThumbnail(b, (int)(CommonUtil.dip2px(mContext, 40)), (int)(CommonUtil.dip2px(mContext, 40)));
							canvas.drawBitmap(newBit, dto.x-newBit.getWidth()/2, dto.y-CommonUtil.dip2px(mContext, 50f), textP);
						}
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}

			//绘制风速风向
			lineP.setColor(0x60ffffff);
			lineP.setStyle(Style.FILL);
			lineP.setStrokeWidth(CommonUtil.dip2px(mContext, 1));
			String windForce = WeatherUtil.getHourWindForce(dto.hourlyWindForceCode);
			float windHeight = h-CommonUtil.dip2px(mContext, 35);
			if (TextUtils.equals(windForce, "微风")) {
				windHeight = h-CommonUtil.dip2px(mContext, 35);
			}else if (TextUtils.equals(windForce, "1级")) {
				windHeight = h-CommonUtil.dip2px(mContext, 40);
			}else if (TextUtils.equals(windForce, "2级")) {
				windHeight = h-CommonUtil.dip2px(mContext, 45);
			}else if (TextUtils.equals(windForce, "3级")) {
				windHeight = h-CommonUtil.dip2px(mContext, 50);
			}else if (TextUtils.equals(windForce, "4级")) {
				windHeight = h-CommonUtil.dip2px(mContext, 55);
			}else if (TextUtils.equals(windForce, "5级")) {
				windHeight = h-CommonUtil.dip2px(mContext, 60);
			}else if (TextUtils.equals(windForce, "6级")) {
				windHeight = h-CommonUtil.dip2px(mContext, 65);
			}else if (TextUtils.equals(windForce, "7级")) {
				windHeight = h-CommonUtil.dip2px(mContext, 70);
			}else if (TextUtils.equals(windForce, "8级")) {
				windHeight = h-CommonUtil.dip2px(mContext, 75);
			}else if (TextUtils.equals(windForce, "9级")) {
				windHeight = h-CommonUtil.dip2px(mContext, 80);
			}else if (TextUtils.equals(windForce, "10级")) {
				windHeight = h-CommonUtil.dip2px(mContext, 85);
			}else if (TextUtils.equals(windForce, "11级")) {
				windHeight = h-CommonUtil.dip2px(mContext, 90);
			}else if (TextUtils.equals(windForce, "12级")) {
				windHeight = h-CommonUtil.dip2px(mContext, 95);
			}
			
			if (index == i) {
				lineP.setColor(getResources().getColor(R.color.white));
				textP.setColor(getResources().getColor(R.color.white));
				textP.setTextSize(getResources().getDimensionPixelSize(R.dimen.level_6));
				String windDir = mContext.getString(WeatherUtil.getWindDirection(dto.hourlyWindDirCode));
				float hWindDir = textP.measureText(windDir);
				canvas.drawText(windDir, dto.x-hWindDir/2, windHeight-CommonUtil.dip2px(mContext, 33f), textP);
				
				textP.setColor(getResources().getColor(R.color.white));
				textP.setTextSize(getResources().getDimensionPixelSize(R.dimen.level_6));
				float hWindForce = textP.measureText(windForce);
				canvas.drawText(windForce, dto.x-hWindForce/2, windHeight-CommonUtil.dip2px(mContext, 8f), textP);
			}else {
				lineP.setColor(0x60ffffff);
			}
			canvas.drawRect(dto.x-halfX+CommonUtil.dip2px(mContext, 2), windHeight, dto.x+halfX-CommonUtil.dip2px(mContext, 2), h-CommonUtil.dip2px(mContext, 25), lineP);

			//绘制每个时间点上的时间值
			textP.setColor(getResources().getColor(R.color.white));
			textP.setTextSize(getResources().getDimensionPixelSize(R.dimen.level_6));
			try {
				String hourlyTime = sdf2.format(sdf0.parse(tempList.get(i).hourlyTime));
				canvas.drawText(hourlyTime, dto.x-CommonUtil.dip2px(mContext, 12f), h-bottomMargin, textP);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		//绘制曲线上每个点的信息
		if (tempList.get(index).hourlyTemp > averTemp) {
			canvas.drawBitmap(bitmap2, tempList.get(index).x-bitmap.getWidth()/2-(int)(CommonUtil.dip2px(mContext, 2)), tempList.get(index).y+(int)(CommonUtil.dip2px(mContext, 5)), textP);
			textP.setColor(Color.WHITE);
			textP.setTextSize(getResources().getDimensionPixelSize(R.dimen.level_5));
			canvas.drawText(String.valueOf(tempList.get(index).hourlyTemp)+mContext.getString(R.string.unit_degree), tempList.get(index).x+(int)(CommonUtil.dip2px(mContext, 5)), tempList.get(index).y+(int)(CommonUtil.dip2px(mContext, 45)), textP);
			try {
				long zao8 = sdf2.parse("06").getTime();
				long wan8 = sdf2.parse("18").getTime();
				long current = sdf2.parse(sdf2.format(sdf0.parse(tempList.get(index).hourlyTime))).getTime();
				Bitmap lb = null;
				if (current >= zao8 && current < wan8) {
					lb = WeatherUtil.getBitmap(mContext, tempList.get(index).hourlyCode);
				}else {
					lb = WeatherUtil.getNightBitmap(mContext, tempList.get(index).hourlyCode);
				}
				Bitmap newLbit = ThumbnailUtils.extractThumbnail(lb, (int)(CommonUtil.dip2px(mContext, 30)), (int)(CommonUtil.dip2px(mContext, 30)));
				canvas.drawBitmap(newLbit, tempList.get(index).x-newLbit.getWidth()-(int)(CommonUtil.dip2px(mContext, 5)), tempList.get(index).y+(int)(CommonUtil.dip2px(mContext, 20)), textP);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else {
			canvas.drawBitmap(bitmap, tempList.get(index).x-bitmap.getWidth()/2+(int)(CommonUtil.dip2px(mContext, 2)), tempList.get(index).y-(int)(CommonUtil.dip2px(mContext, 58)), textP);
			textP.setColor(Color.WHITE);
			textP.setTextSize(getResources().getDimensionPixelSize(R.dimen.level_5));
			canvas.drawText(String.valueOf(tempList.get(index).hourlyTemp)+mContext.getString(R.string.unit_degree), tempList.get(index).x+(int)(CommonUtil.dip2px(mContext, 5)), tempList.get(index).y-(int)(CommonUtil.dip2px(mContext, 25)), textP);
			try {
				long zao8 = sdf2.parse("06").getTime();
				long wan8 = sdf2.parse("18").getTime();
				long current = sdf2.parse(sdf2.format(sdf0.parse(tempList.get(index).hourlyTime))).getTime();
				Bitmap lb = null;
				if (current >= zao8 && current < wan8) {
					lb = WeatherUtil.getBitmap(mContext, tempList.get(index).hourlyCode);
				}else {
					lb = WeatherUtil.getNightBitmap(mContext, tempList.get(index).hourlyCode);
				}
				Bitmap newLbit = ThumbnailUtils.extractThumbnail(lb, (int)(CommonUtil.dip2px(mContext, 30)), (int)(CommonUtil.dip2px(mContext, 30)));
				canvas.drawBitmap(newLbit, tempList.get(index).x-newLbit.getWidth()-(int)(CommonUtil.dip2px(mContext, 5)), tempList.get(index).y-(int)(CommonUtil.dip2px(mContext, 50)), textP);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		lineP.reset();
		textP.reset();

	}
	
	public Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if (msg.what == CONST.MSG_101) {
				scrollX = msg.arg1;
				float itemWidth = hScrollViewWidth/1.0f/tempList.size();
				index = (int) (scrollX/itemWidth);
				if (index >= tempList.size()) {
					index = tempList.size()-1;
				}
				Log.e("index", index+"");

				postInvalidate();
			}
		};
	};
	
}
