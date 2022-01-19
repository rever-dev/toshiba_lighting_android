package com.sttptech.toshiba_lighting.Application

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.sttptech.toshiba_lighting.AppUtil.KeyOfShp
import com.sttptech.toshiba_lighting.RetrofitUtil.APIService
import com.sttptech.toshiba_lighting.RetrofitUtil.RetrofitUtil
import com.sttptech.toshiba_lighting.RetrofitUtil.ServerResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.lang.NullPointerException

class Repository(private val context: Context) {

    val remoteS: DataSource.RemoteData
    val localS: DataSource.LocalData

    init {
        remoteS = RemoteService(context)
        localS = LocalService(context)
    }
}