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
    }

    private ArrowDirection mArrowDirection = ArrowDirection.None;
    private float mArrowHeight = 0;
    private float mArrowWidth = 0;

    private RectF mRect = new RectF();
    private Paint mFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Path mPath = new Path();
    private float mBorderWidth = 0;
    private int mBackColor = 0xCC000000;
    private int mBorderColor = Color.WHITE;
    private float mCornerTopLeftRadius = 0;
    private float mCornerTopRightRadius = 0;
    private float mCornerBottomLeftRadius = 0;
    private float mCornerBottomRightRadius = 0;
    private float mArrowToX = 0;
    private float mArrowToY = 0;

    public void resetRect(int width, int height) {
        mRect.set(0, 0, width, height);
    }

    public void setBackColor(int backColor) {
        mBackColor = backColor;
    }

    public void setBorderColor(int borderColor) {
        mBorderColor = borderColor;
    }

    public void setBorderWidth(float borderWidth) {
        mBorderWidth = borderWidth;
    }

    public void setCornerRadius(float topLeft, float topRight, float bottomRight, float bottomLeft) {
        mCornerTopLeftRadius = topLeft;
        mCornerTopRightRadius = topRight;
        mCornerBottomRightRadius = bottomRight;
        mCornerBottomLeftRadius = bottomLeft;
    }

    public void setArrowDirection(ArrowDirection arrowDirection) {
        mArrowDirection = arrowDirection;
    }

    /**
     * 设置箭头指向的View对象中心相对坐标
     *
     * @param x
     * @param y
     */
    public void setArrowTo(float x, float y) {
        mArrowToX = x;
        mArrowToY = y;
    }

    public void setArrowHeight(float arrowHeight) {
        mArrowHeight = arrowHeight;
    }

    public void setArrowWidth(float arrowWidth) {
        mArrowWidth = arrowWidth;
    }

    @Override
    public void draw(Canvas canvas) {
        mPath.reset();
        buildPath(mPath);

        mFillPaint.setStyle(Paint.Style.FILL);
        mFillPaint.setColor(mBackColor);
        canvas.drawPath(mPath, mFillPaint);

        if (mBorderWidth > 0) {
            mBorderPaint.setStyle(Paint.Style.STROKE);
            mBorderPaint.setStrokeCap(Paint.Cap.ROUND);
            mBorderPaint.setStrokeJoin(Paint.Join.ROUND);
            mBorderPaint.setStrokeWidth(mBorderWidth);
            mBorderPaint.setColor(mBorderColor);
            canvas.drawPath(mPath, mBorderPaint);
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

    private void buildPath(Path path) {
        switch (mArrowDirection) {
            case None:
                buildNoneArrow(path);
                break;
            case Up:
                buildTopArrow(path);
                break;
            case Down:
                buildBottomArrow(path);
                break;
            case Left:
                buildLeftArrow(path);
                break;
            case Right:
                buildRightArrow(path);
                break;
        }
    }

    private void buildNoneArrow(Path path) {
        RectF mBubbleRect = new RectF(mRect.left + mBorderWidth / 2, mRect.top + mBorderWidth / 2, mRect.right - mBorderWidth / 2, mRect.bottom - mBorderWidth / 2);
        path.moveTo(mBubbleRect.left, mBubbleRect.top + mCornerTopLeftRadius);
        path.arcTo(mBubbleRect.left, mBubbleRect.top,
                mBubbleRect.left + 2 * mCornerTopLeftRadius, mBubbleRect.top + 2 * mCornerTopLeftRadius,
                180, 90, false);
        path.lineTo(mBubbleRect.right - mCornerTopRightRadius, mBubbleRect.top);
        path.arcTo(mBubbleRect.right - 2 * mCornerTopRightRadius, mBubbleRect.top,
                mBubbleRect.right, mBubbleRect.top + 2 * mCornerTopRightRadius,
                270, 90, false);
        path.lineTo(mBubbleRect.right, mBubbleRect.bottom - mCornerBottomRightRadius);
        path.arcTo(mBubbleRect.right - 2 * mCornerBottomRightRadius, mBubbleRect.bottom - 2 * mCornerBottomRightRadius,
                mBubbleRect.right, mBubbleRect.bottom,
                0, 90, false);
        path.lineTo(mBubbleRect.left + mCornerBottomLeftRadius, mBubbleRect.bottom);
        path.arcTo(mBubbleRect.left, mBubbleRect.bottom - 2 * mCornerBottomLeftRadius,
                mBubbleRect.left + 2 * mCornerBottomLeftRadius, mBubbleRect.bottom,
                90, 90, false);
        path.lineTo(mBubbleRect.left, mBubbleRect.top + mCornerTopLeftRadius);
    }

    private void buildLeftArrow(Path path) {
        // 气泡矩形区域
        // 预留四周1/2的边框厚度，使得边框能够完全显示
        RectF mBubbleRect = new RectF(mRect.left + mBorderWidth / 2 + mArrowHeight, mRect.top + mBorderWidth / 2,
                mRect.right - mBorderWidth / 2, mRect.bottom - mBorderWidth / 2);

        // 箭头顶点坐标
        float peakX = mBubbleRect.left - mArrowHeight;
        float peakY = mBubbleRect.centerY() + mArrowToY;    // 顶点指向目标中心

        // 从箭头顶点开始沿顺时针方向绘制
        path.moveTo(peakX, peakY);
        path.lineTo(mBubbleRect.left, peakY - mArrowWidth / 2);

        // 左上竖线
        path.lineTo(mBubbleRect.left, mBubbleRect.top + mCornerTopLeftRadius);

        // 左上弧角
        path.arcTo(mBubbleRect.left, mBubbleRect.top,
                mBubbleRect.left + 2 * mCornerTopLeftRadius, mBubbleRect.top + 2 * mCornerTopLeftRadius,
                180, 90, false);

        // 上横向
        path.lineTo(mBubbleRect.right - mCornerTopRightRadius, mBubbleRect.top);

        // 右上弧角
        path.arcTo(mBubbleRect.right - 2 * mCornerTopRightRadius, mBubbleRect.top,
                mBubbleRect.right, mBubbleRect.top + 2 * mCornerTopRightRadius,
                270, 90, false);

        // 右侧竖线
        path.lineTo(mBubbleRect.right, mBubbleRect.bottom - mCornerBottomRightRadius);

        // 右下弧角
        path.arcTo(mBubbleRect.right - 2 * mCornerBottomRightRadius, mBubbleRect.bottom - 2 * mCornerBottomRightRadius,
                mBubbleRect.right, mBubbleRect.bottom,
                0, 90, false);

        // 底部横向
        path.lineTo(mBubbleRect.left + mCornerBottomLeftRadius, mBubbleRect.bottom);

        // 左下弧角
        path.arcTo(mBubbleRect.left, mBubbleRect.bottom - 2 * mCornerBottomLeftRadius,
                mBubbleRect.left + 2 * mCornerBottomLeftRadius, mBubbleRect.bottom,
                90, 90, false);

        // 左下竖线
        path.lineTo(mBubbleRect.left, peakY + mArrowWidth / 2);

        // 回到顶点
        path.lineTo(peakX, peakY);
    }

    private void buildRightArrow(Path path) {
        RectF mBubbleRect = new RectF(mRect.left + mBorderWidth / 2, mRect.top + mBorderWidth / 2,
                mRect.right - mBorderWidth / 2 - mArrowHeight, mRect.bottom - mBorderWidth / 2);
        float peakX = mBubbleRect.right + mArrowHeight;
        float peakY = mBubbleRect.centerY() + mArrowToY;
        path.moveTo(peakX, peakY);
        path.lineTo(mBubbleRect.right, peakY + mArrowWidth / 2);
        path.lineTo(mBubbleRect.right, mBubbleRect.bottom - mCornerBottomRightRadius);
        path.arcTo(mBubbleRect.right - 2 * mCornerBottomRightRadius, mBubbleRect.bottom - 2 * mCornerBottomRightRadius,
                mBubbleRect.right, mBubbleRect.bottom,
                0, 90, false);
        path.lineTo(mBubbleRect.left + mCornerBottomLeftRadius, mBubbleRect.bottom);
        path.arcTo(mBubbleRect.left, mBubbleRect.bottom - 2 * mCornerBottomLeftRadius,
                mBubbleRect.left + 2 * mCornerBottomLeftRadius, mBubbleRect.bottom,
                90, 90, false);
        path.lineTo(mBubbleRect.left, mBubbleRect.top + mCornerTopLeftRadius);
        path.arcTo(mBubbleRect.left, mBubbleRect.top,
                mBubbleRect.left + 2 * mCornerTopLeftRadius, mBubbleRect.top + 2 * mCornerTopLeftRadius,
                180, 90, false);
        path.lineTo(mBubbleRect.right - mCornerTopRightRadius, mBubbleRect.top);
        path.arcTo(mBubbleRect.right - 2 * mCornerTopRightRadius, mBubbleRect.top,
                mBubbleRect.right, mBubbleRect.top + 2 * mCornerTopRightRadius,
                270, 90, false);
        path.lineTo(mBubbleRect.right, peakY - mArrowWidth / 2);
        path.lineTo(peakX, peakY);
    }

    private void buildTopArrow(Path path) {
        RectF mBubbleRect = new RectF(mRect.left + mBorderWidth / 2, mRect.top + mBorderWidth / 2 + mArrowHeight,
                mRect.right - mBorderWidth / 2, mRect.bottom - mBorderWidth / 2);
        float peakX = mBubbleRect.centerX() + mArrowToX;
        float peakY = mBubbleRect.top - mArrowHeight;
        path.moveTo(peakX, peakY);
        path.lineTo(peakX + mArrowWidth / 2, mBubbleRect.top);
        path.lineTo(mBubbleRect.right - mCornerTopRightRadius, mBubbleRect.top);
        path.arcTo(mBubbleRect.right - 2 * mCornerTopRightRadius, mBubbleRect.top,
                mBubbleRect.right, mBubbleRect.top + 2 * mCornerTopRightRadius,
                270, 90, false);
        path.lineTo(mBubbleRect.right, mBubbleRect.bottom - mCornerBottomRightRadius);
        path.arcTo(mBubbleRect.right - 2 * mCornerBottomRightRadius, mBubbleRect.bottom - 2 * mCornerBottomRightRadius,
                mBubbleRect.right, mBubbleRect.bottom,
                0, 90, false);
        path.lineTo(mBubbleRect.left + mCornerBottomLeftRadius, mBubbleRect.bottom);
        path.arcTo(mBubbleRect.left, mBubbleRect.bottom - 2 * mCornerBottomLeftRadius,
                mBubbleRect.left + 2 * mCornerBottomLeftRadius, mBubbleRect.bottom,
                90, 90, false);
        path.lineTo(mBubbleRect.left, mBubbleRect.top + mCornerTopLeftRadius);
        path.arcTo(mBubbleRect.left, mBubbleRect.top,
                mBubbleRect.left + 2 * mCornerTopLeftRadius, mBubbleRect.top + 2 * mCornerTopLeftRadius,
                180, 90, false);
        path.lineTo(peakX - mArrowWidth / 2, mBubbleRect.top);
        path.lineTo(peakX, peakY);
    }

    private void buildBottomArrow(Path path) {
        RectF mBubbleRect = new RectF(mRect.left + mBorderWidth / 2, mRect.top + mBorderWidth / 2,
                mRect.right - mBorderWidth / 2, mRect.bottom - mBorderWidth / 2 - mArrowHeight);
        float peakX = mBubbleRect.centerX() + mArrowToX;
        float peakY = mBubbleRect.bottom + mArrowHeight;
        path.moveTo(peakX, peakY);
        path.lineTo(peakX - mArrowWidth / 2, mBubbleRect.bottom);
        path.lineTo(mBubbleRect.left + mCornerBottomLeftRadius, mBubbleRect.bottom);
        path.arcTo(mBubbleRect.left, mBubbleRect.bottom - 2 * mCornerBottomLeftRadius,
                mBubbleRect.left + 2 * mCornerBottomLeftRadius, mBubbleRect.bottom,
                90, 90, false);
        path.lineTo(mBubbleRect.left, mBubbleRect.top + mCornerTopLeftRadius);
        path.arcTo(mBubbleRect.left, mBubbleRect.top,
                mBubbleRect.left + 2 * mCornerTopLeftRadius, mBubbleRect.top + 2 * mCornerTopLeftRadius,
                180, 90, false);
        path.lineTo(mBubbleRect.right - mCornerTopRightRadius, mBubbleRect.top);
        path.arcTo(mBubbleRect.right - 2 * mCornerTopRightRadius, mBubbleRect.top,
                mBubbleRect.right, mBubbleRect.top + 2 * mCornerTopRightRadius,
                270, 90, false);
        path.lineTo(mBubbleRect.right, mBubbleRect.bottom - mCornerBottomRightRadius);
        path.arcTo(mBubbleRect.right - 2 * mCornerBottomRightRadius, mBubbleRect.bottom - 2 * mCornerBottomRightRadius,
                mBubbleRect.right, mBubbleRect.bottom,
                0, 90, false);
        path.lineTo(peakX + mArrowWidth / 2, mBubbleRect.bottom);
        path.lineTo(peakX, peakY);
    }
}
