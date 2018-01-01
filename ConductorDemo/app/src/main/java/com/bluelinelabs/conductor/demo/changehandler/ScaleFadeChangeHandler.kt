package com.bluelinelabs.conductor.demo.changehandler

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.ViewGroup

import com.bluelinelabs.conductor.changehandler.AnimatorChangeHandler

class ScaleFadeChangeHandler : AnimatorChangeHandler(AnimatorChangeHandler.DEFAULT_ANIMATION_DURATION, true) {

    override fun getAnimator(container: ViewGroup, from: View?, to: View?, isPush: Boolean, toAddedToContainer: Boolean): Animator {
        val animator = AnimatorSet()
        to?.let {
            val start = if (toAddedToContainer) 0F else it.alpha
            animator.play(ObjectAnimator.ofFloat<View>(it, View.ALPHA, start, 1F))
        }
        from?.let {
            with(animator) {
                play(ObjectAnimator.ofFloat<View>(it, View.ALPHA, 0F))
                play(ObjectAnimator.ofFloat(it, View.SCALE_X, 0.8f))
                play(ObjectAnimator.ofFloat(it, View.SCALE_Y, 0.8f))
            }
        }

        return animator
    }

    override fun resetFromView(from: View) {}
}
