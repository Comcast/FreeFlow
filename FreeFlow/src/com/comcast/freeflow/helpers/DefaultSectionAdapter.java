/*******************************************************************************
 * Copyright 2013 Comcast Cable Communications Management, LLC
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.comcast.freeflow.helpers;

import java.util.ArrayList;

import com.comcast.freeflow.core.FreeFlowItem;
import com.comcast.freeflow.core.Section;
import com.comcast.freeflow.core.SectionedAdapter;
import com.comcast.freeflow.utils.ViewUtils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.Shape;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class DefaultSectionAdapter implements SectionedAdapter {

	private ArrayList<Section> sections = new ArrayList<Section>();
	protected Context context;
	protected boolean showHeaders;
	public int headerHeight = 20;
	public int itemHeight = 20;

	public DefaultSectionAdapter(Context context, int headerCount, int itemCount) {
		this(context, headerCount,itemCount, true);
	}
	
	public DefaultSectionAdapter(Context context, int headerCount, int itemCount, boolean showHeaders) {
		this.context = context;
		this.showHeaders = showHeaders;
		setData(headerCount, itemCount);
	}

	public void setData(int headerCount, int itemCount) {
		sections.clear();
		for (int i = 0; i < headerCount; i++) {
			Section s = new Section();
			s.setSectionTitle("Section " + i);
			for (int j = 0; j < itemCount; j++) {
				s.addItem(new Object());
			}
			sections.add(s);
		}

	}

	@Override
	public long getItemId(int section, int position) {
		return section * 1000 + position;
	}

	@Override
	@SuppressWarnings(value = { "deprecation" })
	public View getItemView(int section, int position, View convertView, ViewGroup parent) {
		TextView tv = null;
		if (convertView != null) {
			tv = (TextView) convertView;
		} else {
			tv = new TextView(context);
			RectShape rs = new RectShape();
			ShapeDrawable sd = new  BottomBorderBackground(rs, Color.WHITE, Color.GRAY);
			tv.setBackgroundDrawable(sd);
			tv.setPadding(20, 0, 0, 0);
			tv.setGravity(Gravity.CENTER_VERTICAL);
		}
		tv.setLayoutParams(new LayoutParams(300, itemHeight));
		tv.setText("s" + section + " p" + position);
		return tv;
	}

	@Override
	public View getHeaderViewForSection(int section, View convertView, ViewGroup parent) {
		TextView tv = null;
		if (convertView != null) {
			tv = (TextView) convertView;
		} else {
			tv = new TextView(context);
		}

		tv.setFocusable(false);
		tv.setBackgroundColor(Color.GRAY);
		tv.setLayoutParams(new LayoutParams(300, headerHeight));
		tv.setText("section header" + section);

		return tv;
	}

	@Override
	public int getNumberOfSections() {
		return sections.size();
	}

	@Override
	public Section getSection(int index) {
		if (index < sections.size() && index >= 0)
			return sections.get(index);

		return null;
	}

	public ArrayList<Section> getSections() {
		return sections;
	}

	@Override
	public Class[] getViewTypes() {
		Class[] types = { TextView.class, TextView.class };

		return types;
	}

	@Override
	public Class getViewType(FreeFlowItem proxy) {

		return TextView.class;
	}

	@Override
	public boolean shouldDisplaySectionHeaders() {
		return showHeaders;
	}
	
	public class BottomBorderBackground extends ShapeDrawable {
	    private final Paint fillpaint, strokepaint;
	 
	    public BottomBorderBackground(Shape s, int fill, int stroke) {
	        super(s);
	        fillpaint = new Paint(this.getPaint());
	        fillpaint.setColor(fill);
	        strokepaint = new Paint(fillpaint);
	        strokepaint.setStyle(Paint.Style.STROKE);
	        strokepaint.setStrokeWidth(2);
	        strokepaint.setColor(stroke);
	    }
	 
	    @Override
	    protected void onDraw(Shape shape, Canvas canvas, Paint paint) {
	        shape.draw(canvas, fillpaint);
	        canvas.drawLine(0, shape.getHeight(), shape.getWidth(), shape.getHeight(), strokepaint);   
	    }
	}

}
