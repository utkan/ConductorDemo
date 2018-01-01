package com.bluelinelabs.conductor.demo.controllers

import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.demo.R
import com.bluelinelabs.conductor.demo.controllers.base.BaseController
import com.bluelinelabs.conductor.support.RouterPagerAdapter
import java.util.*

class PagerController : BaseController() {

    private val pageColors = intArrayOf(R.color.green_300, R.color.cyan_300, R.color.deep_purple_300, R.color.lime_300, R.color.red_300)

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager

    private val pagerAdapter: RouterPagerAdapter

    override val title: String?
        get() = "ViewPager Demo"

    init {
        pagerAdapter = object : RouterPagerAdapter(this) {
            override fun configureRouter(router: Router, position: Int) {
                if (!router.hasRootController()) {
                    val page = ChildController(String.format(Locale.getDefault(), "Child #%d (Swipe to see more)", position), pageColors[position], true)
                    router.setRoot(RouterTransaction.with(page))
                }
            }

            override fun getCount(): Int {
                return pageColors.size
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return "Page " + position
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflateView(inflater, container)
        onViewBound(view)
        return view
    }

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.controller_pager, container, false)
    }

    override fun onViewBound(view: View) {
        super.onViewBound(view)
        tabLayout = view.findViewById(R.id.tab_layout)
        viewPager = view.findViewById(R.id.view_pager)
        viewPager.adapter = pagerAdapter
        tabLayout.setupWithViewPager(viewPager)
    }

    override fun onDestroyView(view: View) {
        if (!activity!!.isChangingConfigurations) {
            viewPager.adapter = null
        }
        tabLayout.setupWithViewPager(null)
        super.onDestroyView(view)
    }

}
