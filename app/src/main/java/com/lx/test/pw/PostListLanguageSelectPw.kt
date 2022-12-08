package com.lx.test.pw

import android.annotation.SuppressLint
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.AdaptScreenUtils
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ScreenUtils
import com.lx.test.R
import com.lx.test.adapter.BaseRvFun2ItemClickEvent
import com.lx.test.adapter.DefaultRvAdapter
import com.lx.test.adapter.decoration.LinearDividerDecoration
import com.lx.test.databinding.MainPwPostListLanguageSelectBinding
import com.lx.test.frag.PostListAction
import com.lx.test.model.PostListLanguageSelectModel
import com.lxj.xpopup.core.BasePopupView
import lib.popup.Popup
import lib.popup.interfaces.PopupCallback
import lib.popup.views.AbstractPartShadowPopupView

/**
 *  date: 2022/12/8
 *  version: 1.0
 *  desc: 文章列表语言选择弹窗
 */
// 传递到弹窗里的参数封装
class ToPostListLanguageSelectPwBo(
    val act: FragmentActivity,
    val life: Lifecycle,
    val atView: View,
    val selectedLanguageName: String?,
    val languageModelList: List<PostListLanguageSelectModel>,
    val itemClickCb: (item: PostListLanguageSelectModel) -> Unit,
    val dismissBeforeCb: () -> Unit,
)

@SuppressLint("ViewConstructor")
class PostListLanguageSelectPw(private val bo: ToPostListLanguageSelectPwBo) : AbstractPartShadowPopupView<MainPwPostListLanguageSelectBinding>(bo.act, R.layout.main_pw_post_list_language_select) {
    companion object {
        fun show(bo: ToPostListLanguageSelectPwBo) {
            val pw = PostListLanguageSelectPw(bo)
            Popup.asCustomPartShadow(bo.act, bo.life, bo.atView)
                .setPopupCallback(object : PopupCallback {
                    override fun beforeDismiss(pw: BasePopupView) {
                        bo.dismissBeforeCb.invoke()
                    }
                })
                .show(pw)
        }
    }

    private val mAdapter by lazy(LazyThreadSafetyMode.NONE) { DefaultRvAdapter() }
    override fun onViewCreated(b: MainPwPostListLanguageSelectBinding) {
        initRv(b.recyclerView)
        initPageData()
    }

    private fun initPageData() {
        LogUtils.d("aaa=${bo.languageModelList}")
        bo.languageModelList.forEach {
            it.selected.value = bo.selectedLanguageName == it.languageName
        }
        mAdapter.setItems(bo.languageModelList)
    }

    private fun initRv(rv: RecyclerView) {
        val lm = LinearLayoutManager(bo.act)
        val decoration = LinearDividerDecoration(bo.act, ColorUtils.getColor(R.color.common_divider_color), AdaptScreenUtils.pt2Px(0.5F))
        decoration.setMarginLeftRight(AdaptScreenUtils.pt2Px(16F))
        rv.layoutManager = lm
        rv.addItemDecoration(decoration)
        mAdapter.setItemClickEvent(object : BaseRvFun2ItemClickEvent<PostListLanguageSelectModel, Int> {
            override fun clickRvItem(item: PostListLanguageSelectModel, flag: Int) {
                doListItem(item)
            }
        })
        rv.adapter = mAdapter
    }

    private fun doListItem(item: PostListLanguageSelectModel) {
        dismiss()
        bo.itemClickCb.invoke(item)
    }

    override fun getMaxHeight(): Int {
        return (ScreenUtils.getScreenHeight() * 0.6).toInt()
    }
}