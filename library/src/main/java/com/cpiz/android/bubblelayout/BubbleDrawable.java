package com.cpiz.android.bubblelayout;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import java.util.HashMap;
import java.util.Map;

/**
 * 气泡框背景
 *
 * Created by caijw on 2016/5/26.
 */
public class BubbleDrawable extends Drawable {
    /**
     * 箭头朝向枚举
     */
    public enum ArrowDirection {
        None(0),
        Left(1),
        Up(2),
        Right(3),
        Down(4);

        private static final Map<Integer, ArrowDirection> intToTypeMap = new HashMap<>();

        static {
            for (ArrowDirection type : ArrowDirection.values()) {
                intToTypeMap.put(type.mValue, type);
            }
        }

        private int mValue = 0;

        ArrowDirection(int value) {
            mValue = value;
        }

        public static ArrowDirection valueOf(int value) {
            ArrowDirection type = intToTypeMap.get(value);
            if (type == null)
                return ArrowDirection.None;
            return type;
        }

        public boolean isLeft() {
            return this == Left;
        }

        public boolean isUp() {
            return this == Up;
        }

        public boolean isRight() {
            return this == Right;
        }

        public boolean isDown() {
            return this == Down;
        }
    }

    private ArrowDirection mArrowDirection = ArrowDirection.None;
    private Shape mOriginalShape = new Shape();
    private Shape mBorderShape = new Shape();
    private Shape mFillShape = new Shape();
    private Paint mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Path mBorderPath = new Path();
    private Paint mFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Path mFillPath = new Path();
    private float mBorderWidth = 0;
    private float mFillPadding = 0;
    private int mFillColor = 0xCC000000;
    private int mBorderColor = Color.WHITE;
    private float mArrowToX = 0;
    private float mArrowToY = 0;

    private class Shape {
        RectF Rect = new RectF();
        float ArrowHeight = 0;
        float ArrowWidth = 0;
        float TopLeftRadius = 0;
        float TopRightRadius = 0;
        float BottomLeftRadius = 0;
        float BottomRightRadius = 0;

        void set(Shape shape) {
            this.Rect.set(shape.Rect);
            this.ArrowHeight = shape.ArrowHeight;
            this.ArrowWidth = shape.ArrowWidth;
            this.TopLeftRadius = shape.TopLeftRadius;
            this.TopRightRadius = shape.TopRightRadius;
            this.BottomLeftRadius = shape.BottomLeftRadius;
            this.BottomRightRadius = shape.BottomRightRadius;
        }
    }

    public void resetRect(int width, int height) {
        mOriginalShape.Rect.set(0, 0, width, height);
        rebuildShapes();
    }

    public void setFillColor(int fillColor) {
        mFillColor = fillColor;
    }

    public void setBorderColor(int borderColor) {
        mBorderColor = borderColor;
    }

    public void setBorderWidth(float borderWidth) {
        mBorderWidth = borderWidth;
        rebuildShapes();
    }

    public void setFillPadding(float fillPadding) {
        mFillPadding = fillPadding;
        rebuildShapes();
    }

    public void rebuildShapes() {
        buildBorderShape();
        buildFillShape();
    }

    private void buildBorderShape() {
        // 预留四周1/2的边框厚度，使得边框能够完全显示
        mBorderShape.set(mOriginalShape);
        mBorderShape.Rect.set(
                mOriginalShape.Rect.left + mBorderWidth / 2 + (mArrowDirection.isLeft() ? mOriginalShape.ArrowHeight : 0),
                mOriginalShape.Rect.top + mBorderWidth / 2 + (mArrowDirection.isUp() ? mOriginalShape.ArrowHeight : 0),
                mOriginalShape.Rect.right - mBorderWidth / 2 - (mArrowDirection.isRight() ? mOriginalShape.ArrowHeight : 0),
                mOriginalShape.Rect.bottom - mBorderWidth / 2 - (mArrowDirection.isDown() ? mOriginalShape.ArrowHeight : 0)
        );
    }

