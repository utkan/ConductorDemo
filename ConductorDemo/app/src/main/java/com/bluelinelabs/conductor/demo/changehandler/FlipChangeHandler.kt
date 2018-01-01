package com.bluelinelabs.conductor.demo.changehandler

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.util.Property
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator

import com.bluelinelabs.conductor.changehandler.AnimatorChangeHandler

class FlipChangeHandler @JvmOverloads constructor(private val flipDirection: FlipDirection = FlipDirection.RIGHT, private val flipChangeAnimationDuration: Long = DEFAULT_ANIMATION_DURATION) : AnimatorChangeHandler() {

    enum class FlipDirection(internal val inStartRotation: Float, internal val outEndRotation: Float, internal val property: Property<View, Float>) {
        LEFT(-180F, 180F, View.ROTATION_Y),
        RIGHT(180F, -180F, View.ROTATION_Y),
        UP(-180F, 180F, View.ROTATION_X),
        DOWN(180F, -180F, View.ROTATION_X)
    }

    constructor(flipChangeAnimationDuration: Long) : this(FlipDirection.RIGHT, flipChangeAnimationDuration) {}

    override fun getAnimator(container: ViewGroup, from: View?, to: View?, isPush: Boolean, toAddedToContainer: Boolean): Animator {
        val animatorSet = AnimatorSet()

        to?.let {
            it.alpha = 0f

            val rotation = ObjectAnimator.ofFloat<View>(to, flipDirection.property, flipDirection.inStartRotation, 0F).setDuration(flipChangeAnimationDuration)
            rotation.interpolator = AccelerateDecelerateInterpolator()
            animatorSet.play(rotation)

            val alpha = ObjectAnimator.ofFloat<View>(it, View.ALPHA, 1F).setDuration(flipChangeAnimationDuration / 2)
            alpha.startDelay = flipChangeAnimationDuration / 3
            animatorSet.play(alpha)
        }
        from?.let {
            val rotation = ObjectAnimator.ofFloat<View>(it, flipDirection.property, 0F, flipDirection.outEndRotation).setDuration(flipChangeAnimationDuration)
            rotation.interpolator = AccelerateDecelerateInterpolator()
            animatorSet.play(rotation)

            val alpha = ObjectAnimator.ofFloat<View>(it, View.ALPHA, 0F).setDuration(flipChangeAnimationDuration / 2)
            alpha.startDelay = flipChangeAnimationDuration / 3
            animatorSet.play(alpha)
        }
        return animatorSet
    }

    override fun resetFromView(from: View) {
        from.alpha = 1f

        when (flipDirection) {
            FlipChangeHandler.FlipDirection.LEFT, FlipChangeHandler.FlipDirection.RIGHT -> from.rotationY = 0f
            FlipChangeHandler.FlipDirection.UP, FlipChangeHandler.FlipDirection.DOWN -> from.rotationX = 0f
        }
    }

    companion object {

        private val DEFAULT_ANIMATION_DURATION: Long = 300
    }
}
