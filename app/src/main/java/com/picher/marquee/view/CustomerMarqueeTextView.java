package com.picher.marquee.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.picher.marquee.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by picher on 2018/8/2.
 * Describe：Scrolling TextView
 */

public class CustomerMarqueeTextView extends AppCompatTextView {

    // 切换下一条之前的停顿时间
    private static final int DEFAULT_PAUSE_TIME = 5000;
    // 默认切换一条的时间
    private static final int DEFAULT_SCROLL_TIME = 500;
    // 超长文本滚动完当前的时间
    private static final int DEFAULT_SCROLL_NOW_TIME = 5000;
    // 默认文本大小
    private static final int DEFAULT_CONTENT_SIZE = 50;
    // 默认文本颜色
    private static final int DEFAULT_CONTENT_COLOR = Color.BLACK;

    private ArrayList<String> mContentList = new ArrayList<>();

    // 布局尺寸
    private int viewWidth;
    private int viewHeight;
    private int paddingLeft;
    private int paddingTop;
    private int paddingRight;
    private int paddingBottom;
    // 文字属性
    private Paint contentPaint;
    private int contentColor = Color.BLACK;
    private int contentTextSize = 50;
    private int maxContentHeight;
    private int maxContentWidth;
    private int mCurrentIndex = 0;
    private String mCurrentString;
    private String mNextString;
    private float mCurrentX = 0;
    private float mCurrentY = 0;
    private int currentStringWidth;
    private int currentStringHeight;
    // 控制开关
    private boolean isScrollNext = false;
    private boolean isScrollNow = false;
    private boolean isStart = false;
    private boolean isPause = false;
    private int screenIndex;
    private int mSwitchDuration;
    private int mNextPauseDuration;

    public CustomerMarqueeTextView(Context context) {
        this(context, null);
    }

