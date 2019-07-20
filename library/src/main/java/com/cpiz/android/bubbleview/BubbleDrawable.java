package com.cpiz.android.bubbleview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import static com.cpiz.android.bubbleview.Utils.bound;

/**
 * 气泡框背景
 * <p>
 * Created by caijw on 2016/5/26.
 * https://github.com/cpiz/BubbleView
 */
class BubbleDrawable extends Drawable {
    private BubbleStyle.ArrowDirection mArrowDirection = BubbleStyle.ArrowDirection.None;
    private BubbleStyle.ArrowPosPolicy mArrowPosPolicy = BubbleStyle.ArrowPosPolicy.TargetCenter;
    private Shape mOriginalShape = new Shape();
    private Shape mBorderShape = new Shape();
    private Shape mFillShape = new Shape();
    private Paint mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Path mBorderPath = new Path();
    private Paint mFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Path mFillPath = new Path();
    private float mFillPadding = 0;
    private int mFillColor = 0xCC000000;
    private int mFillPressColor = mFillColor;
    private boolean mPressed = false;
    private int mBorderColor = Color.WHITE;
    private PointF mArrowTo = new PointF(0, 0);

    private static class Shape {
        RectF Rect = new RectF();
        float BorderWidth = 0;
        float ArrowHeight = 0;
        float ArrowWidth = 0;
        float ArrowDelta = 0;
        float ArrowPeakX = 0;
        float ArrowPeakY = 0;
        float TopLeftRadius = 0;
        float TopRightRadius = 0;
        float BottomLeftRadius = 0;
        float BottomRightRadius = 0;

        void set(Shape shape) {
            this.Rect.set(shape.Rect);
            this.BorderWidth = shape.BorderWidth;
            this.ArrowHeight = shape.ArrowHeight;
            this.ArrowWidth = shape.ArrowWidth;
            this.ArrowDelta = shape.ArrowDelta;
            this.ArrowPeakX = shape.ArrowPeakX;
            this.ArrowPeakY = shape.ArrowPeakY;
            this.TopLeftRadius = shape.TopLeftRadius;
            this.TopRightRadius = shape.TopRightRadius;
            this.BottomLeftRadius = shape.BottomLeftRadius;
            this.BottomRightRadius = shape.BottomRightRadius;
        }
    }

    void resetRect(int width, int height) {
        mOriginalShape.Rect.set(0, 0, width, height);
    }

    void setFillColor(int fillColor) {
        mFillColor = fillColor;
    }

    void setFillPressColor(int fillPressColor) {
        mFillPressColor = fillPressColor;
    }

    void setBorderColor(int borderColor) {
        mBorderColor = borderColor;
    }

    void setBorderWidth(float borderWidth) {
        mOriginalShape.BorderWidth = borderWidth;
    }

    void setFillPadding(float fillPadding) {
        mFillPadding = fillPadding;
    }

    void updateShapes() {
        updateBorderShape();
        updateFillShape();
    }

    private void updateBorderShape() {
        mBorderShape.set(mOriginalShape);
        mBorderShape.Rect.set(  // 内缩四周1/2的边线厚度，使得边线能够完全显示
                mOriginalShape.Rect.left + mOriginalShape.BorderWidth / 2 + (mArrowDirection.isLeft() ? mOriginalShape.ArrowHeight : 0),
                mOriginalShape.Rect.top + mOriginalShape.BorderWidth / 2 + (mArrowDirection.isUp() ? mOriginalShape.ArrowHeight : 0),
                mOriginalShape.Rect.right - mOriginalShape.BorderWidth / 2 - (mArrowDirection.isRight() ? mOriginalShape.ArrowHeight : 0),
                mOriginalShape.Rect.bottom - mOriginalShape.BorderWidth / 2 - (mArrowDirection.isDown() ? mOriginalShape.ArrowHeight : 0)
        );

        // 外层的箭头顶点位置通过箭头位置策略、箭头偏移设定、目标位置决定
        updateBorderArrowPeak(mArrowDirection, mArrowPosPolicy, mArrowTo, mBorderShape);

        updatePath(mBorderShape, mBorderPath);
    }

