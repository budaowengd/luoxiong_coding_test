package com.lx.test.act

import com.lx.test.R
import com.lx.test.api.MainApi
import com.lx.test.base.BaseAct
import com.lx.test.databinding.MainActBinding
import com.lx.test.http.createApi
import com.lx.test.manager.MainManager
import com.lx.test.vm.MainVm
import com.lx.test.vo.PostItemVo
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

/**
 *  date: 2022/12/8
 *  version: 1.0
 *  desc: 首页
 */
class MainAct : BaseAct<MainActBinding, MainVm, MainManager>(R.layout.main_act) {

    override fun onCreateAfter() {
        // 请求列表接口
        render(MainActState.NewsList())
    }

    /**
     * 所有的网络请求都基于该方法进行分发
     */
    private fun render(state: MainActState) {
        when (state) {
            // 请求列表接口
            is MainActState.NewsList -> {
                getScope().launch(Main) {
                    // 显示非阻塞加载中
                    getB().stateLayout.showLoading()
                    runCatching {
                        createApi<MainApi>().queryPosts()
                    }.onSuccess {
                        // 显示内容
                        getB().stateLayout.showContent()
                        // 初始化页面
                        getM().initTabLayoutAndRefreshPage(it)
                    }.onFailure {
                        // 显示错误布局
                        getB().stateLayout.showError {
                            // 点击重试后，重新请求接口
                            render(state)
                        }
                    }
                }
            }
        }
    }

    fun getPostVos(): List<PostItemVo> {
        return getVm().postVoList
    }
}

sealed class MainActState {
    class NewsList : MainActState()
}