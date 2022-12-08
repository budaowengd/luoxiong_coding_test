package com.lx.test.manager

import android.annotation.SuppressLint
import android.net.http.SslError
import android.text.TextUtils
import android.view.View
import android.webkit.*
import com.blankj.utilcode.util.*
import com.lx.test.R
import com.lx.test.act.PostDetailAct
import com.lx.test.act.PostDetailAction
import com.lx.test.base.AbstractActManager
import com.lx.test.base.DefaultVm
import com.lx.test.databinding.MainActPostDetaillBinding

/**
 *  date: 2022/12/8
 *  version: 1.0
 *  desc: 文章详情业务逻辑处理
 */
class PostDetailManager : AbstractActManager<PostDetailAct, MainActPostDetaillBinding, DefaultVm>() {
    private lateinit var mWebView: WebView

    override fun initActView(act: PostDetailAct, b: MainActPostDetaillBinding) {
        BarUtils.addMarginTopEqualStatusBarHeight(getB().root)
        BarUtils.setStatusBarColor(act, ColorUtils.getColor(R.color.window_bg))
        BarUtils.setStatusBarLightMode(act, true)

        b.ivClose.setOnClickListener { act.doAction(PostDetailAction.CloseAct()) }
        b.llSendComment.setOnClickListener { act.doAction(PostDetailAction.SendComment()) }
        mWebView = b.webView
        handleSetting(mWebView)
        initClient(mWebView)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun handleSetting(webView: WebView) {
        val ws: WebSettings = webView.getSettings()
        // 保存表单数据
        ws.saveFormData = true
        // 是否应该支持使用其屏幕缩放控件和手势缩放
        ws.setSupportZoom(true)
        ws.builtInZoomControls = true
        ws.displayZoomControls = false
        // 启动应用缓存
        ws.setAppCacheEnabled(true)
        // 设置缓存模式
        ws.cacheMode = WebSettings.LOAD_DEFAULT
        // setDefaultZoom  api19被弃用
        // 网页内容的宽度自适应屏幕
        ws.loadWithOverviewMode = true
        ws.useWideViewPort = true
        // 告诉WebView启用JavaScript执行。默认的是false。
        ws.javaScriptEnabled = true
        //  页面加载好以后，再放开图片
        ws.blockNetworkImage = false
        // 使用localStorage则必须打开
        ws.domStorageEnabled = true
        // 排版适应屏幕
        ws.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
        // WebView从5.0开始默认不允许混合模式,https中不能加载http资源,需要设置开启。
        ws.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
    }

    private fun initClient(webView: WebView) {
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                getB().progressBar.progress = newProgress
                if (newProgress == 100) {
                    getB().progressBar.visibility = View.GONE
                    if (!getB().stateLayout.isShowingError()) {
                        view.visibility = View.VISIBLE
                    }
                } else {
                    getB().progressBar.visibility = View.VISIBLE
                }
                getB().csl.checkLayoutChange()
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                if (getB().stateLayout.isShowingError()) {
                    getB().tvTitle.text = StringUtils.getString(R.string.main_k10)
                } else {
                    getB().tvTitle.text = title.orEmpty()
                }
            }
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val url = request.url.toString()
                if (TextUtils.isEmpty(url)) {
                    return false
                }
                return !url.startsWith("http:") && !url.startsWith("https:");
            }


            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                if (request.isForMainFrame) {
                    view.visibility = View.INVISIBLE
                    getB().stateLayout.showError {
                        // 点击重试后，重新请求接口
                        view.reload()
                    }
                }
            }

            @SuppressLint("WebViewClientOnReceivedSslError")
            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                handler?.proceed()
            }
        }
    }
}