package com.hf.tianjin.manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.overlay.DrivingRouteOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DrivePath;
import com.hf.tianjin.R;

public class DrivingOverlay extends DrivingRouteOverlay {
	private Context mContext;
	public DrivingOverlay(Context context, AMap arg1, DrivePath arg2, LatLonPoint arg3, LatLonPoint arg4) {
		super(context, arg1, arg2, arg3, arg4);
		mContext = context;
	}
	
	@Override
	protected BitmapDescriptor getDriveBitmapDescriptor() {
//		BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.drawable.iv_day_clear);
		return setMarker();
	}
	
	@Override
	protected BitmapDescriptor getEndBitmapDescriptor() {
//		BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.drawable.iv_day_clear);
		return setMarker();
	}
	
	@Override
	protected BitmapDescriptor getStartBitmapDescriptor() {
//		BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.drawable.iv_day_clear);
		return setMarker();
	}
	
	private BitmapDescriptor setMarker() {
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.marker_view, null);
		ImageView ivMarker = (ImageView) view.findViewById(R.id.ivMarker);
		BitmapDescriptor descriptor = BitmapDescriptorFactory.fromView(view);
		return descriptor;
	}
	
}
