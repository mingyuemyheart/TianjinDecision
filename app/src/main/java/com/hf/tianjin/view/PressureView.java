package com.hf.tianjin.view;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.media.ThumbnailUtils;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.hf.tianjin.R;
import com.hf.tianjin.common.CONST;
import com.hf.tianjin.dto.StationMonitorDto;
import com.hf.tianjin.dto.WarningDto;
import com.hf.tianjin.utils.CommonUtil;

/**
 * 气压曲线
 * @author shawn_sun
 *
 */

public class PressureView extends View{
	
	private Context mContext = null;
	private List<StationMonitorDto> tempList = new ArrayList<StationMonitorDto>();
	private float maxValue = 0;
	private float minValue = 0;
	private float min = 0;
	private Paint lineP = null;//画线画笔
	private Paint textP = null;//写字画笔
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHH");
	private SimpleDateFormat sdf2 = new SimpleDateFormat("HH");
	private SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMddHHmmss");
	private SimpleDateFormat sdf4 = new SimpleDateFormat("mm");
	private SimpleDateFormat sdf5 = new SimpleDateFormat("HH:mm");
	private Bitmap bitmap = null;
	private Bitmap point = null;
	private List<WarningDto> warningList = new ArrayList<WarningDto>();
	
	public PressureView(Context context) {
		super(context);
		mContext = context;
		init();
	}
	
