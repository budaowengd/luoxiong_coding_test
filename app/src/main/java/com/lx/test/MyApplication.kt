package com.lx.test

import android.app.Application
import android.view.Gravity
import com.blankj.utilcode.util.*
import com.lx.test.const.MyConfig
import com.lx.test.http.EnvConfig
import com.lx.test.http.RetrofitClient

/**
 *  date: 2022/12/7
 *  version: 1.0
 *  desc: 程序入口
 */
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 工具类
        Utils.init(this)
        // 网络请求
        initHttp()
        // 全局配置
        initConfig()
        // 吐司和log
        initToastAndLog()
    }

    private fun initToastAndLog() {
        LogUtils.getConfig().setGlobalTag(AppUtils.getAppPackageName() + "MVVM")
            .setLogHeadSwitch(true).setBorderSwitch(false)
        ToastUtils.getDefaultMaker()
            .setMode(ToastUtils.MODE.LIGHT)
            .setGravity(Gravity.CENTER, 0, 100)
            .setBgColor(ColorUtils.getColor(R.color.white90))
            .setTextColor(ColorUtils.getColor(R.color.main_text_color))
    }

    private fun initConfig() {
        MyConfig.glide_img_load_placeholder = ResourceUtils.getDrawable(R.color.glide_place_color)
    }


    private fun initHttp() {
        RetrofitClient.init(EnvConfig.getBaseUrl())
    }
}