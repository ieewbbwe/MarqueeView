package com.picher.marquee;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.picher.marquee.view.CustomerMarqueeView;
import com.picher.marquee.view.FlipperMarqueeView;
import com.picher.marquee.view.RecycleMarqueeView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private CustomerMarqueeView customerMarqueeView;
    List<String> marqueeText = new ArrayList<>();
    private RecycleMarqueeView recycleMarqueeView;
    private FlipperMarqueeView flipperMarquee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        marqueeText.add("Start! 11111111111111111111111111111111111111111111111111111 End！！");
        //marqueeText.add("Start! 1111111 End！！");
        marqueeText.add("Start! 2222222 End！！");
        marqueeText.add("Start! 3333333 End！！");
        customerMarqueeView = findViewById(R.id.marqueeView);
        flipperMarquee = findViewById(R.id.flipperMarquee);
        recycleMarqueeView = findViewById(R.id.recycleMarquee);

        flipperMarquee();
        recycleViewMarquee();
        customerMarquee();

    }

    /**
     * Flipper 实现
     */
    private void flipperMarquee() {
        flipperMarquee.setContentList(marqueeText);
    }

    /**
     * recycleView 跑马灯
     */
    private void recycleViewMarquee() {
        recycleMarqueeView.setContentList(marqueeText);
    }

    /**
     * 自定义控件_Draw() 跑马灯
     */
    private void customerMarquee() {
        customerMarqueeView.setContentList(marqueeText);
        customerMarqueeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("picher","" + customerMarqueeView.getCurrentIndex());
            }
        });
    }

    public void clickStart(View v){
        customerMarqueeView.start();
        recycleMarqueeView.start();
        flipperMarquee.start();
    }

    public void clickPause(View v){
        customerMarqueeView.pause();
    }
}
