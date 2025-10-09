package com.e510.commons.view;


import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.Nullable;

import com.e510.incidencelibrary.R;

public class BottomSheetLayout extends FrameLayout
{
    private ValueAnimator valueAnimator;
    private int collapsedHeight = 0;
    private float progress = 0f;
    private boolean startsCollapsed = true;
    private float scrollTranslationY = 0f;
    private float userTranslationY = 0f;
    private boolean isScrollingUp = false;
    private OnClickListener clickListener;
    private long animationDuration = 300;
    private OnProgressListener progressListener;
    private final TouchToDragListener touchToDragListener;

    private final int DIRECTION_VERTICAL = 0;
    private final int DIRECTION_HORIZONTAL = 1;
    private final int DIRECTION_NO_VALUE = -1;
    private int mTouchSlop;
    private int mGestureDirection;
    private float mDistanceX;
    private float mDistanceY;
    private float mLastX;
    private float mLastY;

    public final long getAnimationDuration() {
        return this.animationDuration;
    }

    public final void setAnimationDuration(long var1) {
        this.animationDuration = var1;
    }

    public void setOnClickListener(@Nullable OnClickListener l) {
        this.clickListener = l;
    }

    public final void setOnProgressListener(@Nullable OnProgressListener l) {
        this.progressListener = l;
    }

    public final boolean isExpanded() {
        return this.progress == 1.0f;
    }

    public void setTranslationY(float translationY) {
        this.userTranslationY = translationY;
        super.setTranslationY(this.scrollTranslationY + this.userTranslationY);
    }

