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

import com.cpiz.android.bubbleview.BubbleStyle.ArrowDirection;

/**
 * 气泡弹窗控件
 * 可装入自定义气泡在需要时弹出，不受目标布局的约束
 * <p>
 * Created by cpiz on 2016/8/2.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
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
     * @param direction 箭头方向，同时也决定了气泡出现的位置
     * @deprecated 已经有更灵活的方式，使用showArrowTo(View anchor, Relative relative, int offsetX, int offsetY)替代
     */
    public void showArrowTo(View anchor, ArrowDirection direction) {
        //noinspection deprecation
        showArrowTo(anchor, direction, 0);
    }

    /**
     * 显示气泡弹窗，并将箭头指向目标
     *
     * @param anchor    气泡箭头要指向的目标
     * @param direction 箭头方向，同时也决定了气泡出现的位置
     * @param offset    气泡箭头与目标的距离
     * @deprecated 已经有更灵活的方式，使用showArrowTo(View anchor, Relative relative, int offsetX, int offsetY)替代
     */
    @SuppressWarnings("WeakerAccess")
    public void showArrowTo(View anchor, ArrowDirection direction, int offset) {
        Relative relative;
        switch (direction) {
            case Up:
                relative = new Relative(Relative.CENTER_HORIZONTAL, Relative.BELOW);
                break;
            case Down:
                relative = new Relative(Relative.CENTER_HORIZONTAL, Relative.ABOVE);
                break;
            case Left:
                relative = new Relative(Relative.TO_RIGHT_OF, Relative.CENTER_VERTICAL);
                break;
            case Right:
                relative = new Relative(Relative.TO_LEFT_OF, Relative.CENTER_VERTICAL);
                break;
            default:
                relative = new Relative(Relative.CENTER_HORIZONTAL, Relative.CENTER_VERTICAL);
                break;

        }
        showArrowTo(anchor, relative, offset, offset);
    }

    /**
     * 显示气泡弹窗，并将箭头指向目标
     *
     * @param anchor   气泡箭头对齐的目标
     * @param relative 气泡与目标的对齐方式
     */
    public void showArrowTo(View anchor, Relative relative, int offsetX, int offsetY) {
        dismiss();

        final int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        final int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        final int navigationBarHeight = getNavigationBarHeight(anchor);
        final Rect anchorRect = getAnchorRectInWindow(anchor);

        ArrowDirection direction = relative.getArrowDirection();
        mBubbleView.setArrowDirection(direction);
        getContentView().measure(
                View.MeasureSpec.makeMeasureSpec(screenWidth - 2 * mPadding, View.MeasureSpec.AT_MOST),
                View.MeasureSpec.makeMeasureSpec(screenHeight - 2 * mPadding, View.MeasureSpec.AT_MOST));
        final int contentWidth = getContentView().getMeasuredWidth();
        final int contentHeight = getContentView().getMeasuredHeight();
        Log.d(TAG, String.format("w:%d, h:%d", contentWidth, contentHeight));

        PopupProp outProp = new PopupProp();
        getPopupProp(screenWidth, screenHeight, navigationBarHeight, anchorRect, contentWidth, contentHeight, relative, offsetX, offsetY, outProp);

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

    private void getPopupProp(final int screenWidth, final int screenHeight,
                              final int navigationBarHeight, final Rect anchorRect,
                              final int contentWidth, final int contentHeight,
                              final Relative relative, final int offsetX, final int offsetY,
                              PopupProp outProp) {
        outProp.direction = relative.getArrowDirection();
        outProp.animationStyle = getAnimationStyle(outProp.direction);
        switch (outProp.direction) {
            case Left:
                outProp.maxWidth = screenWidth - anchorRect.right - offsetX - mPadding;
                break;
            case Right:
                outProp.maxWidth = anchorRect.left - offsetX - mPadding;
                break;
            default:
                outProp.maxWidth = screenWidth - 2 * mPadding;
        }

        outProp.gravity = 0;
        switch (relative.getHorizontalRelate()) {
            case Relative.ALIGN_LEFT:
                outProp.gravity |= Gravity.LEFT;
                outProp.x = anchorRect.left + offsetX;
                break;
            case Relative.TO_RIGHT_OF:
                outProp.gravity |= Gravity.LEFT;
                outProp.x = anchorRect.right + offsetX;
                break;
            case Relative.TO_LEFT_OF:
                outProp.gravity |= Gravity.RIGHT;
                outProp.x = screenWidth - anchorRect.left + offsetX;
                break;
            case Relative.ALIGN_RIGHT:
                outProp.gravity |= Gravity.RIGHT;
                outProp.x = screenWidth - anchorRect.right + offsetX;
                break;
            case Relative.CENTER_HORIZONTAL:
                if (anchorRect.centerX() < contentWidth / 2 + mPadding) {
                    outProp.gravity |= Gravity.LEFT;
                    outProp.x = mPadding;
                } else if (screenWidth - anchorRect.centerX() < contentWidth / 2 + mPadding) {
                    outProp.gravity |= Gravity.RIGHT;
                    outProp.x = mPadding;
                } else {
                    outProp.gravity = Gravity.CENTER_HORIZONTAL;
                    outProp.x = anchorRect.centerX() - screenWidth / 2;
                }
                break;
        }

        switch (relative.getVerticalRelate()) {
            case Relative.ALIGN_TOP:
                outProp.gravity |= Gravity.TOP;
                outProp.y = anchorRect.top + offsetY;
                break;
            case Relative.BELOW:
                outProp.gravity |= Gravity.TOP;
                outProp.y = anchorRect.bottom + offsetY;
                break;
            case Relative.ALIGN_BOTTOM:
                outProp.gravity |= Gravity.BOTTOM;
                outProp.y = screenHeight + navigationBarHeight - anchorRect.bottom + offsetY;
                break;
            case Relative.ABOVE:
                outProp.gravity |= Gravity.BOTTOM;
                outProp.y = screenHeight + navigationBarHeight - anchorRect.top + offsetY;
                break;
            case Relative.CENTER_VERTICAL:
                outProp.gravity |= Gravity.CENTER_VERTICAL;
                outProp.y = anchorRect.centerY() - navigationBarHeight / 2 - screenHeight / 2;
                break;
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

    private static int getAnimationStyle(ArrowDirection direction) {
        switch (direction) {
            case Up:
                return R.style.AnimationArrowUp;
            case Down:
                return R.style.AnimationArrowDown;
            case Left:
                return R.style.AnimationArrowLeft;
            case Right:
                return R.style.AnimationArrowRight;
            default:
                return R.style.AnimationArrowNone;
        }
    }

    private class PopupProp {
        ArrowDirection direction;
        int animationStyle;
        int maxWidth;
        int gravity, x, y;
    }
}
