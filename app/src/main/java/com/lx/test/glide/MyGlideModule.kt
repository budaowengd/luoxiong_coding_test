package com.lx.test.glide

import android.content.Context
import com.blankj.utilcode.util.AppUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.lx.test.http.RetrofitClient
import okhttp3.OkHttpClient
import okhttp3.Protocol
import java.io.InputStream
import java.util.*


/**
 *  date: 2022/12/7
 *  version: 1.0
 *  desc: Glide参数全局配置
 */
@GlideModule
class MyGlideModule : AppGlideModule() {
    /**
     * Using the @GlideModule annotation requires a dependency on Glide’s annotations:
     */
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        val builder = OkHttpClient.Builder().protocols(Collections.singletonList(Protocol.HTTP_1_1))
        RetrofitClient.initSslSecureByBuilder(builder)
        val client = builder.build()
        registry.replace(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory(client))
    }

    /**
     * Implementations should return `false` after they and their dependencies have migrated
     * to Glide's annotation processor.
     */
    override fun isManifestParsingEnabled(): Boolean {
        return false
    }
}

