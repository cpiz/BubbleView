package com.cpiz.android.bubbleview.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by uchia on 8/24/2016.
 */
public class DisplayHelper {
    public static int[] getScreenDemension(WindowManager wm){
        int[] location = new int[2];
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        location[0] = dm.widthPixels;
        location[1] = dm.heightPixels;
        return location;
    }
}
