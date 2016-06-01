package com.cpiz.android.bubblelayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * 气泡样式的RelativeLayout布局
 * 支持在XML布局中通过自定义属性设定样式，
 * Created by caijw on 2016/5/26.
 */
@SuppressWarnings("unused")
public class BubbleLayout extends RelativeLayout {
    private BubbleDrawable mBubbleDrawable = new BubbleDrawable();
    private BubbleDrawable.ArrowDirection mArrowDirection = BubbleDrawable.ArrowDirection.None;
    private int mArrowToViewId = 0;
    private float mArrowHeight = 0;
    private float mArrowWidth = 0;
    private float mArrowOffset = 0;
    private int mPaddingLeftOffset = 0, mPaddingTopOffset = 0, mPaddingRightOffset = 0, mPaddingBottomOffset = 0;
    private float mCornerTopLeftRadius = 0;
    private float mCornerTopRightRadius = 0;
    private float mCornerBottomLeftRadius = 0;
    private float mCornerBottomRightRadius = 0;
    private int mFillColor = 0xCC000000;
    private int mBorderColor = Color.WHITE;
    private float mBorderWidth = 0;
    private float mFillPadding = 0;

    public BubbleLayout(Context context) {
        super(context);
        init(context, null);
    }

    public BubbleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BubbleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BubbleLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BubbleLayout);
            mArrowDirection = BubbleDrawable.ArrowDirection.valueOf(
                    ta.getInt(R.styleable.BubbleLayout_bb_arrowDirection, 0));
            mArrowHeight = ta.getDimension(R.styleable.BubbleLayout_bb_arrowHeight, dpToPx(6));
            mArrowWidth = ta.getDimension(R.styleable.BubbleLayout_bb_arrowWidth, dpToPx(10));
            mArrowOffset = ta.getDimension(R.styleable.BubbleLayout_bb_arrowOffset, 0);
            mArrowToViewId = ta.getResourceId(R.styleable.BubbleLayout_bb_arrowTo, 0);
            if (mArrowToViewId != 0) {
                mArrowOffset = 0; // 箭头自动指向优先
            }

            float radius = ta.getDimension(R.styleable.BubbleLayout_bb_cornerRadius, dpToPx(10));
            mCornerTopLeftRadius = mCornerTopRightRadius = mCornerBottomLeftRadius = mCornerBottomRightRadius = radius;
            mCornerTopLeftRadius = ta.getDimension(R.styleable.BubbleLayout_bb_cornerTopLeftRadius, mCornerTopLeftRadius);
            mCornerTopRightRadius = ta.getDimension(R.styleable.BubbleLayout_bb_cornerTopRightRadius, mCornerTopLeftRadius);
            mCornerBottomLeftRadius = ta.getDimension(R.styleable.BubbleLayout_bb_cornerBottomLeftRadius, mCornerTopLeftRadius);
            mCornerBottomRightRadius = ta.getDimension(R.styleable.BubbleLayout_bb_cornerBottomRightRadius, mCornerTopLeftRadius);

            mFillColor = ta.getColor(R.styleable.BubbleLayout_bb_fillColor, 0xCC000000);
            mFillPadding = ta.getDimension(R.styleable.BubbleLayout_bb_fillPadding, 0);
            mBorderColor = ta.getColor(R.styleable.BubbleLayout_bb_borderColor, Color.WHITE);
            mBorderWidth = ta.getDimension(R.styleable.BubbleLayout_bb_borderWidth, 0);

            ta.recycle();
        }

        updateDrawable(getWidth(), getHeight(), false);
    }

    public BubbleDrawable.ArrowDirection getArrowDirection() {
        return mArrowDirection;
    }

    /**
     * 设置箭头朝向
     *
     * @param arrowDirection 上下左右或者无
     */
    public void setArrowDirection(BubbleDrawable.ArrowDirection arrowDirection) {
        mArrowDirection = arrowDirection;
        updateDrawable(getWidth(), getHeight());
    }

    /**
     * 设置箭头三角形厚度
     *
     * @param arrowHeight 箭头厚度
     */
    public void setArrowHeight(float arrowHeight) {
        mArrowHeight = arrowHeight;
        updateDrawable(getWidth(), getHeight());
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
        updateDrawable(getWidth(), getHeight());
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
        updateDrawable(getWidth(), getHeight());
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
        updateDrawable(getWidth(), getHeight());
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
        updateDrawable(getWidth(), getHeight());
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
        updateDrawable(getWidth(), getHeight());
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
        updateDrawable(getWidth(), getHeight());
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
        updateDrawable(getWidth(), getHeight());
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
        updateDrawable(getWidth(), getHeight());
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
     * 设定Padding
     * 将自动将箭头区域占用空间加入Padding，使内容能够完全被气泡包含
     *
     * @param left   用户指定的 LeftPadding
     * @param top    用户指定的 TopPadding
     * @param right  用户指定的 RightPadding
     * @param bottom 用户指定的 BottomPadding
     */
    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        resetPadding(left, top, right, bottom);
    }

    @Override
    public int getPaddingLeft() {
        return super.getPaddingLeft() - mPaddingLeftOffset;
    }

    @Override
    public int getPaddingTop() {
        return super.getPaddingTop() - mPaddingTopOffset;
    }

    @Override
    public int getPaddingRight() {
        return super.getPaddingRight() - mPaddingRightOffset;
    }

    @Override
    public int getPaddingBottom() {
        return super.getPaddingBottom() - mPaddingBottomOffset;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            updateDrawable(right - left, bottom - top);
        }
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private void resetPadding(int left, int top, int right, int bottom) {
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
        super.setPadding(left + mPaddingLeftOffset, top + mPaddingTopOffset,
                right + mPaddingRightOffset, bottom + mPaddingBottomOffset);
    }

    // 方便计算用的中间值对象，避免重复创建
    private int[] mLocation = new int[2];
    private Rect mRectTo = new Rect();
    private Rect mRectSelf = new Rect();

    private void updateDrawable(int width, int height, boolean drawImmediately) {
        int arrowToOffsetX = 0;
        int arrowToOffsetY = 0;
        View rootView = getRootView();
        if (mArrowToViewId != 0 && rootView instanceof ViewGroup) {
            View arrowToView = rootView.findViewById(mArrowToViewId);
            if (arrowToView != null) {
                arrowToView.getLocationInWindow(mLocation);
                mRectTo.set(mLocation[0], mLocation[1], mLocation[0] + arrowToView.getWidth(), mLocation[1] + arrowToView.getHeight());

                getLocationInWindow(mLocation);
                mRectSelf.set(mLocation[0], mLocation[1], mLocation[0] + getWidth(), mLocation[1] + getHeight());

                arrowToOffsetX = mRectTo.centerX() - mRectSelf.centerX();
                arrowToOffsetY = mRectTo.centerY() - mRectSelf.centerY();
                mArrowDirection = getArrowDirection(width, height, arrowToOffsetX, arrowToOffsetY);
            }
        }
        resetPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());

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
                setBackground(mBubbleDrawable);
            } else {
                // noinspection deprecation
                setBackgroundDrawable(mBubbleDrawable);
            }
        }
    }

    private void updateDrawable(int width, int height) {
        updateDrawable(width, height, true);
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
    private BubbleDrawable.ArrowDirection getArrowDirection(int width, int height, int offsetX, int offsetY) {
        int targetCenterX = offsetX + width / 2;
        int targetCenterY = offsetY + height / 2;

        if (targetCenterX < 0 && targetCenterY > 0 && targetCenterY < height) {
            return BubbleDrawable.ArrowDirection.Left;
        } else if (targetCenterY < 0 && targetCenterX > 0 && targetCenterX < width) {
            return BubbleDrawable.ArrowDirection.Up;
        } else if (targetCenterX > width && targetCenterY > 0 && targetCenterY < height) {
            return BubbleDrawable.ArrowDirection.Right;
        } else if (targetCenterY > height && targetCenterX > 0 && targetCenterX < width) {
            return BubbleDrawable.ArrowDirection.Down;
        } else if (Math.abs(offsetX) > Math.abs(offsetY) && offsetX < 0) {
            return BubbleDrawable.ArrowDirection.Left;
        } else if (Math.abs(offsetX) < Math.abs(offsetY) && offsetY < 0) {
            return BubbleDrawable.ArrowDirection.Up;
        } else if (Math.abs(offsetX) > Math.abs(offsetY) && offsetX > 0) {
            return BubbleDrawable.ArrowDirection.Right;
        } else if (Math.abs(offsetX) < Math.abs(offsetY) && offsetY > 0) {
            return BubbleDrawable.ArrowDirection.Down;
        } else {
            return BubbleDrawable.ArrowDirection.None;
        }
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
}
