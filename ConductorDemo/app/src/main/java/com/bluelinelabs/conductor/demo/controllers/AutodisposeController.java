package com.bluelinelabs.conductor.demo.controllers;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.ControllerChangeType;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.autodispose.ControllerScopeProvider;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.bluelinelabs.conductor.demo.ActionBarProvider;
import com.bluelinelabs.conductor.demo.DemoApplication;
import com.bluelinelabs.conductor.demo.R;
import com.uber.autodispose.LifecycleScopeProvider;
import com.uber.autodispose.ObservableScoper;

import java.util.concurrent.TimeUnit;

//import butterknife.BindView;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

// Shamelessly borrowed from the official RxLifecycle demo by Trello and adapted for Conductor Controllers
// instead of Activities or Fragments.
public class AutodisposeController extends Controller {

    private static final String TAG = "AutodisposeController";

//    @BindView(R.id.tv_title)
    TextView tvTitle;

//    private Unbinder unbinder;
    private boolean hasExited;
    private final LifecycleScopeProvider scopeProvider = ControllerScopeProvider.from(this);

    public AutodisposeController() {
        Observable.interval(1, TimeUnit.SECONDS)
                .doOnDispose(new Action() {
                    @Override
                    public void run() {
                        Log.i(TAG, "Disposing from constructor");
                    }
                })
                .to(new ObservableScoper<Long>(scopeProvider))
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long num) {
                        Log.i(TAG, "Started in constructor, running until onDestroy(): " + num);
                    }
                });
    }

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        Log.i(TAG, "onCreateView() called");

        View view = inflater.inflate(R.layout.controller_lifecycle, container, false);
        view.setBackgroundColor(ContextCompat.getColor(container.getContext(), R.color.purple_300));
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
        Observable.interval(1, TimeUnit.SECONDS)
                .doOnDispose(new Action() {
                    @Override
                    public void run() {
                        Log.i(TAG, "Disposing from onCreateView()");
                    }
                })
                .to(new ObservableScoper<Long>(scopeProvider))
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long num) {
                        Log.i(TAG, "Started in onCreateView(), running until onDestroyView(): " + num);
                    }
                });

        return view;
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);

        Log.i(TAG, "onAttach() called");

        (((ActionBarProvider) getActivity()).getSupportActionBar()).setTitle("Autodispose Demo");

        Observable.interval(1, TimeUnit.SECONDS)
                .doOnDispose(new Action() {
                    @Override
                    public void run() {
                        Log.i(TAG, "Disposing from onAttach()");
                    }
                })
                .to(new ObservableScoper<Long>(scopeProvider))
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long num) {
                        Log.i(TAG, "Started in onAttach(), running until onDetach(): " + num);
                    }
                });
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

        getRouter().pushController(RouterTransaction.with(new TextController("Logcat should now report that the observables from onAttach() and onViewBound() have been disposed of, while the constructor observable is still running."))
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }

//    @OnClick(R.id.btn_next_retain_view)
    void onNextWithRetainClicked() {
        setRetainViewMode(RetainViewMode.RETAIN_DETACH);

        getRouter().pushController(RouterTransaction.with(new TextController("Logcat should now report that the observables from onAttach() has been disposed of, while the constructor and onViewBound() observables are still running."))
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }
}
