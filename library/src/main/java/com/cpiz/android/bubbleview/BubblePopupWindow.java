package com.cpiz.android.bubbleview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * 气泡弹窗控件
 * 可装入自定义气泡在需要时弹出，不受目标布局的约束
 *
 * Created by cpiz on 2016/8/2.
 */
@SuppressLint("RtlHardcoded")
public class BubblePopupWindow extends PopupWindow {
    private static final String TAG = "BubblePopupWindow";

    private int mPadding = BubbleImpl.dpToPx(2);
    private BubbleStyle mBubbleView;
    private long mDelayMillis = 0;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable mDismissRunnable = new Runnable() {
        @Override
        public void run() {
            dismiss();
        }
    };

    /**
     * 构造函数
     *
     * @param contentView 弹窗内容View，可以是个包裹BubbleView的Layout（方便指定BubbleView的大小），也可以就是一个 BubbleView
     * @param bubbleView  气泡View
     */
    public BubblePopupWindow(View contentView, BubbleStyle bubbleView) {
        super(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if (bubbleView == null) {
            throw new NullPointerException("Bubble can not be null");
        }

        mBubbleView = bubbleView;
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCanceledOnTouchOutside(true);
        setCanceledOnTouch(true);
    }

    @Override
    public void dismiss() {
        mHandler.removeCallbacks(mDismissRunnable); // prevent leak
        super.dismiss();
    }

    /**
     * 设置点击气泡关闭弹窗
     *
     * @param cancel 是否点击气泡关闭弹窗，默认是
     */
    public void setCanceledOnTouch(boolean cancel) {
        getContentView().setOnClickListener(cancel ? new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        } : null);
    }

    /**
     * 设置点击外部区域关闭弹窗
     *
     * @param cancel 设置点击外部区域关闭弹窗，默认是
     */
    public void setCanceledOnTouchOutside(boolean cancel) {
        setOutsideTouchable(cancel);
        setFocusable(cancel);
    }

    /**
     * 设置超时后自动关闭弹窗
     *
     * @param delayMillis 自动关闭延时，设0将不会自动关闭
     */
    public void setCanceledOnLater(long delayMillis) {
        mHandler.removeCallbacks(mDismissRunnable);
        mDelayMillis = delayMillis;
        if (delayMillis > 0) {
            mHandler.postDelayed(mDismissRunnable, delayMillis);
        }
    }

    /**
     * 设置气泡与屏幕边缘的（最小）间距
     * 因为气泡紧贴着屏幕边缘不太美观
     *
     * @param padding 边距px
     */
    public void setPadding(int padding) {
        mPadding = padding;
    }

    /**
     * 显示气泡弹窗，并将箭头指向目标
     *
     * @param anchor    气泡箭头要指向的目标
     * @param direction 箭头方向，同时也决定了气泡出现的位置，因此不能是 BubbleStyle.ArrowDirection#None
     */
    public void showArrowTo(View anchor, BubbleStyle.ArrowDirection direction) {
        showArrowTo(anchor, direction, 0);
    }

    /**
     * 显示气泡弹窗，并将箭头指向目标
     *
     * @param anchor    气泡箭头要指向的目标
     * @param direction 箭头方向，同时也决定了气泡出现的位置，不能是 BubbleStyle.ArrowDirection#None
     * @param offset    气泡箭头与目标的距离
     */
    @SuppressWarnings("WeakerAccess")
    public void showArrowTo(View anchor, BubbleStyle.ArrowDirection direction, int offset) {
        dismiss();

        final int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        final int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        final int navigationBarHeight = getNavigationBarHeight(anchor);
        final Rect anchorRect = getAnchorRectInWindow(anchor);

        mBubbleView.setArrowDirection(direction);
        getContentView().measure(
                View.MeasureSpec.makeMeasureSpec(screenWidth - 2 * mPadding, View.MeasureSpec.AT_MOST),
                View.MeasureSpec.makeMeasureSpec(screenHeight - 2 * mPadding, View.MeasureSpec.AT_MOST));
        final int contentWidth = getContentView().getMeasuredWidth();
        final int contentHeight = getContentView().getMeasuredHeight();
        Log.d(TAG, String.format("w:%d, h:%d", contentWidth, contentHeight));

        PopupProp outProp = new PopupProp();
        switch (direction) {
            case Left:
                getLeftPopupProp(screenWidth, screenHeight, navigationBarHeight, anchorRect, contentWidth, contentHeight, offset, outProp);
                break;
            case Right:
                getRightPopupProp(screenWidth, screenHeight, navigationBarHeight, anchorRect, contentWidth, contentHeight, offset, outProp);
                break;
            case Up:
                getUpPopupProp(screenWidth, screenHeight, navigationBarHeight, anchorRect, contentWidth, contentHeight, offset, outProp);
                break;
            case Down:
            default:
                getDownPopupProp(screenWidth, screenHeight, navigationBarHeight, anchorRect, contentWidth, contentHeight, offset, outProp);
                break;
        }

        mBubbleView.setArrowDirection(outProp.direction);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setAnimationStyle(outProp.animationStyle);
        if (contentWidth > outProp.maxWidth) {
            setWidth(outProp.maxWidth);
        }
        showAtLocation(anchor, outProp.gravity, outProp.x, outProp.y);
        mBubbleView.setArrowTo(anchor);

        if (mDelayMillis > 0) {
            setCanceledOnLater(mDelayMillis);
        }
    }

