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
package com.comcast.freeflow.animations;

import java.util.ArrayList;

import com.comcast.freeflow.core.ItemProxy;

import android.graphics.Rect;
import android.util.Pair;
import android.view.View;
import android.view.ViewPropertyAnimator;

public class ScaleAnimator extends DefaultLayoutAnimator {

	private int duration = 250;
	private ArrayList<ViewPropertyAnimator> scaleSet = new ArrayList<ViewPropertyAnimator>();

	public ScaleAnimator() {
	}

	protected void animateMovedViews() {
		ArrayList<Pair<ItemProxy, Rect>> moved = changeSet.getMoved();

		for (Pair<ItemProxy, Rect> item : moved) {
			ItemProxy proxy = ItemProxy.clone(item.first);
			View v = proxy.view;

			proxy.frame.left -= callback.getViewportLeft();
			proxy.frame.top -= callback.getViewportTop();

			// if (v instanceof StateListener)
			// ((StateListener) v).ReportCurrentState(proxy.state);

			transitionToFrame(item.second, proxy, v);

		}
	}

//	@Override
//	public void transitionToFrame(final Frame of, final ItemProxy nf, final View v) {
//
//		if (v == null)
//			return;
//
//		int wSpec = MeasureSpec.makeMeasureSpec(nf.frame.width, MeasureSpec.EXACTLY);
//		int hSpec = MeasureSpec.makeMeasureSpec(nf.frame.height, MeasureSpec.EXACTLY);
//
//		final Frame nff = nf.frame;
//
//		v.measure(wSpec, hSpec);
//		v.layout(nff.left, nff.top, nff.left + nff.width, nff.top + nff.height);
//
//		v.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
//
//			@Override
//			public boolean onPreDraw() {
//
//				v.getViewTreeObserver().removeOnPreDrawListener(this);
//
//				float scaleX = (float) of.width / (float) nff.width;
//				float scaleY = (float) of.height / (float) nff.height;
//
//				int tx = nff.left - of.left + (nff.width - of.width) / 2;
//				int ty = nff.top - of.top + (nff.height - of.height) / 2;
//
//				v.setScaleX(scaleX);
//				v.setScaleY(scaleY);
//				v.setTranslationX(-tx);
//				v.setTranslationY(-ty);
//
//				scaleSet.add(v.animate().translationX(0).translationY(0).scaleX(1).scaleY(1).setDuration(duration)
//						.withEndAction(new Runnable() {
//
//							@Override
//							public void run() {
//								int wSpec = MeasureSpec.makeMeasureSpec(nff.width, MeasureSpec.EXACTLY);
//								int hSpec = MeasureSpec.makeMeasureSpec(nff.height, MeasureSpec.EXACTLY);
//
//								v.setTranslationX(0);
//								v.setTranslationY(0);
//								v.measure(wSpec, hSpec);
//								v.layout(nf.frame.left, nf.frame.top, nf.frame.left + nf.frame.width, nf.frame.top
//										+ nf.frame.height);
//							}
//						}));
//
//				return true;
//			}
//		});
//
//	}

	@Override
	public void cancel() {
		super.cancel();

		for (ViewPropertyAnimator anim : scaleSet) {
			anim.cancel();
		}

		scaleSet.clear();
	}

}
