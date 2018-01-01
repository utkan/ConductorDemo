package com.bluelinelabs.conductor.demo.controllers

import android.arch.lifecycle.Lifecycle.Event
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
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
import com.bluelinelabs.conductor.archlifecycle.LifecycleController
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.bluelinelabs.conductor.demo.ActionBarProvider
import com.bluelinelabs.conductor.demo.DemoApplication
import com.bluelinelabs.conductor.demo.R

class ArchLifecycleController : LifecycleController() {

    lateinit var tvTitle: TextView

    private var hasExited: Boolean = false

    init {
        val lifecycleObserver = object : LifecycleObserver {
            @OnLifecycleEvent(Event.ON_CREATE)
            internal fun onCreate() {
                Log.d(TAG, "LifecycleObserver onCreate() called")
            }

            @OnLifecycleEvent(Event.ON_START)
            internal fun onStart() {
                Log.d(TAG, "LifecycleObserver onStart() called")
            }

            @OnLifecycleEvent(Event.ON_RESUME)
            internal fun onResume() {
                Log.d(TAG, "LifecycleObserver onResume() called")
            }

            @OnLifecycleEvent(Event.ON_PAUSE)
            internal fun onPause() {
                Log.d(TAG, "LifecycleObserver onPause() called")
            }

            @OnLifecycleEvent(Event.ON_STOP)
            internal fun onStop() {
                Log.d(TAG, "LifecycleObserver onStop() called")
            }

            @OnLifecycleEvent(Event.ON_DESTROY)
            internal fun onDestroy() {
                Log.d(TAG, "LifecycleObserver onDestroy() called")
            }
        }

        Log.i(TAG, "constructor called")

        lifecycle.addObserver(lifecycleObserver)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        Log.i(TAG, "onCreateView() called")

        val view = inflater.inflate(R.layout.controller_lifecycle, container, false)
        view.setBackgroundColor(ContextCompat.getColor(container.context, R.color.orange_300))

        tvTitle = view.findViewById(R.id.tv_title)
        tvTitle.text = resources!!.getString(R.string.rxlifecycle_title, TAG)

        view.findViewById<View>(R.id.btn_next_release_view).setOnClickListener { onNextWithReleaseClicked() }
        view.findViewById<View>(R.id.btn_next_retain_view).setOnClickListener { onNextWithRetainClicked() }
        return view
    }

    override fun onAttach(view: View) {
        super.onAttach(view)

        Log.i(TAG, "onAttach() called")

        (activity as ActionBarProvider).supportActionBar()?.title = "Arch Components Lifecycle Demo"
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

        router.pushController(RouterTransaction.with(TextController("Logcat should now report that the Controller's onDetach() and LifecycleObserver's onPause() methods were called, followed by the Controller's onDestroyView() and LifecycleObserver's onStop()."))
                .pushChangeHandler(HorizontalChangeHandler())
                .popChangeHandler(HorizontalChangeHandler()))
    }

    private fun onNextWithRetainClicked() {
        retainViewMode = Controller.RetainViewMode.RETAIN_DETACH

        router.pushController(RouterTransaction.with(TextController("Logcat should now report that the Controller's onDetach() and LifecycleObserver's onPause() methods were called."))
                .pushChangeHandler(HorizontalChangeHandler())
                .popChangeHandler(HorizontalChangeHandler()))
    }

    companion object {

        private val TAG = "ArchLifecycleController"
    }

}
