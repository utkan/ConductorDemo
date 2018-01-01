package com.bluelinelabs.conductor.demo.controllers.base

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.demo.ActionBarProvider

abstract class BaseController : RefWatchingController {
    // Note: This is just a quick demo of how an ActionBar *can* be accessed, not necessarily how it *should*
    // be accessed. In a production app, this would use Dagger instead.
    private val actionBar: ActionBar?
        get() {
            val actionBarProvider = activity as ActionBarProvider?
            return actionBarProvider?.supportActionBar()
        }

    open protected val title: String?
        get() = null

    protected constructor()

    protected constructor(args: Bundle) : super(args)

    protected open fun onViewBound(view: View) {}

    protected abstract fun inflateView(inflater: LayoutInflater, container: ViewGroup): View

    override fun onAttach(view: View) {
        setTitle()
        super.onAttach(view)
    }

    protected fun setTitle() {
        var parentController = parentController
        while (parentController != null) {
            if (parentController is BaseController && parentController.title != null) {
                return
            }
            parentController = parentController.parentController
        }

        val title = title
        val actionBar = actionBar
        if (title != null && actionBar != null) {
            actionBar.title = title
        }
    }
}
