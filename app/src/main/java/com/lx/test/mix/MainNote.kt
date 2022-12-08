package com.lx.test.mix



// 列表排序状态注解类
annotation class LanguageNameTypeNote {
    companion object {
        const val all_languages = "0" // 标识全部语言
        const val a_languages = "1" // 指的是某一个语言
    }
}

// 列表排序状态注解类
annotation class SortStateFlagNote {
    companion object {
        const val blank = 0 // 上下箭头都不选中
        const val top = 1 // 上的箭头选中
        const val bottom = 2 // 下的箭头选中
    }
}