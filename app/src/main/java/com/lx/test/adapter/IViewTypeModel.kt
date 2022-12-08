package com.lx.test.adapter

import androidx.annotation.LayoutRes


/**
 *  date: 2022/12/8
 *  version: 1.0
 *  desc: 适配器模型接口
 */
interface IViewTypeModel {
    /**
     * 获取viewType，把 layout id 当作 viewType。
     */
    @LayoutRes
    fun getViewType(): Int
}