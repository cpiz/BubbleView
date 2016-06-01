package com.cpiz.android.bubbleview;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.cpiz.android.bubblelayout.R;

/**
 * 气泡控件的实现类，将与真正的气泡View进行聚合，方便扩展
 *
 * Created by caijw on 2016/6/1.
 */
class BubbleImpl implements BubbleStyle {
    private View mParentView;
    private BubbleCallback mHolderCallback;

    private BubbleDrawable mBubbleDrawable = new BubbleDrawable();
    private ArrowDirection mArrowDirection = ArrowDirection.None;
    private int mArrowToViewId = 0;
    private float mArrowHeight = 0;
    private float mArrowWidth = 0;
    private float mArrowOffset = 0;
    private float mCornerTopLeftRadius = 0;
    private float mCornerTopRightRadius = 0;
    private float mCornerBottomLeftRadius = 0;
    private float mCornerBottomRightRadius = 0;
    private int mPaddingLeftOffset = 0, mPaddingTopOffset = 0, mPaddingRightOffset = 0, mPaddingBottomOffset = 0;
    private int mFillColor = 0xCC000000;
    private int mBorderColor = Color.WHITE;
    private float mBorderWidth = 0;
    private float mFillPadding = 0;

    public void init(View view, Context context, AttributeSet attrs) {
        mParentView = view;
        mHolderCallback = (BubbleCallback) view;

        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BubbleStyle);
            mArrowDirection = ArrowDirection.valueOf(ta.getInt(R.styleable.BubbleStyle_bb_arrowDirection, 0));
            mArrowHeight = ta.getDimension(R.styleable.BubbleStyle_bb_arrowHeight, dpToPx(6));
            mArrowWidth = ta.getDimension(R.styleable.BubbleStyle_bb_arrowWidth, dpToPx(10));
            mArrowOffset = ta.getDimension(R.styleable.BubbleStyle_bb_arrowOffset, 0);
            mArrowToViewId = ta.getResourceId(R.styleable.BubbleStyle_bb_arrowTo, 0);
            if (mArrowToViewId != 0) {
                mArrowOffset = 0; // 箭头自动指向优先
            }

            float radius = ta.getDimension(R.styleable.BubbleStyle_bb_cornerRadius, dpToPx(10));
            mCornerTopLeftRadius = mCornerTopRightRadius = mCornerBottomLeftRadius = mCornerBottomRightRadius = radius;
            mCornerTopLeftRadius = ta.getDimension(R.styleable.BubbleStyle_bb_cornerTopLeftRadius, mCornerTopLeftRadius);
            mCornerTopRightRadius = ta.getDimension(R.styleable.BubbleStyle_bb_cornerTopRightRadius, mCornerTopLeftRadius);
            mCornerBottomLeftRadius = ta.getDimension(R.styleable.BubbleStyle_bb_cornerBottomLeftRadius, mCornerTopLeftRadius);
            mCornerBottomRightRadius = ta.getDimension(R.styleable.BubbleStyle_bb_cornerBottomRightRadius, mCornerTopLeftRadius);

            mFillColor = ta.getColor(R.styleable.BubbleStyle_bb_fillColor, 0xCC000000);
            mFillPadding = ta.getDimension(R.styleable.BubbleStyle_bb_fillPadding, 0);
            mBorderColor = ta.getColor(R.styleable.BubbleStyle_bb_borderColor, Color.WHITE);
            mBorderWidth = ta.getDimension(R.styleable.BubbleStyle_bb_borderWidth, 0);

