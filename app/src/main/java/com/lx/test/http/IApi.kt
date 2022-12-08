package com.lx.test.http


/**
 *  date: 2022/12/7
 *  version: 1.0
 *  desc: Api 基类
 */
interface IApi

/**
 * 基于Retrofit动态创建ApiService
 */
inline fun <reified T : IApi> createApi(): T {
    return RetrofitClient.getServiceApi(T::class.java)
}


