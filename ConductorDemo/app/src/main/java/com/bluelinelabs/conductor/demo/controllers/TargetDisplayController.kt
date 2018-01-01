package com.bluelinelabs.conductor.demo.controllers

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.bluelinelabs.conductor.demo.R
import com.bluelinelabs.conductor.demo.controllers.TargetTitleEntryController.TargetTitleEntryControllerListener
import com.bluelinelabs.conductor.demo.controllers.base.BaseController
import com.squareup.picasso.Picasso

class TargetDisplayController : BaseController(), TargetTitleEntryControllerListener {

    private lateinit var tvSelection: TextView
    private lateinit var imageView: ImageView

    private var selectedText: String? = null
    private var imageUri: Uri? = null

    override val title: String?
        get() = "Target Controller Demo"

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.controller_target_display, container, false)
    }

    private fun launchTitlePicker() {
        router.pushController(RouterTransaction.with(TargetTitleEntryController(this))
                .pushChangeHandler(HorizontalChangeHandler())
                .popChangeHandler(HorizontalChangeHandler()))
    }

    private fun launchImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        startActivityForResult(Intent.createChooser(intent, "Select Image"), REQUEST_SELECT_IMAGE)
    }

    override fun onTitlePicked(option: String) {
        selectedText = option
        setTextView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
            imageUri = data!!.data
            setImageView()
        }
    }

    override fun onViewBound(view: View) {
        super.onViewBound(view)
        tvSelection = view.findViewById(R.id.tv_selection)
        imageView = view.findViewById(R.id.image_view)
        view.findViewById<View>(R.id.btn_pick_title).setOnClickListener { launchTitlePicker() }
        view.findViewById<View>(R.id.btn_pick_image).setOnClickListener { launchImagePicker() }
        setTextView()
        setImageView()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflateView(inflater, container)
        onViewBound(view)
        return view
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_SELECTED_TEXT, selectedText)
        outState.putString(KEY_SELECTED_IMAGE, if (imageUri != null) imageUri!!.toString() else null)
    }

    public override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        selectedText = savedInstanceState.getString(KEY_SELECTED_TEXT)

        val uriString = savedInstanceState.getString(KEY_SELECTED_IMAGE)
        if (!TextUtils.isEmpty(uriString)) {
            imageUri = Uri.parse(uriString)
        }
    }

    private fun setImageView() {
        Picasso.with(activity)
                .load(imageUri)
                .fit()
                .centerCrop()
                .into(imageView)
    }

    private fun setTextView() {
        if (!TextUtils.isEmpty(selectedText)) {
            tvSelection.text = selectedText
        } else {
            tvSelection.text = "Press pick title to set this title, or pick image to fill in the image view."
        }
    }

    companion object {

        private val REQUEST_SELECT_IMAGE = 126

        private val KEY_SELECTED_TEXT = "TargetDisplayController.selectedText"
        private val KEY_SELECTED_IMAGE = "TargetDisplayController.selectedImage"
    }
}
