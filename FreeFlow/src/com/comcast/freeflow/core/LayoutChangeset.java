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

import android.graphics.Rect;
import android.util.Pair;

public class LayoutChangeset {
	protected ArrayList<Pair<FreeFlowItem, Rect>> moved = new ArrayList<Pair<FreeFlowItem, Rect>>();
	protected ArrayList<FreeFlowItem> removed = new ArrayList<FreeFlowItem>();
	protected ArrayList<FreeFlowItem> added = new ArrayList<FreeFlowItem>();

	public LayoutChangeset() {
	}

	public void addToMoved(FreeFlowItem proxy, Rect oldFrame) {
		moved.add(new Pair<FreeFlowItem, Rect>(proxy, oldFrame));
	}

	public void addToDeleted(FreeFlowItem proxy) {
		removed.add(proxy);
	}

	public void addToAdded(FreeFlowItem proxy) {
		added.add(proxy);
	}

	public ArrayList<FreeFlowItem> getAdded() {
		return added;
	}
	
	public ArrayList<FreeFlowItem> getRemoved() {
		return removed;
	}

	public ArrayList<Pair<FreeFlowItem, Rect>> getMoved() {
		return moved;
	}

	@Override
	public String toString() {
		return 	"Added: " + added.size() + "," +
				"Removed: " + removed.size()+ ","+
				"Moved: " + moved.size();
	}

	public boolean isEmpty() {
		return (added.size() == 0 && removed.size() == 0 && moved.size() == 0);	
	}

}
