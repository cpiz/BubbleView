package com.cpiz.android.bubbleview;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

/**
 * 工具类
 * <p>
 * Created by caijw on 2016/10/28.
 */
@SuppressWarnings("WeakerAccess")
public class Utils {
    private static final String TAG = "Utils";

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
     * dp转为px
     *
     * @param dp dp值
     * @return px值
     */
    public static int dp2px(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * 获得View所在界面 NavigationBar 高度
     *
     * @param view 目标View
     * @return 如果存在NavigationBar则返回高度，否则0
     */
    public static int getNavigationBarHeight(View view) {
        Activity activity = getActivity(view);
        if (activity != null) {
            int flags = activity.getWindow().getAttributes().flags;
            if ((flags & WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS) != 0) { // 表示当前屏幕有显示导航条
                // 方法一
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                    Display defaultDisplay = activity.getWindowManager().getDefaultDisplay();
//                    DisplayMetrics metrics = new DisplayMetrics();
//                    defaultDisplay.getMetrics(metrics);
//                    int usableHeight = metrics.heightPixels;
//                    defaultDisplay.getRealMetrics(metrics); // getRealMetrics is only available with API 17 and +
//                    int realHeight = metrics.heightPixels;
//                    return realHeight > usableHeight ? realHeight - usableHeight : 0;
//                }

                // 方法二
                Resources resources = activity.getResources();
                try {
                    int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
                    if (resourceId > 0) {
                        return resources.getDimensionPixelSize(resourceId);
                    }
                } catch (Exception ignored) {
                    Log.w(TAG, "getNavigationBarHeight error", ignored);
                }
            }
        }

        return 0;
    }

    private static Activity getActivity(View view) {
        Context context = view.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }
}
