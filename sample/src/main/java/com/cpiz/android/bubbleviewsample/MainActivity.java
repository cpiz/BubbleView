package com.cpiz.android.bubbleviewsample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @SuppressWarnings("unused")
    @OnClick({R.id.btn_bubble_popup_window_sample, R.id.btn_bubble_text_view_sample})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_bubble_popup_window_sample:
                startActivity(new Intent(MainActivity.this, PopupWindowSampleActivity.class));
                break;
            case R.id.btn_bubble_text_view_sample:
                startActivity(new Intent(MainActivity.this, BubbleTextViewSampleActivity.class));
                break;
        }
    }
}
