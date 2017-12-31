package com.bluelinelabs.conductor.demo

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.demo.controllers.HomeController
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), ActionBarProvider {
    override fun supportActionBar(): ActionBar? {
        return supportActionBar
    }

    private var router: Router? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        router = Conductor.attachRouter(this, controller_container, savedInstanceState)
        if (!router!!.hasRootController()) {
            router!!.setRoot(RouterTransaction.with(HomeController()))
        }
    }

    override fun onBackPressed() {
        if (!router!!.handleBack()) {
            super.onBackPressed()
        }
    }

}
