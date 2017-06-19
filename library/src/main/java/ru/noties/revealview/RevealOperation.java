package ru.noties.revealview;

import android.animation.Animator;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.view.View;
import android.view.ViewPropertyAnimator;

public abstract class RevealOperation {


    public abstract void start();


    protected View target;
    protected int x;
    protected int y;
    protected int radius;
    protected int startColor;
    protected int endColor;
    protected long duration = -1;
    protected Animator.AnimatorListener listener;

    public RevealOperation target(@NonNull View target) {
        this.target = target;
        return this;
    }

    public RevealOperation coordinates(@Px int x, @Px int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public RevealOperation radius(@Px @IntRange(from = 0) int radius) {
        this.radius = radius;
        return this;
    }

    public RevealOperation color(@ColorInt int color) {
        this.startColor = color;
        this.endColor = color;
        return this;
    }

    public RevealOperation colors(@ColorInt int startColor, @ColorInt int endColor) {
        this.startColor = startColor;
        this.endColor = endColor;
        return this;
    }

    public RevealOperation duration(@IntRange(from = 0L) long duration) {
        this.duration = duration;
        return this;
    }

    public RevealOperation listener(@Nullable Animator.AnimatorListener listener) {
        this.listener = listener;
        return this;
    }

    public static void startAnimator(@NonNull ViewPropertyAnimator animator) {
        animator
                .withLayer()
                .setInterpolator(BakedBezierInterpolator.getInstance())
                .start();
    }

    public static void prepareView(@NonNull View view, @Px int x, @Px int y, float scale) {
        final int centerX = (view.getWidth() / 2);
        final int centerY = (view.getHeight() / 2);
        view.setTranslationX(x - centerX);
        view.setTranslationY(y - centerY);
        view.setPivotX(centerX);
        view.setPivotY(centerY);
        view.setScaleX(scale);
        view.setScaleY(scale);
    }

    public static float calculateScale(int width, int height, int x, int y) {

        final float centerX = width / 2.F;
        final float centerY = height / 2.F;
        final float maxDistance = (float) Math.sqrt(centerX * centerX + centerY * centerY);

        final float deltaX = centerX - x;
        final float deltaY = centerY - y;
        final float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        return 0.5F + (distance / maxDistance) * 0.5F;
    }

    protected void calculateCoordinates() {

        // if View as target is specified -> ignore x, y
        if (target != null) {
            final Rect rect = new Rect();
            target.getGlobalVisibleRect(rect);
            x = rect.centerX();
            y = rect.centerY();
        }
    }
}
