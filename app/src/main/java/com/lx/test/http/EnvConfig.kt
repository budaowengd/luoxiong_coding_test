package com.lx.test.http

/**
 *  date: 2022/12/7
 *  version: 1.0
 *  desc: 封装了App中所有域名信息
 */
interface Env {
    /**
     * 接口域名
     */
    fun getBaseUrl(): String
}

abstract class AbstractEnv(
    private val baseUrl: String,
) : Env {
    override fun getBaseUrl(): String {
        return baseUrl
    }
}

object EnvConfig : Env {
    private val devConfig = object : AbstractEnv(
        baseUrl = "https://arcblockio.cn",
    ) {}

    private val onlineConfig = object : AbstractEnv(
        baseUrl = "https://arcblockio.cn",
    ) {}

    override fun getBaseUrl(): String {
        return getCurrentEnv().getBaseUrl()
    }

    fun getCurrentEnv(): Env {
        return onlineConfig
    }

}