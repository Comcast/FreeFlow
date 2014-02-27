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
package com.comcast.freeflow.debug;

import com.comcast.freeflow.core.FreeFlowContainer;

import android.view.MotionEvent;

public class TouchDebugUtils {
	
	public static String getTouchModeString(int touchMode){
		switch(touchMode){
			case (FreeFlowContainer.TOUCH_MODE_REST): return "touch_mode_rest";
			case (FreeFlowContainer.TOUCH_MODE_DOWN): return "touch_mode_down";
			case (FreeFlowContainer.TOUCH_MODE_TAP): return "touch_mode_tap";
			case (FreeFlowContainer.TOUCH_MODE_SCROLL): return "touch_mode_scroll";
			case (FreeFlowContainer.TOUCH_MODE_FLING): return "touch_mode_fling";
			case (FreeFlowContainer.TOUCH_MODE_DONE_WAITING): return "touch_mode_done_waiting";
		}
		return "unknown touch event: "+touchMode;
	}
	
	/* Only single touches for the time being */
	public static String getMotionEventString(int action){
		switch(action){
			case(MotionEvent.ACTION_DOWN): return "action_down";
			case(MotionEvent.ACTION_UP): return "action_down";
			case(MotionEvent.ACTION_CANCEL): return "action_down";
			case(MotionEvent.ACTION_MOVE): return "action_move";
			case(MotionEvent.ACTION_OUTSIDE): return "action_outside";
			case(MotionEvent.ACTION_HOVER_ENTER): return "action_hover_enter";
			case(MotionEvent.ACTION_HOVER_EXIT): return "action_hover_exit";
			case(MotionEvent.ACTION_HOVER_MOVE): return "action_hover_move";
			case(MotionEvent.ACTION_MASK): return "action_mask";
		}
		return "unknown action event";
	}
}
