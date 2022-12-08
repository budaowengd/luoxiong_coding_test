package com.lx.test.adapter


/**
 *  date: 2022/12/8
 *  version: 1.0
 *  desc: 适配器列表布局的点击事件接口
 */
interface ClickListener

/**
 *  适配器列表布局的点击事件具体实现，通过flag参数可以区分item布局里不同View的点击事件
 */
interface BaseRvFun2ItemClickEvent<T, R> : ClickListener {
    fun clickRvItem(item: T, flag: R)
}