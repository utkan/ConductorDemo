package com.bluelinelabs.conductor.demo.controllers

import android.content.res.ColorStateList
import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.annotation.LayoutRes
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.ControllerChangeHandler
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler
import com.bluelinelabs.conductor.demo.R
import com.bluelinelabs.conductor.demo.changehandler.ArcFadeMoveChangeHandlerCompat
import com.bluelinelabs.conductor.demo.changehandler.CircularRevealChangeHandlerCompat
import com.bluelinelabs.conductor.demo.changehandler.FlipChangeHandler
import com.bluelinelabs.conductor.demo.controllers.base.BaseController
import com.bluelinelabs.conductor.demo.util.BundleBuilder

class TransitionDemoController(args: Bundle) : BaseController(args) {

    private lateinit var tvTitle: TextView
    private lateinit var btnNext: FloatingActionButton
    private lateinit var containerView: View

    private val transitionDemo: TransitionDemo

    override val title: String?
        get() = "Transition Demos"

    enum class TransitionDemo(internal var title: String, @param:LayoutRes internal var layoutId: Int, @param:ColorRes internal var colorId: Int) {
        VERTICAL("Vertical Slide Animation", R.layout.controller_transition_demo, R.color.blue_grey_300),
        CIRCULAR("Circular Reveal Animation (on Lollipop and above, else Fade)", R.layout.controller_transition_demo, R.color.red_300),
        FADE("Fade Animation", R.layout.controller_transition_demo, R.color.blue_300),
        FLIP("Flip Animation", R.layout.controller_transition_demo, R.color.deep_orange_300),
        HORIZONTAL("Horizontal Slide Animation", R.layout.controller_transition_demo, R.color.green_300),
        ARC_FADE("Arc/Fade Shared Element Transition (on Lollipop and above, else Fade)", R.layout.controller_transition_demo_shared, 0),
        ARC_FADE_RESET("Arc/Fade Shared Element Transition (on Lollipop and above, else Fade)", R.layout.controller_transition_demo, R.color.pink_300);


        companion object {

            fun fromIndex(index: Int): TransitionDemo {
                return TransitionDemo.values()[index]
            }
        }
    }

    constructor(index: Int) : this(BundleBuilder(Bundle())
            .putInt(KEY_INDEX, index)
            .build())

    init {
        transitionDemo = TransitionDemo.fromIndex(args.getInt(KEY_INDEX))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflateView(inflater, container)
        onViewBound(view)
        return view
    }

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(transitionDemo.layoutId, container, false)
    }

    override fun onViewBound(view: View) {
        super.onViewBound(view)

        val bgView = view.findViewById<View>(R.id.bg_view)
        if (transitionDemo.colorId != 0 && bgView != null) {
            bgView.setBackgroundColor(ContextCompat.getColor(activity!!, transitionDemo.colorId))
        }

        val nextIndex = transitionDemo.ordinal + 1
        var buttonColor = 0
        if (nextIndex < TransitionDemo.values().size) {
            buttonColor = TransitionDemo.fromIndex(nextIndex).colorId
        }
        if (buttonColor == 0) {
            buttonColor = TransitionDemo.fromIndex(0).colorId
        }
        tvTitle = view.findViewById(R.id.tv_title)
        btnNext = view.findViewById(R.id.btn_next)

        btnNext.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(activity!!, buttonColor))
        tvTitle.text = transitionDemo.title

        view.findViewById<View>(R.id.btn_next).setOnClickListener { onNextClicked() }

        containerView = view.findViewById(R.id.transition_root)
    }

    private fun onNextClicked() {
        val nextIndex = transitionDemo.ordinal + 1

        if (nextIndex < TransitionDemo.values().size) {
            router.pushController(getRouterTransaction(nextIndex, this))
        } else {
            router.popToRoot()
        }
    }

    fun getChangeHandler(from: Controller): ControllerChangeHandler {
        return when (transitionDemo) {
            TransitionDemoController.TransitionDemo.VERTICAL -> VerticalChangeHandler()
            TransitionDemoController.TransitionDemo.CIRCULAR -> {
                val demoController = from as TransitionDemoController
                CircularRevealChangeHandlerCompat(demoController.btnNext, demoController.containerView)
            }
            TransitionDemoController.TransitionDemo.FADE -> FadeChangeHandler()
            TransitionDemoController.TransitionDemo.FLIP -> FlipChangeHandler()
            TransitionDemoController.TransitionDemo.ARC_FADE -> ArcFadeMoveChangeHandlerCompat()
            TransitionDemoController.TransitionDemo.ARC_FADE_RESET -> ArcFadeMoveChangeHandlerCompat()
            TransitionDemoController.TransitionDemo.HORIZONTAL -> HorizontalChangeHandler()
        }
    }

    companion object {

        private val KEY_INDEX = "TransitionDemoController.index"

        fun getRouterTransaction(index: Int, fromController: Controller): RouterTransaction {
            val toController = TransitionDemoController(index)

            return RouterTransaction.with(toController)
                    .pushChangeHandler(toController.getChangeHandler(fromController))
                    .popChangeHandler(toController.getChangeHandler(fromController))
        }
    }

}
