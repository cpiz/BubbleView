package com.cpiz.android.bubbleviewsample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.cpiz.android.bubbleview.BubbleTextView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        setContentView(R.layout.activity_test);

        BubbleTextView bubbleTextView = (BubbleTextView) findViewById(R.id.bubble);
        if (bubbleTextView != null) {
            bubbleTextView.setPadding(50, 50, 50, 50);
        }
    }
}
