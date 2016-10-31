package com.cpiz.android.bubbleviewsample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.cpiz.android.bubbleview.BubbleTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BubbleTextViewSampleActivity extends AppCompatActivity {

    @BindView(R.id.bubble_text_sample)
    BubbleTextView mBubbleTextSample;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bubble_text_view_sample);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.view_anchor})
    public void onClick(View view) {
        mBubbleTextSample.requestUpdateBubble();
    }
}
