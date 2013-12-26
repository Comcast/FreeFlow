package com.arpitonline.ubercal;

import org.freeflow.core.AbsLayoutContainer;
import org.freeflow.core.AbsLayoutContainer.OnItemSelectedListener;
import org.freeflow.core.Container;
import org.freeflow.core.ItemProxy;
import org.freeflow.layouts.HGridLayout;
import org.freeflow.layouts.VGridLayout;

import com.arpitonline.ubercal.core.DaysInMonthAdapter;
import com.arpitonline.ubercal.core.Month;
import com.arpitonline.ubercal.core.YearAdapter;
import com.arpitonline.utils.DisplayUtils;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Point;
import android.util.Log;
import android.view.Menu;

public class CalendarMain extends Activity implements OnItemSelectedListener {
	
	public static final String TAG = "CalendarMain";
	
	private YearAdapter yearProvider = new YearAdapter();
	private DaysInMonthAdapter days = new DaysInMonthAdapter();
	private VGridLayout yearLayout;
	private VGridLayout monthLayout;
	
	private Container container;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		container = new Container(this);
		
		
		Point size = DisplayUtils.getScreenSize(this);
		
		yearLayout = new VGridLayout();
		yearLayout.setItemWidth(size.x/3);
		yearLayout.setItemHeight(size.y/2);
		
		
		monthLayout = new VGridLayout();
		monthLayout.setItemWidth(size.x/7);
		monthLayout.setItemHeight(100);
		
		
		container.setAdapter(yearProvider);
		//container.setAdapter(new DaysInMonthAdapter());
		container.setLayout(yearLayout);
		container.setOnItemSelectedListener(this);
		
		setContentView(container);
		
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.calendar_main, menu);
		return true;
	}


	@Override
	public void onItemSelected(AbsLayoutContainer parent, ItemProxy proxy) {
		Log.d(TAG, "==> "+proxy.itemSection+"/"+proxy.itemIndex);
		if(container.getLayout() == yearLayout){
			container.setLayout(monthLayout);
			container.setAdapter(new DaysInMonthAdapter());
		}
	}


	@Override
	public void onNothingSelected(AbsLayoutContainer parent) {
	}

}
