package com.lx.test.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import com.lx.test.BR
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableArrayList
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView



/**
 *  date: 2022/12/8
 *  version: 1.0
 *  desc: 基于Databinding封装的适配器基类
 */
abstract class BaseRvAdapter : RecyclerView.Adapter<XRvBindingViewHolder>() {
    // 数据源
    private val items: ObservableArrayList<IViewTypeModel> = ObservableArrayList()

    // 针对每一种ViewType的业务处理
    protected val mVtdManager = ViewTypeDelegateManager<IViewTypeModel>()
    protected var mInflater: LayoutInflater? = null
    private var itemIds: ItemIds? = null
    protected var mLifecycleOwner: LifecycleOwner? = null
    protected var _itemClickEvent2: BaseRvFun2ItemClickEvent<*, *>? = null

    open fun onCreateVHForAll(holder: XRvBindingViewHolder, b: ViewDataBinding, viewType: Int) {}

    open fun onBindVHForAll(b: ViewDataBinding, m: IViewTypeModel, position: Int) {}

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        addViewTypeDelegate(mVtdManager)
        super.onAttachedToRecyclerView(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        mVtdManager.clear()
        super.onDetachedFromRecyclerView(recyclerView)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    @LayoutRes
    override fun getItemViewType(position: Int): Int {
        return getItem(position).getViewType()
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        @LayoutRes viewType: Int
    ): XRvBindingViewHolder {
        tryGetLifecycleOwner(parent)
        val holder = createVH(parent, viewType)
        holder.b.setVariable(BR.click, _itemClickEvent2)
        holder.b.lifecycleOwner = mLifecycleOwner
        onCreateVHForAll(holder, holder.b, viewType)
        mVtdManager.onCreateViewHolder(holder.b, viewType)
        return holder
    }

    override fun onBindViewHolder(holder: XRvBindingViewHolder, position: Int) {
        val model = getItem(position)
        holder.b.setVariable(BR.item, model)
        onBindVHForAll(holder.b, model, position)
        mVtdManager.onBindViewHolder(holder, holder.b, model, position)
        holder.b.executePendingBindings()
    }

    private fun tryGetLifecycleOwner(view: View) {
        if (mLifecycleOwner == null || mLifecycleOwner!!.lifecycle.currentState == Lifecycle.State.DESTROYED) {
            mLifecycleOwner = findLifecycleOwner(view);
        }
    }

    private fun findLifecycleOwner(view: View): LifecycleOwner? {
        val binding = DataBindingUtil.findBinding<ViewDataBinding>(view)
        var lifecycleOwner: LifecycleOwner? = null
        if (binding != null) {
            lifecycleOwner = binding.lifecycleOwner
        }
        val ctx: Context = view.context
        if (lifecycleOwner == null && ctx is LifecycleOwner) {
            lifecycleOwner = ctx
        }
        return lifecycleOwner
    }

    /**
     * 初始化各种 viewType 处理委托。添加到 Manager 中。
     */
    protected open fun addViewTypeDelegate(manager: ViewTypeDelegateManager<IViewTypeModel>) {

    }

    fun getItem(position: Int): IViewTypeModel {
        return items[position]
    }

    /**
     * 获取数据。
     */
    open fun <T:IViewTypeModel> getItems(): List<T> {
        return items as List<T>
    }

    /**
     * 设置数据
     */
    @SuppressLint("NotifyDataSetChanged")
    open fun setItems(list: List<IViewTypeModel>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    /**
     * 添加数据
     */
    fun addItems(list: List<IViewTypeModel>) {
        val preSize = items.size
        items.addAll(list)
        notifyItemRangeInserted(preSize, list.size)
    }

    /**
     * 只刷新局部数据。
     */
    fun changeItem(list: List<IViewTypeModel>) {
        for (m in list) {
            val index = items.indexOf(m)
            if (index >= 0) {
                notifyItemChanged(index)
            }
        }
    }

    /**
     * 只移除局部数据。
     */
    fun removeItem(list: List<IViewTypeModel>) {
        for (m in list) {
            if (m in items) {
                val index = items.indexOf(m)
                items.remove(m)
                notifyItemRemoved(index)
            }
        }
    }

    /**
     * 只移除局部数据。
     */
    fun removeItem(vararg ms: IViewTypeModel) {
        removeItem(ms.toList())
    }
    /**
     * 设置数据
     */
//    fun setItems(vararg ms: IViewTypeModel) {
//        setItems(ms.toList())
//    }


    /**
     * 清空数据
     */
    @SuppressLint("NotifyDataSetChanged")
    fun clearData() {
        if (items.isNotEmpty()) {
            items.clear()
            notifyDataSetChanged()
        }
    }

    /**
     * 清空数据
     */
    @SuppressLint("NotifyDataSetChanged")
    fun clearDataAndAdd(list: List<IViewTypeModel>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    protected open fun createVH(parent: ViewGroup, @LayoutRes viewType: Int): XRvBindingViewHolder {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(parent.context)
        }
        val b = DataBindingUtil.inflate<ViewDataBinding>(
            mInflater!!, viewType, parent, false
        )
        return XRvBindingViewHolder(b)
    }

    fun setItemClickEvent(clickEvent: BaseRvFun2ItemClickEvent<*, *>?): BaseRvAdapter {
        _itemClickEvent2 = clickEvent
        return this
    }

    /**
     * List数据结构需要改变时重写。即传入 List 经过变化后变成 items 需要的list。默认不改变结构。
     */
//    @Suppress("UselessCallOnCollection")
//    protected open fun multiList(list: List<IViewTypeModel>): List<IViewTypeModel> {
//        return list.filterNotNull()
//    }
    /**
     * IllegalStateException -> Cannot change whether this adapter has stable IDs while the adapter has registered observers
     * Set the item id's for the items. If not null, this will set [ ][RecyclerView.Adapter.setHasStableIds] to true.
     */
    fun setItemIds(ids: ItemIds?) {
        ids ?: return
        if (itemIds == null && itemIds != ids) {
            this.itemIds = ids
            setHasStableIds(true)
        }
    }

    /**
     * 此方法只在setHasStableIds设置为true才会生效
     * 1、如果只返回position,当item位置改变,数据会错乱
     * 2、不重写的话,当前grid_span=2的布局时,item刷新会闪烁,因为item在重新测量,要么写死item的宽高解决问题
     * 3、要么返回具体的itemId.如果返回position, 列表滑动到下面,此时刷新还是会闪烁
     */
    override fun getItemId(position: Int): Long {
        if (itemIds == null) {
            return position.toLong()
        }
        return itemIds!!.getItemId(position, getItem(position))
    }


    interface ItemIds {
        fun getItemId(position: Int, item: IViewTypeModel): Long
    }
}