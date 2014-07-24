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
package com.comcast.freeflow.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import android.view.View;

public class ViewPool {

	private LinkedHashMap<Class, ArrayList<View>> viewPool;

	public ViewPool() {
	}

	public void initializeViewPool(Class[] viewTypes) {
		viewPool = new LinkedHashMap<Class, ArrayList<View>>();
		for (int i = 0; i < viewTypes.length; i++) {
			viewPool.put(viewTypes[i], new ArrayList<View>());
		}
	}

	public void returnViewToPool(View view) {
		if (viewPool.containsKey(view.getClass()))
			viewPool.get(view.getClass()).add(view);
	}

	public View getViewFromPool(Class viewType) {
		if (viewPool.get(viewType) == null || viewPool.get(viewType).size() == 0)
			return null;

		return viewPool.get(viewType).remove(0);
	}

}
