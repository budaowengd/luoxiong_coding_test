package com.lx.test.base.views

import android.content.Context
import android.content.res.Resources
import android.os.Handler
import android.os.Looper
import android.util.ArrayMap
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.core.util.Consumer
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.blankj.utilcode.util.LogUtils
import com.lx.test.R
import com.lx.test.base.state.StateChangedHandler
import com.lx.test.base.state.StateLayoutConfig

/**
 *  date: 2022/10/21
 *  version: 1.0
 *  desc: 状态布局相关的类
 */
enum class StatusEnum {
    LOADING, ERROR, CONTENT
}

/**
 * 状态View的信息
 */
data class StatusViewInfo(var b: ViewDataBinding, var tag: Any?) {
    fun setRootViewMarginTop(px: Int) {
        val params = b.root.layoutParams
        if (params is ViewGroup.MarginLayoutParams && params.topMargin != px) {
            params.topMargin = px
            b.root.parent?.requestLayout()// 必须要调用，才能让布局生效
        }
    }
}

class StateLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val statusContainer = ArrayMap<StatusEnum, StatusViewInfo>()

    private var onErrorViewCreatedCb: ((info: StatusViewInfo) -> Unit)? = null
        get() = field ?: StateLayoutConfig.onErrorCb

    private var onLoadingCb: ((info: StatusViewInfo) -> Unit)? = null
        get() = field ?: StateLayoutConfig.onLoadingCb

    /** 错误页面布局 */
    @LayoutRes
    var errorLayout: Int = NO_ID
        get() = if (field == NO_ID) StateLayoutConfig.errorLayout else field
        set(value) {
            if (field != value) {
                removeStatus(StatusEnum.ERROR)
                field = value
            }
        }

    /** 加载中页面布局 */
    @LayoutRes
    var loadingLayout: Int = NO_ID
        get() = if (field == NO_ID) StateLayoutConfig.loadingLayout else field
        set(value) {
            if (field != value) {
                removeStatus(StatusEnum.LOADING)
                field = value
            }
        }

    /** 处理缺省页状态变更 */
    var stateChangedHandler: StateChangedHandler? = null
        get() {
            return field ?: StateLayoutConfig.stateChangedHandler ?: StateChangedHandler
        }

    /** 当前缺省页状态[Status] */
    var currentStatusEnum = StatusEnum.CONTENT
        private set

    private var mErrorLayoutClickBtnCb: ((viewId: Int) -> Unit)? = null

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.StateLayout)
        errorLayout = a.getResourceId(R.styleable.StateLayout_error_layout, NO_ID)
        loadingLayout = a.getResourceId(R.styleable.StateLayout_loading_layout, NO_ID)
        a.recycle()
    }

    /**
     * 回调指的是显示错误布局的时候执行block
     */
    fun showError(viewCreatedCb: ((info: StatusViewInfo) -> Unit)? = null, clickBtnViewIdCb: ((viewId: Int) -> Unit)? = null) {
        onErrorViewCreatedCb = viewCreatedCb
        mErrorLayoutClickBtnCb = clickBtnViewIdCb
        showStatus(StatusEnum.ERROR)
    }

    fun showLoading(viewCreatedCb: ((info: StatusViewInfo) -> Unit)? = null) {
        onLoadingCb = viewCreatedCb
        showStatus(StatusEnum.LOADING)
    }

    fun showContent() {
        showStatus(StatusEnum.CONTENT)
    }

    fun isShowingError(): Boolean {
        return currentStatusEnum == StatusEnum.ERROR
    }


    /**
     * 显示视图
     */
    private fun showStatus(status: StatusEnum, tag: Any? = null) {
        LogUtils.d("显示视图()..111..准备显示status=$status  上次=${this.currentStatusEnum}")
        if (this.currentStatusEnum == status) return
        val previousStatus = this.currentStatusEnum
        this.currentStatusEnum = status
        runMain {
            try {
                // 如果显示内容布局, 把其他布局隐藏
                if (status == StatusEnum.CONTENT) {
                    hideAllWithoutContent()
                } else {
                    val viewInfo = getStatusView(status, tag)
                    statusContainer.filter { it.key != status }.forEach { map ->
                        if (map.key == previousStatus) {
                            val info = map.value
                            stateChangedHandler?.onRemove(this, map.key, info)
                        }
                    }
                    stateChangedHandler?.onAdd(this, status, viewInfo)
                    if (status == StatusEnum.ERROR) {
                        StateLayoutConfig.retryIds.forEach { idRInt ->
                            viewInfo.b.root.findViewById<View>(idRInt)?.setOnClickListener {
                                currentStatusEnum = StatusEnum.CONTENT
                                hideAllWithoutContent()
                                if (status == StatusEnum.ERROR) {
                                    mErrorLayoutClickBtnCb?.invoke(idRInt)
                                }
                                // viewInfo.clickBtnCb?.run()
                            }
                        }
                    }
                    when (status) {
                        StatusEnum.ERROR -> onErrorViewCreatedCb?.invoke(viewInfo)
                        StatusEnum.LOADING -> onLoadingCb?.invoke(viewInfo)
                        else -> {}
                        // StatusEnum.CONTENT -> onLoadingCb?.invoke(viewInfo)
                    }
                }
            } catch (e: Exception) {
                Log.e(javaClass.simpleName, "", e)
            }
        }
    }

    /**
     * 隐藏所有视图, 除了内容
     */
    private fun hideAllWithoutContent() {
        statusContainer.forEach { map ->
            if (map.key == StatusEnum.CONTENT) {
                stateChangedHandler?.onAdd(this, map.key, map.value)
            } else {
                stateChangedHandler?.onRemove(this, map.key, map.value)
            }
        }
    }

    /**
     * 删除指定的缺省页，只有重新设置布局的时候，才会执行
     */
    private fun removeStatus(currentStatusEnum: StatusEnum) {
        statusContainer.remove(currentStatusEnum)
    }

    /**
     * 返回缺省页视图对象
     */
    @Throws(NullPointerException::class)
    private fun getStatusView(statusEnum: StatusEnum, tag: Any?): StatusViewInfo {
        statusContainer[statusEnum]?.let {
            it.tag = tag
            return it
        }
        val layoutId = when (statusEnum) {
            StatusEnum.ERROR -> errorLayout
            StatusEnum.LOADING -> loadingLayout
            else -> NO_ID
            // StatusEnum.CONTENT -> NO_ID
        }
        if (layoutId == NO_ID) {
            when (statusEnum) {
                StatusEnum.ERROR -> throw Resources.NotFoundException("No StateLayout errorLayout is set")
                StatusEnum.LOADING -> throw Resources.NotFoundException("No StateLayout loadingLayout is set")
                else -> throw Resources.NotFoundException("No StateLayout Layout is set")
                // StatusEnum.CONTENT -> throw Resources.NotFoundException("No StateLayout contentView is set")
            }
        }
        val b = DataBindingUtil.inflate<ViewDataBinding>(LayoutInflater.from(context), layoutId, this, false)
        val statusViewInfo = StatusViewInfo(b, tag)
        statusContainer[statusEnum] = statusViewInfo
        return statusViewInfo
    }

    /**
     * 保证运行在主线程
     */
    private fun runMain(block: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            block()
        } else {
            Handler(Looper.getMainLooper()).post { block() }
        }
    }
}