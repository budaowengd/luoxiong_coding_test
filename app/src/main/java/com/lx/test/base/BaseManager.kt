package com.lx.test.base

import android.app.Activity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*

/**
 *  date: 2022/12/7
 *  version: 1.0
 *  desc: Manager专门处理Activity和Fragment的业务逻辑，生命周期也和Activity和Fragment平级。
 */
abstract class BaseManager<VM : ViewModel> {
    private lateinit var mAct: FragmentActivity
    private lateinit var mModel: VM
    private var mFrag: Fragment? = null

    // 创建Manager
    fun <M : BaseManager<VM>, VM : BaseVm> createManager(
        managerClass: Class<M>,
        modelClass: Class<VM>
    ): M {
        val manager = managerClass.newInstance()
        manager.setFragment(mFrag)
            .setActivity(getAct())
        val vm = ViewModelProvider(getViewModelStoreOwner()).get(modelClass)
        manager.setViewModel(vm)
        return manager
    }

    fun getViewModelStoreOwner(): ViewModelStoreOwner {
        if (getFragOrNull() is ViewModelStoreOwner) {
            return getFragOrNull()!!
        }
        return getAct()
    }

    fun getVm(): VM {
        return mModel
    }

    fun getBaseVm(): BaseVm {
        return mModel as BaseVm
    }

    fun setActivity(activity: FragmentActivity): BaseManager<VM> {
        mAct = activity
        return this
    }

    fun setViewModel(model: VM): BaseManager<VM> {
        mModel = model
        return this
    }

    fun setFragment(frag: Fragment?): BaseManager<VM> {
        mFrag = frag
        return this
    }

    fun <T> getFrag(): T? {
        return mFrag as? T
    }

    fun getLifecycle(): Lifecycle {
        if (mFrag != null) {
            return mFrag!!.lifecycle
        }
        return mAct.lifecycle
    }

    fun getLifecycleOwner(): LifecycleOwner {
        if (mFrag == null) {
            return mAct
        }
        return mFrag!!.viewLifecycleOwner
    }

    fun getScope(): LifecycleCoroutineScope {
        // 当view为空, 获取 Scope 会抛异常
        if (mFrag == null || mFrag!!.view == null) return mAct.lifecycleScope

        return mFrag!!.viewLifecycleOwner.lifecycle.coroutineScope
    }

    fun getAct(): FragmentActivity {
        return mAct
    }

    fun requireActivity(): Activity {
        return mAct
    }

    fun getFragAct(): FragmentActivity {
        return mAct
    }

    fun getFragOrNull(): Fragment? {
        return mFrag
    }

    fun getFragClass(): Class<out Fragment>? {
        if (mFrag == null) return null
        return mFrag!!::class.java
    }

    fun getFrag(): Fragment {
        return mFrag!!
    }

    fun getBaseActOrNull(): BaseAct<*, *, *>? {
        return mAct as? BaseAct<*, *, *>
    }


    fun getBaseAct(): BaseAct<*, *, *> {
        return mAct as BaseAct<*, *, *>
    }
}

/**
 * 如果某个Activity或者Fragment业务逻辑很少，不想单独抽出来，可以用该类作为默认的。
 */
class DefaultManager<VM : ViewModel> : BaseManager<VM>()

/**
 * Fragment对应的业务逻辑基类，在该类中可以获取对应Fragment的View和Activity的引用。
 */
abstract class AbstractFragManager<F : Fragment, B : ViewDataBinding, VM : ViewModel> : BaseManager<VM>() {
    abstract fun initFragView(frag: F, b: B)
    private var mBinding: B? = null
    fun getB(): B {
        return mBinding!!
    }

    fun setBinding(b: B): AbstractFragManager<F, B, VM> {
        mBinding = b
        return this
    }

    fun getRealFrag(): F {
        return super.getFrag() as F
    }
}

/**
 * Activity对应的业务逻辑基类，在该类中可以获取对应Activity的View。
 */
abstract class AbstractActManager<A : FragmentActivity, B : ViewDataBinding, VM : ViewModel> : BaseManager<VM>() {
    abstract fun initActView(act: A, b: B)
    private var mBinding: B? = null

    fun getB(): B {
        return mBinding!!
    }

    fun setBinding(b: B): AbstractActManager<A, B, VM> {
        mBinding = b
        return this
    }

    fun getRealAct(): A {
        return super.getAct() as A
    }
}


