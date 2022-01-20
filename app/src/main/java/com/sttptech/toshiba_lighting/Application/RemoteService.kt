package com.sttptech.toshiba_lighting.Application

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.orhanobut.logger.Logger
import com.sttptech.toshiba_lighting.AppUtil.KeyOfShp
import com.sttptech.toshiba_lighting.RetrofitUtil.APIService
import com.sttptech.toshiba_lighting.RetrofitUtil.RetrofitUtil
import com.sttptech.toshiba_lighting.RetrofitUtil.ServerResponse
import com.sttptech.toshiba_lighting.RetrofitUtil.InfoListRes
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.*


class RemoteService(var context: Context) : DataSource.RemoteData {
    
    companion object {
        private const val TAG: String = "RemoteService"
    }
    
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(APIService.BASE_URL)
        .client(RetrofitUtil.getUnsafeOkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    override fun memberLogin(account: String, password: String): ServerResponse? {
        val service = retrofit.create(APIService.Member::class.java)
        val jsMain = JsonObject()
        val jsParms = JsonObject()
        
        jsParms.addProperty("acct", account)
        jsParms.addProperty("bizPcode", "GTOEM900_113")
        jsParms.addProperty("pwd", password)
        jsParms.addProperty("sso", false)
        jsParms.addProperty("svt", "A")
        jsMain.add("parms", jsParms)
        
        Logger.d("Member login request")
        Logger.json(jsMain.toString())
        
        val call = service.memberLogin(RetrofitUtil.getRequestBody(jsMain))
        return try {
            val jsonResponse = call!!.execute().body()!!.string()
            
            Logger.d("Member login response")
            Logger.json(jsonResponse)
            
            Gson().fromJson(jsonResponse, ServerResponse::class.java)
            
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } catch (e: NullPointerException) {
            e.printStackTrace()
            null
        }
    }
    
    override fun getInfoList(): InfoListRes? {
        val service = retrofit.create(APIService.Device::class.java)
        
        // 獲取登入時 Member token
        val token: String? =
            context.getSharedPreferences(KeyOfShp.SHP_NAME, Context.MODE_PRIVATE)
                .getString(KeyOfShp.SHP_TOKEN, null)
        
        // 請求頭
        val headerMap: MutableMap<String?, String?> = HashMap()
        val strHeader = "Bearer $token"
        headerMap["Authorization"] = strHeader
        
        // 放入請求體
        val jsMain = JsonObject()
        
        Logger.d("Get info list request")
        Logger.json("")
        
        val call = service.getInfoList(headerMap, RetrofitUtil.getRequestBody(jsMain))
        return try {
            val jsonResponse = call!!.execute().body()!!.string()
            
            Logger.d("Get info list response")
            Logger.json(jsonResponse)
            
            Gson().fromJson(jsonResponse, InfoListRes::class.java)
            
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } catch (e: NullPointerException) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * 註冊裝置
     *
     * @param mac   device mac ID
     * @param model device model ex: LEDTWRGB80
     * @param name  device name ex: Ceiling light
     * @param group device group
     */
    override fun devSignup(
        mac: String,
        group: String,
        name: String,
        model: String
    ): ServerResponse? {
        val service = retrofit.create(APIService.Device::class.java)
        
        // 獲取登入時 Member token
        val token: String? =
            context.getSharedPreferences(KeyOfShp.SHP_NAME, Context.MODE_PRIVATE)
                .getString(KeyOfShp.SHP_TOKEN, null)
        
        // 請求頭
        val headerMap: MutableMap<String?, String?> = HashMap()
        val strHeader = "Bearer $token"
        headerMap["Authorization"] = strHeader
        
        // 放入請求體
        
        // Json body
        val jsMain = JsonObject()
        val jsParms = JsonObject()
        jsParms.addProperty("devMac", mac)
        jsParms.addProperty("devLoc", group)
        jsParms.addProperty("devName", name)
        jsParms.addProperty("devSn", mac)
        jsParms.addProperty("prodScode", model)
        jsMain.add("parms", jsParms)
        
        Logger.d("Device sign up request")
        Logger.json(jsMain.toString())
        
        val call = service.devSignUp(headerMap, RetrofitUtil.getRequestBody(jsMain))
        return try {
            val jsonResponse = call!!.execute().body()!!.string()
            
            Logger.d("Device sign up response")
            Logger.json(jsonResponse)
            
            Gson().fromJson(jsonResponse, ServerResponse::class.java)
            
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } catch (e: NullPointerException) {
            e.printStackTrace()
            null
        }
    }
}