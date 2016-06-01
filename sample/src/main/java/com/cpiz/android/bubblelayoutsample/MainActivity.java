package com.cpiz.android.bubblelayoutsample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.cpiz.android.bubbleview.BubbleTextView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        setContentView(R.layout.activity_test);
//        final BubbleTextView mBubbleLayout = (BubbleTextView) findViewById(R.id.bubble);
//        if (mBubbleLayout != null) {
//            mBubbleLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.d(TAG, String.format("left=%d, top=%d, right=%d, bottom=%d",
//                            mBubbleLayout.getPaddingLeft(),
//                            mBubbleLayout.getPaddingTop(),
//                            mBubbleLayout.getPaddingRight(),
//                            mBubbleLayout.getPaddingBottom()));
//                    mBubbleLayout.setPadding(
//                            mBubbleLayout.getPaddingLeft() + 2,
//                            mBubbleLayout.getPaddingTop() + 2,
//                            mBubbleLayout.getPaddingRight() + 2,
//                            mBubbleLayout.getPaddingBottom() + 2);
//                }
//            });
//        }
    }
}
