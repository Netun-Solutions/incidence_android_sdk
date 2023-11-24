package com.e510.commons.utils.swipe;

import android.animation.Animator;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;

public class SwipeToHideViewListener  implements View.OnTouchListener {

    private boolean isTouching;
    private float swipeStartX;
    private float swipeStartY;
    private float viewStartY;
    private float deltaX = 0;
    private float deltaY = 0;
    private boolean isSwipingHorizontal = false;

    private View animatingView;
    private boolean shouldDismissView;
    private SwipeToHideCompletionListener listener;

    private static final int SWIPE_TO_DISMISS_THRESHOLD = 250;
    private static final int SWIPE_TO_DISMISS_ANIMATION_DURATION = 100;

    public SwipeToHideViewListener(View animatingView, boolean shouldDismissView, SwipeToHideCompletionListener listener) {
        this.animatingView = animatingView;
        this.shouldDismissView = shouldDismissView;
        this.listener = listener;
    }

    public void setAnimatingView(View animatingView) {
        this.animatingView = animatingView;
    }

    public void setShouldDismissView(boolean shouldDismissView) {
        this.shouldDismissView = shouldDismissView;
    }

    public void setListener(SwipeToHideCompletionListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:

                // Set Touched view as the animatingView if not set
                if (animatingView == null) animatingView = view;

                isTouching = true;
                startSwipe(motionEvent);
                break;

            case MotionEvent.ACTION_MOVE:

                if (!isTouching) break;
                moveSwipe(motionEvent);
                break;

            case MotionEvent.ACTION_UP:

                view.performClick();
            case MotionEvent.ACTION_OUTSIDE:

                isTouching = false;
                isSwipingHorizontal = false;
                endSwipe();
                break;
        }

        return isSwipingHorizontal;
    }

    private void startSwipe(MotionEvent event) {

        // Keep the initial swipe action position
        swipeStartX = event.getRawX();
        swipeStartY = event.getRawY();

        viewStartY = animatingView.getY();
    }

    private void moveSwipe(MotionEvent event) {

        // Check if the motion is horizontal
        deltaX = event.getRawX() - swipeStartX;
        deltaY = event.getRawY() - swipeStartY;

        if (deltaY < 0) {
            return;
        }


        if (Math.abs(deltaX) > 0 && Math.abs(deltaX) > Math.abs(deltaY)) {
            return;
        }

        if (Math.abs(deltaY) > 0) {
            animateViewVertically(deltaY, 0, false, null);
            isSwipingHorizontal = true;
        }
    }

    private void endSwipe() {
        if (deltaY < 0) {
            return;
        }

        if (shouldDismissView && Math.abs(deltaY) > (animatingView.getHeight() - SWIPE_TO_DISMISS_THRESHOLD)) {

            // Check whether view should animate left or right
            float endPos = (deltaY > 0) ? animatingView.getHeight() : -animatingView.getHeight();
            animateViewVertically(endPos, SWIPE_TO_DISMISS_ANIMATION_DURATION, true, new AnimatorCompletionListener() {
                @Override
                void onAnimationCompleted() {
                    if (listener != null) listener.viewDismissed();
                }
            });
        }
        else {
            animateViewVertically(0, SWIPE_TO_DISMISS_ANIMATION_DURATION, false, null);
        }
    }

    private void animateViewVertically(float dY, int duration, boolean shouldHide, AnimatorCompletionListener listener) {

        float animatingDistance = viewStartY + dY;

        ViewPropertyAnimator animator = animatingView.animate()
                .y(animatingDistance)
                .setDuration(duration)
                .setListener(listener);

        if (shouldHide) {
            animator.alpha(0);
        }

        animator.start();
    }

    private abstract class AnimatorCompletionListener implements Animator.AnimatorListener {
        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {
            onAnimationCompleted();
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }

        abstract void onAnimationCompleted();
    }

    public interface SwipeToHideCompletionListener {
        void viewDismissed();
    }

}
