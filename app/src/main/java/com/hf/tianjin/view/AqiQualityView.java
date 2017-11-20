package com.hf.tianjin.view;

/**
 * 绘制平滑曲线
 */
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.hf.tianjin.R;
import com.hf.tianjin.dto.AqiDto;
import com.hf.tianjin.utils.CommonUtil;

@SuppressLint({ "DrawAllocation", "SimpleDateFormat" })
public class AqiQualityView extends View{
	
	private Context mContext = null;
	private SimpleDateFormat sdf1 = new SimpleDateFormat("HH");
	private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
	private SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH");
	private List<AqiDto> tempList = new ArrayList<AqiDto>();
	private int maxTemp = 0;//最高温度
	private int minTemp = 0;//最低温度
	private Paint lineP = null;//画线画笔
	private Paint textP = null;//写字画笔
	private int saveTime = 0;
	private int time = 0;//发布时间
	private String aqiDate = null;
	private Paint roundP = null;
	
	public AqiQualityView(Context context) {
		super(context);
		mContext = context;
		init();
	}
	
	public AqiQualityView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}
	
	public AqiQualityView(Context context, AttributeSet attrs, int defStyleAttr) {
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
	}
	
	/**
	 * 对cubicView进行赋值
	 */
	public void setData(List<AqiDto> dataList, String aqiDate) {
		try {
			if (!TextUtils.isEmpty(aqiDate)) {
				this.aqiDate = aqiDate;
				this.time = Integer.valueOf(sdf1.format(sdf2.parse(aqiDate)));
				this.saveTime = time;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		if (!dataList.isEmpty()) {
			tempList.clear();
			tempList.addAll(dataList);
			
			maxTemp = Integer.valueOf(tempList.get(0).aqi);
			minTemp = Integer.valueOf(tempList.get(0).aqi);
			for (int i = 0; i < tempList.size(); i++) {
				if (maxTemp <= Integer.valueOf(tempList.get(i).aqi)) {
					maxTemp = Integer.valueOf(tempList.get(i).aqi);
				}
				if (minTemp >= Integer.valueOf(tempList.get(i).aqi)) {
					minTemp = Integer.valueOf(tempList.get(i).aqi);
				}
			}
			
//			maxTemp = maxTemp + (50 - maxTemp%50);
//			if (maxTemp <= 150) {
//				maxTemp = maxTemp + 50;
//			}else if (maxTemp == 200) {
//				maxTemp = maxTemp + 100;
//			}else if (maxTemp == 250) {
//				maxTemp = maxTemp + 50;
//			}else if (maxTemp >= 300) {
//				maxTemp = 500;
//			}
			minTemp = 0;
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (tempList.isEmpty()) {
			return;
		}
		
		canvas.drawColor(Color.TRANSPARENT);
		float w = canvas.getWidth();
		float h = canvas.getHeight();
		float chartW = w-CommonUtil.dip2px(mContext, 110);
		float chartH = h-CommonUtil.dip2px(mContext, 40);
		float leftMargin = CommonUtil.dip2px(mContext, 40);
		float rightMargin = CommonUtil.dip2px(mContext, 70);
		float topMargin = CommonUtil.dip2px(mContext, 20);
		float bottomMargin = CommonUtil.dip2px(mContext, 20);
		float chartMaxH = chartH * maxTemp / (Math.abs(maxTemp)+Math.abs(minTemp));//同时存在正负值时，正值高度
		float chartMinH = chartH * minTemp / (Math.abs(maxTemp)+Math.abs(minTemp));//同时存在正负值时，负值高度
		
		int size = tempList.size();
		
		//获取曲线上每个温度点的坐标
		for (int i = 0; i < size; i++) {
			AqiDto dto = tempList.get(i);
			dto.x = (chartW/(tempList.size()-1))*i + leftMargin;
			
			float value = Float.valueOf(tempList.get(i).aqi);
			if (value <= 200) {
				dto.y = chartH - chartH*Math.abs(value)/(300) + topMargin;
			}else if (value > 200 && value <= 300) {
				float y = chartH*Math.abs((value-200)/2)/(300);
				y = chartH - (y + chartH*Math.abs(200)/(300));
				dto.y = y + topMargin;
			}else if (value > 300 && value <= 500) {
				float y = chartH*Math.abs((value-250)/5)/(300);
				y = chartH - (y + chartH*Math.abs(250)/(300));
				dto.y = y + topMargin;
			}else {
				dto.y = topMargin;
			}
			tempList.set(i, dto);
		}
		
		//绘制纵向分割线
		for (int i = 0; i < tempList.size(); i++) {
			AqiDto dto = tempList.get(i);
			lineP.setColor(0x20ffffff);
			lineP.setStrokeWidth(CommonUtil.dip2px(mContext, 1));
			canvas.drawLine(dto.x, topMargin, dto.x, chartH+topMargin, lineP);
		}
		
		int totalDivider = 300;
		int itemDivider = 50;
		for (int i = 0; i <= totalDivider; i+=itemDivider) {
			float dividerY = chartH - chartH*Math.abs(i)/(300) + topMargin;
			
			//绘制横向刻度线对应的值
			textP.setColor(0xffdfdfdf);
			textP.setTextSize(CommonUtil.dip2px(mContext, 12));
			int value = i;
			if (i == 250) {
				value = 300;
			}else if (i == 300) {
				value = 500;
			}
			canvas.drawText(String.valueOf(value), CommonUtil.dip2px(mContext, 5), dividerY-CommonUtil.dip2px(mContext, 3), textP);
			
			//绘制横向刻度线
			lineP.setColor(0x20ffffff);
			lineP.setStrokeWidth(CommonUtil.dip2px(mContext, 1));
			canvas.drawLine(CommonUtil.dip2px(mContext, 5), dividerY, w-CommonUtil.dip2px(mContext, 5), dividerY, lineP);
			
			textP.setColor(0xffdfdfdf);
			if (i == 50) {
				canvas.drawText(getResources().getString(R.string.aqi0), chartW+CommonUtil.dip2px(mContext, 55), dividerY+CommonUtil.dip2px(mContext, 15), textP);
			}else if (i == 100) {
				canvas.drawText(getResources().getString(R.string.aqi1), chartW+CommonUtil.dip2px(mContext, 55), dividerY+CommonUtil.dip2px(mContext, 15), textP);
			}else if (i == 150) {
				canvas.drawText(getResources().getString(R.string.aqi2), chartW+CommonUtil.dip2px(mContext, 55), dividerY+CommonUtil.dip2px(mContext, 15), textP);
			}else if (i == 200) {
				canvas.drawText(getResources().getString(R.string.aqi3), chartW+CommonUtil.dip2px(mContext, 55), dividerY+CommonUtil.dip2px(mContext, 15), textP);
			}else if (i == 250) {
				canvas.drawText(getResources().getString(R.string.aqi4), chartW+CommonUtil.dip2px(mContext, 55), dividerY+CommonUtil.dip2px(mContext, 15), textP);
			}else if (i == 300) {
				canvas.drawText(getResources().getString(R.string.aqi5), chartW+CommonUtil.dip2px(mContext, 55), dividerY+CommonUtil.dip2px(mContext, 15), textP);
			}
			
			if (i == 300) {
				canvas.drawText("(AQI)", CommonUtil.dip2px(mContext, 30), dividerY-CommonUtil.dip2px(mContext, 3), textP);
			}
		}
		
		//绘制贝塞尔曲线
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
			lineP.setColor(Color.WHITE);
			lineP.setStrokeWidth(3.0f);
			canvas.drawPath(pathLow, lineP);
		}
		
		for (int i = 0; i < tempList.size(); i++) {
			AqiDto dto = tempList.get(i);
			
			//绘制曲线上每个时间点marker
			lineP.setColor(0xffff9600);
			lineP.setStrokeWidth(CommonUtil.dip2px(mContext, 5));
			canvas.drawPoint(dto.x, dto.y, lineP);
			
			//绘制曲线上每个时间点的温度值
			if (Integer.valueOf(dto.aqi) <= 50) {
				roundP.setColor(0xff00FF01);
			} else if (Integer.valueOf(dto.aqi) >= 51 && Integer.valueOf(dto.aqi) <= 100)  {
				roundP.setColor(0xff96EF01);
			} else if (Integer.valueOf(dto.aqi) >= 101 && Integer.valueOf(dto.aqi) <= 150)  {
				roundP.setColor(0xffFFFF01);
			} else if (Integer.valueOf(dto.aqi) >= 151 && Integer.valueOf(dto.aqi) <= 200)  {
				roundP.setColor(0xffFF6400);
			} else if (Integer.valueOf(dto.aqi) >= 201 && Integer.valueOf(dto.aqi) <= 300)  {
				roundP.setColor(0xffFE0000);
			} else if (Integer.valueOf(dto.aqi) >= 301)  {
				roundP.setColor(0xff7E0123);
			}
			RectF rectF = new RectF(dto.x-CommonUtil.dip2px(mContext, 10f), dto.y-CommonUtil.dip2px(mContext, 28), dto.x+CommonUtil.dip2px(mContext, 10f), dto.y-CommonUtil.dip2px(mContext, 10));
			canvas.drawRoundRect(rectF, CommonUtil.dip2px(mContext, 5), CommonUtil.dip2px(mContext, 5), roundP);
			
			textP.setColor(getResources().getColor(R.color.black));
			textP.setTextSize(CommonUtil.dip2px(mContext, 12));
			float tempWidth = textP.measureText(dto.aqi);
			canvas.drawText(dto.aqi, dto.x-tempWidth/2, dto.y-CommonUtil.dip2px(mContext, 15f), textP);
			
			if (time > 23) {
				time = 0;
			}
			
			//绘制每个时间点上的时间值
			textP.setColor(getResources().getColor(R.color.gray));
			textP.setTextSize(CommonUtil.dip2px(mContext, 10));
			float hourWidth = textP.measureText(String.valueOf(time)+getResources().getString(R.string.hour));
			canvas.drawText(String.valueOf(time)+getResources().getString(R.string.hour), dto.x-hourWidth/2, h-bottomMargin+CommonUtil.dip2px(mContext, 15), textP);
			time++;
		}
		
		time = saveTime;
		
		//绘制时间点一行的直线
		lineP.setColor(getResources().getColor(R.color.title_bg));
		lineP.setStrokeWidth(CommonUtil.dip2px(mContext, 1));
		canvas.drawLine(CommonUtil.dip2px(mContext, 5), h-bottomMargin, w-CommonUtil.dip2px(mContext, 5), h-bottomMargin, lineP);
	}
	
}
