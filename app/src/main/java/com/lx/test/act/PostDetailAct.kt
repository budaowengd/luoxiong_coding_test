package com.lx.test.act

import android.content.Intent
import android.os.Parcelable
import android.view.ViewGroup
import com.blankj.utilcode.util.*
import com.lx.test.R
import com.lx.test.base.BaseAct
import com.lx.test.base.DefaultVm
import com.lx.test.databinding.MainActPostDetaillBinding
import com.lx.test.manager.PostDetailManager
import kotlinx.parcelize.Parcelize


/**
 * 传递到文章详情页面的数据Bean
 */
@Parcelize
data class ToPostDetailActDto(
    val h5Url: String, // h5地址
    val title: String, // 页面标题
) : Parcelable

/**
 *  date: 2022/12/8
 *  version: 1.0
 *  desc: 文章详情页面
 */
class PostDetailAct : BaseAct<MainActPostDetaillBinding, DefaultVm, PostDetailManager>(R.layout.main_act_post_detaill) {
    companion object {
        fun openAct(dto: ToPostDetailActDto?) {
            val intent = Intent(Utils.getApp(), PostDetailAct::class.java)
            intent.putExtra("dto", dto)
            ActivityUtils.startActivity(intent)
        }
    }

    lateinit var intentDto: ToPostDetailActDto

    override fun onParseIntent() {
        intentDto = intent?.getParcelableExtra("dto") ?: ToPostDetailActDto("", "")
    }


    override fun onCreateAfter() {
        getB().webView.loadUrl(intentDto.h5Url)
    }

    fun doAction(action: PostDetailAction) {
        when (action) {
            is PostDetailAction.SendComment -> {
                ToastUtils.showShort(R.string.main_k8)
            }
            is PostDetailAction.CloseAct -> {
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getB().webView.onResume()
    }

    override fun onPause() {
        super.onPause()
        getB().webView.onPause()
    }

    override fun onBackPressed() {
        if (getBOrNull()?.webView?.canGoBack() == true) {
            getBOrNull()?.webView!!.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        onWebViewDestroy()
        super.onDestroy()
    }

    /**
     * 销毁WebView，避免内存泄漏。注意：由于onDestroy()方法执行时，manager对象可能被回收了，所以不能获取manager处理这个逻辑。
     */
    private fun onWebViewDestroy() {
        val webView = getBOrNull()?.webView ?: return
        val parent = webView.parent as? ViewGroup
        parent?.removeView(webView)
        webView.removeAllViews()
        webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
        webView.stopLoading()
        webView.webChromeClient = null
        webView.destroy()
    }
}

sealed class PostDetailAction {
    class CloseAct : PostDetailAction()
    class SendComment : PostDetailAction()
}

