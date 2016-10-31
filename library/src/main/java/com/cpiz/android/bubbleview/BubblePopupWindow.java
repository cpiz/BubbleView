package com.cpiz.android.bubbleview;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.cpiz.android.bubbleview.BubbleStyle.ArrowDirection;
import com.cpiz.android.bubbleview.BubbleStyle.ArrowPosPolicy;

import static com.cpiz.android.bubbleview.RelativePos.CENTER_HORIZONTAL;
import static com.cpiz.android.bubbleview.Utils.dp2px;

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

    private int mPadding = dp2px(2);
    private int mArrowPosDelta = 0;
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
        setCancelOnTouchOutside(true);
        setCancelOnTouch(true);
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
    public void setCancelOnTouch(boolean cancel) {
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
    public void setCancelOnTouchOutside(boolean cancel) {
        setOutsideTouchable(cancel);
        setFocusable(cancel);
    }

    /**
     * 设置超时后自动关闭弹窗
     *
     * @param delayMillis 自动关闭延时，设0将不会自动关闭
     */
    public void setCancelOnLater(long delayMillis) {
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
     * 设置箭头在所在边线上的偏移距离
     * 这是一个快捷入口，将转调BubbleView的setArrowPosDelta
     *
     * @param arrowPosDelta 基于箭头位置策略，相应的偏差
     *                      值必须>0，朝上/下时在X轴方向偏移，朝左/右时在Y轴方向偏移
     */
    public void setArrowPosDelta(int arrowPosDelta) {
        mArrowPosDelta = arrowPosDelta;
    }

    /**
     * 显示气泡弹窗，并将箭头指向目标中央
     *
     * @param anchor    气泡箭头要指向的目标
     * @param direction 箭头方向，同时也决定了气泡出现的位置
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
     * @param margin    气泡箭头与目标的距离
     */
    @SuppressWarnings("WeakerAccess")
    public void showArrowTo(View anchor, ArrowDirection direction, int margin) {
        RelativePos relativePos;
        switch (direction) {
            case Up:
                relativePos = new RelativePos(CENTER_HORIZONTAL, RelativePos.BELOW);
                break;
            case Down:
                relativePos = new RelativePos(CENTER_HORIZONTAL, RelativePos.ABOVE);
                break;
            case Left:
                relativePos = new RelativePos(RelativePos.TO_RIGHT_OF, RelativePos.CENTER_VERTICAL);
                break;
            case Right:
                relativePos = new RelativePos(RelativePos.TO_LEFT_OF, RelativePos.CENTER_VERTICAL);
                break;
            default:
                relativePos = new RelativePos(CENTER_HORIZONTAL, RelativePos.CENTER_VERTICAL);
                break;
        }
        showArrowTo(anchor, relativePos, margin, margin);
    }

    /**
     * 显示气泡弹窗，并将箭头指向目标
     *
     * @param anchor      气泡箭头对齐的目标
     * @param relativePos 气泡与目标的对齐方式
     */
    public void showArrowTo(View anchor, RelativePos relativePos, int marginH, int marginV) {
        dismiss();

        final int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        final int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        final int navigationBarHeight = getNavigationBarHeightDelta(anchor);
        final Rect anchorRect = getRectInWindow(anchor);

        getContentView().measure(
                View.MeasureSpec.makeMeasureSpec(screenWidth - 2 * mPadding, View.MeasureSpec.AT_MOST),
                View.MeasureSpec.makeMeasureSpec(screenHeight - 2 * mPadding, View.MeasureSpec.AT_MOST));
        final int contentWidth = getContentView().getMeasuredWidth();
        final int contentHeight = getContentView().getMeasuredHeight();
        Log.d(TAG, String.format("w:%d, h:%d", contentWidth, contentHeight));

        PopupProp outProp = new PopupProp();
        getPopupProp(screenWidth, screenHeight, navigationBarHeight, anchorRect, contentWidth, contentHeight, relativePos, marginH, marginV, mPadding, outProp);

        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setAnimationStyle(outProp.animationStyle);
        if (contentWidth > outProp.maxWidth) {
            setWidth(outProp.maxWidth);
        }
        mBubbleView.setArrowDirection(outProp.direction);
        mBubbleView.setArrowPosPolicy(outProp.arrowPosPolicy);
        mBubbleView.setArrowTo(anchor);
        mBubbleView.setArrowPosDelta(mArrowPosDelta);
        showAtLocation(anchor, outProp.gravity, outProp.x, outProp.y);

        if (mDelayMillis > 0) {
            setCancelOnLater(mDelayMillis);
        }
    }

    private static Rect getRectInWindow(View view) {
        final int[] location = new int[2];
        view.getLocationInWindow(location);
        return new Rect(location[0], location[1], location[0] + view.getWidth(), location[1] + view.getHeight());
    }

    private static void getPopupProp(final int screenWidth, final int screenHeight,
                                     final int navigationBarHeight, final Rect anchorRect,
                                     final int contentWidth, final int contentHeight,
                                     final RelativePos relativePos, final int marginH, final int marginV, final int padding,
                                     PopupProp outProp) {
        outProp.direction = relativePos.getArrowDirection();
        outProp.animationStyle = getAnimationStyle(outProp.direction);
        outProp.gravity = 0;
        getPopupPropOfX(screenWidth, anchorRect, contentWidth, relativePos, marginH, padding, outProp);
        getPopupPropOfMaxWidth(screenWidth, anchorRect, relativePos, marginH, padding, outProp);
        getPopupPropOfY(screenHeight, navigationBarHeight, anchorRect, relativePos, marginV, outProp);

        switch (outProp.direction) {
            case Up:
            case Down:
                switch (relativePos.getHorizontalRelate()) {
                    case RelativePos.CENTER_HORIZONTAL:
                        outProp.arrowPosPolicy = ArrowPosPolicy.TargetCenter;
                        break;
                    case RelativePos.ALIGN_LEFT:
                        outProp.arrowPosPolicy = ArrowPosPolicy.SelfBegin;
                        break;
                    case RelativePos.ALIGN_RIGHT:
                        outProp.arrowPosPolicy = ArrowPosPolicy.SelfEnd;
                        break;
                    default:
                        outProp.arrowPosPolicy = ArrowPosPolicy.TargetCenter;
                        break;
                }
                break;
            case Left:
            case Right:
                switch (relativePos.getVerticalRelate()) {
                    case RelativePos.CENTER_HORIZONTAL:
                        outProp.arrowPosPolicy = ArrowPosPolicy.TargetCenter;
                        break;
                    case RelativePos.ALIGN_TOP:
                        outProp.arrowPosPolicy = ArrowPosPolicy.SelfBegin;
                        break;
                    case RelativePos.ALIGN_BOTTOM:
                        outProp.arrowPosPolicy = ArrowPosPolicy.SelfEnd;
                        break;
                    default:
                        outProp.arrowPosPolicy = ArrowPosPolicy.TargetCenter;
                        break;
                }
                break;
            default:
                outProp.arrowPosPolicy = ArrowPosPolicy.TargetCenter;
                break;
        }
    }

    private static void getPopupPropOfX(int screenWidth, Rect anchorRect, int contentWidth, RelativePos relativePos, int marginH, final int padding, PopupProp outProp) {
        switch (relativePos.getHorizontalRelate()) {
            case RelativePos.ALIGN_LEFT:
                outProp.gravity |= Gravity.LEFT;
                outProp.x = anchorRect.left + marginH;
                break;
            case RelativePos.TO_RIGHT_OF:
                outProp.gravity |= Gravity.LEFT;
                outProp.x = anchorRect.right + marginH;
                break;
            case RelativePos.TO_LEFT_OF:
                outProp.gravity |= Gravity.RIGHT;
                outProp.x = screenWidth - anchorRect.left + marginH;
                break;
            case RelativePos.ALIGN_RIGHT:
                outProp.gravity |= Gravity.RIGHT;
                outProp.x = screenWidth - anchorRect.right + marginH;
                break;
            case CENTER_HORIZONTAL:
                if (anchorRect.centerX() < contentWidth / 2 + padding) {
                    outProp.gravity |= Gravity.LEFT;
                    outProp.x = padding;
                } else if (screenWidth - anchorRect.centerX() < contentWidth / 2 + padding) {
                    outProp.gravity |= Gravity.RIGHT;
                    outProp.x = padding;
                } else {
                    outProp.gravity = Gravity.CENTER_HORIZONTAL;
                    outProp.x = anchorRect.centerX() - screenWidth / 2;
                }
                break;
        }
    }

    private static void getPopupPropOfMaxWidth(int screenWidth, Rect anchorRect, RelativePos relativePos, int marginH, final int padding, PopupProp outProp) {
        switch (relativePos.getHorizontalRelate()) {
            case RelativePos.ALIGN_LEFT:
                outProp.maxWidth = screenWidth - anchorRect.left - marginH - padding;
                break;
            case RelativePos.TO_RIGHT_OF:
                outProp.maxWidth = screenWidth - anchorRect.right - marginH - padding;
                break;
            case RelativePos.TO_LEFT_OF:
                outProp.maxWidth = anchorRect.left - marginH - padding;
                break;
            case RelativePos.ALIGN_RIGHT:
                outProp.maxWidth = anchorRect.right - marginH - padding;
                break;
            case CENTER_HORIZONTAL:
                outProp.maxWidth = screenWidth - 2 * padding;
                break;
        }
    }

    private static void getPopupPropOfY(int screenHeight, int navigationBarHeight, Rect anchorRect, RelativePos relativePos, int marginV, PopupProp outProp) {
        switch (relativePos.getVerticalRelate()) {
            case RelativePos.ALIGN_TOP:
                outProp.gravity |= Gravity.TOP;
                outProp.y = anchorRect.top + marginV;
                break;
            case RelativePos.BELOW:
                outProp.gravity |= Gravity.TOP;
                outProp.y = anchorRect.bottom + marginV;
                break;
            case RelativePos.ALIGN_BOTTOM:
                outProp.gravity |= Gravity.BOTTOM;
                outProp.y = screenHeight + navigationBarHeight - anchorRect.bottom + marginV;
                break;
            case RelativePos.ABOVE:
                outProp.gravity |= Gravity.BOTTOM;
                outProp.y = screenHeight + navigationBarHeight - anchorRect.top + marginV;
                break;
            case RelativePos.CENTER_VERTICAL:
                outProp.gravity |= Gravity.CENTER_VERTICAL;
                outProp.y = anchorRect.centerY() - navigationBarHeight / 2 - screenHeight / 2;
                break;
        }
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

    /**
     * 获得用于补偿位置偏移的 NavigationBar 高度
     * 在 Android5.0 以上系统，showAtLocation 如果使用了 Gravity.BOTTOM 或 Gravity.CENTER_VERTICAL 可能出现显示偏移的Bug
     * 偏移值和 NavigationBar 高度有关
     *
     * @param view 目标View
     * @return 如果需要修正且存在NavigationBar则返回高度，否则为0
     */
    private static int getNavigationBarHeightDelta(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            return Utils.getNavigationBarHeight(view);
        } else {
            return 0;
        }
    }

    private class PopupProp {
        ArrowDirection direction;
        ArrowPosPolicy arrowPosPolicy;
        int animationStyle;
        int maxWidth;
        int gravity, x, y;
    }
}