    private void buildFillShape() {
        mFillShape.set(mOriginalShape);
        mFillShape.Rect.set(
                mOriginalShape.Rect.left + mBorderWidth + mFillPadding + (mArrowDirection.isLeft() ? mOriginalShape.ArrowHeight : 0),
                mOriginalShape.Rect.top + mBorderWidth + mFillPadding + (mArrowDirection.isUp() ? mOriginalShape.ArrowHeight : 0),
                mOriginalShape.Rect.right - mBorderWidth - mFillPadding - (mArrowDirection.isRight() ? mOriginalShape.ArrowHeight : 0),
                mOriginalShape.Rect.bottom - mBorderWidth - mFillPadding - (mArrowDirection.isDown() ? mOriginalShape.ArrowHeight : 0)
        );
        mFillShape.TopLeftRadius = Math.max(0, mOriginalShape.TopLeftRadius - mBorderWidth / 2 - mFillPadding);
        mFillShape.TopRightRadius = Math.max(0, mOriginalShape.TopRightRadius - mBorderWidth / 2 - mFillPadding);
        mFillShape.BottomLeftRadius = Math.max(0, mOriginalShape.BottomLeftRadius - mBorderWidth / 2 - mFillPadding);
        mFillShape.BottomRightRadius = Math.max(0, mOriginalShape.BottomRightRadius - mBorderWidth / 2 - mFillPadding);

        double w = mOriginalShape.ArrowWidth - 2 * (mBorderWidth / 2 + mFillPadding) / Math.sin(Math.atan(mOriginalShape.ArrowHeight / (mOriginalShape.ArrowWidth / 2)));
        double h = w * mOriginalShape.ArrowHeight / mOriginalShape.ArrowWidth;

        mFillShape.ArrowHeight = (float) (h + mBorderWidth / 2 + mFillPadding);
        mFillShape.ArrowWidth = mFillShape.ArrowHeight * mOriginalShape.ArrowWidth / mOriginalShape.ArrowHeight;
    }

    public void setCornerRadius(float topLeft, float topRight, float bottomRight, float bottomLeft) {
        mOriginalShape.TopLeftRadius = topLeft;
        mOriginalShape.TopRightRadius = topRight;
        mOriginalShape.BottomRightRadius = bottomRight;
        mOriginalShape.BottomLeftRadius = bottomLeft;
        rebuildShapes();
    }

    public void setArrowDirection(ArrowDirection arrowDirection) {
        mArrowDirection = arrowDirection;
    }

    /**
     * 设置箭头指向的View对象中心相对坐标
     *
     * @param x 目标中心x
     * @param y 目标中心y
     */
    public void setArrowTo(float x, float y) {
        mArrowToX = x;
        mArrowToY = y;
    }

    public void setArrowHeight(float arrowHeight) {
        mOriginalShape.ArrowHeight = arrowHeight;
        rebuildShapes();
    }

    public void setArrowWidth(float arrowWidth) {
        mOriginalShape.ArrowWidth = arrowWidth;
        rebuildShapes();
    }

    @Override
    public void draw(Canvas canvas) {
        mFillPath.reset();
        buildPath(mFillShape, mFillPath);
        mFillPaint.setStyle(Paint.Style.FILL);
        mFillPaint.setColor(mFillColor);
        canvas.drawPath(mFillPath, mFillPaint);

        if (mBorderWidth > 0) {
            mBorderPath.reset();
            buildPath(mBorderShape, mBorderPath);
            mBorderPaint.setStyle(Paint.Style.STROKE);
            mBorderPaint.setStrokeCap(Paint.Cap.ROUND);
            mBorderPaint.setStrokeJoin(Paint.Join.ROUND);
            mBorderPaint.setStrokeWidth(mBorderWidth);
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
                buildNoneArrow(shape, path);
                break;
            case Up:
                buildTopArrow(shape, path);
                break;
            case Down:
                buildBottomArrow(shape, path);
                break;
            case Left:
                buildLeftArrow(shape, path);
                break;
            case Right:
                buildRightArrow(shape, path);
                break;
        }
    }