    private void updateFillShape() {
        mFillShape.set(mBorderShape);
        mFillShape.BorderWidth = 0;
        mFillShape.Rect.set(
                mOriginalShape.Rect.left + mOriginalShape.BorderWidth + mFillPadding + (mArrowDirection.isLeft() ? mOriginalShape.ArrowHeight : 0),
                mOriginalShape.Rect.top + mOriginalShape.BorderWidth + mFillPadding + (mArrowDirection.isUp() ? mOriginalShape.ArrowHeight : 0),
                mOriginalShape.Rect.right - mOriginalShape.BorderWidth - mFillPadding - (mArrowDirection.isRight() ? mOriginalShape.ArrowHeight : 0),
                mOriginalShape.Rect.bottom - mOriginalShape.BorderWidth - mFillPadding - (mArrowDirection.isDown() ? mOriginalShape.ArrowHeight : 0)
        );
        mFillShape.TopLeftRadius = Math.max(0, mOriginalShape.TopLeftRadius - mOriginalShape.BorderWidth / 2 - mFillPadding);
        mFillShape.TopRightRadius = Math.max(0, mOriginalShape.TopRightRadius - mOriginalShape.BorderWidth / 2 - mFillPadding);
        mFillShape.BottomLeftRadius = Math.max(0, mOriginalShape.BottomLeftRadius - mOriginalShape.BorderWidth / 2 - mFillPadding);
        mFillShape.BottomRightRadius = Math.max(0, mOriginalShape.BottomRightRadius - mOriginalShape.BorderWidth / 2 - mFillPadding);

        double w = mOriginalShape.ArrowWidth - 2 * (mOriginalShape.BorderWidth / 2 + mFillPadding) / Math.sin(Math.atan(mOriginalShape.ArrowHeight / (mOriginalShape.ArrowWidth / 2)));
        double h = w * mOriginalShape.ArrowHeight / mOriginalShape.ArrowWidth;

        mFillShape.ArrowHeight = (float) (h + mOriginalShape.BorderWidth / 2 + mFillPadding);
        mFillShape.ArrowWidth = mFillShape.ArrowHeight * mOriginalShape.ArrowWidth / mOriginalShape.ArrowHeight;

        // 内层的箭头顶点位置通过外层边线上的顶点位置来计算
        updateFillArrowPeak(mArrowDirection, mBorderShape, mFillShape);

        updatePath(mFillShape, mFillPath);
    }

    private static void updateFillArrowPeak(BubbleStyle.ArrowDirection direction, Shape borderShape, Shape outFillShape) {
        switch (direction) {
            case Left:
                outFillShape.ArrowPeakX = outFillShape.Rect.left - outFillShape.ArrowHeight;
                outFillShape.ArrowPeakY = borderShape.ArrowPeakY;
                break;
            case Right:
                outFillShape.ArrowPeakX = outFillShape.Rect.right + outFillShape.ArrowHeight;
                outFillShape.ArrowPeakY = borderShape.ArrowPeakY;
                break;
            case Up:
                outFillShape.ArrowPeakX = borderShape.ArrowPeakX;
                outFillShape.ArrowPeakY = outFillShape.Rect.top - outFillShape.ArrowHeight;
                break;
            case Down:
                outFillShape.ArrowPeakX = borderShape.ArrowPeakX;
                outFillShape.ArrowPeakY = outFillShape.Rect.bottom + outFillShape.ArrowHeight;
                break;
            default:
                break;
        }
    }

