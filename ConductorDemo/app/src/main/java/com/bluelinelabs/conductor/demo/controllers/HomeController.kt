package com.bluelinelabs.conductor.demo.controllers

import android.content.Intent
import android.graphics.PorterDuff.Mode
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.URLSpan
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import com.bluelinelabs.conductor.ControllerChangeHandler
import com.bluelinelabs.conductor.ControllerChangeType
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.bluelinelabs.conductor.changehandler.TransitionChangeHandlerCompat
import com.bluelinelabs.conductor.demo.R
import com.bluelinelabs.conductor.demo.changehandler.ArcFadeMoveChangeHandler
import com.bluelinelabs.conductor.demo.changehandler.FabToDialogTransitionChangeHandler
import com.bluelinelabs.conductor.demo.controllers.NavigationDemoController.DisplayUpMode
import com.bluelinelabs.conductor.demo.controllers.base.BaseController

class HomeController : BaseController() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: View

    override val title: String?
        get() = "Conductor Demos"

    private enum class DemoModel(internal var title: String, @param:ColorRes @field:ColorRes internal var color: Int) {
        NAVIGATION("Navigation Demos", R.color.red_300),
        TRANSITIONS("Transition Demos", R.color.blue_grey_300),
        SHARED_ELEMENT_TRANSITIONS("Shared Element Demos", R.color.purple_300),
        CHILD_CONTROLLERS("Child Controllers", R.color.orange_300),
        VIEW_PAGER("ViewPager", R.color.green_300),
        TARGET_CONTROLLER("Target Controller", R.color.pink_300),
        MULTIPLE_CHILD_ROUTERS("Multiple Child Routers", R.color.deep_orange_300),
        MASTER_DETAIL("Master Detail", R.color.grey_300),
        DRAG_DISMISS("Drag Dismiss", R.color.lime_300),
        EXTERNAL_MODULES("Bonus Modules", R.color.teal_300)
    }

    init {
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflateView(inflater, container)
        onViewBound(view)
        return view
    }

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.controller_home, container, false)
    }

    override fun onViewBound(view: View) {
        super.onViewBound(view)
        fab = view.findViewById(R.id.fab)
        fab.setOnClickListener { onFabClicked() }
        recyclerView = view.findViewById(R.id.recycler_view)
        with(recyclerView) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(view.context)
            adapter = HomeAdapter(LayoutInflater.from(view.context), DemoModel.values())
        }
    }

    override fun onSaveViewState(view: View, outState: Bundle) {
        super.onSaveViewState(view, outState)
        outState.putInt(KEY_FAB_VISIBILITY, fab.visibility)
    }

    override fun onRestoreViewState(view: View, savedViewState: Bundle) {
        super.onRestoreViewState(view, savedViewState)

        fab.visibility = savedViewState.getInt(KEY_FAB_VISIBILITY)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home, menu)
    }

    override fun onChangeStarted(changeHandler: ControllerChangeHandler, changeType: ControllerChangeType) {
        setOptionsMenuHidden(!changeType.isEnter)

        if (changeType.isEnter) {
            setTitle()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.about) {
            onFabClicked(false)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onFabClicked() {
        onFabClicked(true)
    }

    private fun onFabClicked(fromFab: Boolean) {
        val details = SpannableString("A small, yet full-featured framework that allows building View-based Android applications")
        details.setSpan(AbsoluteSizeSpan(16, true), 0, details.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)

        val url = "https://github.com/bluelinelabs/Conductor"
        val link = SpannableString(url)
        link.setSpan(object : URLSpan(url) {
            override fun onClick(widget: View) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }
        }, 0, link.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)

        val description = SpannableStringBuilder()
        with(description) {
            append(details)
            append("\n\n")
            append(link)
        }

        val pushHandler = if (fromFab) TransitionChangeHandlerCompat(FabToDialogTransitionChangeHandler(), FadeChangeHandler(false)) else FadeChangeHandler(false)
        val popHandler = if (fromFab) TransitionChangeHandlerCompat(FabToDialogTransitionChangeHandler(), FadeChangeHandler()) else FadeChangeHandler()

        router
                .pushController(RouterTransaction.with(DialogController("Conductor", description))
                        .pushChangeHandler(pushHandler)
                        .popChangeHandler(popHandler))

    }

    private fun onModelRowClick(model: DemoModel?, position: Int) {
        when (model) {
            HomeController.DemoModel.NAVIGATION -> router.pushController(RouterTransaction.with(NavigationDemoController(0, DisplayUpMode.SHOW_FOR_CHILDREN_ONLY))
                    .pushChangeHandler(FadeChangeHandler())
                    .popChangeHandler(FadeChangeHandler())
                    .tag(NavigationDemoController.TAG_UP_TRANSACTION)
            )
            HomeController.DemoModel.TRANSITIONS -> router.pushController(TransitionDemoController.getRouterTransaction(0, this))
            HomeController.DemoModel.TARGET_CONTROLLER -> router.pushController(
                    RouterTransaction.with(TargetDisplayController())
                            .pushChangeHandler(FadeChangeHandler())
                            .popChangeHandler(FadeChangeHandler()))
            HomeController.DemoModel.VIEW_PAGER -> router.pushController(RouterTransaction.with(PagerController())
                    .pushChangeHandler(FadeChangeHandler())
                    .popChangeHandler(FadeChangeHandler()))
            HomeController.DemoModel.CHILD_CONTROLLERS -> router.pushController(RouterTransaction.with(ParentController())
                    .pushChangeHandler(FadeChangeHandler())
                    .popChangeHandler(FadeChangeHandler()))
            HomeController.DemoModel.SHARED_ELEMENT_TRANSITIONS -> router.pushController(RouterTransaction.with(CityGridController(model.title, model.color, position))
                    .pushChangeHandler(TransitionChangeHandlerCompat(ArcFadeMoveChangeHandler(), FadeChangeHandler()))
                    .popChangeHandler(TransitionChangeHandlerCompat(ArcFadeMoveChangeHandler(), FadeChangeHandler())))
            HomeController.DemoModel.DRAG_DISMISS -> router.pushController(RouterTransaction.with(DragDismissController())
                    .pushChangeHandler(FadeChangeHandler(false))
                    .popChangeHandler(FadeChangeHandler()))
            HomeController.DemoModel.EXTERNAL_MODULES -> router.pushController(RouterTransaction.with(ExternalModulesController())
                    .pushChangeHandler(HorizontalChangeHandler())
                    .popChangeHandler(HorizontalChangeHandler()))
            HomeController.DemoModel.MULTIPLE_CHILD_ROUTERS -> router.pushController(RouterTransaction.with(MultipleChildRouterController())
                    .pushChangeHandler(FadeChangeHandler())
                    .popChangeHandler(FadeChangeHandler()))
            HomeController.DemoModel.MASTER_DETAIL -> router.pushController(RouterTransaction.with(MasterDetailListController())
                    .pushChangeHandler(FadeChangeHandler())
                    .popChangeHandler(FadeChangeHandler()))
        }
    }

    private inner class HomeAdapter(private val inflater: LayoutInflater, private val items: Array<DemoModel>) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(inflater.inflate(R.layout.row_home, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(position, items[position])
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
                    rowRoot = findViewById(R.id.row_root)
                    tvTitle = findViewById(R.id.tv_title)
                    imgDot = findViewById(R.id.img_dot)
                }
            }

            fun bind(position: Int, item: DemoModel) {
                model = item
                tvTitle.text = item.title
                imgDot.drawable.setColorFilter(ContextCompat.getColor(activity!!, item.color), Mode.SRC_ATOP)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    tvTitle.transitionName = resources!!.getString(R.string.transition_tag_title_indexed, position)
                    imgDot.transitionName = resources!!.getString(R.string.transition_tag_dot_indexed, position)
                }
                rowRoot.setOnClickListener { onRowClick() }
            }

            fun onRowClick() {
                onModelRowClick(model, adapterPosition)
            }

        }
    }

    companion object {

        private val KEY_FAB_VISIBILITY = "HomeController.fabVisibility"
    }

}
