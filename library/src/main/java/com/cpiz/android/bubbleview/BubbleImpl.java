package com.cpiz.android.bubbleview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.lang.ref.WeakReference;

import static com.cpiz.android.bubbleview.Utils.dp2px;

/**
 * 气泡控件的实现类，将与真正的气泡View进行聚合，方便扩展
 * <p>
 * Created by caijw on 2016/6/1.
 * https://github.com/cpiz/BubbleView
 */
class BubbleImpl implements BubbleStyle {
    private View mParentView;
    private BubbleCallback mHolderCallback;
    private BubbleDrawable mBubbleDrawable = new BubbleDrawable();
    private ArrowDirection mArrowDirection = ArrowDirection.Auto;
    private ArrowDirection mDrawableArrowDirection = ArrowDirection.None;
    private ArrowPosPolicy mArrowPosPolicy = ArrowPosPolicy.TargetCenter;
    private WeakReference<View> mArrowToViewRef = null;
    private int mArrowToViewId = 0;
    private float mArrowHeight = 0;
    private float mArrowWidth = 0;
    private float mArrowPosDelta = 0;
    private float mCornerTopLeftRadius = 0;
    private float mCornerTopRightRadius = 0;
    private float mCornerBottomLeftRadius = 0;
    private float mCornerBottomRightRadius = 0;
    private int mPaddingLeftOffset = 0, mPaddingTopOffset = 0, mPaddingRightOffset = 0, mPaddingBottomOffset = 0;
    private int mFillColor = 0xCC000000;
    private int mFillPressColor = mFillColor;
    private int mBorderColor = Color.WHITE;
    private float mBorderWidth = 0;
    private float mFillPadding = 0;
    private View.OnLayoutChangeListener mOnLayoutChangeListener = new View.OnLayoutChangeListener() {
        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop,
                                   int oldRight, int oldBottom) {
            requestUpdateBubble();
        }
    };

    void init(View view, Context context, AttributeSet attrs) {
        mParentView = view;
        mHolderCallback = (BubbleCallback) view;

        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BubbleStyle);
            mArrowDirection = ArrowDirection.valueOf(
                    ta.getInt(R.styleable.BubbleStyle_bb_arrowDirection, ArrowDirection.Auto.getValue()));
            mArrowHeight = ta.getDimension(R.styleable.BubbleStyle_bb_arrowHeight, dp2px(6));
            mArrowWidth = ta.getDimension(R.styleable.BubbleStyle_bb_arrowWidth, dp2px(10));
            mArrowPosPolicy = ArrowPosPolicy.valueOf(
                    ta.getInt(R.styleable.BubbleStyle_bb_arrowPosPolicy, ArrowPosPolicy.TargetCenter.getValue()));
            mArrowPosDelta = ta.getDimension(R.styleable.BubbleStyle_bb_arrowPosDelta, 0);
            mArrowToViewId = ta.getResourceId(R.styleable.BubbleStyle_bb_arrowTo, 0);

            float radius = ta.getDimension(R.styleable.BubbleStyle_bb_cornerRadius, dp2px(4));
            mCornerTopLeftRadius = mCornerTopRightRadius = mCornerBottomLeftRadius = mCornerBottomRightRadius = radius;
            mCornerTopLeftRadius =
                    ta.getDimension(R.styleable.BubbleStyle_bb_cornerTopLeftRadius, mCornerTopLeftRadius);
            mCornerTopRightRadius =
                    ta.getDimension(R.styleable.BubbleStyle_bb_cornerTopRightRadius, mCornerTopLeftRadius);
            mCornerBottomLeftRadius =
                    ta.getDimension(R.styleable.BubbleStyle_bb_cornerBottomLeftRadius, mCornerTopLeftRadius);
            mCornerBottomRightRadius =
                    ta.getDimension(R.styleable.BubbleStyle_bb_cornerBottomRightRadius, mCornerTopLeftRadius);

            mFillColor = ta.getColor(R.styleable.BubbleStyle_bb_fillColor, 0xCC000000);
            mFillPressColor = ta.getColor(R.styleable.BubbleStyle_bb_fillPressColor, mFillColor);
            if (mFillColor != mFillPressColor) {
                view.setClickable(true);
                view.setLongClickable(true);
            }
            mFillPadding = ta.getDimension(R.styleable.BubbleStyle_bb_fillPadding, 0);
            mBorderColor = ta.getColor(R.styleable.BubbleStyle_bb_borderColor, Color.WHITE);
            mBorderWidth = ta.getDimension(R.styleable.BubbleStyle_bb_borderWidth, 0);
            ta.recycle();
        }
        updateDrawable(mParentView.getWidth(), mParentView.getHeight(), false);
    }

    @Override
    public void setArrowDirection(ArrowDirection arrowDirection) {
        mArrowDirection = arrowDirection;
    }

    @Override
    public ArrowDirection getArrowDirection() {
        return mArrowDirection;
    }

    @Override
    public void setArrowHeight(float arrowHeight) {
        mArrowHeight = arrowHeight;
    }

    @Override
    public float getArrowHeight() {
        return mArrowHeight;
    }

    @Override
    public void setArrowWidth(float arrowWidth) {
        mArrowWidth = arrowWidth;
    }

    @Override
    public float getArrowWidth() {
        return mArrowWidth;
    }

    public void setArrowPosPolicy(ArrowPosPolicy policy) {
        mArrowPosPolicy = policy;
    }

    @Override
    public void setArrowPosDelta(float delta) {
        mArrowPosDelta = delta;
    }

    public ArrowPosPolicy getArrowPosPolicy() {
        return mArrowPosPolicy;
    }

    public float getArrowPosDelta() {
        return mArrowPosDelta;
    }

    /**
     * 设置箭头指向的View对象ID
     * 设置了View对象后，setArrowPos将不起作用
     *
     * @param targetViewId 指向的ViewId
     */
    @Override
    public void setArrowTo(int targetViewId) {
        mArrowToViewId = targetViewId;
        setArrowToRef(null); // 先不设置，在updateDrawable会重新寻找
    }

    @Override
    public void setArrowTo(View targetView) {
        mArrowToViewId = targetView != null ? targetView.getId() : 0;
        setArrowToRef(targetView);
    }

    public View getArrowTo() {
        return mArrowToViewRef != null ? mArrowToViewRef.get() : null;
    }

    /**
     * 设置气泡背景色
     *
     * @param fillColor 气泡背景颜色
     */
    @Override
    public void setFillColor(int fillColor) {
        mFillColor = fillColor;
    }

    @Override
    public int getFillColor() {
        return mFillColor;
    }

    @Override
    public void setFillPressColor(int fillPressColor) {
        mFillPressColor = fillPressColor;
    }

    @Override
    public int getFillPressColor() {
        return mFillPressColor;
    }

    /**
     * 设置边框线颜色
     *
     * @param borderColor 边框颜色
     */
    @Override
    public void setBorderColor(int borderColor) {
        mBorderColor = borderColor;
    }

    @Override
    public int getBorderColor() {
        return mBorderColor;
    }

    /**
     * 设置边框线宽
     *
     * @param borderWidth 边框厚度
     */
    @Override
    public void setBorderWidth(float borderWidth) {
        mBorderWidth = borderWidth;
    }

    @Override
    public float getBorderWidth() {
        return mBorderWidth;
    }

    /**
     * 设置边框于背景之间的间隙宽度
     *
     * @param fillPadding 间隙宽度
     */
    @Override
    public void setFillPadding(float fillPadding) {
        mFillPadding = fillPadding;
    }

    @Override
    public float getFillPadding() {
        return mFillPadding;
    }

    /**
     * 设置边角弧度
     * 可以为四角指定不同弧度
     *
     * @param topLeft     左上角
     * @param topRight    右上角
     * @param bottomRight 右下角
     * @param bottomLeft  左下角
     */
    @Override
    public void setCornerRadius(float topLeft, float topRight, float bottomRight, float bottomLeft) {
        mCornerTopLeftRadius = topLeft;
        mCornerTopRightRadius = topRight;
        mCornerBottomRightRadius = bottomRight;
        mCornerBottomLeftRadius = bottomLeft;
    }

    @Override
    public void setCornerRadius(float radius) {
        setCornerRadius(radius, radius, radius, radius);
    }

    @Override
    public float getCornerTopLeftRadius() {
        return mCornerTopLeftRadius;
    }

    @Override
    public float getCornerTopRightRadius() {
        return mCornerTopRightRadius;
    }

    @Override
    public float getCornerBottomLeftRadius() {
        return mCornerBottomLeftRadius;
    }

    @Override
    public float getCornerBottomRightRadius() {
        return mCornerBottomRightRadius;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    public void setPadding(final int left, final int top, final int right, final int bottom) {
        if (mHolderCallback == null) {
            return;
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
            StackTraceElement stack[] = (new Throwable()).getStackTrace();
            for (int i = 0; i < 7; i++) {
                if (stack[i].getClassName().equals(View.class.getName()) && stack[i].getMethodName()
                        .equals("recomputePadding")) {
                    Log.w("BubbleImpl", "Called setPadding by View on old Android platform");
                    mHolderCallback.setSuperPadding(left, top, right, bottom);
                    return;
                }
            }
        }

        mPaddingLeftOffset = mPaddingTopOffset = mPaddingRightOffset = mPaddingBottomOffset = 0;
        switch (mDrawableArrowDirection) {
            case Left:
                mPaddingLeftOffset += mArrowHeight;
                break;
            case Up:
                mPaddingTopOffset += mArrowHeight;
                break;
            case Right:
                mPaddingRightOffset += mArrowHeight;
                break;
            case Down:
                mPaddingBottomOffset += mArrowHeight;
                break;
            case Auto:
            case None:
            default:
                break;
        }

        final int superPaddingLeft = left + mPaddingLeftOffset;
        final int superPaddingTop = top + mPaddingTopOffset;
        final int superPaddingRight = right + mPaddingRightOffset;
        final int superPaddingBottom = bottom + mPaddingBottomOffset;

        if (superPaddingLeft != mHolderCallback.getSuperPaddingLeft()
                || superPaddingTop != mHolderCallback.getSuperPaddingTop()
                || superPaddingRight != mHolderCallback.getSuperPaddingRight()
                || superPaddingBottom != mHolderCallback.getSuperPaddingBottom()) {
            mParentView.post(new Runnable() {
                @Override
                public void run() {
                    mHolderCallback.setSuperPadding(superPaddingLeft, superPaddingTop, superPaddingRight,
                            superPaddingBottom);
                }
            });
        }
    }

    @Override
    public int getPaddingLeft() {
        return mHolderCallback.getSuperPaddingLeft() - mPaddingLeftOffset;
    }

    @Override
    public int getPaddingTop() {
        return mHolderCallback.getSuperPaddingTop() - mPaddingTopOffset;
    }

    @Override
    public int getPaddingRight() {
        return mHolderCallback.getSuperPaddingRight() - mPaddingRightOffset;
    }

    @Override
    public int getPaddingBottom() {
        return mHolderCallback.getSuperPaddingBottom() - mPaddingBottomOffset;
    }

    // 方便计算用的中间值对象，避免重复创建
    private int[] mLocation = new int[2];
    private Rect mRectTo = new Rect();
    private Rect mRectSelf = new Rect();

    void updateDrawable(int width, int height, boolean drawImmediately) {
        int arrowToOffsetX = 0;
        int arrowToOffsetY = 0;

        View arrowToView = getArrowTo();

        if (arrowToView == null && mArrowToViewId != 0) {
            arrowToView = findGlobalViewById(mArrowToViewId);
            setArrowToRef(arrowToView);
        }

        mDrawableArrowDirection = mArrowDirection;
        if (arrowToView != null) {
            arrowToView.getLocationOnScreen(mLocation);
            mRectTo.set(mLocation[0], mLocation[1], mLocation[0] + arrowToView.getWidth(),
                    mLocation[1] + arrowToView.getHeight());

            mParentView.getLocationOnScreen(mLocation);
            mRectSelf.set(mLocation[0], mLocation[1], mLocation[0] + width, mLocation[1] + height);

            if (mDrawableArrowDirection == ArrowDirection.Auto) {
                mDrawableArrowDirection = getAutoArrowDirection(mRectSelf, mRectTo);
            }

            arrowToOffsetX = mRectTo.centerX() - mRectSelf.centerX();
            arrowToOffsetY = mRectTo.centerY() - mRectSelf.centerY();
        }
        setPadding(mParentView.getPaddingLeft(), mParentView.getPaddingTop(), mParentView.getPaddingRight(),
                mParentView.getPaddingBottom());

        if (drawImmediately) {
            mBubbleDrawable.resetRect(width, height);
            mBubbleDrawable.setCornerRadius(mCornerTopLeftRadius, mCornerTopRightRadius, mCornerBottomRightRadius,
                    mCornerBottomLeftRadius);
            mBubbleDrawable.setFillColor(mFillColor);
            mBubbleDrawable.setFillPressColor(mFillPressColor);
            mBubbleDrawable.setBorderWidth(mBorderWidth);
            mBubbleDrawable.setFillPadding(mFillPadding);
            mBubbleDrawable.setBorderColor(mBorderColor);
            mBubbleDrawable.setArrowDirection(mDrawableArrowDirection);
            mBubbleDrawable.setArrowPosPolicy(mArrowPosPolicy);
            mBubbleDrawable.setArrowTo(arrowToOffsetX, arrowToOffsetY);
            mBubbleDrawable.setArrowPosDelta(mArrowPosDelta);
            mBubbleDrawable.setArrowHeight(mArrowHeight);
            mBubbleDrawable.setArrowWidth(mArrowWidth);
            mBubbleDrawable.updateShapes();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mParentView.setBackground(mBubbleDrawable);
            } else {
                // noinspection deprecation
                mParentView.setBackgroundDrawable(mBubbleDrawable);
            }
        }
    }

    @Override
    public void requestUpdateBubble() {
        updateDrawable(mParentView.getWidth(), mParentView.getHeight(), true);
        mParentView.invalidate();
    }

    public void setPressed(boolean pressed) {
        mBubbleDrawable.setPressed(pressed);
        requestUpdateBubble();
        mHolderCallback.setSuperPressed(pressed);
    }

    private View findGlobalViewById(int viewId) {
        if (viewId == 0) {
            return null;
        }

        View vp = mParentView;
        while (vp.getParent() instanceof View) {
            // 逐层在父View中查找，是为了查找离自己最近的目标对象，因为ID可能重复
            vp = (View) vp.getParent();
            View arrowToView = vp.findViewById(viewId);
            if (arrowToView != null) {
                return arrowToView;
            }
        }

        return null;
    }

    private void setArrowToRef(View targetView) {
        if (mArrowToViewRef != null) {
            View oldTargetView = mArrowToViewRef.get();
            if (oldTargetView != null) {
                oldTargetView.removeOnLayoutChangeListener(mOnLayoutChangeListener);
            }
        }

        mArrowToViewRef = targetView != null ? new WeakReference<>(targetView) : null;
        if (targetView != null) {
            targetView.addOnLayoutChangeListener(mOnLayoutChangeListener);
        }
    }

    /**
     * 根据目标对象相对中心位置，推导箭头朝向
     *
     * @param bubble 气泡的区域
     * @param target 目标区域
     * @return 推导出的箭头朝向
     */
    private static ArrowDirection getAutoArrowDirection(Rect bubble, Rect target) {
        if (!bubble.intersects(target.left, target.top, target.right, target.bottom)) {
            Point offset = new Point(bubble.centerX() - target.centerX(), bubble.centerY() - target.centerY());
            if (Math.abs(offset.x) < bubble.width() / 2 + target.width() / 2) {
                if (offset.y < 0) {
                    return ArrowDirection.Down;
                } else if (offset.y > 0) {
                    return ArrowDirection.Up;
                }
            } else if (Math.abs(offset.y) < bubble.height() / 2 + target.height() / 2) {
                if (offset.x < 0) {
                    return ArrowDirection.Right;
                } else if (offset.x > 0) {
                    return ArrowDirection.Left;
                }
            }
        }

        return ArrowDirection.None;
    }
}
