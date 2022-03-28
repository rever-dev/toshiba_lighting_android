package com.sttptech.toshiba_lighting.RetrofitUtil

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface APIService {

    companion object {
        const val BASE_URL = "https://intgr.sttptech.com/gtoem900/api/v1/merch/brand/prodline/"
    }

    interface Member {

        /**
         * 會員登入
         */
        @POST("mem/signin")
        fun memberLogin(@Body requestBody: RequestBody?): Call<ResponseBody?>?

        /**
         * 會員註冊
         */
        @POST("mem/signup")
        fun memRegister(@Body requestBody: RequestBody?): Call<ResponseBody?>?
    }

    interface Device {

        /**
         * 獲取用戶資訊列表
         */
        @POST("terminal/device/infolist")
        fun getInfoList(
            @HeaderMap headers: Map<String?, String?>?,
            @Body responseBody: RequestBody?
        ): Call<ResponseBody?>?

        /**
         * 裝置註冊
         */
        @POST("terminal/device/signup")
        fun devSignUp(
            @HeaderMap headers: Map<String?, String?>?,
            @Body responseBody: RequestBody?
        ): Call<ResponseBody?>?

        /**
         * 裝置重置
         */
        @POST("terminal/device/reset")
        fun devReset(
            @HeaderMap headers: Map<String?, String?>?,
            @Body responseBody: RequestBody?
        ): Call<ResponseBody?>?

        /**
         * 裝置刪除
         */
        @POST("terminal/device/delete")
        fun devDelete(
            @HeaderMap headers: Map<String?, String?>?,
            @Body responseBody: RequestBody?
        ): Call<ResponseBody?>

        /**
         * 裝置設定資訊
         */
        @POST("terminal/device/conf/info")
        fun getDevInfo(
            @HeaderMap headers: Map<String?, String?>?,
            @Body responseBody: RequestBody?
        ): Call<ResponseBody?>?

        /**
         * 裝置設定異動
         */
        @POST("terminal/device/conf/modify")
        fun devModify(
            @HeaderMap headers: Map<String?, String?>?,
            @Body responseBody: RequestBody?
        ): Call<ResponseBody?>
    }
    
    interface Group {
        /**
         * 群組新增
         */
        @POST("terminal/devgroup/add")
        fun createGroup(
            @HeaderMap headers: Map<String?, String?>?,
            @Body responseBody: RequestBody?
        ): Call<ResponseBody?>
    
        /**
         * 群組異動
         */
        @POST("terminal/devgroup/modify")
        fun groupModify(
            @HeaderMap headers: Map<String?, String?>?,
            @Body responseBody: RequestBody?
        ): Call<ResponseBody?>
    }

    interface Scene {

        /**
         * 新增情境
         */
        @POST("terminal/grsituation/add")
        fun sceneAdd(
            @HeaderMap headers: Map<String?, String?>?,
            @Body responseBody: RequestBody?
        ): Call<ResponseBody?>

        /**
         * 刪除情境
         */
        @POST("terminal/grsituation/delete")
        fun sceneDelete(
            @HeaderMap headers: Map<String?, String?>?,
            @Body responseBody: RequestBody?
        ): Call<ResponseBody?>

        /**
         * 情境異動
         */
        @POST("terminal/grsituation/modify")
        fun sceneModify(
            @HeaderMap headers: Map<String?, String?>?,
            @Body responseBody: RequestBody?
        ): Call<ResponseBody?>?
    
        /** 獲取情境圖片 */
        @GET
        fun downloadSceneImage(@Url imageUrl: String?): Call<ResponseBody?>
    
        @Multipart
        @POST("terminal/grsituation/modify/{uUid}")
        fun sceneImgUpdate(
            @HeaderMap headers: Map<String?, String?>?,
            @Path("uUid") uUid: String?,
            @Part file: MultipartBody.Part?
        ): Call<ResponseBody?>
    }
}