    private void buildNoneArrow(Shape shape, Path path) {
        RectF rect = shape.Rect;
        path.moveTo(rect.left, rect.top + shape.TopLeftRadius);
        compatPathArcTo(path, rect.left, rect.top,
                rect.left + 2 * shape.TopLeftRadius, rect.top + 2 * shape.TopLeftRadius, 180, 90);

        path.lineTo(rect.right - shape.TopRightRadius, rect.top);
        compatPathArcTo(path, rect.right - 2 * shape.TopRightRadius, rect.top,
                rect.right, rect.top + 2 * shape.TopRightRadius,
                270, 90);
        path.lineTo(rect.right, rect.bottom - shape.BottomRightRadius);
        compatPathArcTo(path, rect.right - 2 * shape.BottomRightRadius, rect.bottom - 2 * shape.BottomRightRadius,
                rect.right, rect.bottom,
                0, 90);
        path.lineTo(rect.left + shape.BottomLeftRadius, rect.bottom);
        compatPathArcTo(path, rect.left, rect.bottom - 2 * shape.BottomLeftRadius,
                rect.left + 2 * shape.BottomLeftRadius, rect.bottom,
                90, 90);
        path.lineTo(rect.left, rect.top + shape.TopLeftRadius);
    }

    private void buildLeftArrow(Shape shape, Path path) {
        RectF rect = shape.Rect;

        // 箭头顶点坐标
        float peakX = rect.left - shape.ArrowHeight;
        float peakY = bound(shape.TopLeftRadius + shape.ArrowWidth / 2 + 1,
                rect.centerY() + mArrowToY, // 顶点指向目标中心，但保证弧角的显示
                shape.Rect.bottom - shape.BottomLeftRadius - shape.ArrowWidth / 2 - 1);


        // 从箭头顶点开始沿顺时针方向绘制
        path.moveTo(peakX, peakY);
        path.lineTo(rect.left, peakY - shape.ArrowWidth / 2);

        // 左上竖线
        path.lineTo(rect.left, rect.top + shape.TopLeftRadius);

        // 左上弧角
        compatPathArcTo(path, rect.left, rect.top,
                rect.left + 2 * shape.TopLeftRadius, rect.top + 2 * shape.TopLeftRadius,
                180, 90);

        // 上横向
        path.lineTo(rect.right - shape.TopRightRadius, rect.top);

        // 右上弧角
        compatPathArcTo(path, rect.right - 2 * shape.TopRightRadius, rect.top,
                rect.right, rect.top + 2 * shape.TopRightRadius,
                270, 90);

        // 右侧竖线
        path.lineTo(rect.right, rect.bottom - shape.BottomRightRadius);

        // 右下弧角
        compatPathArcTo(path, rect.right - 2 * shape.BottomRightRadius, rect.bottom - 2 * shape.BottomRightRadius,
                rect.right, rect.bottom,
                0, 90);

        // 底部横向
        path.lineTo(rect.left + shape.BottomLeftRadius, rect.bottom);

        // 左下弧角
        compatPathArcTo(path, rect.left, rect.bottom - 2 * shape.BottomLeftRadius,
                rect.left + 2 * shape.BottomLeftRadius, rect.bottom,
                90, 90);

        // 左下竖线
        path.lineTo(rect.left, peakY + shape.ArrowWidth / 2);

        // 回到顶点
        path.lineTo(peakX, peakY);
    }

    private void buildRightArrow(Shape shape, Path path) {
        RectF rect = shape.Rect;
        float peakX = rect.right + shape.ArrowHeight;
        float peakY = bound(shape.TopRightRadius + shape.ArrowWidth / 2 + 1,
                rect.centerY() + mArrowToY,
                shape.Rect.bottom - shape.BottomRightRadius - shape.ArrowWidth / 2 - 1);

        path.moveTo(peakX, peakY);
        path.lineTo(rect.right, peakY + shape.ArrowWidth / 2);
        path.lineTo(rect.right, rect.bottom - shape.BottomRightRadius);
        compatPathArcTo(path, rect.right - 2 * shape.BottomRightRadius, rect.bottom - 2 * shape.BottomRightRadius,
                rect.right, rect.bottom,
                0, 90);
        path.lineTo(rect.left + shape.BottomLeftRadius, rect.bottom);
        compatPathArcTo(path, rect.left, rect.bottom - 2 * shape.BottomLeftRadius,
                rect.left + 2 * shape.BottomLeftRadius, rect.bottom,
                90, 90);
        path.lineTo(rect.left, rect.top + shape.TopLeftRadius);
        compatPathArcTo(path, rect.left, rect.top,
                rect.left + 2 * shape.TopLeftRadius, rect.top + 2 * shape.TopLeftRadius,
                180, 90);
        path.lineTo(rect.right - shape.TopRightRadius, rect.top);
        compatPathArcTo(path, rect.right - 2 * shape.TopRightRadius, rect.top,
                rect.right, rect.top + 2 * shape.TopRightRadius,
                270, 90);
        path.lineTo(rect.right, peakY - shape.ArrowWidth / 2);
        path.lineTo(peakX, peakY);
    }

