package com.hf.tianjin.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

/**
 * 解决HorizontalScrollView嵌套HorizontalScrollView滑动冲突
 * @author shawn_sun
 *
 */

public class MyHorizontalScrollView extends HorizontalScrollView{
	
	private ScrollListener scrollListener = null;
	
	public MyHorizontalScrollView(Context context) {
		super(context);
	}
	
	public MyHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public MyHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void setScrollListener(ScrollListener scrollListener) {
		this.scrollListener = scrollListener;
	}
	
	public interface ScrollListener {  
	    void onScrollChanged(MyHorizontalScrollView hScrollView, int x, int y, int oldx, int oldy);  
	}  
	
    @Override  
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {  
        super.onScrollChanged(x, y, oldx, oldy);  
        if (scrollListener != null) {  
        	scrollListener.onScrollChanged(this, x, y, oldx, oldy);  
        }  
    }  
	
}
