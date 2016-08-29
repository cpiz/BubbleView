package com.cpiz.android.bubbleview;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by uchia on 8/23/2016.
 */
public interface FloatFunc {
    void setFloatX(int x);
    void setFloatY(int y);
    void show(View parent, WindowManager wm, Window window);
    void hide();
    void dismiss();
}
