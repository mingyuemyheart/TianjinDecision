package com.hf.tianjin.view;

/**
 * 一周预报曲线图
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.media.ThumbnailUtils;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.hf.tianjin.R;
import com.hf.tianjin.dto.WeatherDto;
import com.hf.tianjin.utils.CommonUtil;
import com.hf.tianjin.utils.WeatherUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class WeeklyView extends View{
	
	private Context mContext = null;
	private List<WeatherDto> tempList = new ArrayList<>();
	private int maxTemp = 0;//最高温度
	private int minTemp = 0;//最低温度
	private Paint lineP = null;//画线画笔
	private Paint textP = null;//写字画笔
	private Paint roundP = null;//aqi背景颜色画笔
	private Bitmap lowBit = null;//低温图标
	private Bitmap highBit = null;//高温图标
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
	private SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd");
	private int totalDivider = 0;
	private int itemDivider = 1;
	
	public WeeklyView(Context context) {
		super(context);
		mContext = context;
		init();
	}
	
	public WeeklyView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}
	
	public WeeklyView(Context context, AttributeSet attrs, int defStyleAttr) {
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
		
		roundP = new Paint();
		roundP.setStyle(Paint.Style.FILL);
		roundP.setStrokeCap(Paint.Cap.ROUND);
		roundP.setAntiAlias(true);
		
		lowBit = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeResource(getResources(), R.drawable.iv_low),
				(int)(CommonUtil.dip2px(mContext, 60f)), (int)(CommonUtil.dip2px(mContext, 70f)));
		highBit = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeResource(getResources(), R.drawable.iv_high), 
				(int)(CommonUtil.dip2px(mContext, 60f)), (int)(CommonUtil.dip2px(mContext, 70f)));
	}
	
	/**
	 * 对polyView进行赋值
	 */
	public void setData(List<WeatherDto> dataList) {
		if (!dataList.isEmpty()) {
			tempList.clear();
			tempList.addAll(dataList);
			
			maxTemp = tempList.get(0).highTemp;
			minTemp = tempList.get(0).lowTemp;
			for (int i = 0; i < tempList.size(); i++) {
				if (maxTemp <= tempList.get(i).highTemp) {
					maxTemp = tempList.get(i).highTemp;
				}
				if (minTemp >= tempList.get(i).lowTemp) {
					minTemp = tempList.get(i).lowTemp;
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
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(Color.TRANSPARENT);
		float w = canvas.getWidth();
		float h = canvas.getHeight();
		float chartH = h-CommonUtil.dip2px(mContext, 500);
		float topMargin = CommonUtil.dip2px(mContext, 240);
		
		int size = tempList.size();
		//获取曲线上每个温度点的坐标
		for (int i = 0; i < size; i++) {
			WeatherDto dto = tempList.get(i);

			float itemWidth = w/(size);

			//获取最高温度对应的坐标点信息
			dto.highX = itemWidth/2+itemWidth*i;
			float highTemp = tempList.get(i).highTemp;
			dto.highY = chartH*Math.abs(maxTemp-highTemp)/totalDivider+topMargin;
			Log.e("highTemp", highTemp+"---"+dto.highY);

			//获取最低温度的对应的坐标点信息
			dto.lowX = itemWidth/2+itemWidth*i;
			float lowTemp = tempList.get(i).lowTemp;
			dto.lowY = chartH*Math.abs(maxTemp-lowTemp)/totalDivider+topMargin;
			Log.e("lowTemp", lowTemp+"---"+dto.lowY);
			
			tempList.set(i, dto);
		}
		
		//绘制最低温度、最高温度曲线
		for (int i = 0; i < size-1; i++) {
			float x1 = tempList.get(i).lowX;
			float y1 = tempList.get(i).lowY;
			float x2 = tempList.get(i+1).lowX;
			float y2 = tempList.get(i+1).lowY;
			
			float wt = (x1 + x2) / 2;
			
			float x3 = wt;
			float y3 = y1;
			float x4 = wt;
			float y4 = y2;

			Path pathLow = new Path();
			pathLow.moveTo(x1, y1);
			pathLow.cubicTo(x3, y3, x4, y4, x2, y2);
			lineP.setColor(getResources().getColor(R.color.low_color));
			lineP.setStrokeWidth(CommonUtil.dip2px(mContext, 5f));
			canvas.drawPath(pathLow, lineP);
		}
		
		for (int i = 0; i < size-1; i++) {
			float x1 = tempList.get(i).highX;
			float y1 = tempList.get(i).highY;
			float x2 = tempList.get(i+1).highX;
			float y2 = tempList.get(i+1).highY;
			
			float wt = (x1 + x2) / 2;
			
			float x3 = wt;
			float y3 = y1;
			float x4 = wt;
			float y4 = y2;

			if (x1 != -100 && y1 != -100) {
				Path pathHigh = new Path();
				pathHigh.moveTo(x1, y1);
				pathHigh.cubicTo(x3, y3, x4, y4, x2, y2);
				lineP.setColor(getResources().getColor(R.color.high_color));
				lineP.setStrokeWidth(CommonUtil.dip2px(mContext, 5f));
				canvas.drawPath(pathHigh, lineP);
			}
		}
		
		for (int i = 0; i < tempList.size(); i++) {
			WeatherDto dto = tempList.get(i);
			
			//绘制周几、日期、天气现象和天气现象图标
			textP.setColor(getResources().getColor(R.color.white));
			textP.setTextSize(getResources().getDimension(R.dimen.level_5));
			if (!TextUtils.isEmpty(dto.week)) {
				String week = mContext.getString(R.string.week)+dto.week.substring(dto.week.length()-1, dto.week.length());
				if (i == 0) {
					week = mContext.getString(R.string.today);
				}else if (i == 1) {
					week = mContext.getString(R.string.tommorrow);
				}
				float weekText = textP.measureText(week);
				canvas.drawText(week, dto.lowX-weekText/2, CommonUtil.dip2px(mContext, 30), textP);
			}
			if (!TextUtils.isEmpty(dto.date)) {
				try {
					String date = sdf2.format(sdf1.parse(dto.date));
					float dateText = textP.measureText(date);
					canvas.drawText(date, dto.lowX-dateText/2, CommonUtil.dip2px(mContext, 60), textP);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}

			if (dto.highX != -100 && dto.highY != -100) {
				float highPheText = textP.measureText(dto.highPhe);//天气现象字符串占像素宽度
				canvas.drawText(dto.highPhe, dto.highX-highPheText/2, CommonUtil.dip2px(mContext, 90), textP);
				
				Bitmap b = WeatherUtil.getBitmap(mContext, dto.highPheCode);
				Bitmap newBit = ThumbnailUtils.extractThumbnail(b, (int)(CommonUtil.dip2px(mContext, 40)), (int)(CommonUtil.dip2px(mContext, 40)));
				canvas.drawBitmap(newBit, dto.highX-newBit.getWidth()/2, CommonUtil.dip2px(mContext, 100), textP);
			}
			
			Bitmap lb = WeatherUtil.getNightBitmap(mContext, dto.lowPheCode);
			Bitmap newLbit = ThumbnailUtils.extractThumbnail(lb, (int)(CommonUtil.dip2px(mContext, 40)), (int)(CommonUtil.dip2px(mContext, 40)));
			canvas.drawBitmap(newLbit, dto.lowX-newLbit.getWidth()/2, h-CommonUtil.dip2px(mContext, 145), textP);
			
			float lowPheText = textP.measureText(dto.lowPhe);//天气现象字符串占像素宽度
			canvas.drawText(dto.lowPhe, dto.lowX-lowPheText/2, h-CommonUtil.dip2px(mContext, 75), textP);
			
			//绘制纵向分隔线
			if (i < tempList.size()-1) {
				lineP.setColor(0x10ffffff);
				lineP.setStrokeWidth(CommonUtil.dip2px(mContext, 0.5f));
				canvas.drawLine((tempList.get(i+1).lowX-dto.lowX)/2+dto.lowX, CommonUtil.dip2px(mContext, 60), (tempList.get(i+1).lowX-dto.lowX)/2+dto.lowX, h-CommonUtil.dip2px(mContext, 10), lineP);
			}
			
			//绘制曲线上每个时间点上的圆点marker
			lineP.setColor(getResources().getColor(R.color.low_color));
			lineP.setStrokeWidth(CommonUtil.dip2px(mContext, 15));
			canvas.drawPoint(dto.lowX, dto.lowY, lineP);
			
			if (dto.highX != -100 && dto.highY != -100) {
				lineP.setColor(getResources().getColor(R.color.high_color));
				lineP.setStrokeWidth(CommonUtil.dip2px(mContext, 15));
				canvas.drawPoint(dto.highX, dto.highY, lineP);
				
				textP.setColor(getResources().getColor(R.color.white));
				textP.setTextSize(getResources().getDimension(R.dimen.level_5));
				canvas.drawBitmap(highBit, dto.highX-highBit.getWidth()/2, dto.highY-highBit.getHeight()-CommonUtil.dip2px(mContext, 7.5f), textP);
				float highText = textP.measureText(String.valueOf(tempList.get(i).highTemp));//高温字符串占像素宽度
				canvas.drawText(String.valueOf(tempList.get(i).highTemp), dto.highX-highText/2, dto.highY-highBit.getHeight()/2-CommonUtil.dip2px(mContext, 2.5f), textP);
			}
			
			textP.setColor(getResources().getColor(R.color.white));
			textP.setTextSize(getResources().getDimension(R.dimen.level_5));
			canvas.drawBitmap(lowBit, dto.lowX-lowBit.getWidth()/2, dto.lowY+CommonUtil.dip2px(mContext, 7.5f), textP);
			float lowText = textP.measureText(String.valueOf(tempList.get(i).lowTemp));//低温字符串所占的像素宽度
			canvas.drawText(String.valueOf(tempList.get(i).lowTemp), dto.lowX-lowText/2, dto.lowY+lowBit.getHeight()/2+CommonUtil.dip2px(mContext, 22.5f), textP);
			
			//绘制风力风向
			textP.setColor(getResources().getColor(R.color.white));
			textP.setTextSize(getResources().getDimension(R.dimen.level_5));
			String windDir = mContext.getString(WeatherUtil.getWindDirection(Integer.valueOf(dto.windDir)));
			String windForce = WeatherUtil.getDayWindForce(Integer.valueOf(dto.windForce));
			float windDirWidth = textP.measureText(windDir);//低温字符串所占的像素宽度
			float windForceWidth = textP.measureText(windForce);//低温字符串所占的像素宽度
			canvas.drawText(windDir, dto.lowX-windDirWidth/2, h-CommonUtil.dip2px(mContext, 40), textP);
			canvas.drawText(windForce, dto.lowX-windForceWidth/2, h-CommonUtil.dip2px(mContext, 10), textP);
			
		}
		//绘制两个边缘的纵向分隔线
		lineP.setColor(0x10ffffff);
		lineP.setStrokeWidth(CommonUtil.dip2px(mContext, 0.5f));
		canvas.drawLine(tempList.get(0).lowX - (tempList.get(1).lowX-tempList.get(0).lowX)/2, CommonUtil.dip2px(mContext, 60), tempList.get(0).lowX - (tempList.get(1).lowX-tempList.get(0).lowX)/2, h-CommonUtil.dip2px(mContext, 10), lineP);
		canvas.drawLine(tempList.get(size-1).lowX + (tempList.get(1).lowX-tempList.get(0).lowX)/2, CommonUtil.dip2px(mContext, 60), tempList.get(size-1).lowX + (tempList.get(1).lowX-tempList.get(0).lowX)/2, h-CommonUtil.dip2px(mContext, 10), lineP);
	}
	
}
