package com.lx.test.manager

import android.content.res.ColorStateList
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import com.google.android.material.tabs.TabLayoutMediator
import com.lx.test.R
import com.lx.test.act.MainAct
import com.lx.test.base.AbstractActManager
import com.lx.test.databinding.MainActBinding
import com.lx.test.frag.PostListFrag
import com.lx.test.frag.ToPostListFragDto
import com.lx.test.vm.MainVm
import com.lx.test.vo.PostItemVo

/**
 *  date: 2022/12/7
 *  version: 1.0
 *  desc: 首页的业务逻辑处理
 */
class MainManager : AbstractActManager<MainAct, MainActBinding, MainVm>() {
    override fun initActView(act: MainAct, b: MainActBinding) {
        BarUtils.addMarginTopEqualStatusBarHeight(getB().root)
        BarUtils.setStatusBarColor(act, ColorUtils.getColor(R.color.window_bg))
        BarUtils.setStatusBarLightMode(act, true)
    }

    /**
     * 根据接口返回的分类数创建TabLayout
     */
    fun initTabLayoutAndRefreshPage(vos: List<PostItemVo>) {
        getVm().postVoList.clear()
        getVm().postVoList.addAll(vos)
        val tabLayout = getB().tabLayout
        val vp2 = getB().viewPager2
        val categories = getVm().getCategories(vos)
        tabLayout.removeAllTabs()
        if (categories.size > 1) {
            tabLayout.visibility = View.VISIBLE
        } else {
            tabLayout.visibility = View.GONE
        }
        // 禁用预加载
        vp2.offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
        vp2.adapter = object : FragmentStateAdapter(getAct()) {
            override fun getItemCount(): Int = categories.size
            override fun createFragment(position: Int): Fragment {
                val categoryName = categories.elementAt(position)
                return PostListFrag.newInstance(ToPostListFragDto(categoryName))
            }
        }
        val mediator = TabLayoutMediator(tabLayout, vp2, true, true) { tab, position ->
            // 创建tab
            val tabView = TextView(getAct())
            val states = arrayOfNulls<IntArray>(2)
            states[0] = intArrayOf(android.R.attr.state_selected)
            states[1] = intArrayOf()
            val colors = intArrayOf(ColorUtils.getColor(R.color.colorPrimary), ColorUtils.getColor(R.color.sub_text_color))
            val colorStateList = ColorStateList(states, colors)
            tabView.text = categories.elementAt(position)
            tabView.setTextColor(colorStateList)
            tabView.gravity = Gravity.CENTER
            tab.customView = tabView
        }
        //要执行这一句才是真正将两者绑定起来
        mediator.attach()
    }

}