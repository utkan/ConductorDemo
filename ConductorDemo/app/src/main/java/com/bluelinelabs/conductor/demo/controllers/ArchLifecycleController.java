package com.bluelinelabs.conductor.demo.controllers;

import android.arch.lifecycle.Lifecycle.Event;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.ControllerChangeType;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.archlifecycle.LifecycleController;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.bluelinelabs.conductor.demo.ActionBarProvider;
import com.bluelinelabs.conductor.demo.DemoApplication;
import com.bluelinelabs.conductor.demo.R;

//import butterknife.BindView;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//import butterknife.Unbinder;

public class ArchLifecycleController extends LifecycleController {

    private static final String TAG = "ArchLifecycleController";

//    @BindView(R.id.tv_title)
    TextView tvTitle;

//    private Unbinder unbinder;
    private boolean hasExited;

    public ArchLifecycleController() {
        LifecycleObserver lifecycleObserver = new LifecycleObserver() {
            @OnLifecycleEvent(Event.ON_CREATE)
            void onCreate() {
                Log.d(TAG, "LifecycleObserver onCreate() called");
            }

            @OnLifecycleEvent(Event.ON_START)
            void onStart() {
                Log.d(TAG, "LifecycleObserver onStart() called");
            }

            @OnLifecycleEvent(Event.ON_RESUME)
            void onResume() {
                Log.d(TAG, "LifecycleObserver onResume() called");
            }

            @OnLifecycleEvent(Event.ON_PAUSE)
            void onPause() {
                Log.d(TAG, "LifecycleObserver onPause() called");
            }

            @OnLifecycleEvent(Event.ON_STOP)
            void onStop() {
                Log.d(TAG, "LifecycleObserver onStop() called");
            }

            @OnLifecycleEvent(Event.ON_DESTROY)
            void onDestroy() {
                Log.d(TAG, "LifecycleObserver onDestroy() called");
            }
        };

        Log.i(TAG, "constructor called");

        getLifecycle().addObserver(lifecycleObserver);
    }

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        Log.i(TAG, "onCreateView() called");

        View view = inflater.inflate(R.layout.controller_lifecycle, container, false);
        view.setBackgroundColor(ContextCompat.getColor(container.getContext(), R.color.orange_300));
//        unbinder = //ButterKnife.bind(this, view);

        tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText(getResources().getString(R.string.rxlifecycle_title, TAG));

        view.findViewById(R.id.btn_next_release_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNextWithReleaseClicked();
            }
        });
        view.findViewById(R.id.btn_next_retain_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNextWithRetainClicked();
            }
        });
        return view;
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);

        Log.i(TAG, "onAttach() called");

        (((ActionBarProvider) getActivity()).getSupportActionBar()).setTitle("Arch Components Lifecycle Demo");
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        super.onDestroyView(view);

        Log.i(TAG, "onDestroyView() called");

//        unbinder.unbind();
//        unbinder = null;
    }

    @Override
    protected void onDetach(@NonNull View view) {
        super.onDetach(view);

        Log.i(TAG, "onDetach() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i(TAG, "onDestroy() called");

        if (hasExited) {
            DemoApplication.refWatcher.watch(this);
        }
    }

    @Override
    protected void onChangeEnded(@NonNull ControllerChangeHandler changeHandler, @NonNull ControllerChangeType changeType) {
        super.onChangeEnded(changeHandler, changeType);

        hasExited = !changeType.isEnter;
        if (isDestroyed()) {
            DemoApplication.refWatcher.watch(this);
        }
    }

//    @OnClick(R.id.btn_next_release_view)
    void onNextWithReleaseClicked() {
        setRetainViewMode(RetainViewMode.RELEASE_DETACH);

        getRouter().pushController(RouterTransaction.with(new TextController("Logcat should now report that the Controller's onDetach() and LifecycleObserver's onPause() methods were called, followed by the Controller's onDestroyView() and LifecycleObserver's onStop()."))
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }

//    @OnClick(R.id.btn_next_retain_view)
    void onNextWithRetainClicked() {
        setRetainViewMode(RetainViewMode.RETAIN_DETACH);

        getRouter().pushController(RouterTransaction.with(new TextController("Logcat should now report that the Controller's onDetach() and LifecycleObserver's onPause() methods were called."))
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }

}
