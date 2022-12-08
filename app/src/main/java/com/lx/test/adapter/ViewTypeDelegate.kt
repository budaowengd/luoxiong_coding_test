package com.lx.test.adapter

import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView


/**
 *  date: 2022/12/8
 *  version: 1.0
 *  desc: 同一种ViewType的每个Item对View的处理委托（接口）。一种 viewType 对应一个 ViewTypeDelegate 对象。
 */
interface ViewTypeDelegate<B : ViewDataBinding, M : IViewTypeModel> {
    /**
     * viewType满足某添加后委托生效。使用时写跟 M 的 getViewType() 值相同。
     * @return viewType。
     */
    @LayoutRes
    fun getViewType(): Int

    /**
     * 当创建ViewHolder时。做什么事。
     * @param binding ViewDataBinding
     */
    fun onCreateVH(b: B) {

    }

    /**
     * 当Bind ViewHolder时。做什么事。
     * @param binding ViewDataBinding
     * @param m 具体的Model。
     * @param position 位置。
     */
    fun onBindVH(holder: RecyclerView.ViewHolder, b: B, m: M, position: Int) {

    }
}