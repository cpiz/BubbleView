package com.cpiz.android.bubbleview;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

/**
 * 工具类
 *
 * Created by caijw on 2016/10/28.
 */
@SuppressWarnings("WeakerAccess")
public class Utils {
    /**
     * 范围内取有效值
     *
     * @param min 最小值
     * @param val 原始值
     * @param max 最大值
     * @return 符合最大最小范围的有效值
     */
    public static float bound(float min, float val, float max) {
        return Math.min(Math.max(val, min), max);
    }

    /**
     * 判断目标值是否在范围内
     *
     * @param val    目标值
     * @param scopeA 范围边界A
     * @param scopeB 范围边界B
     * @return 是否符合
     */
    public static boolean isBetween(int val, int scopeA, int scopeB) {
        return (val >= scopeA && val <= scopeB) || (val >= scopeB && val <= scopeA);
    }

    /**
     * dp转为px
     *
     * @param dp dp值
     * @return px值
     */
    public static int dp2px(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * 获得用于补偿位置偏移的 NavigationBar 高度
     * 在 Android5.0 以上系统，showAtLocation 如果使用了 Gravity.BOTTOM 或 Gravity.CENTER_VERTICAL 可能出现显示偏移的Bug
     * 偏移值和 NavigationBar 高度有关
     *
     * @param view 目标View
     * @return 如果需要修正且存在NavigationBar则返回高度，否则为0
     */
    public static int getNavigationBarHeight(View view) {
        if (view.getRootView().getContext() instanceof Activity) {
            Activity activity = (Activity) view.getRootView().getContext();
            int flags = activity.getWindow().getAttributes().flags;
            if ((flags & WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS) != 0) { // 没有这个属性无须修正
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                    Display defaultDisplay = activity.getWindowManager().getDefaultDisplay();
                    DisplayMetrics metrics = new DisplayMetrics();
                    defaultDisplay.getMetrics(metrics);
                    int usableHeight = metrics.heightPixels;
                    defaultDisplay.getRealMetrics(metrics); // getRealMetrics is only available with API 17 and +
                    int realHeight = metrics.heightPixels;
                    return realHeight > usableHeight ? realHeight - usableHeight : 0;
                }
            }
        }

        return 0;
    }
}
