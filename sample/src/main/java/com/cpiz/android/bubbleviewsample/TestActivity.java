package com.cpiz.android.bubbleviewsample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.cpiz.android.bubbleview.BubbleTextView;

public class TestActivity extends AppCompatActivity {

    @BindView(R.id.btv1)
    BubbleTextView btv1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
    }

    int pad = 10;

    @OnClick(R.id.iv2)
    public void onTestClick() {
        btv1.setPadding(pad, pad, pad, pad);
        pad++;
    }
}
