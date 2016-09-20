package com.cpiz.android.bubbleviewsample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;

import com.cpiz.android.bubbleview.BubblePopupWindow;
import com.cpiz.android.bubbleview.BubbleStyle;
import com.cpiz.android.bubbleview.BubbleTextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private BubblePopupWindow mBubblePopupWindow;
    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_anchor).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        popupBubble(v);
    }

    @Override
    protected void onDestroy() {
        if (mBubblePopupWindow != null) {
            mBubblePopupWindow.dismiss();
        }

        super.onDestroy();
    }

    private void popupBubble(View v) {
        if (mBubblePopupWindow == null) {
            View rootView = LayoutInflater.from(this).inflate(R.layout.simple_text_bubble, null);
            BubbleTextView bubbleView = (BubbleTextView) rootView.findViewById(R.id.popup_bubble);
            mBubblePopupWindow = new BubblePopupWindow(rootView, bubbleView);
            mBubblePopupWindow.setCanceledOnTouch(true);
            mBubblePopupWindow.setCanceledOnTouchOutside(true);
            mBubblePopupWindow.setCanceledOnLater(3000);
        }

        mBubblePopupWindow.showArrowTo(v, BubbleStyle.ArrowDirection.valueOf(1 + (i++ % 4)));
    }
}
