package com.example.healthcare;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class animation {

    private static Context context;

    // Initialize the animation class with a context
    public static void init(Context c) {
        context = c;
    }

    // Apply fade-in animation to the view
    public static void fadein(View v) {
        Animation animFadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        v.startAnimation(animFadeIn);
        v.setVisibility(View.VISIBLE);
    }

    // Apply fade-out animation to the view
    public static void fadeout(View v) {
        Animation animFadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_out);
        v.startAnimation(animFadeIn);
        v.setVisibility(View.GONE);
    }

    // Apply left slide-in animation to the view
    public static void left(View v) {
        Animation animFadeIn = AnimationUtils.loadAnimation(context, R.anim.left);
        v.startAnimation(animFadeIn);
        v.setVisibility(View.VISIBLE);
    }

    // Apply right slide-in animation to the view
    public static void right(View v) {
        Animation animFadeIn = AnimationUtils.loadAnimation(context, R.anim.right);
        v.startAnimation(animFadeIn);
        v.setVisibility(View.VISIBLE);
    }

    // Apply top slide-in animation to the view
    public static void top(View v) {
        Animation animFadeIn = AnimationUtils.loadAnimation(context, R.anim.top);
        v.startAnimation(animFadeIn);
        v.setVisibility(View.VISIBLE);
    }

    // Apply bottom slide-in animation to the view
    public static void bottom(View v) {
        Animation animFadeIn = AnimationUtils.loadAnimation(context, R.anim.bottom);
        v.startAnimation(animFadeIn);
        v.setVisibility(View.VISIBLE);
    }
}