    private void buildTopArrow(Shape shape, Path path) {
        RectF rect = shape.Rect;
        float peakX = bound(shape.TopLeftRadius + shape.ArrowWidth / 2 + 1,
                rect.centerX() + mArrowToX,
                shape.Rect.right - shape.TopRightRadius - shape.ArrowWidth / 2 - 1);
        float peakY = rect.top - shape.ArrowHeight;

        path.moveTo(peakX, peakY);
        path.lineTo(peakX + shape.ArrowWidth / 2, rect.top);
        path.lineTo(rect.right - shape.TopRightRadius, rect.top);
        compatPathArcTo(path, rect.right - 2 * shape.TopRightRadius, rect.top,
                rect.right, rect.top + 2 * shape.TopRightRadius,
                270, 90);
        path.lineTo(rect.right, rect.bottom - shape.BottomRightRadius);
        compatPathArcTo(path, rect.right - 2 * shape.BottomRightRadius, rect.bottom - 2 * shape.BottomRightRadius,
                rect.right, rect.bottom,
                0, 90);
        path.lineTo(rect.left + shape.BottomLeftRadius, rect.bottom);
        compatPathArcTo(path, rect.left, rect.bottom - 2 * shape.BottomLeftRadius,
                rect.left + 2 * shape.BottomLeftRadius, rect.bottom,
                90, 90);
        path.lineTo(rect.left, rect.top + shape.TopLeftRadius);
        compatPathArcTo(path, rect.left, rect.top,
                rect.left + 2 * shape.TopLeftRadius, rect.top + 2 * shape.TopLeftRadius,
                180, 90);
        path.lineTo(peakX - shape.ArrowWidth / 2, rect.top);
        path.lineTo(peakX, peakY);
    }

    private void buildBottomArrow(Shape shape, Path path) {
        RectF rect = shape.Rect;
        float peakX = bound(shape.BottomLeftRadius + shape.ArrowWidth / 2 + 1,
                rect.centerX() + mArrowToX,
                shape.Rect.right - shape.BottomRightRadius - shape.ArrowWidth / 2 - 1);
        float peakY = rect.bottom + shape.ArrowHeight;

        path.moveTo(peakX, peakY);
        path.lineTo(peakX - shape.ArrowWidth / 2, rect.bottom);
        path.lineTo(rect.left + shape.BottomLeftRadius, rect.bottom);
        compatPathArcTo(path, rect.left, rect.bottom - 2 * shape.BottomLeftRadius,
                rect.left + 2 * shape.BottomLeftRadius, rect.bottom,
                90, 90);
        path.lineTo(rect.left, rect.top + shape.TopLeftRadius);
        compatPathArcTo(path, rect.left, rect.top,
                rect.left + 2 * shape.TopLeftRadius, rect.top + 2 * shape.TopLeftRadius,
                180, 90);
        path.lineTo(rect.right - shape.TopRightRadius, rect.top);
        compatPathArcTo(path, rect.right - 2 * shape.TopRightRadius, rect.top,
                rect.right, rect.top + 2 * shape.TopRightRadius,
                270, 90);
        path.lineTo(rect.right, rect.bottom - shape.BottomRightRadius);
        compatPathArcTo(path, rect.right - 2 * shape.BottomRightRadius, rect.bottom - 2 * shape.BottomRightRadius,
                rect.right, rect.bottom,
                0, 90);
        path.lineTo(peakX + shape.ArrowWidth / 2, rect.bottom);
        path.lineTo(peakX, peakY);
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
