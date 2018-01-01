package com.bluelinelabs.conductor.demo.controllers

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.bluelinelabs.conductor.demo.R
import com.bluelinelabs.conductor.demo.controllers.base.BaseController
import com.bluelinelabs.conductor.demo.util.BundleBuilder

class ChildController(args: Bundle) : BaseController(args) {

    lateinit var tvTitle: TextView

    constructor(title: String, backgroundColor: Int, colorIsResId: Boolean) : this(BundleBuilder(Bundle())
            .putString(KEY_TITLE, title)
            .putInt(KEY_BG_COLOR, backgroundColor)
            .putBoolean(KEY_COLOR_IS_RES, colorIsResId)
            .build())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflateView(inflater, container)
        onViewBound(view)
        return view
    }

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.controller_child, container, false)
    }

    override fun onViewBound(view: View) {
        super.onViewBound(view)
        tvTitle = view.findViewById(R.id.tv_title)
        tvTitle.text = args.getString(KEY_TITLE)

        var bgColor = args.getInt(KEY_BG_COLOR)
        if (args.getBoolean(KEY_COLOR_IS_RES)) {
            bgColor = ContextCompat.getColor(activity!!, bgColor)
        }
        view.setBackgroundColor(bgColor)
    }

    companion object {

        private val KEY_TITLE = "ChildController.title"
        private val KEY_BG_COLOR = "ChildController.bgColor"
        private val KEY_COLOR_IS_RES = "ChildController.colorIsResId"
    }
}
