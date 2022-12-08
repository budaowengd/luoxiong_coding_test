package com.lx.test.model

import androidx.lifecycle.MutableLiveData
import com.lx.test.R
import com.lx.test.adapter.IViewTypeModel
import com.lx.test.mix.LanguageNameTypeNote

/**
 *  date: 2022/12/7
 *  version: 1.0
 *  desc: 存放Main模块所有的注解
 */
/**
 * 文章列表语言选择
 */
class PostListLanguageSelectModel : IViewTypeModel {
    override fun getViewType(): Int {
        return R.layout.main_item_pw_language_select
    }

    var languageName = ""
    @LanguageNameTypeNote
    var languageType= LanguageNameTypeNote.a_languages
    val selected = MutableLiveData(false)
}


/**
 * 文章模型基类
 */
abstract class AbstractPostModel : IViewTypeModel {

    // 创建时间,yyyy-MM-dd
    var createDate = ""

    // 创建时间, 毫秒数
    var createDateMills = 0L

    // 当前资讯所属的分类
    val categories = mutableListOf<String>()

    // 标签列表
    val tags = mutableListOf<String>()

    // 是否显示标签布局
    var showTagsLayout = false

    // 是否显示资讯图片
    var showImg = false

    // 资讯图片地址
    var imgUrl = ""

    // 资讯H5详情地址
    var h5DetailUrl = ""

    // 标题
    var title = ""

    // 语言名字
    var languageName = ""
}

/**
 * 纯文本
 */
class PostOnlyTextModel : AbstractPostModel() {
    override fun getViewType(): Int {
        return R.layout.main_item_post_only_text
    }
}

/**
 * 只有一张图片
 */
class PostOneImgModel : AbstractPostModel() {
    override fun getViewType(): Int {
        return R.layout.main_item_post_one_img
    }
}