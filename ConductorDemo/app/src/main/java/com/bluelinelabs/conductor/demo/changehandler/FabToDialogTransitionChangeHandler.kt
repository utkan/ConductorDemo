package com.bluelinelabs.conductor.demo.changehandler

import android.annotation.TargetApi
import android.os.Build
import android.support.v4.content.ContextCompat
import android.transition.Fade
import android.transition.Transition
import android.transition.TransitionSet
import android.view.View
import android.view.ViewGroup

import com.bluelinelabs.conductor.changehandler.TransitionChangeHandler
import com.bluelinelabs.conductor.demo.R
import com.bluelinelabs.conductor.demo.changehandler.transitions.FabTransform
import com.bluelinelabs.conductor.demo.util.AnimUtils

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class FabToDialogTransitionChangeHandler : TransitionChangeHandler() {

    private var fab: View? = null
    private var dialogBackground: View? = null
    private var fabParent: ViewGroup? = null

    override fun getTransition(container: ViewGroup, from: View?, to: View?, isPush: Boolean): Transition {
        val backgroundFade = Fade()
        backgroundFade.addTarget(R.id.dialog_background)

        val fabTransform = FabTransform(ContextCompat.getColor(container.context, R.color.colorAccent), R.drawable.ic_github_face)

        return TransitionSet()
                .addTransition(backgroundFade)
                .addTransition(fabTransform)
    }

    override fun prepareForTransition(container: ViewGroup, from: View?, to: View?, transition: Transition, isPush: Boolean, onTransitionPreparedListener: TransitionChangeHandler.OnTransitionPreparedListener) {
        from?.let {
            if (isPush) {
                fab = it.findViewById(R.id.fab)
            }
        }
        to?.let {
            if (!isPush) {
                fab = it.findViewById(R.id.fab)
            }
        }
        fab?.let {
            fabParent = it.parent as ViewGroup
        }

        if (!isPush) {
            fabParent?.removeView(fab)

            fab?.visibility = View.VISIBLE

            /*
             * Before we transition back we need to move the dialog's background to the new view
             * so its fade won't take place over the fab transition
             */
            dialogBackground = from?.findViewById(R.id.dialog_background)
            (dialogBackground?.parent as? ViewGroup)?.removeView(dialogBackground)
            fabParent?.addView(dialogBackground)
        }

        onTransitionPreparedListener.onPrepared()
    }

    override fun executePropertyChanges(container: ViewGroup, from: View?, to: View?, transition: Transition?, isPush: Boolean) {
        if (isPush) {
            fabParent?.removeView(fab)
            container.addView(to)

            /*
             * After the transition is finished we have to add the fab back to the original container.
             * Because otherwise we will be lost when trying to transition back.
             * Set it to invisible because we don't want it to jump back after the transition
             */
            val endListener = object : AnimUtils.TransitionEndListener() {
                override fun onTransitionCompleted(transition: Transition?) {
                    fab?.visibility = View.GONE
                    fabParent?.addView(fab)
                    fab = null
                    fabParent = null
                }
            }
            if (transition != null) {
                transition.addListener(endListener)
            } else {
                endListener.onTransitionCompleted(null)
            }
        } else {
            dialogBackground?.visibility = View.INVISIBLE
            fabParent?.addView(fab)
            container.removeView(from)

            val endListener = object : AnimUtils.TransitionEndListener() {
                override fun onTransitionCompleted(transition: Transition?) {
                    fabParent?.removeView(dialogBackground)
                    dialogBackground = null
                }
            }
            if (transition != null) {
                transition.addListener(endListener)
            } else {
                endListener.onTransitionCompleted(null)
            }
        }
    }

}
