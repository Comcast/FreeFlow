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

package com.comcast.freeflow.layouts;

import android.util.Log;

import com.comcast.freeflow.core.SectionedAdapter;

public abstract class FreeFlowLayoutBase implements FreeFlowLayout {
	
	protected int width = -1;
	protected int height = -1;
	
	protected SectionedAdapter itemsAdapter;
	
	@Override
	public void setDimensions(int measuredWidth, int measuredHeight) {
		Log.d("dimen",this.getClass().getName()+"set dimension: "+measuredWidth+", "+measuredHeight);
		if (measuredHeight == height && measuredWidth == width) {
			return;
		}
		this.width = measuredWidth;
		this.height = measuredHeight;
	}
	
	
	@Override
	public void setAdapter(SectionedAdapter adapter) {
		this.itemsAdapter = adapter;
	}
	
}
