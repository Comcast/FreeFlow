package com.arpitonline.ubercal;

import org.freeflow.core.AbsLayoutContainer;
import org.freeflow.core.AbsLayoutContainer.OnItemSelectedListener;
import org.freeflow.core.Container;
import org.freeflow.core.ItemProxy;
import org.freeflow.layouts.VGridLayout;
import org.freeflow.layouts.animations.DefaultLayoutAnimator;

import android.R.color;
import android.app.Activity;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;

import com.arpitonline.ubercal.core.DaysInMonthAdapter;
import com.arpitonline.ubercal.core.YearAdapter;

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
		container.setBackgroundColor(0x00000000);

		DefaultLayoutAnimator animator = new DefaultLayoutAnimator();
		animator.newCellsAdditionAnimationStartDelay = 600;
		animator.newCellsAdditionAnimationDurationPerCell = 50;

//		animator.animateIndividualCellsSequentially = false;
		container.setLayoutAnimator(animator);

		Point size = getScreenSize(this);

		yearLayout = new VGridLayout();
		yearLayout.setItemWidth(size.x / 3);
		yearLayout.setItemHeight(size.y / 4);

		monthLayout = new VGridLayout();
		monthLayout.setItemWidth(size.x / 7);
		monthLayout.setItemHeight(100);

		container.setAdapter(yearProvider);
		// container.setAdapter(new DaysInMonthAdapter());
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
		Log.d(TAG, "==> " + proxy.itemSection + "/" + proxy.itemIndex);
		if (container.getLayout() == yearLayout) {
			container.setLayout(monthLayout);
			container.setAdapter(new DaysInMonthAdapter());
		}
	}

	@Override
	public void onNothingSelected(AbsLayoutContainer parent) {
	}

	public static Point getScreenSize(Activity act) {
		Rect rect = new Rect();
		act.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

		Point size = new Point();
		size.x = rect.width();
		size.y = rect.height() - rect.top;
		return size;
	}

}