    private final void initView(AttributeSet attrs, int defStyle)
    {
        final ViewConfiguration configuration = ViewConfiguration.get(this.getContext());
        mTouchSlop = configuration.getScaledTouchSlop();

        TypedArray a = this.getContext().obtainStyledAttributes(attrs, R.styleable.BottomSheetLayout, defStyle, 0);
        collapsedHeight = a.getDimensionPixelSize(R.styleable.BottomSheetLayout_collapsedHeight, 50);
        setCollapsedHeight(collapsedHeight);

        a.recycle();

        valueAnimator = ValueAnimator.ofFloat(new float[]{0.0F, 1.0F});

        setOnTouchListener(touchToDragListener);

        if (this.getHeight() == 0) {
            this.addOnLayoutChangeListener(new OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    BottomSheetLayout.this.removeOnLayoutChangeListener((OnLayoutChangeListener)this);
                    BottomSheetLayout.this.animate(0.0f);
                }
            });
        } else {
            this.animate(0.0f);
        }
    }

    public final void setCollapsedHeight(int height) {
        this.collapsedHeight = height;
        if (VERSION.SDK_INT >= 16) {
            this.setMinimumHeight(Math.max(this.getMinimumHeight(), this.collapsedHeight));
        }
    }

    public final void toggle() {

        if (valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
        long duration = 0l;
        if (this.progress > 0.5F)
        {
            duration = (long)((float)this.animationDuration * this.progress);
            valueAnimator = ValueAnimator.ofFloat(new float[]{this.progress, 0.0F});
        }
        else {
            duration = (long)((float)this.animationDuration * ((float)1 - this.progress));
            valueAnimator = ValueAnimator.ofFloat(new float[]{this.progress, 1.0F});
        }

        valueAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float progress = (float) animation.getAnimatedValue();
                BottomSheetLayout.this.animate(progress);
            }
        });

        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

    public final void collapse()
    {
        if (valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }

        valueAnimator = ValueAnimator.ofFloat(new float[]{this.progress, 0.0F});
        valueAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float progress = (float) animation.getAnimatedValue();
                BottomSheetLayout.this.animate(progress);
            }
        });

        valueAnimator.setDuration((long)((float)this.animationDuration * this.progress));
        valueAnimator.start();
    }

    public final void expand()
    {
        if (valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }

        valueAnimator = ValueAnimator.ofFloat(new float[]{this.progress, 1.0F});
        valueAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float progress = (float) animation.getAnimatedValue();
                BottomSheetLayout.this.animate(progress);
            }
        });

        valueAnimator.setDuration((long)((float)this.animationDuration * ((float)1 - this.progress)));
        valueAnimator.start();
    }

    private final void animate(float progress) {
        this.progress = progress;
        int height = this.getHeight();
        int distance = height - this.collapsedHeight;
        this.scrollTranslationY = (float)distance * ((float)1 - progress);
        super.setTranslationY(this.scrollTranslationY + this.userTranslationY);
        BottomSheetLayout.OnProgressListener var10000 = this.progressListener;
        if (var10000 != null) {
            var10000.onProgress(progress);
        }
    }

    private final void animateScroll(float firstPos, float touchPos) {
        float distance = touchPos - firstPos;
        int height = this.getHeight();
        int totalDistance = height - this.collapsedHeight;
        float progress = this.progress;
        if (!this.startsCollapsed) {
            this.isScrollingUp = false;
            progress = Math.max(0.0F, (float)1 - distance / (float)totalDistance);
        } else if (this.startsCollapsed) {
            this.isScrollingUp = true;
            progress = Math.min(1.0F, -distance / (float)totalDistance);
        }

        progress = Math.max(0.0F, Math.min(1.0F, progress));
        this.animate(progress);
    }

    private final void animateScrollEnd()
    {
        if (valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
        long duration = 0L;
        float progressLimit = this.isScrollingUp ? 0.2F : 0.8F;
        if (this.progress > progressLimit)
        {
            duration = (long)((float)this.animationDuration * ((float)1 - this.progress));
            valueAnimator = ValueAnimator.ofFloat(new float[]{this.progress, 1.0F});
        }
        else {
            duration = (long)((float)this.animationDuration * this.progress);
            valueAnimator = ValueAnimator.ofFloat(new float[]{this.progress, 0.0F});
        }

        valueAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float progress = (float) animation.getAnimatedValue();
                BottomSheetLayout.this.animate(progress);
            }
        });

        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

    @Override
    public boolean onInterceptTouchEvent(@Nullable MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDistanceY = mDistanceX = 0f;
                mLastX = ev.getX();
                mLastY = ev.getY();
                mGestureDirection = DIRECTION_NO_VALUE;
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();
                mDistanceX += Math.abs(curX - mLastX);
                mDistanceY += Math.abs(curY - mLastY);
                mLastX = curX;
                mLastY = curY;
                break;
        }
        return ev != null ? this.touchToDragListener.onTouch((View)this, ev) && shouldIntercept() : false;
    }

    private boolean shouldIntercept(){
        if((mDistanceY > mTouchSlop || mDistanceX > mTouchSlop) && mGestureDirection == DIRECTION_NO_VALUE){
            if(Math.abs(mDistanceY) > Math.abs(mDistanceX)){
                mGestureDirection = DIRECTION_VERTICAL;
            }
            else{
                mGestureDirection = DIRECTION_HORIZONTAL;
            }
        }

        if(mGestureDirection == DIRECTION_VERTICAL){
            return true;
        }
        else{
            return false;
        }
    }

    private final boolean performChildClick(float eventX, float eventY) {
        return this.performChildClick(eventX, eventY, (ViewGroup)this, 0);
    }

    private final boolean performChildClick(float eventX, float eventY, ViewGroup viewGroup, int nest) {
        int i = viewGroup.getChildCount() - 1;

        for(boolean var6 = false; i >= 0; --i) {
            View view = viewGroup.getChildAt(i);
            if (this.isViewAtLocation(eventX, eventY, view)) {
                if (view instanceof ViewGroup) {
                    boolean performChildClick = this.performChildClick(eventX - (float)((ViewGroup)view).getLeft(), eventY - (float)((ViewGroup)view).getTop(), (ViewGroup)view, nest + 1);
                    if (performChildClick) {
                        return true;
                    }
                }

                if (view.performClick()) {
                    return true;
                }
            }
        }

        return this.performClick();
    }

    private final boolean isViewAtLocation(float rawX, float rawY, View view) {
        return (float)view.getLeft() <= rawX && (float)view.getRight() >= rawX && (float)view.getTop() <= rawY && (float)view.getBottom() >= rawY;
    }

    private final void onClick() {
        OnClickListener var10000 = this.clickListener;
        if (var10000 != null) {
            var10000.onClick((View)this);
        }

    }

    public BottomSheetLayout(Context context) {
        super(context);
        this.startsCollapsed = true;
        this.animationDuration = 300L;
        this.touchToDragListener = new BottomSheetLayout.TouchToDragListener(true);
        this.initView(null, 0);
    }

    public BottomSheetLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.startsCollapsed = true;
        this.animationDuration = 300L;
        this.touchToDragListener = new BottomSheetLayout.TouchToDragListener(true);
        this.initView(attrs, 0);
    }

    public BottomSheetLayout(Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.startsCollapsed = true;
        this.animationDuration = 300L;
        this.touchToDragListener = new BottomSheetLayout.TouchToDragListener(true);
        this.initView(attrs, defStyleAttr);
    }

    private final class TouchToDragListener implements OnTouchListener {
        private final int CLICK_ACTION_THRESHOLD;
        private float startX;
        private float startY;
        private double startTime;
        private final boolean touchToDrag;

        public boolean onTouch(View v, MotionEvent ev) {
            int action = ev.getAction();
            float endX;
            switch(action) {
                case 0:
                    if (ev.getPointerCount() == 1) {
                        this.startX = ev.getRawX();
                        this.startY = ev.getRawY();
                        this.startTime = (double)System.currentTimeMillis();
                        BottomSheetLayout.this.startsCollapsed = (double)BottomSheetLayout.this.progress < 0.5D;
                    }
                    break;
                case 1:
                    endX = ev.getRawX();
                    float endY = ev.getRawY();
                    if (this.isAClick(this.startX, endX, this.startY, endY, System.currentTimeMillis())) {
                        if (BottomSheetLayout.this.performChildClick(ev.getX(), ev.getY())) {
                            return true;
                        }

                        if (this.touchToDrag && BottomSheetLayout.this.clickListener != null) {
                            BottomSheetLayout.this.onClick();
                            return true;
                        }
                    }

                    BottomSheetLayout.this.animateScrollEnd();
                    break;
                case 2:
                    endX = ev.getRawY();
                    BottomSheetLayout.this.animateScroll(this.startY, endX);
                    BottomSheetLayout.this.invalidate();
            }

            return true;
        }

        private final boolean isAClick(float startX, float endX, float startY, float endY, long endTime) {
            float differenceX = Math.abs(startX - endX);
            float differenceY = Math.abs(startY - endY);
            double differenceTime = Math.abs(this.startTime - (double)endTime);
            return differenceX <= (float)this.CLICK_ACTION_THRESHOLD && differenceY <= (float)this.CLICK_ACTION_THRESHOLD && differenceTime <= (double)400;
        }

        public TouchToDragListener(boolean touchToDrag) {
            this.touchToDrag = touchToDrag;
            this.CLICK_ACTION_THRESHOLD = 200;
        }
    }

    public interface OnProgressListener {
        void onProgress(float var1);
    }
}