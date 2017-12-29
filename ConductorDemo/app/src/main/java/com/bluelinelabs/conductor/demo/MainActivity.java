package com.bluelinelabs.conductor.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.Conductor;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.demo.controllers.HomeController;

////import butterknife.BindView;
////import butterknife.ButterKnife;

public final class MainActivity extends AppCompatActivity implements ActionBarProvider {

    //@BindView(R.id.toolbar)
    Toolbar toolbar;
    //@BindView(R.id.controller_container)
    ViewGroup container;

    private Router router;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
//        //ButterKnife.bind(this);

        toolbar = findViewById(R.id.toolbar);
        container = findViewById(R.id.controller_container);
        setSupportActionBar(toolbar);

        router = Conductor.attachRouter(this, container, savedInstanceState);
        if (!router.hasRootController()) {
            router.setRoot(RouterTransaction.with(new HomeController()));
        }
    }

    @Override
    public void onBackPressed() {
        if (!router.handleBack()) {
            super.onBackPressed();
        }
    }

}
