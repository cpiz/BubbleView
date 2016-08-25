package com.cpiz.android.bubbleview.utils;

import android.view.View;
import android.view.WindowManager;

import com.cpiz.android.bubbleview.FloatFunc;

/**
 * Created by uchia on 8/23/2016.
 */
public class WindowManagerHelper {
    public static void updateParentWindowManager(WindowManager wm,WindowManager.LayoutParams wlp, View v, int left, int top){
        wlp.x = left;
        wlp.y = top;
        ((FloatFunc)v).setFloatX(left);
        ((FloatFunc)v).setFloatY(top);
        wm.updateViewLayout(v,wlp);
    }

    public static void removeViewFromWindowManager(WindowManager wm,View v){
        wm.removeView(v);
        wm = null;
    }
}
