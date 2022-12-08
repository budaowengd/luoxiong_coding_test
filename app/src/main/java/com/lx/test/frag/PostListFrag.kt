package com.lx.test.frag

import android.os.Bundle
import android.os.Parcelable
import com.lx.test.R
import com.lx.test.base.BaseFrag
import com.lx.test.databinding.MainFragPostListBinding
import com.lx.test.manager.PostListManager
import com.lx.test.model.AbstractPostModel
import com.lx.test.vm.PostListVm
import kotlinx.parcelize.Parcelize

/**
 *  date: 2022/12/7
 *  version: 1.0
 *  desc: 帖子列表
 */
/**
 * 创建帖子列表Fragment，传递的参数
 */
@Parcelize
data class ToPostListFragDto(
    val categoryName: String,
) : Parcelable

class PostListFrag : BaseFrag<MainFragPostListBinding, PostListVm, PostListManager>(R.layout.main_frag_post_list) {
    companion object {
        fun newInstance(dto: ToPostListFragDto): PostListFrag {
            val b = Bundle()
            b.putParcelable("dto", dto)
            val frag = PostListFrag()
            frag.arguments = b
            return frag
        }
    }

    override fun onParseArgument() {
        getVm().intentDto = arguments?.getParcelable("dto") ?: ToPostListFragDto("")
    }

    override fun onViewCreatedAfter() {
        getM().initPageData()
    }

    fun doAction(action: PostListAction) {
        when (action) {
            // 点击时间排序
            is PostListAction.DateSort -> getM().doDateSort()
            // 点击列表item
            is PostListAction.ListItem -> getM().doListItem(action.m)
            // 点击语言排序
            is PostListAction.LanguageSort -> getM().doLanguageSort()
        }
    }
}

sealed class PostListAction {
    class DateSort() : PostListAction()
    class LanguageSort() : PostListAction()
    class ListItem(val m: AbstractPostModel) : PostListAction()
}