package com.techme.jetpack.pags.category

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.ui.text.font.Typeface
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.tabs.TabLayoutMediator
import com.techme.jetpack.ext.invokeViewBinding
import com.techme.jetpack.navigation.BaseFragment
import com.techme.jetpack.pags.home.HomeFragment
import com.techme.jetpack.plugin.runtime.NavDestination
import com.techme.jetpack.utils.AppConfig
import com.techme.jetpack_android_online.databinding.LayoutFragmentCategoryBinding

@NavDestination(type = NavDestination.NavType.Fragment, route = "category_fragment")
class CategoryFragment : BaseFragment() {
    private lateinit var mediator: TabLayoutMediator
    private val viewBinding: LayoutFragmentCategoryBinding by invokeViewBinding()
    private val categoryConfig = AppConfig.getCategory()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tabLayout = viewBinding.tabLayout
        val viewPager = viewBinding.viewPager
        // viewPager.offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
        (viewPager.getChildAt(0) as RecyclerView).layoutManager?.isItemPrefetchEnabled = false
        viewPager.adapter = object : FragmentStateAdapter(childFragmentManager, this.lifecycle) {
            override fun getItemCount(): Int {
                return categoryConfig.tabs!!.size
            }

            override fun createFragment(position: Int): Fragment {
                val tag = categoryConfig.tabs!![position].tag
                return HomeFragment.newInstance(tag)
            }
        }
        tabLayout.tabGravity = categoryConfig.tabGravity
        mediator = TabLayoutMediator(tabLayout, viewPager) {

                tab, position ->
            tab.customView = makeTabView(position)
        }
        mediator.attach()
        viewPager.registerOnPageChangeCallback(pageChangeCallback)
        viewPager.post {
            viewPager.currentItem = categoryConfig.select
        }
    }

    private val pageChangeCallback = object : OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            val childCount = viewBinding.tabLayout.childCount
            for (i in 0 until childCount) {
                val tab = viewBinding.tabLayout.getTabAt(i)
                val customView = tab?.customView as TextView
                if (tab.position == position) {
                    customView.textSize = categoryConfig.activeSize.toFloat()
                    customView.typeface = android.graphics.Typeface.DEFAULT_BOLD
                } else {
                    customView.textSize = categoryConfig.normalSize.toFloat()
                    customView.typeface = android.graphics.Typeface.DEFAULT
                }
            }
        }
    }

    private fun makeTabView(position: Int): View {
        val tabView = TextView(context)
        val state = arrayOfNulls<IntArray>(2)
        state[0] = intArrayOf(android.R.attr.state_selected)
        state[1] = intArrayOf()
        val colors = intArrayOf(
            Color.parseColor(categoryConfig.activeColor),
            Color.parseColor(categoryConfig.normalColor)
        )
        val stateList = ColorStateList(state, colors)
        tabView.setTextColor(stateList)
        tabView.gravity = Gravity.CENTER
        tabView.text = categoryConfig.tabs!![position].title
        tabView.textSize = categoryConfig.normalSize.toFloat()
        return tabView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediator.detach()
        viewBinding.viewPager.unregisterOnPageChangeCallback(pageChangeCallback)
    }

}