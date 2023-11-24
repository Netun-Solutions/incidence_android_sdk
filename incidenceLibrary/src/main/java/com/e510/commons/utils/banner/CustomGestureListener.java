package com.e510.commons.utils.banner;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public abstract class CustomGestureListener extends GestureDetector.SimpleOnGestureListener
{
    public static final int DIRECTION_HORIZONTAL = 0;
    public static final int DIRECTION_VERTICAL = 1;

    private final View mView;
    private int directionCheck;

    public CustomGestureListener(View view, int directionCheck)
    {
        mView = view;
        this.directionCheck = directionCheck;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        mView.onTouchEvent(e);
        return super.onSingleTapConfirmed(e);
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        onTouch();
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
    {
        if (directionCheck == DIRECTION_HORIZONTAL)
        {
            if (e1.getX() < e2.getX()) {
                return onSwipeRight();
            }

            if (e1.getX() > e2.getX()) {
                return onSwipeLeft();
            }
        }
        else if (directionCheck == DIRECTION_VERTICAL)
        {
            if (e1.getY() > e2.getY()) {
                return onSwipeUp();
            }

            if (e1.getY() < e2.getY()) {
                return onSwipeDown();
            }
        }

        return onTouch();
    }

    public abstract boolean onSwipeUp();
    public abstract boolean onSwipeDown();
    public abstract boolean onSwipeRight();
    public abstract boolean onSwipeLeft();
    public abstract boolean onTouch();
}