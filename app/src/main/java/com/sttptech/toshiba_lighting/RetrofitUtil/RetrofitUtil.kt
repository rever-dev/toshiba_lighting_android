package com.sttptech.toshiba_lighting.RetrofitUtil

import android.annotation.SuppressLint
import android.content.Context
import com.google.gson.JsonObject
import com.sttptech.toshiba_lighting.AppUtil.AppKey
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.RequestBody
import java.lang.Exception
import java.lang.RuntimeException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.ArrayList
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class RetrofitUtil {

    companion object {

        fun getUnsafeOkHttpClient(): OkHttpClient? {
            return try {
                // Create a trust manager that does not validate certificate chains
                val trustAllCerts = arrayOf<TrustManager>(
                    object : X509TrustManager {
                        @SuppressLint("TrustAllX509TrustManager")
                        @Throws(CertificateException::class)
                        override fun checkClientTrusted(
                            chain: Array<X509Certificate>,
                            authType: String
                        ) {
                        }

                        @Throws(CertificateException::class)
                        override fun checkServerTrusted(
                            chain: Array<X509Certificate>,
                            authType: String
                        ) {
                        }

                        override fun getAcceptedIssuers(): Array<X509Certificate?> {
                            return arrayOfNulls(0)
                        }
                    }
                )
                // Install the all-trusting trust manager
                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, SecureRandom())
                // Create an ssl socket factory with our all-trusting manager
                val sslSocketFactory = sslContext.socketFactory
                val verifier =
                    HostnameVerifier { hostname, session -> true }
                val list: MutableList<Protocol> = ArrayList()
                list.add(Protocol.HTTP_1_1)
                OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory)
                    .hostnameVerifier(verifier)
                    //                    .connectTimeout(60, TimeUnit.SECONDS)
                    //                    .writeTimeout(120, TimeUnit.SECONDS)
                    //                    .readTimeout(60, TimeUnit.SECONDS)
                    .protocols(list)
                    .build()
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }

        /**
         * Build json format RequestBody
         *
         * @param jsonObject jsonObject
         */
        fun buildReqBody(jsonObject: JsonObject): RequestBody {
            return RequestBody.create(MediaType.parse("application/json"), jsonObject.toString())
        }
        
        fun getHeader(context: Context): MutableMap<String?, String?> {
            // ??????????????? Member token
            val token: String? =
                context.getSharedPreferences(AppKey.SHP_NAME, Context.MODE_PRIVATE)
                    .getString(AppKey.SHP_TOKEN, null)
    
            // ?????????
            val headerMap: MutableMap<String?, String?> = HashMap()
            val strHeader = "Bearer $token"
            headerMap["Authorization"] = strHeader
            return headerMap
        }
    }
}