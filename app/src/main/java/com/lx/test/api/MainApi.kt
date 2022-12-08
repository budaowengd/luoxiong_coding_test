package com.lx.test.api

import com.lx.test.http.IApi
import com.lx.test.vo.PostItemVo
import okhttp3.MultipartBody
import retrofit2.http.*

/**
 *  date: 2022/12/7
 *  version: 1.0
 *  desc: Main模块的接口
 */
interface MainApi : IApi {

    @GET("/blog/posts.json")
    suspend fun queryPosts(): List<PostItemVo>
}