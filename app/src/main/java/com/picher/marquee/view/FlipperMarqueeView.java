package com.picher.marquee.view;

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
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ScrollView;
import android.widget.ViewFlipper;

import java.util.List;

/**
 * Created by picher on 2018/8/7.
 * Describe：
 */

public class FlipperMarqueeView extends ViewFlipper {

    private List<String> strings;
    private boolean isStart;

    public FlipperMarqueeView(Context context) {
        this(context, null);
    }

    public FlipperMarqueeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setContentList(List<String> list){
        this.strings = list;
        if(strings != null && strings.size() > 0){
            generateChild();
        }

        getOutAnimation().setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                int childCount = getChildCount();
                int currentIndex = indexOfChild(getCurrentView());
                int i = (currentIndex - 1 + childCount) % childCount;
                ScrollTextView preView = (ScrollTextView) getChildAt(i);
                preView.reset();

                final ScrollTextView currentView = (ScrollTextView) getCurrentView();
                final int dx = currentView.getTextWidth() - currentView.getWidth();
                if (dx > 0) {
                    postDelayed(new Runnable() {
                        @Override public void run() {
                            currentView.start(dx);
                        }
                    }, 500);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    private void generateChild() {
        removeAllViews();
        for(String marquee : strings){
            ScrollTextView view = new ScrollTextView(getContext());
            view.setText(marquee);
            addView(view);
        }
    }

    public void start() {
        isStart = true;
        post(flipRunnable);
    }

    private final Runnable flipRunnable = new Runnable() {
        @Override
        public void run() {
            if (isStart) {
                showNext();
                postDelayed(flipRunnable, 5000);
            }
        }
    };

    /**
     * 可自动滚动的TextView
     */
    public class ScrollTextView extends View {
        private @Nullable
        String text;
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

        public void reset() {
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

}
