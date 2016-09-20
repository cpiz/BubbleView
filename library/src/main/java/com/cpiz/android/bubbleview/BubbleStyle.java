package com.cpiz.android.bubbleview;

import android.view.View;

import java.util.HashMap;
import java.util.Map;

/**
 * 气泡View抽象接口
 *
 * Created by caijw on 2016/6/1.
 * https://github.com/cpiz/BubbleView
 */
public interface BubbleStyle {
    /**
     * 箭头朝向枚举
     */
    enum ArrowDirection {
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

    /**
     * 设置箭头朝向
     *
     * @param arrowDirection 上下左右或者无
     */
    void setArrowDirection(ArrowDirection arrowDirection);

    ArrowDirection getArrowDirection();

    /**
     * 设置箭头三角形厚度
     *
     * @param arrowHeight 箭头厚度
     */
    void setArrowHeight(float arrowHeight);

    float getArrowHeight();

    /**
     * 设置箭头三角形底宽
     *
     * @param arrowWidth 箭头底边宽度
     */
    void setArrowWidth(float arrowWidth);

    float getArrowWidth();

    /**
     * 设置箭头在边线上的位置，视箭头方向而定
     *
     * @param arrowOffset 根据箭头位置，偏移像素值：
     *                    朝上/下时在X轴方向偏移，>0 时从正方向偏移，<0时从负方向偏移
     *                    朝左/右时在Y轴方向偏移，>0 时从正方向偏移，<0时从负方向偏移
     */
    void setArrowOffset(float arrowOffset);

    float getArrowOffset();

    /**
     * 设置箭头指向的View对象
     * 设置了View对象后，setArrowPos将不起作用
     *
     * @param viewId 指向的ViewId
     */
    void setArrowTo(int viewId);

    void setArrowTo(View view);

    View getArrowTo();

    /**
     * 设置气泡背景色
     *
     * @param fillColor 气泡背景颜色
     */
    void setFillColor(int fillColor);

    int getFillColor();

    /**
     * 设置边框线颜色
     *
     * @param borderColor 边框颜色
     */
    void setBorderColor(int borderColor);

    int getBorderColor();

    /**
     * 设置边框线宽
     *
     * @param borderWidth 边框厚度
     */
    void setBorderWidth(float borderWidth);

    float getBorderWidth();

    /**
     * 设置边框于背景之间的间隙宽度
     *
     * @param fillPadding 间隙宽度
     */
    void setFillPadding(float fillPadding);

    float getFillPadding();

    /**
     * 设置边角弧度
     * 可以为四角指定不同弧度
     *
     * @param topLeft     左上角
     * @param topRight    右上角
     * @param bottomRight 右下角
     * @param bottomLeft  左下角
     */
    void setCornerRadius(float topLeft, float topRight, float bottomRight, float bottomLeft);

    void setCornerRadius(float radius);

    float getCornerTopLeftRadius();

    float getCornerTopRightRadius();

    float getCornerBottomLeftRadius();

    float getCornerBottomRightRadius();

    /**
     * 设定Padding
     * 将自动将箭头区域占用空间加入Padding，使内容能够完全被气泡包含
     *
     * @param left   用户指定的 LeftPadding
     * @param top    用户指定的 TopPadding
     * @param right  用户指定的 RightPadding
     * @param bottom 用户指定的 BottomPadding
     */
    void setPadding(int left, int top, int right, int bottom);

    int getPaddingLeft();

    int getPaddingTop();

    int getPaddingRight();

    int getPaddingBottom();

    void updateDrawable();
}
