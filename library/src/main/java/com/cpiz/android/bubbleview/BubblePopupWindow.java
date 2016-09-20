package com.cpiz.android.bubbleview;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

/**
 * 气泡弹窗控件
 * 可装入自定义气泡在需要时弹出，不受目标布局的约束
 *
 * Created by cpiz on 2016/8/2.
 */
public class BubblePopupWindow extends PopupWindow {
    private static final String TAG = "BubblePopupWindow";
    private static final int MIN_MARGIN = BubbleImpl.dpToPx(2);

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
    public void showArrowTo(View anchor, BubbleStyle.ArrowDirection direction, int offset) {
        dismiss();

        final int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        final int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        final int[] location = new int[2];
        anchor.getLocationInWindow(location);
        final Rect anchorRect = new Rect(location[0], location[1], location[0] + anchor.getWidth(), location[1] + anchor.getHeight());

        mBubbleView.setArrowDirection(direction);
        getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        final int contentWidth = getContentView().getMeasuredWidth();
        final int contentHeight = getContentView().getMeasuredHeight();
        Log.d(TAG, String.format("w:%d, h:%d", contentWidth, contentHeight));

        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        switch (direction) {
            case Left: {
                setAnimationStyle(R.style.AnimationArrowLeft);
                int maxWidth = screenWidth - anchorRect.right - offset - MIN_MARGIN;
                if (contentWidth > maxWidth) {
                    setWidth(maxWidth);
                }
                showAtLocation(anchor, Gravity.LEFT | Gravity.CENTER_VERTICAL, anchorRect.right + offset, anchorRect.centerY() - screenHeight / 2);
                break;
            }
            case Right: {
                setAnimationStyle(R.style.AnimationArrowRight);
                int maxWidth = anchorRect.left - offset - MIN_MARGIN;
                if (contentWidth > maxWidth) {
                    setWidth(maxWidth);
                }
                showAtLocation(anchor, Gravity.RIGHT | Gravity.CENTER_VERTICAL, screenWidth - anchorRect.left + offset, anchorRect.centerY() - screenHeight / 2);
                break;
            }
            case Up: {
                setAnimationStyle(R.style.AnimationArrowUp);
                showAtLocation(anchor, Gravity.CENTER_HORIZONTAL | Gravity.TOP, anchorRect.centerX() - screenWidth / 2, anchorRect.bottom + offset);
                break;
            }
            case Down:
            default: {
                setAnimationStyle(R.style.AnimationArrowDown);
                showAtLocation(anchor, Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, anchorRect.centerX() - screenWidth / 2, screenHeight - anchorRect.top + offset);
                break;
            }
        }
        mBubbleView.setArrowTo(anchor);

        if (mDelayMillis > 0) {
            setCanceledOnLater(mDelayMillis);
        }
    }
}
