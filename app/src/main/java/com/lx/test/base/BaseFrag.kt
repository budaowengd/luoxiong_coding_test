package com.lx.test.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import com.lx.test.BR
import kotlinx.coroutines.CoroutineScope
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 *  date: 2022/10/21
 *  version: 1.0
 *  desc: Fragment的基类
 */
abstract class BaseFrag<B : ViewDataBinding, VM : BaseVm, M : BaseManager<VM>>(private val layoutId: Int) : Fragment(){

    protected abstract fun onViewCreatedAfter()

    private var mBinding: B? = null
    private lateinit var mModel: VM
    private lateinit var mManager: M
    protected var isFirstResume = false

    open fun getB(): B {
        return mBinding!!
    }

    fun getM(): M {
        return mManager
    }

    fun getVm(): VM {
        return mModel
    }

    open fun getRootView(): View? {
        return mBinding?.root
    }

    /**
     * 不使用状态布局作为根布局
     */
    open fun noUseStatusLayoutAtRoot(): Boolean {
        return false
    }

    private lateinit var act: FragmentActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        act = requireActivity()
    }

    fun getAct(): FragmentActivity {
        return act
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        mBinding!!.lifecycleOwner = viewLifecycleOwner
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val argsType = getActualTypeArguments()
        createViewModel(argsType)
        createManager(argsType)
        onParseArgument()
        // 必须放在 initSetVariable()上面
        initView()
        (mManager as? AbstractFragManager<Fragment, ViewDataBinding, ViewModel>)?.initFragView(this, mBinding!!)
        initSetVariable()
        onViewCreatedAfter()
    }



    override fun getViewLifecycleOwner(): LifecycleOwner {
        return try {
            // 因为在onCreate获取Owner, 会抛出空指针异常
            super.getViewLifecycleOwner()
        } catch (e: IllegalStateException) {
            getAct()
        }
    }

    @Suppress("UNCHECKED_CAST")
    protected fun createViewModel(argsType: Array<out Type>) {
        val modelClass = argsType[1] as Class<VM>
        mModel = ViewModelProvider(this).get(modelClass)
    }

    private fun initSetVariable() {
        mBinding!!.setVariable(BR.vm, mModel)
    }

    @Suppress("UNCHECKED_CAST")
    private fun createManager(argsType: Array<out Type>) {
        val type = argsType[2]
        try {
            mManager = if (type is ParameterizedType) {
                (type.rawType as Class<M>).newInstance()
            } else {
                (type as Class<M>).newInstance()
            }
        } catch (e: java.lang.InstantiationException) {
            throw InstantiationException("${this.javaClass.name}  createManager() 出错")
        }
        mManager.setFragment(this).setActivity(requireActivity()).setViewModel(mModel) as M
        if (mManager is AbstractFragManager<*, *, *>) {
            (mManager as AbstractFragManager<Fragment, ViewDataBinding, ViewModel>).setBinding(mBinding!!)
        }
    }

    fun getBNull(): B? {
        return mBinding
    }

    override fun onResume() {
        super.onResume()
        if (!isFirstResume) {
            isFirstResume = true
            onFragmentResume(true)
        } else {
            onFragmentResume(false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding?.unbind()
        mBinding = null
    }

    private fun getActualTypeArguments(): Array<out Type> {
        val type = javaClass.genericSuperclass!! as ParameterizedType
        return type.actualTypeArguments
    }


    protected fun getLifecycleOwner(): LifecycleOwner {
        return viewLifecycleOwner
    }
    /**
     * Fragment 可见回调
     *
     * @param first    是否首次调用
     */
    protected open fun onFragmentResume(first: Boolean) {}

    protected open fun onParseArgument() {}
    protected open fun initView() {}

    fun getScope(): CoroutineScope {
        // 当view为空, 获取 Scope 会抛异常
        if (view == null) return getAct().lifecycle.coroutineScope
        return viewLifecycleOwner.lifecycle.coroutineScope
    }
}