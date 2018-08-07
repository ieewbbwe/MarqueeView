package com.picher.marquee.view;

import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @since 2018/08/07 11:31
 */
public class LooperController {

	private final long switchDelayMillis;

	private ViewFlipper viewFlipper;

	private boolean isLoop = false;

	private final Runnable flipRunnable = new Runnable() {
		@Override
		public void run() {
			if (isLoop) {
				viewFlipper.showNext();
				postDelay(flipRunnable, switchDelayMillis);
			}
		}
	};

	private LooperController(List<String> contentCollection, final ViewFlipper viewFlipper, final long switchDelayMillis) {
		this.viewFlipper = viewFlipper;
		this.switchDelayMillis = switchDelayMillis;

		for (String content : contentCollection) {
			ScrollTextView textView = new ScrollTextView(viewFlipper.getContext());
			textView.setText(content);
			viewFlipper.addView(textView);
		}

		viewFlipper.getOutAnimation().setAnimationListener(new Animation.AnimationListener() {

			@Override public void onAnimationStart(Animation animation) {
			}

			@Override public void onAnimationEnd(Animation animation) {
				int childCount = viewFlipper.getChildCount();
				int currentIndex = viewFlipper.indexOfChild(viewFlipper.getCurrentView());
				int i = (currentIndex - 1 + childCount) % childCount;
				ScrollTextView preView = (ScrollTextView) viewFlipper.getChildAt(i);
				preView.reset();

				final ScrollTextView currentView = (ScrollTextView) viewFlipper.getCurrentView();
				final int dx = currentView.getTextWidth() - currentView.getWidth();
				if (dx > 0) {
					postDelay(new Runnable() {
						@Override public void run() {
							currentView.start(dx);
						}
					}, 500);
				}
			}

			@Override public void onAnimationRepeat(Animation animation) {
			}
		});
	}

	public static LooperController createLooperController(List<String> contentCollection,
	                                                      ViewFlipper viewFlipper,
	                                                      long switchDelayMillis) {
		return new LooperController(contentCollection, viewFlipper, switchDelayMillis);
	}

	public void startLoop() {
		isLoop = true;
		post(flipRunnable);
	}

	private void post(Runnable runnable) {
		viewFlipper.post(runnable);
	}

	private void postDelay(Runnable runnable, long delayMillis) {
		viewFlipper.postDelayed(runnable, delayMillis);
	}
}