    private void updateBorderArrowPeak(BubbleStyle.ArrowDirection direction, BubbleStyle.ArrowPosPolicy policy, PointF arrowTo, Shape outShape) {
        switch (direction) {
            case Left:
                outShape.ArrowPeakX = outShape.Rect.left - outShape.ArrowHeight;
                outShape.ArrowPeakY = bound(outShape.Rect.top + outShape.TopLeftRadius + outShape.ArrowWidth / 2 + outShape.BorderWidth / 2,
                        getLeftRightArrowPeakY(policy, arrowTo, outShape), // 确保弧角的显示
                        outShape.Rect.bottom - outShape.BottomLeftRadius - outShape.ArrowWidth / 2 - outShape.BorderWidth / 2);
                break;
            case Up:
                outShape.ArrowPeakX = bound(outShape.Rect.left + outShape.TopLeftRadius + outShape.ArrowWidth / 2 + outShape.BorderWidth / 2,
                        getUpDownArrowPeakX(policy, arrowTo, outShape),
                        outShape.Rect.right - outShape.TopRightRadius - outShape.ArrowWidth / 2 - outShape.BorderWidth / 2);
                outShape.ArrowPeakY = outShape.Rect.top - outShape.ArrowHeight;
                break;
            case Right:
                outShape.ArrowPeakX = outShape.Rect.right + outShape.ArrowHeight;
                outShape.ArrowPeakY = bound(outShape.Rect.top + outShape.TopRightRadius + outShape.ArrowWidth / 2 + outShape.BorderWidth / 2,
                        getLeftRightArrowPeakY(policy, arrowTo, outShape),
                        outShape.Rect.bottom - outShape.BottomRightRadius - outShape.ArrowWidth / 2 - outShape.BorderWidth / 2);
                break;
            case Down:
                outShape.ArrowPeakX = bound(outShape.Rect.left + outShape.BottomLeftRadius + outShape.ArrowWidth / 2 + outShape.BorderWidth / 2,
                        getUpDownArrowPeakX(policy, arrowTo, outShape),
                        outShape.Rect.right - outShape.BottomRightRadius - outShape.ArrowWidth / 2 - outShape.BorderWidth / 2);
                outShape.ArrowPeakY = outShape.Rect.bottom + outShape.ArrowHeight;
                break;
        }
    }

    void setCornerRadius(float topLeft, float topRight, float bottomRight, float bottomLeft) {
        mOriginalShape.TopLeftRadius = topLeft;
        mOriginalShape.TopRightRadius = topRight;
        mOriginalShape.BottomRightRadius = bottomRight;
        mOriginalShape.BottomLeftRadius = bottomLeft;
    }

    void setArrowDirection(BubbleStyle.ArrowDirection arrowDirection) {
        mArrowDirection = arrowDirection;
    }

    void setArrowPosPolicy(BubbleStyle.ArrowPosPolicy arrowPosPolicy) {
        mArrowPosPolicy = arrowPosPolicy;
    }

    void setArrowHeight(float arrowHeight) {
        mOriginalShape.ArrowHeight = arrowHeight;
    }

    void setArrowWidth(float arrowWidth) {
        mOriginalShape.ArrowWidth = arrowWidth;
    }

    /**
     * 设置箭头指向的View对象中心相对坐标
     *
     * @param x 目标中心x
     * @param y 目标中心y
     */
    void setArrowTo(float x, float y) {
        mArrowTo.x = x;
        mArrowTo.y = y;
    }

    void setArrowPosDelta(float arrowDelta) {
        mOriginalShape.ArrowDelta = arrowDelta;
    }

    void setPressed(boolean pressed) {
        mPressed = pressed;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        mFillPaint.setStyle(Paint.Style.FILL);
        mFillPaint.setColor(mPressed ? mFillPressColor : mFillColor);
        canvas.drawPath(mFillPath, mFillPaint);

        if (mBorderShape.BorderWidth > 0) {
            mBorderPaint.setStyle(Paint.Style.STROKE);
            mBorderPaint.setStrokeCap(Paint.Cap.ROUND);
            mBorderPaint.setStrokeJoin(Paint.Join.ROUND);
            mBorderPaint.setStrokeWidth(mBorderShape.BorderWidth);
            mBorderPaint.setColor(mBorderColor);
            canvas.drawPath(mBorderPath, mBorderPaint);
        }
    }

    @Override
    public void setAlpha(int alpha) {
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }

