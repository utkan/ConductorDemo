package com.bluelinelabs.conductor.demo.controllers

import android.graphics.PorterDuff.Mode
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bluelinelabs.conductor.ControllerChangeHandler
import com.bluelinelabs.conductor.ControllerChangeType
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler
import com.bluelinelabs.conductor.demo.R
import com.bluelinelabs.conductor.demo.controllers.base.BaseController

class ExternalModulesController : BaseController() {

    private lateinit var recyclerView: RecyclerView

    override val title: String?
        get() = "External Module Demos"

    private enum class DemoModel(internal var title: String, @param:ColorRes @field:ColorRes internal var color: Int) {
        RX_LIFECYCLE("Rx Lifecycle", R.color.red_300),
        RX_LIFECYCLE_2("Rx Lifecycle 2", R.color.blue_grey_300),
        AUTODISPOSE("Autodispose", R.color.purple_300),
        ARCH_LIFECYCLE("Arch Components Lifecycle", R.color.orange_300)
    }

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.controller_additional_modules, container, false)
    }

    override fun onViewBound(view: View) {
        super.onViewBound(view)
        recyclerView = view.findViewById(R.id.recycler_view)
        with(recyclerView) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(view.context)
            adapter = AdditionalModulesAdapter(LayoutInflater.from(view.context), DemoModel.values())
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflateView(inflater, container)
        onViewBound(view)
        return view
    }

    override fun onChangeStarted(changeHandler: ControllerChangeHandler, changeType: ControllerChangeType) {
        if (changeType.isEnter) {
            setTitle()
        }
    }

    private fun onModelRowClick(model: DemoModel) {
        when (model) {
            ExternalModulesController.DemoModel.RX_LIFECYCLE -> router.pushController(RouterTransaction.with(RxLifecycleController())
                    .pushChangeHandler(FadeChangeHandler())
                    .popChangeHandler(FadeChangeHandler()))
            ExternalModulesController.DemoModel.RX_LIFECYCLE_2 -> router.pushController(RouterTransaction.with(RxLifecycle2Controller())
                    .pushChangeHandler(FadeChangeHandler())
                    .popChangeHandler(FadeChangeHandler()))
            ExternalModulesController.DemoModel.AUTODISPOSE -> router.pushController(RouterTransaction.with(AutodisposeController())
                    .pushChangeHandler(FadeChangeHandler())
                    .popChangeHandler(FadeChangeHandler()))
            ExternalModulesController.DemoModel.ARCH_LIFECYCLE -> router.pushController(RouterTransaction.with(ArchLifecycleController())
                    .pushChangeHandler(FadeChangeHandler())
                    .popChangeHandler(FadeChangeHandler()))
        }
    }

    private inner class AdditionalModulesAdapter(private val inflater: LayoutInflater, private val items: Array<DemoModel>) : RecyclerView.Adapter<AdditionalModulesAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(inflater.inflate(R.layout.row_home, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int {
            return items.size
        }

        internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            private lateinit var tvTitle: TextView
            private lateinit var imgDot: ImageView
            private lateinit var rowRoot: View

            private var model: DemoModel? = null

            init {
                with(itemView) {
                    tvTitle = findViewById(R.id.tv_title)
                    imgDot = findViewById(R.id.img_dot)
                    rowRoot = findViewById(R.id.row_root)
                }
            }

            fun bind(item: DemoModel) {
                model = item
                tvTitle.text = item.title
                imgDot.drawable.setColorFilter(ContextCompat.getColor(activity!!, item.color), Mode.SRC_ATOP)
                rowRoot.setOnClickListener { onRowClick() }
            }

            fun onRowClick() {
                model?.let {
                    onModelRowClick(it)
                }
            }
        }
    }

}