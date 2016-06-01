package com.cpiz.android.bubbleview;

/**
 * 给BubbleImpl使用的回调接口，方便在BubbleImpl类中调用真正View的父类接口
 *
 * Created by caijw on 2016/6/1.
 * https://github.com/cpiz/BubbleView
 */
interface BubbleCallback {
    void setSuperPadding(int left, int top, int right, int bottom);

    int getSuperPaddingLeft();

    int getSuperPaddingTop();

    int getSuperPaddingRight();

    int getSuperPaddingBottom();
}
