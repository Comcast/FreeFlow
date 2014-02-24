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
package com.comcast.freeflow.examples.freeflowphotogrid;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

public class ControlLinearLayout extends LinearLayout {
	
	private static final String TAG = "ControlLinearLayout";
	
	public ControlLinearLayout(Context context) {
		super(context);
	}

	public ControlLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.d(TAG, "created w xml");
	}

	public ControlLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		
		super(context, attrs, defStyle);
		Log.d(TAG, "created w stys");
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Log.d(TAG, "onMeasure");
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		Log.d(TAG, "onLayout");
		super.onLayout(changed, l, t, r, b);
	}
	

}