	public PressureView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}
	
	public PressureView(Context context, AttributeSet attrs, int defStyleAttr) {
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
		
		bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeResource(getResources(), R.drawable.iv_marker_pressure), 
				(int)(CommonUtil.dip2px(mContext, 35)), (int)(CommonUtil.dip2px(mContext, 30)));
		point = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeResource(getResources(), R.drawable.iv_point_pressure), 
				(int)(CommonUtil.dip2px(mContext, 10)), (int)(CommonUtil.dip2px(mContext, 10)));
	}
	
	/**
	 * 对cubicView进行赋值
	 */
	public void setData(List<StationMonitorDto> dataList, List<WarningDto> warningList) {
		if (!warningList.isEmpty()) {
			this.warningList.addAll(warningList);
		}
		if (!dataList.isEmpty()) {
			for (int i = 0; i < dataList.size(); i++) {
				tempList.add(dataList.get(i));
			}
			if (tempList.isEmpty()) {
				return;
			}
			
			String temp = tempList.get(0).airPressure;
			for (int i = 0; i < tempList.size(); i++) {
				StationMonitorDto dto = tempList.get(i);
				if (!TextUtils.isEmpty(dto.airPressure) && !TextUtils.equals(dto.airPressure, CONST.noValue)) {
					temp = dto.airPressure;
					break;
				}
			}
			if (!TextUtils.isEmpty(temp) && !TextUtils.equals(temp, CONST.noValue)) {
				min  = Float.valueOf(temp);
				for (int i = 0; i < tempList.size(); i++) {
					StationMonitorDto dto = tempList.get(i);
					if (!TextUtils.isEmpty(dto.airPressure) && !TextUtils.equals(dto.airPressure, CONST.noValue)) {
						if (min >= Float.valueOf(dto.airPressure)) {
							min = Float.valueOf(dto.airPressure);
						}
					}
				}
				
				for (int i = 0; i < tempList.size(); i++) {
					StationMonitorDto dto = tempList.get(i);
					if (!TextUtils.isEmpty(dto.airPressure) && !TextUtils.equals(dto.airPressure, CONST.noValue)) {
						float air = Float.valueOf(dto.airPressure);
						dto.airPressure = String.valueOf(air - min);
					}
				}
			}
			
			String temp2 = tempList.get(0).airPressure;
			for (int i = 0; i < tempList.size(); i++) {
				StationMonitorDto dto = tempList.get(i);
				if (!TextUtils.isEmpty(dto.airPressure) && !TextUtils.equals(dto.airPressure, CONST.noValue)) {
					temp = dto.airPressure;
					break;
				}
			}
			if (!TextUtils.isEmpty(temp2) && !TextUtils.equals(temp2, CONST.noValue)) {
				maxValue = Float.valueOf(temp2);
				minValue = Float.valueOf(temp2);
				for (int i = 0; i < tempList.size(); i++) {
					StationMonitorDto dto = tempList.get(i);
					if (!TextUtils.isEmpty(dto.airPressure) && !TextUtils.equals(dto.airPressure, CONST.noValue)) {
						if (maxValue <= Float.valueOf(dto.airPressure)) {
							maxValue = Float.valueOf(dto.airPressure);
						}
						if (minValue >= Float.valueOf(dto.airPressure)) {
							minValue = Float.valueOf(dto.airPressure);
						}
					}
				}
			}
			
			if (maxValue == 0 && minValue == 0) {
				maxValue = 5;
				minValue = 0;
			}else {
				//绘制刻度线，每间隔为20
				int totalDivider = (int) (Math.ceil(Math.abs(maxValue))+Math.floor(Math.abs(minValue)));
				int itemDivider = 1;
				if (totalDivider <= 5) {
					itemDivider = 1;
				}else if (totalDivider <= 10 && totalDivider > 5) {
					itemDivider = 2;
				}else if (totalDivider <= 20 && totalDivider > 10) {
					itemDivider = 5;
				}else if (totalDivider <= 50 && totalDivider > 20) {
					itemDivider = 10;
				}else if (totalDivider > 50) {
					itemDivider = 20;
				}
				maxValue = (float) (Math.ceil(maxValue)+itemDivider);
				minValue = (float) (Math.floor(minValue)-itemDivider);
			}
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
		float chartW = w-CommonUtil.dip2px(mContext, 40);
		float chartH = h-CommonUtil.dip2px(mContext, 80);
		float leftMargin = CommonUtil.dip2px(mContext, 20);
		float rightMargin = CommonUtil.dip2px(mContext, 20);
		float topMargin = CommonUtil.dip2px(mContext, 10);
		float bottomMargin = CommonUtil.dip2px(mContext, 70);
		float chartMaxH = chartH * maxValue / (Math.abs(maxValue)+Math.abs(minValue));//同时存在正负值时，正值高度
		
		int size = tempList.size();
		float columnWidth = chartW/(size-1);
		//获取曲线上每个温度点的坐标
		for (int i = 0; i < size; i++) {
			StationMonitorDto dto = tempList.get(i);
			dto.x = columnWidth*i+leftMargin;
			dto.y = 0;
			
			if (!TextUtils.isEmpty(dto.airPressure) && !TextUtils.equals(dto.airPressure, CONST.noValue)) {
				float value = Float.valueOf(dto.airPressure);
				if (value >= 0) {
					dto.y = chartMaxH - chartH*Math.abs(value)/(Math.abs(maxValue)+Math.abs(minValue)) + topMargin;
					if (minValue >= 0) {
						dto.y = chartH - chartH*Math.abs(value)/(Math.abs(maxValue)+Math.abs(minValue)) + topMargin;
					}
				}else {
					dto.y = chartMaxH + chartH*Math.abs(value)/(Math.abs(maxValue)+Math.abs(minValue)) + topMargin;
					if (maxValue < 0) {
						dto.y = chartH*Math.abs(value)/(Math.abs(maxValue)+Math.abs(minValue)) + topMargin;
					}
				}
				tempList.set(i, dto);
			}
		}
		
		for (int i = 0; i < size-1; i++) {
			StationMonitorDto dto = tempList.get(i);
			//绘制区域
			Path rectPath = new Path();
			rectPath.moveTo(dto.x, topMargin);
			rectPath.lineTo(dto.x+columnWidth, topMargin);
			rectPath.lineTo(dto.x+columnWidth, h-bottomMargin);
			rectPath.lineTo(dto.x, h-bottomMargin);
			rectPath.close();
			if (i == 0 || i == 1 || i == 2 || i == 3 || i == 8 || i == 9 || i == 10 || i == 11
					 || i == 16 || i == 17 || i == 18 || i == 19) {
				lineP.setColor(Color.WHITE);
			}else {
				lineP.setColor(0xfff9f9f9);
			}
			lineP.setStyle(Style.FILL);
			canvas.drawPath(rectPath, lineP);
		}
		
		for (int i = 0; i < size; i++) {
			StationMonitorDto dto = tempList.get(i);
			//绘制分割线
			Path linePath = new Path();
			linePath.moveTo(dto.x, topMargin);
			linePath.lineTo(dto.x, h-bottomMargin);
			linePath.close();
			lineP.setColor(0xfff1f1f1);
			lineP.setStyle(Style.STROKE);
			canvas.drawPath(linePath, lineP);
		}
		
		//绘制刻度线，每间隔为20
		int totalDivider = (int) (Math.ceil(Math.abs(maxValue))+Math.floor(Math.abs(minValue)));
		int itemDivider = 1;
		if (totalDivider <= 5) {
			itemDivider = 1;
		}else if (totalDivider <= 10 && totalDivider > 5) {
			itemDivider = 2;
		}else if (totalDivider <= 20 && totalDivider > 10) {
			itemDivider = 5;
		}else if (totalDivider <= 50 && totalDivider > 20) {
			itemDivider = 10;
		}else if (totalDivider > 50) {
			itemDivider = 20;
		}
		for (int i = (int) minValue; i <= totalDivider; i+=itemDivider) {
			float dividerY = 0;
			int value = i;
			if (value >= 0) {
				dividerY = chartMaxH - chartH*Math.abs(value)/(Math.abs(maxValue)+Math.abs(minValue)) + topMargin;
				if (minValue >= 0) {
					dividerY = chartH - chartH*Math.abs(value)/(Math.abs(maxValue)+Math.abs(minValue)) + topMargin;
				}
			}else {
				dividerY = chartMaxH + chartH*Math.abs(value)/(Math.abs(maxValue)+Math.abs(minValue)) + topMargin;
				if (maxValue < 0) {
					dividerY = chartH*Math.abs(value)/(Math.abs(maxValue)+Math.abs(minValue)) + topMargin;
				}
			}
			lineP.setColor(0xfff1f1f1);
			canvas.drawLine(leftMargin, dividerY, w-rightMargin, dividerY, lineP);
			textP.setColor(0xff999999);
			textP.setTextSize(CommonUtil.dip2px(mContext, 10));
			canvas.drawText(String.valueOf(i+min), CommonUtil.dip2px(mContext, 5), dividerY, textP);
		}
		
		//绘制曲线
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

			if (y2 != 0 && y3 != 0 && y4 != 0) {
				Path linePath = new Path();
				linePath.moveTo(x1, y1);
				linePath.cubicTo(x3, y3, x4, y4, x2, y2);
				lineP.setColor(0xff508ec0);
				lineP.setStyle(Style.STROKE);
				lineP.setStrokeWidth(CommonUtil.dip2px(mContext, 3));
				canvas.drawPath(linePath, lineP);
			}
		}
		
		for (int i = 0; i < size; i++) {
			StationMonitorDto dto = tempList.get(i);
			
			if (!TextUtils.isEmpty(dto.airPressure) && !TextUtils.equals(dto.airPressure, CONST.noValue)) {
				canvas.drawBitmap(point, dto.x-point.getWidth()/2, dto.y-point.getHeight()/2, lineP);
				
				//绘制曲线上每个点的数据值
				textP.setColor(Color.WHITE);
				textP.setTextSize(CommonUtil.dip2px(mContext, 10));
				float tempWidth = textP.measureText(String.valueOf(Float.valueOf(dto.airPressure)+min));
				canvas.drawBitmap(bitmap, dto.x-bitmap.getWidth()/2, dto.y-bitmap.getHeight()-CommonUtil.dip2px(mContext, 2.5f), textP);
				canvas.drawText(String.valueOf(Float.valueOf(dto.airPressure)+min), dto.x-tempWidth/2, dto.y-bitmap.getHeight()/2, textP);
			}
			
			//绘制24小时
			textP.setColor(0xff999999);
			textP.setTextSize(CommonUtil.dip2px(mContext, 12));
			try {
				if (!TextUtils.isEmpty(dto.time)) {
					for (int j = 0; j < warningList.size(); j++) {
						WarningDto wDto = warningList.get(j);
						if (TextUtils.equals(sdf2.format(sdf1.parse(dto.time)), sdf2.format(sdf3.parse(wDto.time)))) {
							Bitmap wBitmap = null;
							if (wDto.color.equals(CONST.blue[0])) {
								wBitmap = CommonUtil.getImageFromAssetsFile(mContext,"warning/"+wDto.type+CONST.blue[1]+CONST.imageSuffix);
								if (wBitmap == null) {
									wBitmap = CommonUtil.getImageFromAssetsFile(mContext,"warning/"+"default"+CONST.blue[1]+CONST.imageSuffix);
								}
							}else if (wDto.color.equals(CONST.yellow[0])) {
								wBitmap = CommonUtil.getImageFromAssetsFile(mContext,"warning/"+wDto.type+CONST.yellow[1]+CONST.imageSuffix);
								if (wBitmap == null) {
									wBitmap = CommonUtil.getImageFromAssetsFile(mContext,"warning/"+"default"+CONST.yellow[1]+CONST.imageSuffix);
								}
							}else if (wDto.color.equals(CONST.orange[0])) {
								wBitmap = CommonUtil.getImageFromAssetsFile(mContext,"warning/"+wDto.type+CONST.orange[1]+CONST.imageSuffix);
								if (wBitmap == null) {
									wBitmap = CommonUtil.getImageFromAssetsFile(mContext,"warning/"+"default"+CONST.orange[1]+CONST.imageSuffix);
								}
							}else if (wDto.color.equals(CONST.red[0])) {
								wBitmap = CommonUtil.getImageFromAssetsFile(mContext,"warning/"+wDto.type+CONST.red[1]+CONST.imageSuffix);
								if (wBitmap == null) {
									wBitmap = CommonUtil.getImageFromAssetsFile(mContext,"warning/"+"default"+CONST.red[1]+CONST.imageSuffix);
								}
							}
							Bitmap b = zoomImage(wBitmap, CommonUtil.dip2px(mContext, 30f), CommonUtil.dip2px(mContext, 25f));
							int minute = Integer.valueOf(sdf4.format(sdf3.parse(wDto.time)));
							float warningX = minute*columnWidth/60;
							canvas.drawBitmap(b, dto.x+warningX-b.getWidth()/2, h-CommonUtil.dip2px(mContext, 40f), textP);
							float tempWidth = textP.measureText(dto.ballTemp);
							textP.setColor(0xff999999);
							textP.setTextSize(CommonUtil.dip2px(mContext, 10));
							canvas.drawText(sdf5.format(sdf3.parse(wDto.time)), dto.x+warningX-tempWidth/2, h-CommonUtil.dip2px(mContext, 5f), textP);
							textP.setColor(0xffdedede);
							canvas.drawLine(dto.x+warningX, h-CommonUtil.dip2px(mContext, 70f), dto.x+warningX, h-CommonUtil.dip2px(mContext, 40f), textP);
						}
					}
					
					String time = sdf2.format(sdf1.parse(dto.time));
					if (!TextUtils.isEmpty(time)) {
						float text = textP.measureText(time+mContext.getString(R.string.hour));
						textP.setColor(0xff999999);
						textP.setTextSize(CommonUtil.dip2px(mContext, 10));
						canvas.drawText(time+mContext.getString(R.string.hour), dto.x-text/2, h-CommonUtil.dip2px(mContext, 55f), textP);
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		lineP.reset();
		textP.reset();
	}
	
	public static Bitmap zoomImage(Bitmap bgimage, double newWidth, double newHeight) {
		// 获取这个图片的宽和高
		float width = bgimage.getWidth();
		float height = bgimage.getHeight();
		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();
		// 计算宽高缩放率
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 缩放图片动作
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width, (int) height, matrix, true);
		return bitmap;
	}

}
