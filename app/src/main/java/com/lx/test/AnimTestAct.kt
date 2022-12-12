package com.lx.test

import android.graphics.drawable.GradientDrawable
import com.lx.test.base.BaseAct
import com.lx.test.base.DefaultManager
import com.lx.test.base.DefaultVm
import com.lx.test.databinding.MainActAnimTestBinding
import com.lx.test.databinding.MainActBinding
import com.lx.test.manager.MainManager
import com.lx.test.vm.MainVm

/**
 *  date: 2022/12/9
 *  version: 1.0
 *  desc:
 */
class AnimTestAct : BaseAct<MainActAnimTestBinding, DefaultVm, DefaultManager<DefaultVm>>(R.layout.main_act_anim_test) {
    override fun onCreateAfter() {
    getB().btn1.setOnClickListener { getB().viewCircle.startAnim() }
    }
}