            ta.recycle();
        }
        updateDrawable(mParentView.getWidth(), mParentView.getHeight(), false);
    }

    /**
     * 设置箭头朝向
     *
     * @param arrowDirection 上下左右或者无
     */
    public void setArrowDirection(ArrowDirection arrowDirection) {
        mArrowDirection = arrowDirection;
        updateDrawable();
    }

    public ArrowDirection getArrowDirection() {
        return mArrowDirection;
    }

    /**
     * 设置箭头三角形厚度
     *
     * @param arrowHeight 箭头厚度
     */
    public void setArrowHeight(float arrowHeight) {
        mArrowHeight = arrowHeight;
        updateDrawable();
    }

    public float getArrowHeight() {
        return mArrowHeight;
    }

    /**
     * 设置箭头三角形底宽
     *
     * @param arrowWidth 箭头底边宽度
     */
    public void setArrowWidth(float arrowWidth) {
        mArrowWidth = arrowWidth;
        updateDrawable();
    }

    public float getArrowWidth() {
        return mArrowWidth;
    }

    /**
     * 设置箭头在边线上的位置，视箭头方向而定
     *
     * @param arrowOffset 根据箭头位置，偏移像素值：
     *                    朝上/下时在X轴方向偏移，>0 时从正方向偏移，<0时从负方向偏移
     *                    朝左/右时在Y轴方向偏移，>0 时从正方向偏移，<0时从负方向偏移
     */
    public void setArrowOffset(float arrowOffset) {
        mArrowOffset = arrowOffset;
        updateDrawable();
    }

    public float getArrowOffset() {
        return mArrowOffset;
    }

    /**
     * 设置箭头指向的View对象ID
     * 设置了View对象后，setArrowPos将不起作用
     *
     * @param arrowToViewId 指向的ViewId
     */
    public void setArrowToViewId(int arrowToViewId) {
        mArrowToViewId = arrowToViewId;
        updateDrawable();
    }

    public int getArrowToViewId() {
        return mArrowToViewId;
    }

    /**
     * 设置气泡背景色
     *
     * @param fillColor 气泡背景颜色
     */
    public void setFillColor(int fillColor) {
        mFillColor = fillColor;
        updateDrawable();
    }

    public int getFillColor() {
        return mFillColor;
    }

    /**
     * 设置边框线颜色
     *
     * @param borderColor 边框颜色
     */
    public void setBorderColor(int borderColor) {
        mBorderColor = borderColor;
        updateDrawable();
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    /**
     * 设置边框线宽
     *
     * @param borderWidth 边框厚度
     */
    public void setBorderWidth(float borderWidth) {
        mBorderWidth = borderWidth;
        updateDrawable();
    }

    public float getBorderWidth() {
        return mBorderWidth;
    }

    /**
     * 设置边框于背景之间的间隙宽度
     *
     * @param fillPadding 间隙宽度
     */
    public void setFillPadding(float fillPadding) {
        mFillPadding = fillPadding;
        updateDrawable();
    }

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
    public void setCornerRadius(float topLeft, float topRight, float bottomRight, float bottomLeft) {
        mCornerTopLeftRadius = topLeft;
        mCornerTopRightRadius = topRight;
        mCornerBottomRightRadius = bottomRight;
        mCornerBottomLeftRadius = bottomLeft;
        updateDrawable();
    }

    public void setCornerRadius(float radius) {
        setCornerRadius(radius, radius, radius, radius);
    }

    public float getCornerTopLeftRadius() {
        return mCornerTopLeftRadius;
    }

    public float getCornerTopRightRadius() {
        return mCornerTopRightRadius;
    }

    public float getCornerBottomLeftRadius() {
        return mCornerBottomLeftRadius;
    }

    public float getCornerBottomRightRadius() {
        return mCornerBottomRightRadius;
    }

    /**
     * dp转为px
     *
     * @param dp dp值
     * @return px值
     */
    private static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    // 方便计算用的中间值对象，避免重复创建
    private int[] mLocation = new int[2];
    private Rect mRectTo = new Rect();
    private Rect mRectSelf = new Rect();

    protected void updateDrawable(int width, int height, boolean drawImmediately) {
        int arrowToOffsetX = 0;
        int arrowToOffsetY = 0;
        View rootView = mParentView.getRootView();
        if (mArrowToViewId != 0 && rootView instanceof ViewGroup) {
            View arrowToView = rootView.findViewById(mArrowToViewId);
            if (arrowToView != null) {
                arrowToView.getLocationInWindow(mLocation);
                mRectTo.set(mLocation[0], mLocation[1], mLocation[0] + arrowToView.getWidth(), mLocation[1] + arrowToView.getHeight());

                mParentView.getLocationInWindow(mLocation);
                mRectSelf.set(mLocation[0], mLocation[1], mLocation[0] + width, mLocation[1] + height);

                arrowToOffsetX = mRectTo.centerX() - mRectSelf.centerX();
                arrowToOffsetY = mRectTo.centerY() - mRectSelf.centerY();
                mArrowDirection = getArrowDirection(width, height, arrowToOffsetX, arrowToOffsetY);
            }
        }
        mParentView.setPadding(mParentView.getPaddingLeft(), mParentView.getPaddingTop(), mParentView.getPaddingRight(), mParentView.getPaddingBottom());

        if (drawImmediately) {
            mBubbleDrawable.resetRect(width, height);
            mBubbleDrawable.setCornerRadius(mCornerTopLeftRadius, mCornerTopRightRadius, mCornerBottomRightRadius, mCornerBottomLeftRadius);
            mBubbleDrawable.setFillColor(mFillColor);
            mBubbleDrawable.setBorderWidth(mBorderWidth);
            mBubbleDrawable.setFillPadding(mFillPadding);
            mBubbleDrawable.setBorderColor(mBorderColor);
            mBubbleDrawable.setArrowDirection(mArrowDirection);
            mBubbleDrawable.setArrowTo(arrowToOffsetX, arrowToOffsetY);
            mBubbleDrawable.setArrowPos(mArrowOffset);
            mBubbleDrawable.setArrowHeight(mArrowHeight);
            mBubbleDrawable.setArrowWidth(mArrowWidth);
            mBubbleDrawable.rebuildShapes();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mParentView.setBackground(mBubbleDrawable);
            } else {
                // noinspection deprecation
                mParentView.setBackgroundDrawable(mBubbleDrawable);
            }
        }
    }

    private void updateDrawable() {
        updateDrawable(mParentView.getWidth(), mParentView.getHeight(), true);
    }

    /**
     * 根据目标对象相对中心位置，推导箭头朝向
     *
     * @param width   自己的宽度
     * @param height  自己的高度
     * @param offsetX 目标对象中心相对X
     * @param offsetY 目标对象中心相对Y
     * @return 推导出的箭头朝向
     */
    private ArrowDirection getArrowDirection(int width, int height, int offsetX, int offsetY) {
        int targetCenterX = offsetX + width / 2;
        int targetCenterY = offsetY + height / 2;

        if (targetCenterX < 0 && targetCenterY > 0 && targetCenterY < height) {
            return ArrowDirection.Left;
        } else if (targetCenterY < 0 && targetCenterX > 0 && targetCenterX < width) {
            return ArrowDirection.Up;
        } else if (targetCenterX > width && targetCenterY > 0 && targetCenterY < height) {
            return ArrowDirection.Right;
        } else if (targetCenterY > height && targetCenterX > 0 && targetCenterX < width) {
            return ArrowDirection.Down;
        } else if (Math.abs(offsetX) > Math.abs(offsetY) && offsetX < 0) {
            return ArrowDirection.Left;
        } else if (Math.abs(offsetX) < Math.abs(offsetY) && offsetY < 0) {
            return ArrowDirection.Up;
        } else if (Math.abs(offsetX) > Math.abs(offsetY) && offsetX > 0) {
            return ArrowDirection.Right;
        } else if (Math.abs(offsetX) < Math.abs(offsetY) && offsetY > 0) {
            return ArrowDirection.Down;
        } else {
            return ArrowDirection.None;
        }
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public void setPadding(int left, int top, int right, int bottom) {
        mPaddingLeftOffset = mPaddingTopOffset = mPaddingRightOffset = mPaddingBottomOffset = 0;
        switch (mArrowDirection) {
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
        }

        mHolderCallback.setSuperPadding(
                left + mPaddingLeftOffset,
                top + mPaddingTopOffset,
                right + mPaddingRightOffset,
                bottom + mPaddingBottomOffset);
    }

    public int getPaddingLeft() {
        return mHolderCallback.getSuperPaddingLeft() - mPaddingLeftOffset;
    }

    public int getPaddingTop() {
        return mHolderCallback.getSuperPaddingTop() - mPaddingTopOffset;
    }

    public int getPaddingRight() {
        return mHolderCallback.getSuperPaddingRight() - mPaddingRightOffset;
    }

    public int getPaddingBottom() {
        return mHolderCallback.getSuperPaddingBottom() - mPaddingBottomOffset;
    }
}
