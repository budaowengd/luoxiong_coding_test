package com.lx.test.base

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import com.blankj.utilcode.util.AdaptScreenUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


/**
 *  date: 2022/10/21
 *  version: 1.0
 *  desc: Activity的基类
 */
abstract class BaseAct<B : ViewDataBinding, VM : BaseVm, M : BaseManager<VM>>(private val layoutId: Int) : AppCompatActivity() {

    abstract fun onCreateAfter()
    protected var mBinding: B? = null
    private lateinit var mModel: VM
    private lateinit var mManager: M

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //保持屏幕不暗和竖屏
        keepPortraitOrientationAndScreenOn()
        mBinding = DataBindingUtil.setContentView(this, layoutId)
        mBinding!!.lifecycleOwner = this
        val argsType = getActualTypeArguments()
        createViewModel(argsType)
        createManager(argsType)
        onParseIntent()
        initSetVariable()
        initManagerActView()
        initView()
        initListener()
        onCreateAfter()
    }

    fun getAct(): BaseAct<B, VM, M> {
        return this
    }

    fun getB(): B {
        return mBinding!!
    }

    fun getBOrNull(): B? {
        return mBinding
    }

    fun getM(): M {
        return mManager
    }

    fun getVm(): VM {
        return mModel
    }

    fun getViewLifecycleOwner(): LifecycleOwner {
        return this
    }


    protected open fun initView() {}
    protected open fun onParseIntent() {}

    protected open fun initListener() {}

    /**
     * 设置一直竖屏
     */
    @SuppressLint("SourceLockedOrientationActivity")
    open fun keepPortraitOrientationAndScreenOn() {
        // 一直竖屏
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        // 保持屏幕不暗
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }


    private fun createViewModel(argsType: Array<out Type>) {
        val modelClass = argsType[1] as Class<VM>
        mModel = ViewModelProvider(this).get(modelClass)
    }

    private fun initSetVariable() {
        mBinding!!.setVariable(com.lx.test.BR.vm, mModel)
    }

    @Suppress("UNCHECKED_CAST")
    private fun createManager(argsType: Array<out Type>) {
        val type = argsType[2]
        mManager = if (type is ParameterizedType) {
            (type.rawType as Class<M>).newInstance()
        } else {
            (type as Class<M>).newInstance()
        }
        mManager.setActivity(this).setViewModel(mModel)
    }

    private fun initManagerActView() {
        if (mManager is AbstractActManager<*, *, *>) {
            (mManager as AbstractActManager<FragmentActivity, ViewDataBinding, ViewModel>)
                .setBinding(mBinding!!)
                .initActView(this, mBinding!!)
        }
    }


    private fun getActualTypeArguments(): Array<out Type> {
        val genericSuperclass = javaClass.genericSuperclass!!
        val type = genericSuperclass as ParameterizedType
        return type.actualTypeArguments
    }

    fun getScope(): CoroutineScope {
        if (mBinding == null) {
            // 当view为空, 获取 Scope 会抛异常, 所以得返回null
            return GlobalScope
        }
        return lifecycle.coroutineScope
    }

    override fun getResources(): Resources {
        val res = super.getResources()
        if (res.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // 竖屏
            return AdaptScreenUtils.adaptWidth(res, 375)
        }
        // 横屏
        // 解决播放器横屏时尺寸不对的bug
        return AdaptScreenUtils.adaptHeight(res, 375)
    }

    override fun onDestroy() {
        mBinding?.unbind()
        mBinding = null
        super.onDestroy()
    }
}