    private void updatePath(Shape shape, Path path) {
        path.reset();
        switch (mArrowDirection) {
            case Up:
                buildWithUpArrow(shape, path);
                break;
            case Down:
                buildWithDownArrow(shape, path);
                break;
            case Left:
                buildWithLeftArrow(shape, path);
                break;
            case Right:
                buildWithRightArrow(shape, path);
                break;
            default:
                buildWithNoneArrow(shape, path);
                break;
        }
    }

    private void buildWithNoneArrow(Shape shape, Path path) {
        RectF rect = shape.Rect;
        path.moveTo(rect.left, rect.top + shape.TopLeftRadius);
        compatPathArcTo(path, rect.left, rect.top,
                rect.left + 2 * shape.TopLeftRadius, rect.top + 2 * shape.TopLeftRadius, 180, 90);
        path.lineTo(rect.right - shape.TopRightRadius, rect.top);
        buildTopRightCorner(shape, path);
        path.lineTo(rect.right, rect.bottom - shape.BottomRightRadius);
        buildBottomRightCorner(shape, path);
        path.lineTo(rect.left + shape.BottomLeftRadius, rect.bottom);
        buildBottomLeftCorner(shape, path);
        path.lineTo(rect.left, rect.top + shape.TopLeftRadius);
    }

    private void buildWithLeftArrow(Shape shape, Path path) {
        RectF rect = shape.Rect;
        path.moveTo(shape.ArrowPeakX, shape.ArrowPeakY); // 从箭头顶点开始沿顺时针方向绘制
        path.lineTo(rect.left, shape.ArrowPeakY - shape.ArrowWidth / 2);
        path.lineTo(rect.left, rect.top + shape.TopLeftRadius); // 左上竖线
        buildTopLeftCorner(shape, path); // 左上弧角
        path.lineTo(rect.right - shape.TopRightRadius, rect.top); // 上横线
        buildTopRightCorner(shape, path); // 右上弧角
        path.lineTo(rect.right, rect.bottom - shape.BottomRightRadius); // 右侧竖线
        buildBottomRightCorner(shape, path); // 右下弧角
        path.lineTo(rect.left + shape.BottomLeftRadius, rect.bottom); // 底部横向
        buildBottomLeftCorner(shape, path); // 左下弧角
        path.lineTo(rect.left, shape.ArrowPeakY + shape.ArrowWidth / 2); // 左下竖线
        path.lineTo(shape.ArrowPeakX, shape.ArrowPeakY); // 回到顶点
    }

    private void buildWithUpArrow(Shape shape, Path path) {
        RectF rect = shape.Rect;
        path.moveTo(shape.ArrowPeakX, shape.ArrowPeakY);
        path.lineTo(shape.ArrowPeakX + shape.ArrowWidth / 2, rect.top);
        path.lineTo(rect.right - shape.TopRightRadius, rect.top);
        buildTopRightCorner(shape, path);
        path.lineTo(rect.right, rect.bottom - shape.BottomRightRadius);
        buildBottomRightCorner(shape, path);
        path.lineTo(rect.left + shape.BottomLeftRadius, rect.bottom);
        buildBottomLeftCorner(shape, path);
        path.lineTo(rect.left, rect.top + shape.TopLeftRadius);
        buildTopLeftCorner(shape, path);
        path.lineTo(shape.ArrowPeakX - shape.ArrowWidth / 2, rect.top);
        path.lineTo(shape.ArrowPeakX, shape.ArrowPeakY);
    }

    private void buildWithRightArrow(Shape shape, Path path) {
        RectF rect = shape.Rect;
        path.moveTo(shape.ArrowPeakX, shape.ArrowPeakY);
        path.lineTo(rect.right, shape.ArrowPeakY + shape.ArrowWidth / 2);
        path.lineTo(rect.right, rect.bottom - shape.BottomRightRadius);
        buildBottomRightCorner(shape, path);
        path.lineTo(rect.left + shape.BottomLeftRadius, rect.bottom);
        buildBottomLeftCorner(shape, path);
        path.lineTo(rect.left, rect.top + shape.TopLeftRadius);
        buildTopLeftCorner(shape, path);
        path.lineTo(rect.right - shape.TopRightRadius, rect.top);
        buildTopRightCorner(shape, path);
        path.lineTo(rect.right, shape.ArrowPeakY - shape.ArrowWidth / 2);
        path.lineTo(shape.ArrowPeakX, shape.ArrowPeakY);
    }

