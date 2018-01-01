package com.bluelinelabs.conductor.demo.controllers

import android.annotation.TargetApi
import android.os.Build.VERSION_CODES
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.bluelinelabs.conductor.demo.R
import com.bluelinelabs.conductor.demo.changehandler.ScaleFadeChangeHandler
import com.bluelinelabs.conductor.demo.controllers.base.BaseController
import com.bluelinelabs.conductor.demo.widget.ElasticDragDismissFrameLayout
import com.bluelinelabs.conductor.demo.widget.ElasticDragDismissFrameLayout.ElasticDragDismissCallback

@TargetApi(VERSION_CODES.LOLLIPOP)
class DragDismissController : BaseController() {

    private val dragDismissListener = object : ElasticDragDismissCallback() {
        override fun onDragDismissed() {
            overridePopHandler(ScaleFadeChangeHandler())
            router.popController(this@DragDismissController)
        }
    }

    override val title: String?
        get() = "Drag to Dismiss"

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.controller_drag_dismiss, container, false)
    }

    override fun onViewBound(view: View) {
        (view as ElasticDragDismissFrameLayout).addListener(dragDismissListener)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflateView(inflater, container)
        onViewBound(view)
        return view
    }

    override fun onDestroyView(view: View) {
        super.onDestroyView(view)

        (view as ElasticDragDismissFrameLayout).removeListener(dragDismissListener)
    }
}
