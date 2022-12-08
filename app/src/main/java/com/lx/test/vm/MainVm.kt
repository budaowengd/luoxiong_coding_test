package com.lx.test.vm

import android.text.TextUtils
import com.blankj.utilcode.util.StringUtils
import com.lx.test.R
import com.lx.test.base.BaseVm
import com.lx.test.vo.PostItemVo

/**
 *  date: 2022/12/6
 *  version: 1.0
 *  desc:
 */
class MainVm : BaseVm() {

    val postVoList = mutableListOf<PostItemVo>()

    fun getCategories(vos: List<PostItemVo>): List<String> {
        val categorySet = LinkedHashSet<String>()
        categorySet.add(StringUtils.getString(R.string.main_k4))
        vos.forEach { vo ->
            vo.frontmatter?.categories?.forEach {
                val category = it?.trim()
                if (!TextUtils.isEmpty(category) && !categorySet.contains(category)) {
                    categorySet.add(category!!)
                }
            }
        }
        return categorySet.toList()
    }

}