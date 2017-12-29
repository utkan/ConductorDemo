package com.bluelinelabs.conductor.demo.controllers;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bluelinelabs.conductor.demo.R;
import com.bluelinelabs.conductor.demo.controllers.base.BaseController;
import com.bluelinelabs.conductor.demo.util.BundleBuilder;

//import butterknife.BindView;
//import butterknife.OnClick;

public class DialogController extends BaseController {

    private static final String KEY_TITLE = "DialogController.title";
    private static final String KEY_DESCRIPTION = "DialogController.description";

//    @BindView(R.id.tv_title)
    TextView tvTitle;
//    @BindView(R.id.tv_description)
    TextView tvDescription;

    public DialogController(CharSequence title, CharSequence description) {
        this(new BundleBuilder(new Bundle())
                .putCharSequence(KEY_TITLE, title)
                .putCharSequence(KEY_DESCRIPTION, description)
                .build());
    }

    public DialogController(Bundle args) {
        super(args);
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return inflater.inflate(R.layout.controller_dialog, container, false);
    }

    @Override
    public void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        tvTitle = view.findViewById(R.id.tv_title);
        tvDescription = view.findViewById(R.id.tv_description);
        tvTitle.setText(getArgs().getCharSequence(KEY_TITLE));
        tvDescription.setText(getArgs().getCharSequence(KEY_DESCRIPTION));
        tvDescription.setMovementMethod(LinkMovementMethod.getInstance());
        view.findViewById(R.id.dismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
dismissDialog();
            }
        });
        view.findViewById(R.id.dialog_window).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
dismissDialog();
            }
        });
    }

//    @OnClick({R.id.dismiss, R.id.dialog_window})
    public void dismissDialog() {
        getRouter().popController(this);
    }

    @Override
    public boolean handleBack() {
        return super.handleBack();
    }
}
