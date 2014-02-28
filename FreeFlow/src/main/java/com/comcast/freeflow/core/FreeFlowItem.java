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

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

public class FreeFlowItem {
	public int itemIndex;
	public int itemSection;
	public Object data;
	public boolean isHeader = false;
	public Rect frame;
	public View view;
	public Bundle extras;

	public static FreeFlowItem clone(FreeFlowItem desc) {
		if (desc == null)
			return null;

		FreeFlowItem fd = new FreeFlowItem();
		fd.itemIndex = desc.itemIndex;
		fd.itemSection = desc.itemSection;
		fd.data = desc.data;
		fd.frame = new Rect(desc.frame);
		fd.isHeader = desc.isHeader;
		fd.view = desc.view;
		fd.extras = desc.extras;
		return fd;
	}
}
