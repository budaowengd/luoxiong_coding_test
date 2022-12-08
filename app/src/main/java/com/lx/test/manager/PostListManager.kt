package com.lx.test.manager

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.AdaptScreenUtils
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.ToastUtils
import com.lx.test.R
import com.lx.test.act.MainAct
import com.lx.test.act.PostDetailAct
import com.lx.test.act.ToPostDetailActDto
import com.lx.test.adapter.BaseRvFun2ItemClickEvent
import com.lx.test.adapter.DefaultRvAdapter
import com.lx.test.adapter.decoration.LinearDividerDecoration
import com.lx.test.base.AbstractFragManager
import com.lx.test.databinding.MainFragPostListBinding
import com.lx.test.frag.PostListAction
import com.lx.test.frag.PostListFrag
import com.lx.test.mix.LanguageNameTypeNote
import com.lx.test.mix.SortStateFlagNote
import com.lx.test.model.AbstractPostModel
import com.lx.test.model.PostListLanguageSelectModel
import com.lx.test.pw.PostListLanguageSelectPw
import com.lx.test.pw.ToPostListLanguageSelectPwBo
import com.lx.test.vm.PostListVm

/**
 *  date: 2022/12/7
 *  version: 1.0
 *  desc: 文章列表业务逻辑处理
 */
class PostListManager : AbstractFragManager<PostListFrag, MainFragPostListBinding, PostListVm>() {
    private val mAdapter by lazy(LazyThreadSafetyMode.NONE) { DefaultRvAdapter() }
    override fun initFragView(frag: PostListFrag, b: MainFragPostListBinding) {
        initRv(getB().recyclerView)
        b.clDateSort.setOnClickListener { frag.doAction(PostListAction.DateSort()) }
        b.btnLanguageSort.setOnClickListener { frag.doAction(PostListAction.LanguageSort()) }
    }

    private fun initRv(rv: RecyclerView) {
        val lm = LinearLayoutManager(getAct())
        val decoration = LinearDividerDecoration(getAct(), ColorUtils.getColor(R.color.common_divider_color), AdaptScreenUtils.pt2Px(0.5F))
        decoration.setMarginLeftRight(AdaptScreenUtils.pt2Px(16F))
        rv.layoutManager = lm
        rv.addItemDecoration(decoration)
        mAdapter.setItemClickEvent(object : BaseRvFun2ItemClickEvent<AbstractPostModel, Int> {
            override fun clickRvItem(item: AbstractPostModel, flag: Int) {
                getRealFrag().doAction(PostListAction.ListItem(item))
            }
        })
        rv.adapter = mAdapter
    }

    fun initPageData() {
        val vos = (getAct() as? MainAct)?.getPostVos()
        getVm().rawPostList.clear()
        if (vos.isNullOrEmpty()) return
        // 把后端列表数据转换成页面模型列表
        val models = getVm().convert2Models(vos)
        // 更新语言排序
        updateLanguageSort(models)
        // 保存原始数据源
        getVm().rawPostList.addAll(models)
        // 刷新列表
        mAdapter.setItems(models)
    }

    private fun updateLanguageSort(models: List<AbstractPostModel>) {
        val languageModelList = getVm().languageModelList
        languageModelList.clear()
        // 获取列表中所有的语言
        val languages = getVm().getLanguagesByModels(models)
        // 最少2种语言时才显示语言筛选布局
        getVm().showLanguageSortLayout.value = languages.size > 1
        if (languages.size > 1) {
            // 默认第1个item为所有语言
            languageModelList.add(getVm().createAllLanguageModel())
            languageModelList.addAll(getVm().convert2LanguageModels(languages))
            getVm().selectedLanguageName.value = languageModelList.first().languageName
        }
    }

    /**
     * 点击语言排序
     */
    fun doLanguageSort() {
        getVm().isShowingLanguagePw.value = true
        val bo = ToPostListLanguageSelectPwBo(getAct(), getLifecycle(), getB().btnLanguageSort, getVm().selectedLanguageName.value, getVm().languageModelList,
            itemClickCb = {
                if (getVm().selectedLanguageName.value != it.languageName) {
                    getVm().selectedLanguageName.value = it.languageName
                    refreshPageByLanguageName(it)
                }
            },
            dismissBeforeCb = {
                getVm().isShowingLanguagePw.value = false
            }
        )
        PostListLanguageSelectPw.show(bo)
    }

    private fun refreshPageByLanguageName(model: PostListLanguageSelectModel) {
        getVm().sortDateFlag.value = SortStateFlagNote.blank
        val matchedList = if (model.languageType == LanguageNameTypeNote.all_languages) {
            getVm().rawPostList
        } else {
            getVm().rawPostList.filter { it.languageName == model.languageName }
        }
        getB().recyclerView.scrollToPosition(0)
        mAdapter.setItems(matchedList)
    }

    /**
     * 点击时间排序
     */
    fun doDateSort() {
        when (getVm().sortDateFlag.value) {
            SortStateFlagNote.blank -> {
                getVm().sortDateFlag.value = SortStateFlagNote.top
            }
            SortStateFlagNote.top -> {
                getVm().sortDateFlag.value = SortStateFlagNote.bottom
            }
            SortStateFlagNote.bottom -> {
                getVm().sortDateFlag.value = SortStateFlagNote.blank
            }
        }
        againSortList(getVm().sortDateFlag.value!!)
    }

    /**
     * 对列表进行排序
     */
    private fun againSortList(@SortStateFlagNote sortFlag: Int) {
        val items = mutableListOf<AbstractPostModel>()
        when (sortFlag) {
            SortStateFlagNote.blank -> {
                // 还原成默认排序
                items.addAll(getVm().rawPostList)
            }
            SortStateFlagNote.top -> {
                items.addAll(mAdapter.getItems())
                items.sortBy { it.createDateMills }
            }
            SortStateFlagNote.bottom -> {
                items.addAll(mAdapter.getItems())
                items.sortByDescending { it.createDateMills }
            }
        }
        getB().recyclerView.scrollToPosition(0)
        mAdapter.setItems(items)
    }

    /**
     * 点击列表item
     */
    fun doListItem(m: AbstractPostModel) {
        val dto= ToPostDetailActDto(m.h5DetailUrl,m.title)
        PostDetailAct.openAct(dto)
    }
}
