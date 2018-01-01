package com.bluelinelabs.conductor.demo.changehandler

import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.transition.Transition
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnPreDrawListener
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.ControllerChangeHandler
import com.bluelinelabs.conductor.changehandler.TransitionChangeHandler
import java.util.*

/**
 * A TransitionChangeHandler that will wait for views with the passed transition names to be fully laid out
 * before executing. An OnPreDrawListener will be added to the "to" view, then to all of its subviews that
 * match the transaction names we're interested in. Once all of the views are fully ready, the "to" view
 * is set to invisible so that it'll fade in nicely, and the views that we want to use as shared elements
 * are removed from their containers, then immediately re-added within the beginDelayedTransition call so
 * the system picks them up as shared elements.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class SharedElementDelayingChangeHandler : ArcFadeMoveChangeHandler {

    private val waitForTransitionNames: ArrayList<String>
    private val removedViews = ArrayList<ViewParentPair>()
    private var onPreDrawListener: OnPreDrawListener? = null

    constructor() {
        waitForTransitionNames = ArrayList()
    }

    constructor(waitForTransitionNames: List<String>) {
        this.waitForTransitionNames = ArrayList(waitForTransitionNames)
    }

    override fun prepareForTransition(container: ViewGroup, from: View?, to: View?, transition: Transition, isPush: Boolean, onTransitionPreparedListener: TransitionChangeHandler.OnTransitionPreparedListener) {
        if (to != null && to.parent == null && waitForTransitionNames.size > 0) {
            onPreDrawListener = object : OnPreDrawListener {
                internal var addedSubviewListeners: Boolean = false

                override fun onPreDraw(): Boolean {
                    val foundViews = ArrayList<View>()
                    waitForTransitionNames
                            .map { getViewWithTransitionName(to, it) }
                            .forEach { viewWithTransitionName ->
                                viewWithTransitionName?.let {
                                    foundViews.add(it)
                                }
                            }

                    if (!addedSubviewListeners) {
                        addedSubviewListeners = true

                        for (view in foundViews) {
                            view.viewTreeObserver.addOnPreDrawListener(object : OnPreDrawListener {
                                override fun onPreDraw(): Boolean {
                                    view.viewTreeObserver.removeOnPreDrawListener(this)
                                    waitForTransitionNames.remove(view.transitionName)

                                    val parent = view.parent as ViewGroup
                                    removedViews.add(ViewParentPair(view, parent))
                                    parent.removeView(view)

                                    if (waitForTransitionNames.size == 0) {
                                        to.viewTreeObserver.removeOnPreDrawListener(onPreDrawListener)
                                        onPreDrawListener = null

                                        to.visibility = View.INVISIBLE

                                        onTransitionPreparedListener.onPrepared()
                                    }
                                    return true
                                }
                            })
                        }
                    }


                    return false
                }
            }

            to.viewTreeObserver.addOnPreDrawListener(onPreDrawListener)

            container.addView(to)
        } else {
            onTransitionPreparedListener.onPrepared()
        }
    }

    override fun executePropertyChanges(container: ViewGroup, from: View?, to: View?, transition: Transition?, isPush: Boolean) {
        if (to != null) {
            to.visibility = View.VISIBLE

            for (removedView in removedViews) {
                removedView.parent.addView(removedView.view)
            }

            removedViews.clear()
        }

        super.executePropertyChanges(container, from, to, transition, isPush)
    }

    override fun saveToBundle(bundle: Bundle) {
        bundle.putStringArrayList(KEY_WAIT_FOR_TRANSITION_NAMES, waitForTransitionNames)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        val savedNames = bundle.getStringArrayList(KEY_WAIT_FOR_TRANSITION_NAMES)
        if (savedNames != null) {
            waitForTransitionNames.addAll(savedNames)
        }
    }

    override fun onAbortPush(newHandler: ControllerChangeHandler, newTop: Controller?) {
        super.onAbortPush(newHandler, newTop)

        removedViews.clear()
    }

    internal fun getViewWithTransitionName(view: View, transitionName: String): View? {
        if (transitionName == view.transitionName) {
            return view
        }

        if (view is ViewGroup) {
            val childCount = view.childCount
            (0 until childCount)
                    .mapNotNull { getViewWithTransitionName(view.getChildAt(it), transitionName) }
                    .forEach { return it }
        }
        return null
    }

    private class ViewParentPair(internal var view: View, internal var parent: ViewGroup)

    companion object {

        private val KEY_WAIT_FOR_TRANSITION_NAMES = "SharedElementDelayingChangeHandler.waitForTransitionNames"
    }

}
