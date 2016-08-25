package com.cpiz.android.bubbleviewsample;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cpiz.android.bubbleview.FloatBubbleFrameLayout;
import com.cpiz.android.bubbleview.FloatListener;

/**
 * Created by uchia on 8/23/2016.
 */
public class TwoActivity extends Activity {

    Button btn;
    Button btn2;
    Button btn3;
    Button btn4;
    Button btn5;
    Button btn6;
    Button btn7;
    Button btn8;
    Button btn9;
    Button btn10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.acitivty_two);

        btn = (Button)findViewById(R.id.bubble_btn);
        btn2 = (Button)findViewById(R.id.bubble_btn1);
        btn3 = (Button)findViewById(R.id.bubble_btn2);
        btn4 = (Button)findViewById(R.id.bubble_btn4);
        btn5 = (Button)findViewById(R.id.bubble_btn5);
        btn6 = (Button)findViewById(R.id.bubble_btn6);
        btn7 = (Button)findViewById(R.id.bubble_btn7);
        btn8 = (Button)findViewById(R.id.bubble_btn8);
        btn9 = (Button)findViewById(R.id.bubble_btn9);
        btn10 = (Button)findViewById(R.id.bubble_btn10);
        setClickContent(btn,220f);
        setClickContent(btn2,128f);
        setClickContent(btn3,120f);
        setClickContent(btn4,200f);
        setClickContent(btn5,200f);
        setClickContent(btn6,200f);
        setClickContent(btn7,200f);
        setClickContent(btn8,200f);
        setClickContent(btn9,200f);
        setClickContent(btn10,100f);
    }

    private void setClickContent(final Button btn, final float size){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FloatBubbleFrameLayout bubbleFrameLayout = new FloatBubbleFrameLayout(TwoActivity.this);
                bubbleFrameLayout.setLayoutParams(
                        new FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.WRAP_CONTENT,
                                FrameLayout.LayoutParams.WRAP_CONTENT));
                bubbleFrameLayout.setFillColor(Color.WHITE);
                TextView textView = new TextView(TwoActivity.this);
                textView.setTextSize(size);
                textView.setPadding(10,10,10,10);
                textView.setText("哈哈");
                textView.setTextColor(Color.BLUE);
                textView.setGravity(Gravity.CENTER);
                textView.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));

                //添加content
                bubbleFrameLayout.addView(textView);

                //弹出bubbleFrameLayout
                bubbleFrameLayout.show(btn,getWindowManager());

                //控件由于继承FrameLayout，其具有点击事件
                bubbleFrameLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bubbleFrameLayout.dismiss();
                    }
                });

                //控件出现与消失回调
                bubbleFrameLayout.addFloatLayoutListener(new FloatListener() {
                    @Override
                    public void show() {

                    }

                    @Override
                    public void dismiss() {
                        Toast.makeText(TwoActivity.this,"I will dismiss!",Toast.LENGTH_SHORT).show();
                    }
                });

                //解除回调注册
                //bubbleFrameLayout.removeFloatLayoutListener();
            }
        });
    }

}
