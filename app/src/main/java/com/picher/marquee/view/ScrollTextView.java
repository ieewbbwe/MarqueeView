package com.picher.marquee.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @since 2018/08/07 10:52
 */
public class ScrollTextView extends View {
	private @Nullable String text;
	private Paint paint = new Paint();
	private ValueAnimator animator = new ValueAnimator();
	private int dx = 0;
	private Rect rect = new Rect();

	public ScrollTextView(Context context) {
		this(context, null);
	}

	public ScrollTextView(Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ScrollTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, getResources().getDisplayMetrics()));
		paint.setColor(Color.BLACK);

		//设置跑马灯动画
		animator.setInterpolator(new LinearInterpolator());
		animator.setDuration(1000);
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override public void onAnimationUpdate(ValueAnimator animation) {
				dx = -(int) animation.getAnimatedValue();
				invalidate();
			}
		});
	}


	public void setText(@NonNull String text) {
		this.text = text;
	}

	public void start(int dx) {
		animator.setIntValues(0, dx);
		animator.start();
	}

	public void reset(){
		dx = 0;
	}

	public int getTextWidth() {
		if (text != null) {
			paint.getTextBounds(text, 0, text.length(), rect);
			return rect.width();
		} else {
			return 0;
		}
	}

	@Override protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (text == null) {
			return;
		}
		paint.getTextBounds(text, 0, text.length(), rect);
		Paint.FontMetrics fontMetrics = paint.getFontMetrics();
		float baseLine = (getHeight() - fontMetrics.bottom - fontMetrics.top) / 2;
		canvas.drawText(text, dx, baseLine, paint);
	}
}
