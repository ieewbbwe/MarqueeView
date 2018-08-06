package com.picher.marquee;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.picher.marquee.view.CustomerTextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private CustomerTextView customerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        customerTextView = findViewById(R.id.marqueeView);
        List<String> strings = new ArrayList<>();
        strings.add("Start! 11111111111111111111111111111111111111111111111111111 End！！");
        //strings.add("Start! 1111111 End！！");
        strings.add("Start! 2222222 End！！");
        strings.add("Start! 3333333 End！！");
        customerTextView.setContentList(strings);
    }

    public void clickStart(View v){
        customerTextView.start();
    }

    public void clickPause(View v){
        customerTextView.pause();
    }
}
