package com.picher.marquee;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.picher.marquee.view.CustomerTextView;
import com.picher.marquee.view.LooperController;
import com.picher.marquee.view.ScrollTextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

	private CustomerTextView customerTextView;
	ViewFlipper viewFlipper;
	private int currentIndex = 0;
	List<String> strings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

//		customerTextView = findViewById(R.id.marqueeView);
		strings = new ArrayList<>();
		strings.add("Start! 1111111111111111111111111111asdasd1111111111111111111111111 End！！");
		//strings.add("Start! 1111111 End！！");
		strings.add("Start! 222222222222222222222222222222222222222222222222222222222222222 End！！");
		strings.add("Start! 3333333 End！！");
//		customerTextView.setContentList(strings);
//
//		customerTextView.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Log.d("picher", "" + customerTextView.getCurrentIndex());
//			}
//		});
		viewFlipper = findViewById(R.id.filpper);

		LooperController controller = LooperController.createLooperController(strings, viewFlipper, 3000);
		controller.startLoop();
	}

}
