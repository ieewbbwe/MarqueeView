package com.picher.marquee.view;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

public class MarqueeScrollerView extends AppCompatTextView {

    /**
     * 默认滚动时间
     */
    private static final int ROLLING_INTERVAL_DEFAULT = 5000;
    /**
     * 第一次滚动默认延迟
     */
    private static final int FIRST_SCROLL_DELAY_DEFAULT = 0;
    /**
     * 滚动模式-一直滚动
     */
    public static final int SCROLL_FOREVER = 100;
    /**
     * 滚动模式-只滚动一次
     */
    public static final int SCROLL_ONCE = 101;
    /**
     * 每行内容得留白
     **/
    public static final int DEFAULT_EMPTY = 100;

    /**
     * 滚动器
     */
    private Scroller mScroller;
    /**
     * 滚动一次的时间
     */
    private int mRollingInterval;
    /**
     * 滚动的初始 X 位置
     */
    private int mXPaused = 0;
    /**
     * 是否暂停
     */
    private boolean mPaused = true;
    /**
     * 是否第一次
     */
    private boolean mFirst = true;
    /**
     * 滚动模式
     */
    private int mScrollMode;
    /**
     * 初次滚动时间间隔
     */
    private int mFirstScrollDelay;
    /**
     * 滚动完成后是否返回起始位置
     **/
    private boolean isReverse = false;

    public MarqueeScrollerView(Context context) {
        this(context, null);
    }

    public MarqueeScrollerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarqueeScrollerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs, defStyle);
    }

    @Override
    public boolean isFocused() {
        return true;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        if (focused) {
            super.onFocusChanged(focused, direction, previouslyFocusedRect);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (hasWindowFocus)
            super.onWindowFocusChanged(hasWindowFocus);
    }

    private void initView(Context context, AttributeSet attrs, int defStyle) {
        //TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MarqueeTextView);
        mRollingInterval = ROLLING_INTERVAL_DEFAULT;//typedArray.getInt(R.styleable.MarqueeTextView_scroll_interval, ROLLING_INTERVAL_DEFAULT);
        mScrollMode = SCROLL_ONCE;//typedArray.getInt(R.styleable.MarqueeTextView_scroll_mode, SCROLL_FOREVER);
        mFirstScrollDelay = FIRST_SCROLL_DELAY_DEFAULT;//typedArray.getInt(R.styleable.MarqueeTextView_scroll_first_delay, FIRST_SCROLL_DELAY_DEFAULT);
        //typedArray.recycle();
        setSingleLine();
        setEllipsize(null);
    }

    public void startScroll() {
        mXPaused = 0;
        mPaused = true;
        mFirst = true;
        resumeScroll();
    }

    public void resumeScroll() {
        int scrollingLen = calculateScrollingLen();
        final int distance = scrollingLen - getScreenWidth(getContext()) + DEFAULT_EMPTY;

        if (!mPaused || distance < DEFAULT_EMPTY)
            return;
        // 设置水平滚动
        setHorizontallyScrolling(true);

        // 使用 LinearInterpolator 进行滚动
        if (mScroller == null) {
            mScroller = new Scroller(this.getContext(), new LinearInterpolator());
            setScroller(mScroller);
        }
        final int duration = (Double.valueOf(mRollingInterval * distance * 1.00000
                / scrollingLen)).intValue();
        if (mFirst) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScroller.startScroll(mXPaused, 0, distance, 0, duration);
                    invalidate();
                    mPaused = false;
                }
            }, mFirstScrollDelay);
        } else {
            mScroller.startScroll(mXPaused, 0, distance, 0, duration);
            invalidate();
            mPaused = false;
        }
    }

    public void pauseScroll() {
        if (null == mScroller)
            return;
        if (mPaused)
            return;
        mPaused = true;
        mXPaused = mScroller.getCurrX();
        mScroller.abortAnimation();
    }

    public void stopScroll() {
        if (null == mScroller) {
            return;
        }
        mPaused = true;
        if (isReverse) {
            mScroller.startScroll(0, 0, 0, 0, 0);
        }
    }

    /**
     * 计算滚动的距离
     *
     * @return 滚动的距离
     */
    private int calculateScrollingLen() {
        TextPaint tp = getPaint();
        Rect rect = new Rect();
        String strTxt = getText().toString();
        tp.getTextBounds(strTxt, 0, strTxt.length(), rect);
        return rect.width();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (null == mScroller) return;
        if (mScroller.isFinished() && (!mPaused)) {
            if (mScrollMode == SCROLL_ONCE) {
                stopScroll();
                return;
            }
            mPaused = true;
            mXPaused = -1 * getWidth();
            mFirst = false;
            this.resumeScroll();
        }
    }

    public static int getScreenWidth(Context context) {
        if(context != null){
            WindowManager wm = (WindowManager) context
                    .getSystemService(Context.WINDOW_SERVICE);
            if(wm != null){
                DisplayMetrics outMetrics = new DisplayMetrics();
                wm.getDefaultDisplay().getMetrics(outMetrics);
                return outMetrics.widthPixels;
            }
        }
        return 0;
    }

    public int getRndDuration() {
        return mRollingInterval;
    }

    public void setRndDuration(int duration) {
        this.mRollingInterval = duration;
    }

    public void setScrollMode(int mode) {
        this.mScrollMode = mode;
    }

    public int getScrollMode() {
        return this.mScrollMode;
    }

    public void setScrollFirstDelay(int delay) {
        this.mFirstScrollDelay = delay;
    }

    public int getScrollFirstDelay() {
        return mFirstScrollDelay;
    }

    public boolean isReverse() {
        return isReverse;
    }

    public boolean isPaused() {
        return mPaused;
    }
}