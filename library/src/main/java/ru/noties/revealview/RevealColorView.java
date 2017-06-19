package ru.noties.revealview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;

import ru.noties.ccf.CCFAnimator;

public class RevealColorView extends ViewGroup {

    private static final float SCALE = 8.F;

    private View inkView;

    private ShapeDrawable circle;

    private ViewPropertyAnimator revealAnimator;
    private ValueAnimator revealColorAnimator;

    private long defDuration;

    private boolean detached;

    public RevealColorView(Context context) {
        this(context, null);
    }

    public RevealColorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RevealColorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        defDuration = context.getResources().getInteger(android.R.integer.config_longAnimTime);

        if (isInEditMode()) {
            return;
        }

        inkView = new View(context);
        addView(inkView);

        circle = new ShapeDrawable(new OvalShape());

        inkView.setBackground(circle);
        inkView.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        if (isInEditMode()) {
            return;
        }

        inkView.layout(left, top, left + inkView.getMeasuredWidth(), top + inkView.getMeasuredHeight());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (isInEditMode()) {
            return;
        }

        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();

        final float circleSize = (float) Math.sqrt(width * width + height * height) * 2.F;
        final int size = (int) (circleSize / SCALE);
        final int sizeSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
        inkView.measure(sizeSpec, sizeSpec);
    }

    public RevealOperation newReveal() {
        return new Reveal();
    }

    public RevealOperation newHide() {
        return new Hide();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.detached = true;
        cancelAnimations();
    }

    public void cancelAnimations() {
        if (revealAnimator != null) {
            revealAnimator.cancel();
        }
        if (revealColorAnimator != null) {
            revealColorAnimator.cancel();
        }
    }

    private class Reveal extends RevealOperation {

        @Override
        public void start() {

            if (detached) {
                throw new IllegalStateException("The view is detached");
            }

            cancelAnimations();

            calculateCoordinates();

            if (duration <= 0L) {
                duration = defDuration;
            }

            final ValueAnimator colorAnimator;
            if (startColor != endColor) {
                colorAnimator = CCFAnimator.argb(startColor, endColor)
                        .asValueAnimator(new CCFAnimator.OnNewColorListener() {
                            @Override
                            public void onNewColor(@ColorInt int color) {
                                circle.getPaint().setColor(color);
                                circle.invalidateSelf();
                            }
                        });
                colorAnimator.setInterpolator(BakedBezierInterpolator.getInstance());
                colorAnimator.setDuration(duration);
            } else {
                colorAnimator = null;
                circle.getPaint().setColor(startColor);
                circle.invalidateSelf();
            }
            revealColorAnimator = colorAnimator;

            inkView.setVisibility(VISIBLE);

            final float startScale = radius * 2.F / inkView.getHeight();
            final float finalScale = calculateScale(getWidth(), getHeight(), x, y) * SCALE;

            prepareView(inkView, x, y, startScale);

            revealAnimator = inkView.animate()
                    .scaleX(finalScale)
                    .scaleY(finalScale)
                    .setDuration(duration)
                    .setListener(listener);
            // we set listener anyway
            // because ViewPropertyAnimator will preserve it
            // so we need to set a new one or null (otherwise previous listener will be called again)

            startAnimator(revealAnimator);

            if (revealColorAnimator != null) {
                revealColorAnimator
                        .start();
            }
        }
    }

    private class Hide extends RevealOperation {

        @Override
        public void start() {

            if (detached) {
                throw new IllegalStateException("The view is detached");
            }

            cancelAnimations();

            calculateCoordinates();

            if (duration <= 0L) {
                duration = defDuration;
            }

            final ValueAnimator colorAnimator;
            if (startColor != endColor) {
                colorAnimator = CCFAnimator.argb(startColor, endColor)
                        .asValueAnimator(new CCFAnimator.OnNewColorListener() {
                            @Override
                            public void onNewColor(@ColorInt int color) {
                                circle.getPaint().setColor(color);
                                circle.invalidateSelf();
                            }
                        });
                colorAnimator.setInterpolator(BakedBezierInterpolator.getInstance());
                colorAnimator.setDuration(duration);
            } else {
                colorAnimator = null;
                circle.getPaint().setColor(startColor);
                circle.invalidateSelf();
            }
            revealColorAnimator = colorAnimator;

            inkView.setVisibility(VISIBLE);

            final float startScale = calculateScale(getWidth(), getHeight(), x, y) * SCALE;
            final float finalScale = radius * SCALE / inkView.getWidth();

            prepareView(inkView, x, y, startScale);

            revealAnimator = inkView.animate()
                    .scaleX(finalScale)
                    .scaleY(finalScale)
                    .setDuration(duration)
                    .setListener(new AnimatorListenerDelegate(listener) {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            inkView.setVisibility(INVISIBLE);
                        }
                    });

            startAnimator(revealAnimator);

            if (revealColorAnimator != null) {
                revealColorAnimator.start();
            }
        }
    }
}