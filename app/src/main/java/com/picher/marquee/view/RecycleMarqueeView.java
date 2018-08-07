package com.picher.marquee.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.picher.marquee.R;

import java.util.List;

/**
 * Created by picher on 2018/8/7.
 * Describe：RecycleView 跑马灯
 */

public class RecycleMarqueeView extends RelativeLayout {

    private RecyclerView marqueeRv;
    private LinearLayoutManager linearLayoutManager;
    private List<String> marqueeListData;
    private MarqueeAdapter marqueeAdapter;
    private View shadowView;
    private boolean isStart = false;

    public RecycleMarqueeView(Context context) {
        this(context, null);
    }

    public RecycleMarqueeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecycleMarqueeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        marqueeRv = new RecyclerView(getContext());
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        marqueeAdapter = new MarqueeAdapter();
        marqueeRv.setLayoutManager(linearLayoutManager);
        marqueeRv.setAdapter(marqueeAdapter);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(marqueeRv, layoutParams);

        // 创建遮罩防止手动滑动RV
        shadowView = new View(getContext());
        shadowView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        //addView(shadowView, layoutParams);

        // 滚动完成手动调用条目的startScroll 如果文本超长则会滚动，如果没有超过一屏则不变
        marqueeRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    View firstChild = recyclerView.getLayoutManager().getChildAt(0);
                    if (firstChild != null && firstChild instanceof MarqueeScrollerView) {
                        ((MarqueeScrollerView) firstChild).startScroll();
                    }
                }
            }
        });
    }

    public void smoothScrollToPosition(int pos){
        if(marqueeRv != null){
            marqueeRv.smoothScrollToPosition(pos);
        }
    }

    public void setContentList(List<String> marqueeList) {
        this.marqueeListData = marqueeList;
        marqueeAdapter.notifyDataSetChanged();
    }

    public void start() {
        if(isStart){
            return;
        }
        CountDownTimer countDownTimer = new CountDownTimer(6000, 6000) {

            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                Log.d("picher","滑动到："+marqueeAdapter.getPosition());
                marqueeAdapter.setPosition(marqueeAdapter.getPosition() + 1);
                marqueeRv.smoothScrollToPosition(marqueeAdapter.getPosition());
                start();
            }
        };
        countDownTimer.start();
        isStart = true;
    }

    public class SmoothLinearLayoutManager extends LinearLayoutManager {

        private static final float MILLISECONDS_PER_INCH = 300f;
        private Context mContext;

        public SmoothLinearLayoutManager(Context context, int horizontal, boolean b) {
            super(context, horizontal, b);
            mContext = context;
        }

        @Override
        public void smoothScrollToPosition(RecyclerView recyclerView,
                                           RecyclerView.State state, final int position) {

            if(mContext == null) {
                return;
            }
            LinearSmoothScroller smoothScroller =
                    new LinearSmoothScroller(mContext) {

                        @Override
                        public PointF computeScrollVectorForPosition
                                (int targetPosition) {
                            return SmoothLinearLayoutManager.this
                                    .computeScrollVectorForPosition(targetPosition);
                        }

                        @Override
                        protected float calculateSpeedPerPixel
                                (DisplayMetrics displayMetrics) {
                            return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
                        }
                    };

            smoothScroller.setTargetPosition(position);
            startSmoothScroll(smoothScroller);
        }
    }

    /**
     * Marquee Adapter
     */
    public class MarqueeAdapter extends RecyclerView.Adapter<MarqueeAdapter.ViewHolder> {

        private int currentPosition = 0;

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_marquee, parent, false);
            v.getLayoutParams().width = parent.getMeasuredWidth();
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.textView.setText(marqueeListData.get(position % marqueeListData.size()));
        }

        public void setPosition(int pos) {
            this.currentPosition = pos;
        }

        public int getPosition() {
            return currentPosition;
        }

        @Override
        public int getItemCount() {
            return Integer.MAX_VALUE;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public MarqueeScrollerView textView;

            public ViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.breaking_news_title);
            }
        }
    }
}
