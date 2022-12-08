package com.lx.test.http

import android.annotation.SuppressLint
import androidx.collection.ArrayMap
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.Protocol
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 *  date: 2022/12/7
 *  version: 1.0
 *  desc: 网络请求统一封装。
 */
object RetrofitClient {
    private const val connect_timeout: Long = 30
    private const val write_timeout: Long = 30
    private const val read_timeout: Long = 30
    private val mServiceObjMap = ArrayMap<String, Any>()
    private lateinit var mRetrofitBaseUrl: String
    private lateinit var mRetrofit: Retrofit
    private val mGson = GsonBuilder()
        .registerTypeAdapter(Int::class.java, IntTypeAdapter())  // 保证后端返回"",也能解析
        .registerTypeAdapter(Long::class.java, LongTypeAdapter())// 保证后端返回"",也能解析
        .disableHtmlEscaping() // 原来，Gson会把html标签，转换为Unicode转义字符。导致微信群发内容异常。该方法禁止谷歌转换
        .create()

    fun init(baseUrl: String) {
        if (!baseUrl.startsWith("http")) {
            throw NullPointerException("baseUrl必须要以http开头。。。。。baseUrl=$baseUrl")
        }
        mRetrofitBaseUrl = baseUrl
        val client = createOkHttpClient()
        mRetrofit = createRetrofit(baseUrl, client)
    }

    fun <S> getServiceApi(clazz: Class<S>): S {
        var serviceObj = mServiceObjMap[clazz.canonicalName]
        if (serviceObj == null) {
            serviceObj = mRetrofit.create(clazz)
            mServiceObjMap[clazz.canonicalName] = serviceObj
        }
        return serviceObj as S
    }

    fun getRetrofitBaseUrl(): String {
        return mRetrofitBaseUrl
    }

    fun getRetrofit(): Retrofit {
        return mRetrofit
    }


    private fun createRetrofit(baseUrl: String, client: OkHttpClient): Retrofit {
        val builder = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(mGson))
            .client(client)
        return builder.build()
    }

    private fun createOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(connect_timeout, TimeUnit.SECONDS)
            .readTimeout(read_timeout, TimeUnit.SECONDS)
            .writeTimeout(write_timeout, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .protocols(Collections.singletonList(Protocol.HTTP_1_1))
        initSslSecureByBuilder(builder)
        return builder.build()
    }

    @SuppressLint("TrustAllX509TrustManager")
    fun initSslSecureByBuilder(builder: OkHttpClient.Builder) {
        try {
            val trustAllCerts = arrayOf<TrustManager>(@SuppressLint("CustomX509TrustManager") object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                }

                override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            })
            /* 使用SSL会报错：
             * javax.net.ssl.SSLHandshakeException: java.security.cert.CertPathValidatorException:
             *  Trust anchor for certification path not found.
             */
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())
            val sslSocketFactory = sslContext.socketFactory
            builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            builder.hostnameVerifier { _, _ -> true }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
