package com.bluelinelabs.conductor.demo.controllers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.demo.R
import com.bluelinelabs.conductor.demo.controllers.base.BaseController

class MultipleChildRouterController : BaseController() {

    override val title: String?
        get() = "Child Router Demo"

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.controller_multiple_child_routers, container, false)
    }

    override fun onViewBound(view: View) {
        super.onViewBound(view)
        arrayOf(R.id.container_0, R.id.container_1, R.id.container_2)
                .forEach {
                    val childContainer = view.findViewById<ViewGroup>(it)
                    childContainer?.let { cc ->
                        val childRouter = getChildRouter(cc).setPopsLastView(false)
                        with(childRouter) {
                            if (!hasRootController()) {
                                setRoot(RouterTransaction.with(NavigationDemoController(0, NavigationDemoController.DisplayUpMode.HIDE)))
                            }
                        }
                    }
                }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflateView(inflater, container)
        onViewBound(view)
        return view
    }
}
