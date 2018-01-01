package com.bluelinelabs.conductor.demo.controllers

import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.ControllerChangeHandler
import com.bluelinelabs.conductor.ControllerChangeType
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.bluelinelabs.conductor.demo.ActionBarProvider
import com.bluelinelabs.conductor.demo.DemoApplication
import com.bluelinelabs.conductor.demo.R
import com.bluelinelabs.conductor.rxlifecycle.ControllerEvent
import com.bluelinelabs.conductor.rxlifecycle.RxController
import rx.Observable
import java.util.concurrent.TimeUnit

// Shamelessly borrowed from the official RxLifecycle demo by Trello and adapted for Conductor Controllers
// instead of Activities or Fragments.
class RxLifecycleController : RxController() {

    private lateinit var tvTitle: TextView

    private var hasExited: Boolean = false

    init {
        Observable.interval(1, TimeUnit.SECONDS)
                .doOnUnsubscribe { Log.i(TAG, "Unsubscribing from constructor") }
                .compose(this.bindUntilEvent(ControllerEvent.DESTROY))
                .subscribe { num -> Log.i(TAG, "Started in constructor, running until onDestroy(): " + num!!) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        Log.i(TAG, "onCreateView() called")

        val view = inflater.inflate(R.layout.controller_lifecycle, container, false)
        view.setBackgroundColor(ContextCompat.getColor(container.context, R.color.red_300))

        if (::tvTitle.isInitialized) {
            tvTitle!!.text = resources!!.getString(R.string.rxlifecycle_title, TAG)
        }

        Observable.interval(1, TimeUnit.SECONDS)
                .doOnUnsubscribe { Log.i(TAG, "Unsubscribing from onCreateView)") }
                .compose(this.bindUntilEvent(ControllerEvent.DESTROY_VIEW))
                .subscribe { num -> Log.i(TAG, "Started in onCreateView(), running until onDestroyView(): " + num!!) }

        view.findViewById<View>(R.id.btn_next_release_view)
                .setOnClickListener { onNextWithReleaseClicked() }
        view.findViewById<View>(R.id.btn_next_retain_view).setOnClickListener { onNextWithRetainClicked() }
        return view
    }

    override fun onAttach(view: View) {
        super.onAttach(view)

        Log.i(TAG, "onAttach() called")

        (activity as ActionBarProvider).supportActionBar()?.setTitle("RxLifecycle Demo")

        Observable.interval(1, TimeUnit.SECONDS)
                .doOnUnsubscribe { Log.i(TAG, "Unsubscribing from onAttach()") }
                .compose(this.bindUntilEvent(ControllerEvent.DETACH))
                .subscribe { num -> Log.i(TAG, "Started in onAttach(), running until onDetach(): " + num!!) }
    }

    override fun onDestroyView(view: View) {
        super.onDestroyView(view)

        Log.i(TAG, "onDestroyView() called")
    }

    override fun onDetach(view: View) {
        super.onDetach(view)

        Log.i(TAG, "onDetach() called")
    }

    public override fun onDestroy() {
        super.onDestroy()

        Log.i(TAG, "onDestroy() called")

        if (hasExited) {
                        DemoApplication.refWatcher.watch(this)
        }
    }

    override fun onChangeEnded(changeHandler: ControllerChangeHandler, changeType: ControllerChangeType) {
        super.onChangeEnded(changeHandler, changeType)

        hasExited = !changeType.isEnter
        if (isDestroyed) {
                        DemoApplication.refWatcher.watch(this)
        }
    }

    private fun onNextWithReleaseClicked() {
        retainViewMode = Controller.RetainViewMode.RELEASE_DETACH

        router.pushController(RouterTransaction.with(TextController("Logcat should now report that the observables from onAttach() and onViewBound() have been unsubscribed from, while the constructor observable is still running."))
                .pushChangeHandler(HorizontalChangeHandler())
                .popChangeHandler(HorizontalChangeHandler()))
    }

    private fun onNextWithRetainClicked() {
        retainViewMode = Controller.RetainViewMode.RETAIN_DETACH

        router.pushController(RouterTransaction.with(TextController("Logcat should now report that the observables from onAttach() has been unsubscribed from, while the constructor and onViewBound() observables are still running."))
                .pushChangeHandler(HorizontalChangeHandler())
                .popChangeHandler(HorizontalChangeHandler()))
    }

    companion object {

        private val TAG = "RxLifecycleController"
    }
}
