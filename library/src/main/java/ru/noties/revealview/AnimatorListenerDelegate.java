package ru.noties.revealview;

import android.animation.Animator;
import android.support.annotation.Nullable;

class AnimatorListenerDelegate implements Animator.AnimatorListener {

    private final Animator.AnimatorListener wrapped;

    AnimatorListenerDelegate(@Nullable Animator.AnimatorListener wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void onAnimationStart(Animator animation) {
        if (wrapped != null) {
            wrapped.onAnimationStart(animation);
        }
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if (wrapped != null) {
            wrapped.onAnimationEnd(animation);
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        if (wrapped != null) {
            wrapped.onAnimationCancel(animation);
        }
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
        if (wrapped != null) {
            wrapped.onAnimationRepeat(animation);
        }
    }
}