    private Rect getAnchorRectInWindow(View anchor) {
        final int[] location = new int[2];
        anchor.getLocationInWindow(location);
        return new Rect(location[0], location[1], location[0] + anchor.getWidth(), location[1] + anchor.getHeight());
    }

    private void getLeftPopupProp(final int screenWidth, final int screenHeight,
                                  final int navigationBarHeight, final Rect anchorRect,
                                  final int contentWidth, final int contentHeight,
                                  final int offset, PopupProp outProp) {
        outProp.direction = BubbleStyle.ArrowDirection.Left;
        outProp.animationStyle = R.style.AnimationArrowLeft;
        outProp.maxWidth = screenWidth - anchorRect.right - offset - mPadding;
        outProp.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
        outProp.x = anchorRect.right + offset;
        outProp.y = anchorRect.centerY() - navigationBarHeight / 2 - screenHeight / 2;
    }

    private void getRightPopupProp(final int screenWidth, final int screenHeight,
                                   final int navigationBarHeight, final Rect anchorRect,
                                   final int contentWidth, final int contentHeight,
                                   final int offset, PopupProp outProp) {
        outProp.direction = BubbleStyle.ArrowDirection.Right;
        outProp.animationStyle = R.style.AnimationArrowRight;
        outProp.maxWidth = anchorRect.left - offset - mPadding;
        outProp.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
        outProp.x = screenWidth - anchorRect.left + offset;
        outProp.y = anchorRect.centerY() - navigationBarHeight / 2 - screenHeight / 2;
    }

    private void getUpPopupProp(final int screenWidth, final int screenHeight,
                                final int navigationBarHeight, final Rect anchorRect,
                                final int contentWidth, final int contentHeight,
                                final int offset, PopupProp outProp) {
        outProp.direction = BubbleStyle.ArrowDirection.Up;
        outProp.animationStyle = R.style.AnimationArrowUp;
        outProp.maxWidth = screenWidth - 2 * mPadding;

        if (anchorRect.centerX() < contentWidth / 2 + mPadding) {
            outProp.gravity = Gravity.LEFT | Gravity.TOP;
            outProp.x = mPadding;
        } else if (screenWidth - anchorRect.centerX() < contentWidth / 2 + mPadding) {
            outProp.gravity = Gravity.RIGHT | Gravity.TOP;
            outProp.x = mPadding;
        } else {
            outProp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
            outProp.x = anchorRect.centerX() - screenWidth / 2;
        }
        outProp.y = anchorRect.bottom + offset;

        if (screenHeight - anchorRect.bottom < contentHeight + offset
                && anchorRect.top >= contentHeight + offset) {
            getDownPopupProp(screenWidth, screenHeight, navigationBarHeight, anchorRect, contentWidth, contentHeight, offset, outProp);
        }
    }

    private void getDownPopupProp(final int screenWidth, final int screenHeight,
                                  final int navigationBarHeight, final Rect anchorRect,
                                  final int contentWidth, final int contentHeight,
                                  final int offset, PopupProp outProp) {
        outProp.direction = BubbleStyle.ArrowDirection.Down;
        outProp.animationStyle = R.style.AnimationArrowDown;
        outProp.maxWidth = screenWidth - 2 * mPadding;

        if (anchorRect.centerX() < contentWidth / 2 + mPadding) {
            outProp.gravity = Gravity.LEFT | Gravity.BOTTOM;
            outProp.x = mPadding;
        } else if (screenWidth - anchorRect.centerX() < contentWidth / 2 + mPadding) {
            outProp.gravity = Gravity.RIGHT | Gravity.BOTTOM;
            outProp.x = mPadding;
        } else {
            outProp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
            outProp.x = anchorRect.centerX() - screenWidth / 2;
        }
        outProp.y = screenHeight + navigationBarHeight - anchorRect.top + offset;

        if (anchorRect.top < contentHeight + offset
                && screenHeight - anchorRect.bottom >= contentHeight + offset) {
            getUpPopupProp(screenWidth, screenHeight, navigationBarHeight, anchorRect, contentWidth, contentHeight, offset, outProp);
        }
    }

    /**
     * 获得用于补偿位置偏移的 NavigationBar 高度
     * 在 Android5.0 以上系统，showAtLocation 如果使用了 Gravity.BOTTOM 或 Gravity.CENTER_VERTICAL 可能出现显示偏移的Bug
     * 偏移值和 NavigationBar 高度有关
     *
     * @param anchorView 目标View
     * @return 如果需要修正且存在NavigationBar则返回高度，否则为0
     */
    private static int getNavigationBarHeight(View anchorView) {
        if (anchorView.getRootView().getContext() instanceof Activity) {
            Activity activity = (Activity) anchorView.getRootView().getContext();
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

    private class PopupProp {
        BubbleStyle.ArrowDirection direction;
        int animationStyle;
        int maxWidth;
        int gravity, x, y;
    }
}
