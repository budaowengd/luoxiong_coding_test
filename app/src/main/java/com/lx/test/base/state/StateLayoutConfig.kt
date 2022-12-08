package com.lx.test.base.state

import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import com.lx.test.R
import com.lx.test.base.views.StatusViewInfo


/**
 * 全局的缺省页布局[StateLayout]配置
 */
object StateLayoutConfig {

    internal var retryIds: IntArray  = intArrayOf(R.id.btnErrorRefresh)
    internal var onEmptyCb: ((StatusViewInfo) -> Unit)? = null
    internal var onErrorCb: ((StatusViewInfo) -> Unit)? = null
    internal var onLoadingCb: ((StatusViewInfo) -> Unit)? = null
    internal var onContentCb: ((StatusViewInfo) -> Unit)? = null
    /** 错误页布局的layoutRes, 如果[StateLayout.errorLayout]设置则该属性无效 */
    @LayoutRes
    @JvmStatic
    var errorLayout = R.layout.base_state_error

    /** 加载页布局的layoutRes, 如果[StateLayout.loadingLayout]设置则该属性无效 */
    @LayoutRes
    @JvmStatic
    var loadingLayout = R.layout.base_state_pageloading

    /** 处理缺省页状态变更 */
    @JvmStatic
    var stateChangedHandler: StateChangedHandler? = null

    /**
     * 设置[setRetryIds]点击重试要求网络可用才会显示加载缺省,
     * 会回调[StateLayout.onRefresh]但不会回调[StateLayout.onLoading]
     * 为避免无网络情况下点击重试导致闪屏
     */
    var isNetworkingRetry = true

    /**
     * 全局的空缺省页显示回调
     */
    @JvmStatic
    fun onEmpty(block: (StatusViewInfo) -> Unit) {
        onEmptyCb = block
    }

    /**
     * 全局的错误缺省页显示回调
     */
    @JvmStatic
    fun onError(block: (StatusViewInfo) -> Unit) {
        onErrorCb = block
    }

    // /**
    //  * 全局的内容缺省页显示回调
    //  */
    // @JvmStatic
    // fun onContent(block: (StatusViewInfo) -> Unit) {
    //     onContent = block
    // }

    /**
     * 全局的加载中缺省页显示回调
     */
    @JvmStatic
    fun onLoading(block: (StatusViewInfo) -> Unit) {
        onLoadingCb = block
    }

    /**
     * 会为所有[StateLayout.emptyLayout]/[StateLayout.errorLayout]中的指定Id的视图对象添加一个点击事件
     * 该点击事件会触发[StateLayout.showLoading], 同时500ms内防抖动
     *
     * @see isNetworkingRetry 点击重试是否检查网络
     */
    @JvmStatic
    fun setRetryIds(@IdRes vararg ids: Int) {
        retryIds = ids
    }
}