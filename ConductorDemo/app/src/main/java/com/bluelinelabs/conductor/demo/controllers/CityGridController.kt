package com.bluelinelabs.conductor.demo.controllers

import android.graphics.PorterDuff.Mode
import android.os.Build
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler
import com.bluelinelabs.conductor.changehandler.TransitionChangeHandlerCompat
import com.bluelinelabs.conductor.demo.R
import com.bluelinelabs.conductor.demo.changehandler.SharedElementDelayingChangeHandler
import com.bluelinelabs.conductor.demo.controllers.base.BaseController
import com.bluelinelabs.conductor.demo.util.BundleBuilder
import java.util.*

class CityGridController(args: Bundle) : BaseController(args) {

    private lateinit var tvTitle: TextView
    private lateinit var imgDot: ImageView
    private lateinit var recyclerView: RecyclerView

    private var cityTitle: String? = null
    private var dotColor: Int? = null
    private var fromPosition: Int? = null

    override val title: String?
        get() = "Shared Element Demos"

    constructor(title: String, dotColor: Int, fromPosition: Int) : this(BundleBuilder(Bundle())
            .putString(KEY_TITLE, title)
            .putInt(KEY_DOT_COLOR, dotColor)
            .putInt(KEY_FROM_POSITION, fromPosition)
            .build())

    init {
        val bundle = getArgs()
        with(bundle) {
            cityTitle = getString(KEY_TITLE)
            dotColor = getInt(KEY_DOT_COLOR)
            fromPosition = getInt(KEY_FROM_POSITION)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflateView(inflater, container)
        onViewBound(view)
        return view
    }

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.controller_city_grid, container, false)
    }

    override fun onViewBound(view: View) {
        super.onViewBound(view)
        with(view) {
            tvTitle = findViewById(R.id.tv_title)
            imgDot = findViewById(R.id.img_dot)
            recyclerView = findViewById(R.id.recycler_view)
        }

        tvTitle.text = cityTitle
        dotColor?.let { c ->
            activity?.let {
                imgDot.drawable.setColorFilter(ContextCompat.getColor(it, c), Mode.SRC_ATOP)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tvTitle.transitionName = resources!!.getString(R.string.transition_tag_title_indexed, fromPosition)
            imgDot.transitionName = resources!!.getString(R.string.transition_tag_dot_indexed, fromPosition)
        }

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(view.context, 2)
        recyclerView.adapter = CityGridAdapter(LayoutInflater.from(view.context), CITY_MODELS)
    }

    private fun onModelRowClick(model: CityModel) {
        val imageTransitionName = resources!!.getString(R.string.transition_tag_image_named, model.title)
        val titleTransitionName = resources!!.getString(R.string.transition_tag_title_named, model.title)

        val names = ArrayList<String>()
        names.add(imageTransitionName)
        names.add(titleTransitionName)

        router.pushController(RouterTransaction.with(CityDetailController(model.drawableRes, model.title))
                .pushChangeHandler(TransitionChangeHandlerCompat(SharedElementDelayingChangeHandler(names), FadeChangeHandler()))
                .popChangeHandler(TransitionChangeHandlerCompat(SharedElementDelayingChangeHandler(names), FadeChangeHandler())))
    }

    private inner class CityGridAdapter(private val inflater: LayoutInflater, private val items: Array<CityModel>) : RecyclerView.Adapter<CityGridAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(inflater.inflate(R.layout.row_city_grid, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int {
            return items.size
        }

        internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private lateinit var textView: TextView
            private lateinit var imageView: ImageView
            private lateinit var rowRoot: View
            private var model: CityModel? = null

            init {
                with(itemView) {
                    textView = findViewById(R.id.tv_title)
                    imageView = findViewById(R.id.img_city)
                    rowRoot = findViewById(R.id.row_root)
                }
            }

            fun bind(item: CityModel) {
                model = item
                imageView.setImageResource(item.drawableRes)
                textView.text = item.title

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    textView.transitionName = resources!!.getString(R.string.transition_tag_title_named, model!!.title)
                    imageView.transitionName = resources!!.getString(R.string.transition_tag_image_named, model!!.title)
                }
                rowRoot.setOnClickListener { onRowClick() }
            }

            fun onRowClick() {
                model?.let {
                    onModelRowClick(it)
                }
            }

        }
    }

    private class CityModel(@param:DrawableRes @field:DrawableRes internal var drawableRes: Int, internal var title: String)

    companion object {

        private val KEY_TITLE = "CityGridController.title"
        private val KEY_DOT_COLOR = "CityGridController.dotColor"
        private val KEY_FROM_POSITION = "CityGridController.position"

        private val CITY_MODELS = arrayOf(CityModel(R.drawable.chicago, "Chicago"), CityModel(R.drawable.jakarta, "Jakarta"), CityModel(R.drawable.london, "London"), CityModel(R.drawable.sao_paulo, "Sao Paulo"), CityModel(R.drawable.tokyo, "Tokyo"))
    }
}