    private void buildWithDownArrow(Shape shape, Path path) {
        RectF rect = shape.Rect;
        path.moveTo(shape.ArrowPeakX, shape.ArrowPeakY);
        path.lineTo(shape.ArrowPeakX - shape.ArrowWidth / 2, rect.bottom);
        path.lineTo(rect.left + shape.BottomLeftRadius, rect.bottom);
        buildBottomLeftCorner(shape, path);
        path.lineTo(rect.left, rect.top + shape.TopLeftRadius);
        buildTopLeftCorner(shape, path);
        path.lineTo(rect.right - shape.TopRightRadius, rect.top);
        buildTopRightCorner(shape, path);
        path.lineTo(rect.right, rect.bottom - shape.BottomRightRadius);
        buildBottomRightCorner(shape, path);
        path.lineTo(shape.ArrowPeakX + shape.ArrowWidth / 2, rect.bottom);
        path.lineTo(shape.ArrowPeakX, shape.ArrowPeakY);
    }

    private static float getLeftRightArrowPeakY(BubbleStyle.ArrowPosPolicy policy, PointF arrowTo, Shape shape) {
        float y;
        switch (policy) {
            case TargetCenter:
                y = shape.Rect.centerY() + arrowTo.y;
                break;
            case SelfCenter:
                y = shape.Rect.centerY();
                break;
            case SelfBegin:
                y = shape.Rect.top;
                y += shape.ArrowDelta;
                break;
            case SelfEnd:
                y = shape.Rect.bottom;
                y -= shape.ArrowDelta;
                break;
            default:
                y = 0;
        }
        return y;
    }

    private static float getUpDownArrowPeakX(BubbleStyle.ArrowPosPolicy policy, PointF arrowTo, Shape shape) {
        float x;
        switch (policy) {
            case TargetCenter:
                x = shape.Rect.centerX() + arrowTo.x;
                break;
            case SelfCenter:
                x = shape.Rect.centerX();
                break;
            case SelfBegin:
                x = shape.Rect.left;
                x += shape.ArrowDelta;
                break;
            case SelfEnd:
                x = shape.Rect.right;
                x -= shape.ArrowDelta;
                break;
            default:
                x = 0;
        }

        return x;
    }

    private void buildTopLeftCorner(Shape shape, Path path) {
        compatPathArcTo(path,
                shape.Rect.left,
                shape.Rect.top,
                shape.Rect.left + 2 * shape.TopLeftRadius,
                shape.Rect.top + 2 * shape.TopLeftRadius,
                180,
                90);
    }

    private void buildTopRightCorner(Shape shape, Path path) {
        compatPathArcTo(path,
                shape.Rect.right - 2 * shape.TopRightRadius,
                shape.Rect.top,
                shape.Rect.right,
                shape.Rect.top + 2 * shape.TopRightRadius,
                270,
                90);
    }

    private void buildBottomRightCorner(Shape shape, Path path) {
        compatPathArcTo(path,
                shape.Rect.right - 2 * shape.BottomRightRadius,
                shape.Rect.bottom - 2 * shape.BottomRightRadius,
                shape.Rect.right,
                shape.Rect.bottom,
                0,
                90);
    }

    private void buildBottomLeftCorner(Shape shape, Path path) {
        compatPathArcTo(path,
                shape.Rect.left,
                shape.Rect.bottom - 2 * shape.BottomLeftRadius,
                shape.Rect.left + 2 * shape.BottomLeftRadius,
                shape.Rect.bottom,
                90,
                90);
    }

    private RectF mOvalRect = new RectF();

    private void compatPathArcTo(Path path,
                                 float left,
                                 float top,
                                 float right,
                                 float bottom,
                                 float startAngle,
                                 float sweepAngle) {
        mOvalRect.set(left, top, right, bottom);
        path.arcTo(mOvalRect, startAngle, sweepAngle);
    }
}
