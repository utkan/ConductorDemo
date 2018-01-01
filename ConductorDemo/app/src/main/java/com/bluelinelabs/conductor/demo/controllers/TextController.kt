package com.bluelinelabs.conductor.demo.controllers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.bluelinelabs.conductor.demo.R
import com.bluelinelabs.conductor.demo.controllers.base.BaseController
import com.bluelinelabs.conductor.demo.util.BundleBuilder

class TextController(args: Bundle) : BaseController(args) {

    private lateinit var textView: TextView

    constructor(text: String) : this(BundleBuilder(Bundle())
            .putString(KEY_TEXT, text)
            .build()
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflateView(inflater, container)
        onViewBound(view)
        return view
    }

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.controller_text, container, false)
    }

    public override fun onViewBound(view: View) {
        super.onViewBound(view)
        textView = view.findViewById(R.id.text_view)
        textView.text = args.getString(KEY_TEXT)
    }

    companion object {

        private val KEY_TEXT = "TextController.text"
    }

}
