package com.comcast.freeflow.debug;

import com.comcast.freeflow.core.Container;

import android.view.MotionEvent;

public class TouchDebugUtils {
	
	public static String getTouchModeString(int touchMode){
		switch(touchMode){
			case (Container.TOUCH_MODE_REST): return "touch_mode_rest";
			case (Container.TOUCH_MODE_DOWN): return "touch_mode_down";
			case (Container.TOUCH_MODE_TAP): return "touch_mode_tap";
			case (Container.TOUCH_MODE_SCROLL): return "touch_mode_scroll";
			case (Container.TOUCH_MODE_FLING): return "touch_mode_fling";
			case (Container.TOUCH_MODE_DONE_WAITING): return "touch_mode_done_waiting";
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
