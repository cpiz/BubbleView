package com.cpiz.android.bubbleviewsample;

import android.content.res.Resources;

/**
 * Created by caijw on 2016/10/28.
 */

public class Utils {
    /**
     * dp转为px
     *
     * @param dp dp值
     * @return px值
     */
    public static int dp2px(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}
