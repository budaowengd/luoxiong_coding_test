package com.lx.test.vm

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.TimeUtils
import com.lx.test.R
import com.lx.test.base.BaseVm
import com.lx.test.frag.ToPostListFragDto
import com.lx.test.http.EnvConfig
import com.lx.test.mix.LanguageNameTypeNote
import com.lx.test.mix.SortStateFlagNote
import com.lx.test.model.AbstractPostModel
import com.lx.test.model.PostListLanguageSelectModel
import com.lx.test.model.PostOneImgModel
import com.lx.test.model.PostOnlyTextModel
import com.lx.test.vo.PostItemVo

/**
 *  date: 2022/12/6
 *  version: 1.0
 *  desc:
 */
class PostListVm : BaseVm() {
    // 时间排序方式
    @SortStateFlagNote
    val sortDateFlag = MutableLiveData(SortStateFlagNote.blank)

    // 传递到Fragment中的数据
    lateinit var intentDto: ToPostListFragDto

    // 后端返回的原始数据源
    val rawPostList = mutableListOf<AbstractPostModel>()

    // 是否正在显示语言排序弹窗，用来控制文字旁边的▲箭头朝上还是朝下
    val isShowingLanguagePw = MutableLiveData(false)

    // 是否有显示语言排序
    val showLanguageSortLayout = MutableLiveData(false)

    // 语言排序的文字
    val selectedLanguageName = MutableLiveData("")

    // 根据后端返回的列表数据，从列表中获取的语言列表
    val languageModelList = mutableListOf<PostListLanguageSelectModel>()

    /**
     * 把后端列表数据转换成页面模型列表
     */
    fun convert2Models(vos: List<PostItemVo>): List<AbstractPostModel> {
        var index = 0
        return vos.map {
            val bannerSrc = it.frontmatter?.banner?.childImageSharp?.fixed?.src
            val model: AbstractPostModel = if (TextUtils.isEmpty(bannerSrc) || index % 2 == 0) {
                PostOnlyTextModel()
            } else {
                PostOneImgModel()
            }
            fillModel(model, it)
            index++
            model
        }
    }

    fun createAllLanguageModel(): PostListLanguageSelectModel {
        val m = PostListLanguageSelectModel()
        m.languageType = LanguageNameTypeNote.all_languages
        m.languageName = StringUtils.getString(R.string.main_k5)
        return m
    }

    /**
     * 把后端语言列表转成model
     */
    fun convert2LanguageModels(languageNameList: List<String>): List<PostListLanguageSelectModel> {
        return languageNameList.map {
            val m = PostListLanguageSelectModel()
            m.languageType = LanguageNameTypeNote.a_languages
            m.languageName = it
            m
        }
    }

    /**
     * 获取页面模型列表里的语言国家
     */
    fun getLanguagesByModels(models: List<AbstractPostModel>): List<String> {
        val languageSet = LinkedHashSet<String>()
        models.forEach {
            if (!languageSet.contains(it.languageName) && it.languageName.isNotEmpty()) {
                languageSet.add(it.languageName)
            }
        }
        return languageSet.toList()
    }

    private fun fillModel(model: AbstractPostModel, vo: PostItemVo) {
        val frontVo = vo.frontmatter ?: return
        val bannerSrc = frontVo.banner?.childImageSharp?.fixed?.src
        val baseUrl = EnvConfig.getBaseUrl()
        model.apply {
            title = frontVo.title.orEmpty()
            languageName = frontVo.language.orEmpty()
            if (!TextUtils.isEmpty(frontVo.date)) {
                createDate = frontVo.date!!
                createDateMills = TimeUtils.string2Millis(frontVo.date, "yyyy-MM-dd")
            }
            if (!TextUtils.isEmpty(bannerSrc)) {
                imgUrl = baseUrl + bannerSrc.orEmpty()
            }
            if (!TextUtils.isEmpty(frontVo.path)) {
                h5DetailUrl = baseUrl + frontVo.path
            }
            frontVo.categories?.forEach {
                if (!TextUtils.isEmpty(it)) {
                    categories.add(it!!)
                }
            }
            frontVo.tags?.forEach {
                if (!TextUtils.isEmpty(it)) {
                    tags.add(it!!)
                }
            }
        }

    }

}