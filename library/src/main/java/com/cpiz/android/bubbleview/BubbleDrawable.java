package com.cpiz.android.bubbleview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;

/**
 * 气泡框背景
 *
 * Created by caijw on 2016/5/26.
 * https://github.com/cpiz/BubbleView
 */
class BubbleDrawable extends Drawable {
    private BubbleStyle.ArrowDirection mArrowDirection = BubbleStyle.ArrowDirection.None;
    private Shape mOriginalShape = new Shape();
    private Shape mBorderShape = new Shape();
    private Shape mFillShape = new Shape();
    private Paint mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Path mBorderPath = new Path();
    private Paint mFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Path mFillPath = new Path();
    private float mFillPadding = 0;
    private int mFillColor = 0xCC000000;
    private int mBorderColor = Color.WHITE;
    private PointF mArrowTo = new PointF(0, 0);

    private class Shape {
        RectF Rect = new RectF();
        float BorderWidth = 0;
        float ArrowHeight = 0;
        float ArrowWidth = 0;
        float ArrowOffset = 0;
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
            this.ArrowOffset = shape.ArrowOffset;
            this.ArrowPeakX = shape.ArrowPeakX;
            this.ArrowPeakY = shape.ArrowPeakY;
            this.TopLeftRadius = shape.TopLeftRadius;
            this.TopRightRadius = shape.TopRightRadius;
            this.BottomLeftRadius = shape.BottomLeftRadius;
            this.BottomRightRadius = shape.BottomRightRadius;
        }
    }

    protected void resetRect(int width, int height) {
        mOriginalShape.Rect.set(0, 0, width, height);
    }

    public void setFillColor(int fillColor) {
        mFillColor = fillColor;
    }

    public void setBorderColor(int borderColor) {
        mBorderColor = borderColor;
    }

    public void setBorderWidth(float borderWidth) {
        mOriginalShape.BorderWidth = borderWidth;
    }

    public void setFillPadding(float fillPadding) {
        mFillPadding = fillPadding;
    }

    public void rebuildShapes() {
        buildBorderShape();
        buildFillShape();
    }

    private void buildBorderShape() {
        // 预留四周1/2的边框厚度，使得边框能够完全显示
        mBorderShape.set(mOriginalShape);
        mBorderShape.Rect.set(
                mOriginalShape.Rect.left + mOriginalShape.BorderWidth / 2 + (mArrowDirection.isLeft() ? mOriginalShape.ArrowHeight : 0),
                mOriginalShape.Rect.top + mOriginalShape.BorderWidth / 2 + (mArrowDirection.isUp() ? mOriginalShape.ArrowHeight : 0),
                mOriginalShape.Rect.right - mOriginalShape.BorderWidth / 2 - (mArrowDirection.isRight() ? mOriginalShape.ArrowHeight : 0),
                mOriginalShape.Rect.bottom - mOriginalShape.BorderWidth / 2 - (mArrowDirection.isDown() ? mOriginalShape.ArrowHeight : 0)
        );
        buildArrowPeak(mArrowDirection, mBorderShape);

        mBorderPath.reset();
        buildPath(mBorderShape, mBorderPath);
    }

    private void buildFillShape() {
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
        buildArrowPeak(mArrowDirection, mFillShape);

        mFillPath.reset();
        buildPath(mFillShape, mFillPath);
    }

    private void buildArrowPeak(BubbleStyle.ArrowDirection direction, Shape shape) {
        switch (direction) {
            case Left:
                buildLeftArrowPeak(shape);
                break;
            case Up:
                buildUpArrowPeak(shape);
                break;
            case Right:
                buildRightArrowPeak(shape);
                break;
            case Down:
                buildDownArrowPeak(shape);
                break;
        }
    }

    public void setCornerRadius(float topLeft, float topRight, float bottomRight, float bottomLeft) {
        mOriginalShape.TopLeftRadius = topLeft;
        mOriginalShape.TopRightRadius = topRight;
        mOriginalShape.BottomRightRadius = bottomRight;
        mOriginalShape.BottomLeftRadius = bottomLeft;
    }

    public void setArrowDirection(BubbleStyle.ArrowDirection arrowDirection) {
        mArrowDirection = arrowDirection;
    }

    public void setArrowHeight(float arrowHeight) {
        mOriginalShape.ArrowHeight = arrowHeight;
    }

    public void setArrowWidth(float arrowWidth) {
        mOriginalShape.ArrowWidth = arrowWidth;
    }

    /**
     * 设置箭头指向的View对象中心相对坐标
     *
     * @param x 目标中心x
     * @param y 目标中心y
     */
    public void setArrowTo(float x, float y) {
        mArrowTo.x = x;
        mArrowTo.y = y;
    }

    public void setArrowPos(float arrowPos) {
        mOriginalShape.ArrowOffset = arrowPos;
    }

    @Override
    public void draw(Canvas canvas) {
        mFillPaint.setStyle(Paint.Style.FILL);
        mFillPaint.setColor(mFillColor);
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
        return 0;
    }

    private void buildPath(Shape shape, Path path) {
        switch (mArrowDirection) {
            case None:
                buildWithNoneArrow(shape, path);
                break;
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

    private void buildLeftArrowPeak(Shape shape) {
        float y;
        if (mArrowTo.x == 0 && mArrowTo.y == 0 && shape.ArrowOffset != 0) {
            y = shape.ArrowOffset + (shape.ArrowOffset > 0 ? 0 : shape.Rect.bottom + shape.Rect.top);
        } else {
            y = shape.Rect.centerY() + mArrowTo.y;
        }

        shape.ArrowPeakX = shape.Rect.left - shape.ArrowHeight;
        shape.ArrowPeakY = bound(shape.Rect.top + shape.TopLeftRadius + shape.ArrowWidth / 2 + shape.BorderWidth / 2,
                y, // 确保弧角的显示
                shape.Rect.bottom - shape.BottomLeftRadius - shape.ArrowWidth / 2 - shape.BorderWidth / 2);
        shape.ArrowOffset = shape.ArrowPeakY;
    }

    private void buildUpArrowPeak(Shape shape) {
        float x;
        if (mArrowTo.x == 0 && mArrowTo.y == 0 && shape.ArrowOffset != 0) {
            x = shape.ArrowOffset + (shape.ArrowOffset > 0 ? 0 : shape.Rect.right + shape.Rect.left);
        } else {
            x = shape.Rect.centerX() + mArrowTo.x;
        }

        shape.ArrowPeakX = bound(shape.Rect.left + shape.TopLeftRadius + shape.ArrowWidth / 2 + shape.BorderWidth / 2,
                x,
                shape.Rect.right - shape.TopRightRadius - shape.ArrowWidth / 2 - shape.BorderWidth / 2);
        shape.ArrowPeakY = shape.Rect.top - shape.ArrowHeight;
        shape.ArrowOffset = shape.ArrowPeakX;
    }

    private void buildDownArrowPeak(Shape shape) {
        float x;
        if (mArrowTo.x == 0 && mArrowTo.y == 0 && shape.ArrowOffset != 0) {
            x = shape.ArrowOffset + (shape.ArrowOffset > 0 ? 0 : shape.Rect.right + shape.Rect.left);
        } else {
            x = shape.Rect.centerX() + mArrowTo.x;
        }

        shape.ArrowPeakX = bound(shape.Rect.left + shape.BottomLeftRadius + shape.ArrowWidth / 2 + shape.BorderWidth / 2,
                x,
                shape.Rect.right - shape.BottomRightRadius - shape.ArrowWidth / 2 - shape.BorderWidth / 2);
        shape.ArrowPeakY = shape.Rect.bottom + shape.ArrowHeight;
        shape.ArrowOffset = shape.ArrowPeakX;
    }

    private void buildRightArrowPeak(Shape shape) {
        float y;
        if (mArrowTo.x == 0 && mArrowTo.y == 0 && shape.ArrowOffset != 0) {
            y = shape.ArrowOffset + (shape.ArrowOffset > 0 ? 0 : shape.Rect.bottom + shape.Rect.top);
        } else {
            y = shape.Rect.centerY() + mArrowTo.y;
        }

        shape.ArrowPeakX = shape.Rect.right + shape.ArrowHeight;
        shape.ArrowPeakY = bound(shape.Rect.top + shape.TopRightRadius + shape.ArrowWidth / 2 + shape.BorderWidth / 2,
                y,
                shape.Rect.bottom - shape.BottomRightRadius - shape.ArrowWidth / 2 - shape.BorderWidth / 2);
        shape.ArrowOffset = shape.ArrowPeakY;
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

    void compatPathArcTo(Path path,
                         float left,
                         float top,
                         float right,
                         float bottom,
                         float startAngle,
                         float sweepAngle) {
        mOvalRect.set(left, top, right, bottom);
        path.arcTo(mOvalRect, startAngle, sweepAngle);
    }

    private float bound(float min, float val, float max) {
        return Math.min(Math.max(val, min), max);
    }
}
