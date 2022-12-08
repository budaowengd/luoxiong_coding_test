package com.lx.test.adapter

import android.util.SparseArray
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

/**
 *  date: 2022/12/8
 *  version: 1.0
 *  desc: 不同布局的业务逻辑处理
 */
class ViewTypeDelegateManager<M : IViewTypeModel> {

    /**
     *  一种viewType 只会对应一个 ViewTypeDelegate 。
     *
     * @see SparseArray  key: ViewType, Value: ViewTypeDelegate
     */
    private val mVtdMap = SparseArray<ViewTypeDelegate<ViewDataBinding, M>>(1)

    /**
     * 当创建 ViewHolder时。getViewType=viewType 的 ViewTypeDelegate 响应执行 onCreateVH 方法。
     * @param binding ViewDataBinding
     * @param viewType viewType
     */
    fun onCreateViewHolder(binding: ViewDataBinding, @LayoutRes viewType: Int) {
        if (mVtdMap.size() == 0) return
        mVtdMap.get(viewType)?.onCreateVH(binding)
    }

    /**
     * 当Bind ViewHolder时。getViewType=viewType 的 ViewTypeDelegate 响应执行 onBindVH 方法。
     * @param b ViewDataBinding
     * @param m
     */
    fun onBindViewHolder(holder: RecyclerView.ViewHolder, b: ViewDataBinding, m: M, position: Int) {
        if (mVtdMap.size() == 0) return
        mVtdMap.get(m.getViewType())?.onBindVH(holder, b, m, position)
    }

    /**
     * 添加ItemViewDelegate。
     *
     * @param vtd ViewTypeDelegate
     */
    @Suppress("UNCHECKED_CAST")
    fun <V : ViewDataBinding, Y : M> add(vtd: ViewTypeDelegate<V, Y>): ViewTypeDelegateManager<M> {
        mVtdMap.put(vtd.getViewType(), vtd as ViewTypeDelegate<ViewDataBinding, M>)
        return this
    }

    @Suppress("UNCHECKED_CAST")
    fun <V : ViewDataBinding, Y : M> addAll(vtdList: List<ViewTypeDelegate<V, Y>>): ViewTypeDelegateManager<M> {
        vtdList.forEach {
            add(it)
        }
        return this
    }

    /**
     * 消除所有ItemViewDelegate。
     */
    fun clear() {
        mVtdMap.clear()
    }
}