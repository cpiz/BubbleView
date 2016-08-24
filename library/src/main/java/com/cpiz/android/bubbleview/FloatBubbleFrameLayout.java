package com.cpiz.android.bubbleview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.cpiz.android.bubbleview.utils.DisplayHelper;
import com.cpiz.android.bubbleview.utils.WindowManagerHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by uchia on 8/23/2016.
 */
public class FloatBubbleFrameLayout extends FrameLayout implements BubbleStyle, BubbleCallback, FloatFunc {
    private BubbleImpl mBubbleImpl = new BubbleImpl();
    private int floatX;
    private int floatY;
    private boolean flag = false;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowLayoutParams;
    private View mParentView;

    public void setFloatX(int floatX) {
        this.floatX = floatX;
    }

    public void setFloatY(int floatY) {
        this.floatY = floatY;
    }

    public FloatBubbleFrameLayout(Context context) {
        super(context);
        init(context, null);
    }

    public FloatBubbleFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FloatBubbleFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FloatBubbleFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mBubbleImpl.init(this, context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (flag) {
            locateSelfInParentWindow(right - left,bottom - top);
            WindowManagerHelper.updateParentWindowManager(mWindowManager,mWindowLayoutParams,this,floatX,floatY);
            mBubbleImpl.updateDrawable(floatX, floatY, right - left, bottom - top, true);

        }
    }

    @Override
    public void setArrowDirection(ArrowDirection arrowDirection) {
        mBubbleImpl.setArrowDirection(arrowDirection);
    }

    @Override
    public ArrowDirection getArrowDirection() {
        return mBubbleImpl.getArrowDirection();
    }

    @Override
    public void setArrowHeight(float arrowHeight) {
        mBubbleImpl.setArrowHeight(arrowHeight);
    }

    @Override
    public float getArrowHeight() {
        return mBubbleImpl.getArrowHeight();
    }

    @Override
    public void setArrowWidth(float arrowWidth) {
        mBubbleImpl.setArrowWidth(arrowWidth);
    }

    @Override
    public float getArrowWidth() {
        return mBubbleImpl.getArrowWidth();
    }

    @Override
    public void setArrowOffset(float arrowOffset) {
        mBubbleImpl.setArrowOffset(arrowOffset);
    }

    @Override
    public float getArrowOffset() {
        return mBubbleImpl.getArrowOffset();
    }

    @Override
    public void setArrowTo(int viewId) {
        mBubbleImpl.setArrowTo(viewId);
    }

    @Override
    public void setArrowTo(View view) {
        mBubbleImpl.setArrowTo(view);
    }

    public View getArrowTo() {
        return mBubbleImpl.getArrowTo();
    }

    @Override
    public void setFillColor(int fillColor) {
        mBubbleImpl.setFillColor(fillColor);
    }

    @Override
    public int getFillColor() {
        return mBubbleImpl.getFillColor();
    }

    @Override
    public void setBorderColor(int borderColor) {
        mBubbleImpl.setBorderColor(borderColor);
    }

    @Override
    public int getBorderColor() {
        return mBubbleImpl.getBorderColor();
    }

    @Override
    public void setBorderWidth(float borderWidth) {
        mBubbleImpl.setBorderWidth(borderWidth);
    }

    @Override
    public float getBorderWidth() {
        return mBubbleImpl.getBorderWidth();
    }

    @Override
    public void setFillPadding(float fillPadding) {
        mBubbleImpl.setFillPadding(fillPadding);
    }

    @Override
    public float getFillPadding() {
        return mBubbleImpl.getFillPadding();
    }

    @Override
    public void setCornerRadius(float topLeft, float topRight, float bottomRight, float bottomLeft) {
        mBubbleImpl.setCornerRadius(topLeft, topRight, bottomRight, bottomLeft);
    }

    @Override
    public void setCornerRadius(float radius) {
        mBubbleImpl.setCornerRadius(radius);
    }

    @Override
    public float getCornerTopLeftRadius() {
        return mBubbleImpl.getCornerTopLeftRadius();
    }

    @Override
    public float getCornerTopRightRadius() {
        return mBubbleImpl.getCornerTopRightRadius();
    }

    @Override
    public float getCornerBottomLeftRadius() {
        return mBubbleImpl.getCornerBottomLeftRadius();
    }

    @Override
    public float getCornerBottomRightRadius() {
        return mBubbleImpl.getCornerBottomRightRadius();
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        if (mBubbleImpl == null) {
            Log.w("BubbleView", "mBubbleImpl == null on old Android platform");
            setSuperPadding(left, top, right, bottom);
            return;
        }

        mBubbleImpl.setPadding(left, top, right, bottom);
    }

    @Override
    public int getPaddingLeft() {
        return mBubbleImpl.getPaddingLeft();
    }

    @Override
    public int getPaddingTop() {
        return mBubbleImpl.getPaddingTop();
    }

    @Override
    public int getPaddingRight() {
        return mBubbleImpl.getPaddingRight();
    }

    @Override
    public int getPaddingBottom() {
        return mBubbleImpl.getPaddingBottom();
    }

