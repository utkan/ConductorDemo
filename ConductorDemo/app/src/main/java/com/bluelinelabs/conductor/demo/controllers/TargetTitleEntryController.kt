package com.bluelinelabs.conductor.demo.controllers

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.demo.R
import com.bluelinelabs.conductor.demo.controllers.base.BaseController

class TargetTitleEntryController<T> : BaseController where T : Controller, T : TargetTitleEntryController.TargetTitleEntryControllerListener {

    private lateinit var editText: EditText

    override val title: String?
        get() = "Target Controller Demo"

    interface TargetTitleEntryControllerListener {
        fun onTitlePicked(option: String)
    }

    constructor(targetController: T) {
        setTargetController(targetController)
    }

    @Suppress("unused")
    constructor()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflateView(inflater, container)
        onViewBound(view)
        return view
    }

    override fun onDetach(view: View) {
        val imm = editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.controller_target_title_entry, container, false)
    }

    override fun onAttach(view: View) {
        super.onAttach(view)

        editText = view.findViewById(R.id.edit_text)
        view.findViewById<View>(R.id.btn_use_title).setOnClickListener {
            val targetController = targetController
            if (targetController != null) {
                (targetController as TargetTitleEntryControllerListener).onTitlePicked(editText.text.toString())
                router.popController(this@TargetTitleEntryController)
            }
        }
    }
}
