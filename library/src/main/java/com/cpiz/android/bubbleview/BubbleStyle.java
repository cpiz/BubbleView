package com.cpiz.android.bubbleview;

import android.util.SparseArray;
import android.view.View;

/**
 * 气泡View抽象接口
 * <p>
 * Created by caijw on 2016/6/1.
 * https://github.com/cpiz/BubbleView
 */
@SuppressWarnings("unused")
public interface BubbleStyle {
    /**
     * 箭头朝向定义
     */
    enum ArrowDirection {

        /**
         * 无箭头
         */
        None(-1),
        /**
         * 自动确定指向
         */
        Auto(0),
        Left(1),
        Up(2),
        Right(3),
        Down(4);

        private static final SparseArray<ArrowDirection> intToTypeDict = new SparseArray<>();

        static {
            for (ArrowDirection type : ArrowDirection.values()) {
                intToTypeDict.put(type.mValue, type);
            }
        }

        @SuppressWarnings("UnusedAssignment")
        private int mValue = 0;

        public int getValue() {
            return mValue;
        }

        ArrowDirection(int value) {
            mValue = value;
        }

        public static ArrowDirection valueOf(int value) {
            ArrowDirection type = intToTypeDict.get(value);
            if (type == null)
                return ArrowDirection.Auto;
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
     * 箭头位置策略定义
     */
    enum ArrowPosPolicy {
        /**
         * 箭头指向目标View的中心点
         */
        TargetCenter(0),

        /**
         * 箭头从自己的中心点发出
         */
        SelfCenter(1),

        /**
         * 结合setArrowPosDelta，箭头从所在轴的头端开始偏移
         */
        SelfBegin(2),

        /**
         * 结合setArrowPosDelta，箭头从所在轴的尾端开始偏移
         */
        SelfEnd(3);

        private static final SparseArray<ArrowPosPolicy> intToTypeDict = new SparseArray<>();

        static {
            for (ArrowPosPolicy type : ArrowPosPolicy.values()) {
                intToTypeDict.put(type.mValue, type);
            }
        }

        @SuppressWarnings("UnusedAssignment")
        private int mValue = 0;

        public int getValue() {
            return mValue;
        }

        ArrowPosPolicy(int value) {
            mValue = value;
        }

        public static ArrowPosPolicy valueOf(int value) {
            ArrowPosPolicy type = intToTypeDict.get(value);
            if (type == null)
                return ArrowPosPolicy.TargetCenter;
            return type;
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
     * 设置箭头在边线上的位置策略
     *
     * @param policy 箭头位置策略
     */
    void setArrowPosPolicy(ArrowPosPolicy policy);

    ArrowPosPolicy getArrowPosPolicy();

    /**
     * 设置箭头在所在边线上的偏移距离
     * 视 ArrowPosPolicy 而定，为 TargetCenter 或 SelfCenter 时无意义
     *
     * @param delta 基于箭头位置策略，相应的偏差
     *              朝上/下时在X轴方向偏移，朝左/右时在Y轴方向偏移
     *              值必须 >0，视 ArrowPosPolicy 从首段或尾端开始偏移
     */
    void setArrowPosDelta(float delta);

    float getArrowPosDelta();

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
     * 设置点击时气泡背景色
     * @param fillPressColor 点击时气泡的背景颜色
     */
    void setFillPressColor(int fillPressColor);

    int getFillPressColor();

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

    /**
     * 请求刷新UI样式
     * 设置好以上属性后，调用该函数进行刷新
     */
    void requestUpdateBubble();

    void setPressed(boolean pressed);
}