    @Override
    public void setSuperPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
    }

    @Override
    public int getSuperPaddingLeft() {
        return super.getPaddingLeft();
    }

    @Override
    public int getSuperPaddingTop() {
        return super.getPaddingTop();
    }

    @Override
    public int getSuperPaddingRight() {
        return super.getPaddingRight();
    }

    @Override
    public int getSuperPaddingBottom() {
        return super.getPaddingBottom();
    }

    @Override
    public void updateDrawable() {
        mBubbleImpl.updateDrawable();
    }


    @Override
    public void show(View parent, WindowManager wm) {
        mParentView = parent;
        mWindowManager = wm;
        flag = true;
        mWindowLayoutParams =  new WindowManager.LayoutParams();
        mWindowLayoutParams.format = PixelFormat.RGBA_8888;
        mWindowLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        mWindowManager.addView(this,mWindowLayoutParams);
    }

    @Override
    public void hide() {

    }

    @Override
    public void dismiss() {
        flag = false;
        WindowManagerHelper.removeViewFromWindowManager(mWindowManager,this);
    }


    private void locateSelfInParentWindow(int width, int height) {
        int left = 0;
        int top = 0;
        int[] screenSize = DisplayHelper.getScreenDemension(mWindowManager);
        int parentViewWidth = mParentView.getWidth();
        int parentViewHeight = mParentView.getHeight();

        int deltaLeft = mParentView.getLeft() - width;
        int deltaRight = screenSize[0] - (mParentView.getLeft() + parentViewWidth) - width;
        int deltaTop = mParentView.getTop() - height;
        int deltaBottom = screenSize[1] - (mParentView.getTop() + parentViewHeight) - height;
        List<DirectionHelper> direction = new ArrayList<>();
        direction.add(new DirectionHelper('L', deltaLeft));
        direction.add(new DirectionHelper('R', deltaRight));
        direction.add(new DirectionHelper('T', deltaTop));
        direction.add(new DirectionHelper('B', deltaBottom));

        Collections.sort(direction, new Comparator<DirectionHelper>() {
            @Override
            public int compare(DirectionHelper lhs, DirectionHelper rhs) {
                return rhs.delta - lhs.delta;
            }
        });

        Rect rect = new Rect();
        rect.set(mParentView.getLeft(), mParentView.getTop(),
                mParentView.getLeft() + parentViewWidth,
                mParentView.getTop() + parentViewHeight);

        if (direction.get(0).getDelta() > 0) {
            switch (direction.get(0).getDescription()) {
                case 'L':
                    left = mParentView.getLeft() - width;
                    if (rect.centerY() - height / 2 <= 0) {
                        top = mParentView.getTop();
                    }
                    if (rect.centerY() + height / 2 >= screenSize[1]) {
                        top = mParentView.getTop() + parentViewHeight - height;
                    }
                    if(rect.centerY() - height / 2 > 0 && rect.centerY() + height / 2 < screenSize[1]){
                        top = rect.centerY() - height / 2;
                    }
                    setArrowDirection(ArrowDirection.Right);
                    break;
                case 'R':
                    left = mParentView.getRight();
                    if (rect.centerY() - height / 2 <= 0) {
                        top = mParentView.getTop();
                    }
                    if(rect.centerY() + height / 2 >= screenSize[1]){
                        top = mParentView.getTop() + parentViewHeight - height;
                    }
                    if(rect.centerY() - height / 2 > 0 && rect.centerY() + height / 2 < screenSize[1]){
                        top = rect.centerY() - height / 2;
                    }
                    setArrowDirection(ArrowDirection.Left);
                    break;
                case 'T':
                    top = mParentView.getTop() - height;
                    if(rect.centerX() + width / 2 >= screenSize[0]){
                        left = mParentView.getLeft() + parentViewWidth - width;
                    }
                    if(rect.centerX() - width / 2 <= 0){
                        left = mParentView.getLeft();
                    }
                    if(rect.centerX() + width / 2 < screenSize[0] && rect.centerX() - width / 2 > 0){
                        left = rect.centerX() - width / 2;
                    }
                    setArrowDirection(ArrowDirection.Down);
                    break;
                case 'B':
                    top = mParentView.getBottom() ;
                    if(rect.centerX() + width / 2 >= screenSize[0]){
                        left = mParentView.getLeft() + parentViewWidth - width;
                    }
                    if(rect.centerX() - width / 2 <= 0){
                        left = mParentView.getLeft() ;
                    }
                    if(rect.centerX() + width / 2 < screenSize[0] && rect.centerX() - width / 2 > 0){
                        left = rect.centerX() - width / 2;
                    }
                    setArrowDirection(ArrowDirection.Up);
                    break;
            }
            mWindowLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        } else {
            left = 0;
            top = 0;
            mWindowLayoutParams.gravity = Gravity.CENTER;
        }
        floatX = left;
        floatY = top;
    }

    private class DirectionHelper {

        private char description;
        private int delta;

        public DirectionHelper() {

        }

        public DirectionHelper(char description, int delta) {
            this.description = description;
            this.delta = delta;
        }

        public char getDescription() {
            return description;
        }

        public void setDescription(char description) {
            this.description = description;
        }

        public int getDelta() {
            return delta;
        }

        public void setDelta(int delta) {
            this.delta = delta;
        }

    }
}