    public CustomerMarqueeTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomerMarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.CustomerMarqueeTextView);
        mNextPauseDuration = array.getInt(R.styleable.CustomerMarqueeTextView_next_pause_duration, DEFAULT_PAUSE_TIME);
        mSwitchDuration = array.getInt(R.styleable.CustomerMarqueeTextView_switch_item_duration, DEFAULT_SCROLL_TIME);
        if (array.hasValue(R.styleable.CustomerMarqueeTextView_switch_item_duration)) {
            contentTextSize = (int) array.getDimension(R.styleable.CustomerMarqueeTextView_switch_item_duration, DEFAULT_CONTENT_SIZE);
        }
        contentColor = array.getColor(R.styleable.CustomerMarqueeTextView_content_text_color, DEFAULT_CONTENT_COLOR);
        array.recycle();

        init();
    }

    public void setContentList(List<String> strings) {
        this.mContentList.addAll(strings);
    }

    private void init() {
        contentPaint = new Paint();
        contentPaint.setFlags(Paint.FAKE_BOLD_TEXT_FLAG);
        contentPaint.setAntiAlias(true);
        contentPaint.setDither(true);
        contentPaint.setTextSize(contentTextSize);
        contentPaint.setColor(contentColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = getMeasuredWidth();
        viewHeight = getMeasuredHeight();

        paddingLeft = getPaddingLeft();
        paddingTop = getPaddingTop();
        paddingRight = getPaddingRight();
        paddingBottom = getPaddingBottom();

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);


        if (null != mContentList && mContentList.size() > 0) {
            for (int i = 0; i < mContentList.size(); i++) {
                String contentString = mContentList.get(i);
                Rect contentBound = new Rect();
                contentPaint.getTextBounds(contentString, 0, contentString.length(), contentBound);
                int tempWidth = contentBound.width();
                int tempHeight = contentBound.height();
                maxContentHeight = Math.max(viewHeight, tempHeight);
                maxContentWidth = Math.max(viewWidth, tempWidth);
            }
        }

        if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
            setMeasuredDimension(widthSize, heightSize);
        } else if (widthMode == MeasureSpec.EXACTLY) {
            setMeasuredDimension(widthSize, maxContentHeight);
        } else if (heightMode == MeasureSpec.EXACTLY) {
            setMeasuredDimension(maxContentWidth, heightSize);
        } else {
            setMeasuredDimension(maxContentWidth, maxContentHeight);
        }

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mContentList.size() > 1) {
            // 循环滚动
            if (mCurrentIndex >= mContentList.size()) {
                mCurrentIndex = 0;
            }
            // 计算文本
            mCurrentString = mContentList.get(mCurrentIndex);
            mNextString = mContentList.get(mCurrentIndex + 1 >= mContentList.size() ? 0 : mCurrentIndex + 1);
            // 计算绘制区域
            currentStringWidth = calcStringSize(mCurrentString).width();
            currentStringHeight = calcStringSize(mCurrentString).height();
            if (getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
                mCurrentY = currentStringHeight + layoutParams.topMargin + paddingTop;
                //mCurrentX += paddingLeft; // 停止post后还会回掉一次 导致计算错误
            }

            screenIndex = currentStringWidth / viewWidth + 1;
            if (screenIndex > 1) {
                // 超过一屏则滚动到末尾 然后切换下一条新闻
                if (!isScrollNow && isStart) {
                    startScrollNow();
                }
            } else {
                // 切换下一条新闻
                if (!isScrollNext && isStart) {
                    startScrollNext();
                }
            }

            canvas.drawText(mCurrentString, mCurrentX, mCurrentY, contentPaint);
            canvas.drawText(mNextString, mCurrentX + screenIndex * viewWidth, mCurrentY, contentPaint);
        }
    }

    /**
     * 滚动完当前
     */
    private void startScrollNow() {
        isScrollNow = true;
        horizontalScroll(0, (screenIndex - 1) * viewWidth, DEFAULT_SCROLL_NOW_TIME, new LinearInterpolator(), new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                delay(mNextPauseDuration, new Runnable() {
                    @Override
                    public void run() {
                        isScrollNext = true;
                        horizontalScroll((screenIndex - 1) * viewWidth, screenIndex * viewWidth, new AccelerateDecelerateInterpolator(), new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                delay(mNextPauseDuration, new Runnable() {
                                    @Override
                                    public void run() {
                                        isScrollNext = false;
                                        isScrollNow = false;
                                        nextRefresh();
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });

    }

    /**
     * 切换到下一个
     */
    public void startScrollNext() {
        isScrollNext = true;
        ValueAnimator animator = ValueAnimator.ofFloat(0, viewWidth);
        animator.setDuration(mSwitchDuration);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());// 先快后慢
        animator.start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mCurrentX = -value + paddingLeft;
                postInvalidate();
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // 停顿2s 后切换下一条
                delay(mNextPauseDuration, new Runnable() {
                    @Override
                    public void run() {
                        isScrollNext = false;
                        nextRefresh();
                    }
                });
            }
        });
    }

    private void delay(int ms, Runnable runnable) {
        new Handler().postDelayed(runnable, ms);
    }

    private void horizontalScroll(int start, int end, TimeInterpolator interpolator, AnimatorListenerAdapter animatorListenerAdapter) {
        horizontalScroll(start, end, mSwitchDuration, interpolator, animatorListenerAdapter);
    }

    private void horizontalScroll(int start, int end, int time, TimeInterpolator interpolator, AnimatorListenerAdapter animatorListenerAdapter) {
        ValueAnimator animator = ValueAnimator.ofFloat(start, end);
        animator.setDuration(time);
        animator.setInterpolator(interpolator);
        animator.start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mCurrentX = -value + paddingLeft;
                postInvalidate();
            }
        });
        animator.addListener(animatorListenerAdapter);
    }

    private void nextRefresh() {
        mCurrentIndex++;
        mCurrentX = paddingLeft;
        postInvalidate();
    }

    private Rect calcStringSize(String str) {
        Rect rect = new Rect();
        contentPaint.getTextBounds(str, 0, str.length(), rect);
        return rect;
    }

    public int getCurrentIndex() {
        if (isScrollNext) {
            if (mCurrentIndex + 1 >= mContentList.size()) {
                return 0;
            } else {
                return mCurrentIndex + 1;
            }
        }
        return mCurrentIndex;
    }

    public void start() {
        isStart = true;
        isPause = false;
        postInvalidate();
    }

    public void pause() {
        isPause = true;
        isStart = false;
        postInvalidate();
    }
}
