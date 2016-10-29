package com.cpiz.android.bubbleview;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 气泡与目标View相对位置
 *
 * Created by caijw on 2016/10/28.
 */
@SuppressWarnings({"PointlessBitwiseExpression", "WeakerAccess"})
public class RelativePos {
    @IntDef({CENTER_HORIZONTAL, TO_LEFT_OF, TO_RIGHT_OF, ALIGN_LEFT, ALIGN_RIGHT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RelativeH {
    }

    public static final int CENTER_HORIZONTAL = 0;
    public static final int TO_LEFT_OF = 1;
    public static final int TO_RIGHT_OF = 2;
    public static final int ALIGN_LEFT = 3;
    public static final int ALIGN_RIGHT = 4;

    @IntDef({CENTER_VERTICAL, ABOVE, BELOW, ALIGN_TOP, ALIGN_BOTTOM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RelativeV {
    }

    public static final int CENTER_VERTICAL = 0;
    public static final int ABOVE = 1;
    public static final int BELOW = 2;
    public static final int ALIGN_TOP = 3;
    public static final int ALIGN_BOTTOM = 4;

    private int mHorizontalRelate = CENTER_HORIZONTAL;
    private int mVerticalRelate = CENTER_VERTICAL;

    public RelativePos(@RelativeH int mHorizontalRelate, @RelativeV int mVerticalRelate) {
        this.mHorizontalRelate = mHorizontalRelate;
        this.mVerticalRelate = mVerticalRelate;
    }

    public int getHorizontalRelate() {
        return mHorizontalRelate;
    }

    public void setHorizontalRelate(int horizontalRelate) {
        mHorizontalRelate = horizontalRelate;
    }

    public int getVerticalRelate() {
        return mVerticalRelate;
    }

    public void setVerticalRelate(int verticalRelate) {
        mVerticalRelate = verticalRelate;
    }

    private boolean isHorizontalToTargetOf() {
        return mHorizontalRelate == TO_LEFT_OF || mHorizontalRelate == TO_RIGHT_OF;
    }

    private boolean isVerticalToTargetOf() {
        return mVerticalRelate == ABOVE || mVerticalRelate == BELOW;
    }

    public BubbleStyle.ArrowDirection getArrowDirection() {
        if (isHorizontalToTargetOf() && !isVerticalToTargetOf()) {
            if (mHorizontalRelate == TO_RIGHT_OF) {
                return BubbleStyle.ArrowDirection.Left;
            } else if (mHorizontalRelate == TO_LEFT_OF) {
                return BubbleStyle.ArrowDirection.Right;
            }
        }

        if (!isHorizontalToTargetOf() && isVerticalToTargetOf()) {
            if (mVerticalRelate == BELOW) {
                return BubbleStyle.ArrowDirection.Up;
            } else if (mVerticalRelate == ABOVE) {
                return BubbleStyle.ArrowDirection.Down;
            }
        }

        return BubbleStyle.ArrowDirection.None;
    }
}
