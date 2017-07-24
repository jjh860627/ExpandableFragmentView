package com.jjh.libs.expandablefragmentview.utils;

import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;

/**
 * Created by jjh860627 on 2017. 7. 24..
 */

public class AnimationUtil {

    //Expand Layout + Rotate View
    public static void expandLayoutWithRotateView(final View v, View rotateView, Animation.AnimationListener listener, float animWeight){

        v.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        final int targetHeight = v.getMeasuredHeight();
        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? targetHeight
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        a.setAnimationListener(listener);


        int duration = (int)(targetHeight * animWeight / v.getContext().getResources().getDisplayMetrics().density);

        if(rotateView != null) {
            RotateAnimation ra = new RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            ra.setDuration((int) (duration * 0.8));
            ra.setFillAfter(true);
            ra.setInterpolator(new AccelerateDecelerateInterpolator());
            rotateView.startAnimation(ra);
        }

        a.setInterpolator(new AccelerateDecelerateInterpolator());
        a.setDuration(duration);
        v.startAnimation(a);
    }

    //Collaspse Layout + Rotate View
    public static void collapseLayoutWithRotateView(final View v, View rotateView, Animation.AnimationListener listener, float animWeight) {

        final int initialHeight = v.getMeasuredHeight();
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        a.setAnimationListener(listener);

        int duration = (int)(initialHeight * animWeight / v.getContext().getResources().getDisplayMetrics().density);

        if(rotateView != null) {
            RotateAnimation ra = new RotateAnimation(180f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            ra.setDuration((int) (duration * 0.8));
            ra.setFillAfter(true);
            ra.setInterpolator(new AccelerateDecelerateInterpolator());
            rotateView.startAnimation(ra);
        }

        a.setInterpolator(new AccelerateDecelerateInterpolator());
        a.setDuration(duration);
        v.startAnimation(a);
    }

}
