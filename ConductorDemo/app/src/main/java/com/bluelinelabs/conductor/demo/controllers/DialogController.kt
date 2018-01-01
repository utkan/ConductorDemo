package com.bluelinelabs.conductor.demo.controllers


import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bluelinelabs.conductor.demo.R
import com.bluelinelabs.conductor.demo.controllers.base.BaseController
import com.bluelinelabs.conductor.demo.util.BundleBuilder

class DialogController(args: Bundle) : BaseController(args) {

    private lateinit var tvTitle: TextView
    private lateinit var tvDescription: TextView

    constructor(title: CharSequence, description: CharSequence) : this(BundleBuilder(Bundle())
            .putCharSequence(KEY_TITLE, title)
            .putCharSequence(KEY_DESCRIPTION, description)
            .build())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflateView(inflater, container)
        onViewBound(view)
        return view
    }

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.controller_dialog, container, false)
    }

    public override fun onViewBound(view: View) {
        super.onViewBound(view)
        with(view) {
            tvTitle = findViewById(R.id.tv_title)
            tvDescription = findViewById(R.id.tv_description)
            findViewById<View>(R.id.dismiss).setOnClickListener { dismissDialog() }
            findViewById<View>(R.id.dialog_window).setOnClickListener { dismissDialog() }
        }
        tvTitle.text = args.getCharSequence(KEY_TITLE)
        tvDescription.text = args.getCharSequence(KEY_DESCRIPTION)
        tvDescription.movementMethod = LinkMovementMethod.getInstance()
    }

    fun dismissDialog() {
        router.popController(this)
    }

    companion object {

        private val KEY_TITLE = "DialogController.title"
        private val KEY_DESCRIPTION = "DialogController.description"
    }
}
