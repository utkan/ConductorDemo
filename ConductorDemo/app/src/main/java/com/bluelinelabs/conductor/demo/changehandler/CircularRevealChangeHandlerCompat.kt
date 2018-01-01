package com.bluelinelabs.conductor.demo.changehandler

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.view.View
import android.view.ViewGroup

class CircularRevealChangeHandlerCompat : CircularRevealChangeHandler {

    constructor()

    constructor(fromView: View, containerView: View) : super(fromView, containerView)

    override fun getAnimator(container: ViewGroup, from: View?, to: View?, isPush: Boolean, toAddedToContainer: Boolean): Animator {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.getAnimator(container, from, to, isPush, toAddedToContainer)
        } else {
            val animator = AnimatorSet()
            to?.let {
                val start = if (toAddedToContainer) 0F else it.alpha
                animator.play(ObjectAnimator.ofFloat<View>(it, View.ALPHA, start, 1F))
            }
            from?.let {
                animator.play(ObjectAnimator.ofFloat<View>(it, View.ALPHA, 0F))
            }
            animator
        }
    }
}
