package com.sttptech.toshiba_lighting.Application

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.orhanobut.logger.Logger
import com.sttptech.toshiba_lighting.AppUtil.AppKey
import com.sttptech.toshiba_lighting.Application.RepositoryService.RemoteData.ModifyDeviceGroup
import com.sttptech.toshiba_lighting.Application.RepositoryService.RemoteData.ModifyDeviceGroup.ADD_DEVICE
import com.sttptech.toshiba_lighting.Application.RepositoryService.RemoteData.ModifyDeviceGroup.REMOVE_DEVICE
import com.sttptech.toshiba_lighting.Application.RepositoryService.RemoteData.ModifyDeviceSettings
import com.sttptech.toshiba_lighting.Application.RepositoryService.RemoteData.ModifyDeviceSettings.*
import com.sttptech.toshiba_lighting.Data.Bean.Device
import com.sttptech.toshiba_lighting.Data.Bean.Scene
import com.sttptech.toshiba_lighting.RetrofitUtil.APIService
import com.sttptech.toshiba_lighting.RetrofitUtil.InfoListRes
import com.sttptech.toshiba_lighting.RetrofitUtil.RetrofitUtil
import com.sttptech.toshiba_lighting.RetrofitUtil.ServerResponse
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException


class RemoteService(var context: Context) : RepositoryService.RemoteData {
    
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
    
        val jsBody = JsonObject().also { m ->
            m.add("parms", JsonObject().also { p ->
                p.addProperty("acct", account)
                p.addProperty("bizPcode", "GTOEM900_113")
                p.addProperty("pwd", password)
                p.addProperty("sso", false)
                p.addProperty("svt", "A")
            })
        }
    
        Logger.i("Member login request \n$jsBody")
    
        val call = service.memberLogin(RetrofitUtil.buildReqBody(jsBody))
    
        return try { // TODO: fix response handle
            val jsonResponse = call!!.execute().body()!!.string()
        
            Logger.i("Member login response \n$jsonResponse")
        
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
            context.getSharedPreferences(AppKey.SHP_NAME, Context.MODE_PRIVATE)
                .getString(AppKey.SHP_TOKEN, null)
    
        // 請求頭
        val headerMap: MutableMap<String?, String?> = HashMap()
        val strHeader = "Bearer $token"
        headerMap["Authorization"] = strHeader
    
        // 放入請求體
        val jsMain = JsonObject()
    
        Logger.i("Get info list request \n$jsMain")
    
        val call = service.getInfoList(headerMap, RetrofitUtil.buildReqBody(jsMain))
        return try {
            val jsonResponse = call!!.execute().body()!!.string()
        
            Logger.i("Get info list response \n$jsonResponse")
        
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
            context.getSharedPreferences(AppKey.SHP_NAME, Context.MODE_PRIVATE)
                .getString(AppKey.SHP_TOKEN, null)
        
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
    
        Logger.i("Device sign up request \n$jsMain")
    
        val call = service.devSignUp(headerMap, RetrofitUtil.buildReqBody(jsMain))
        return try { // TODO: fix response handle
            val jsonResponse = call!!.execute().body()!!.string()
        
            Logger.i("Device sign up response \n$jsonResponse")
        
            Gson().fromJson(jsonResponse, ServerResponse::class.java)
        
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } catch (e: NullPointerException) {
            e.printStackTrace()
            null
        }
    }
    
    override fun modifyDeviceName(uUid: String, newName: String): Boolean {
        val service = retrofit.create(APIService.Device::class.java)
        
        val jsBody = JsonObject().also { m ->
            m.add("parms", JsonObject().also { p ->
                p.addProperty("devUuid", uUid)
                p.add("infoD", JsonObject().also { i ->
                    i.addProperty("confFw", "")
                    i.addProperty("confHw", "")
                    i.addProperty(
                        "confPc",
                        Gson().toJson(JsonObject().apply { addProperty("devName", newName) })
                    )
                    i.addProperty("confSw", "")
                })
            })
        }
        
        Logger.i("modify device name request: \n$jsBody")
        
        val call = service.devModify(
            RetrofitUtil.getHeader(context),
            RetrofitUtil.buildReqBody(jsBody)
        )
        
        
        return try {
            val response = call.execute()
            if (response.isSuccessful) {
                val responseStr = response.body()!!.string()
                
                Logger.i("modify device name response: \n$responseStr")
                
                val serverResponse = Gson().fromJson(responseStr, ServerResponse::class.java)
                serverResponse.isSuccess()
            } else
                false
        } catch (e: IOException) {
            e.printStackTrace()
            false
        } catch (e: RuntimeException) {
            e.printStackTrace()
            false
        }
    }
    
    override fun modifyDeviceGroup(
        devUuid: String,
        groupUuid: String,
        action: ModifyDeviceGroup
    ): ServerResponse? {
        val service = retrofit.create(APIService.Group::class.java)
        
        val jsBody = JsonObject()
        val jsParms = JsonObject()
        val jsArray = JsonArray()
        
        when (action) {
            
            ADD_DEVICE -> {
                jsArray.add(devUuid)
                with(jsParms) {
                    add("addDevUuids", jsArray)
                    addProperty("groupUuid", groupUuid)
                }
                jsBody.add("parms", jsParms)
            }
            
            REMOVE_DEVICE -> {
                jsArray.add(devUuid)
                with(jsParms) {
                    add("removeDevUuids", jsArray)
                    addProperty("groupUuid", groupUuid)
                }
                jsBody.add("parms", jsParms)
            }
        }
        
        Logger.i("modify device group request: \n$jsBody")
        
        val call = service.groupModify(
            RetrofitUtil.getHeader(context),
            RetrofitUtil.buildReqBody(jsBody)
        )
        
        
        return try {
            val response = call.execute()
            if (response.isSuccessful) {
                val responseStr = response.body()!!.string()
                
                Logger.i("modify device group response: \n$responseStr")
                
                Gson().fromJson(responseStr, ServerResponse::class.java) ?: null
            } else
                null
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } catch (e: RuntimeException) {
            e.printStackTrace()
            null
        }
    }
    
    override fun modifyDeviceSetting(
        devUuid: String,
        action: ModifyDeviceSettings,
        time: String,
        status: String
    ): ServerResponse? {
        val service = retrofit.create(APIService.Device::class.java)
        
        val timeKey: String
        val statusKey: String
        when (action) {
            Wakeup -> {
                timeKey = "dailyWakeUp"
                statusKey = "dailyWakeUpS"
            }
            
            DailyOn -> {
                timeKey = "dailyOpen"
                statusKey = "dailyOpenS"
            }
            
            DailyOff -> {
                timeKey = "dailyClose"
                statusKey = "dailyCloseS"
            }
        }
        
        val jsBody = JsonObject().apply {
            add("parms", JsonObject().apply {
                addProperty("devUuid", devUuid)
                add("infoD", JsonObject().apply {
                    addProperty("confFw", "")
                    addProperty("confHw", "")
                    addProperty("confPc", "")
                    addProperty(
                        "confSw", "${
                            JsonObject().apply {
                                add("rcuSetting", JsonObject().apply {
                                    addProperty(timeKey, time)
                                    addProperty(statusKey, status)
                                })
                            }
                        }"
                    )
                })
            })
        }
        
        Logger.i("modify device settings request: \n$jsBody")
        
        val call = service.devModify(
            RetrofitUtil.getHeader(context),
            RetrofitUtil.buildReqBody(jsBody)
        )
        
        
        return try {
            val response = call.execute()
            if (response.isSuccessful) {
                val responseStr = response.body()!!.string()
                
                Logger.i("modify device settings response: \n$responseStr")
                
                Gson().fromJson(responseStr, ServerResponse::class.java) ?: null
            } else {
                Logger.e(response.errorBody()!!.string())
                
                null
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } catch (e: RuntimeException) {
            e.printStackTrace()
            null
        }
    }
    
    override fun createGroup(devUuid: String, groupName: String): ServerResponse? {
        val service = retrofit.create(APIService.Group::class.java)
        
        val jsBody = JsonObject().apply {
            add("parms", JsonObject().apply {
                addProperty("groupName", groupName)
                add("devUuids", JsonArray().apply {
                    add(devUuid)
                })
            })
        }
        
        Logger.i("create device group request: \n$jsBody")
        
        val call = service.createGroup(
            RetrofitUtil.getHeader(context),
            RetrofitUtil.buildReqBody(jsBody)
        )
        
        
        return try {
            val response = call.execute()
            if (response.isSuccessful) {
                val responseStr = response.body()!!.string()
                
                Logger.i("create device group response: \n$responseStr")
                
                Gson().fromJson(responseStr, ServerResponse::class.java) ?: null
            } else
                null
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } catch (e: RuntimeException) {
            e.printStackTrace()
            null
        }
    }
    
    override fun modifyCustomModeName(
        modeNum: Int,
        name: String,
        devUuid: String
    ): Boolean {
        val service = retrofit.create(APIService.Device::class.java)
        // 獲取登入時 Member token
        val token: String? =
            context.getSharedPreferences(AppKey.SHP_NAME, Context.MODE_PRIVATE)
                .getString(AppKey.SHP_TOKEN, null)
        
        // 請求頭
        val headerMap: MutableMap<String?, String?> = HashMap()
        val strHeader = "Bearer $token"
        headerMap["Authorization"] = strHeader
        
        // json body
        val jsonArray = JsonArray()
        val jsModeName = JsonObject()
        val jsRcu = JsonObject()
        jsModeName.addProperty("c", modeNum.toString())
        jsModeName.addProperty("cn", name)
        jsonArray.add(jsModeName)
        jsRcu.add("rcuModes", jsonArray)
        
        val jsMain = JsonObject()
        val jsParms = JsonObject()
        val jsInfo = JsonObject()
        jsInfo.addProperty("confFw", "")
        jsInfo.addProperty("confHw", "")
        jsInfo.addProperty("confPc", "")
        jsInfo.addProperty("confSw", jsRcu.toString())
        
        jsParms.addProperty("devUuid", devUuid)
        jsParms.add("infoD", jsInfo)
        jsMain.add("parms", jsParms)
        
        Logger.i(jsMain.toString())
        
        val call = service.devModify(headerMap, RetrofitUtil.buildReqBody(jsMain))
        return try {
            val response = call.execute()
            if (response.body() != null) {
                val resStr = response.body()?.string()
                
                Logger.i(resStr!!)
                
                Gson().fromJson(resStr, ServerResponse::class.java).isSuccess()
            } else
                false
        } catch (e: IOException) {
            e.printStackTrace()
            false
        } catch (e: RuntimeException) {
            e.printStackTrace()
            false
        }
    }
    
    override fun deleteDevice(devUuid: String): ServerResponse? {
        val service = retrofit.create(APIService.Device::class.java)
        
        val jsBody = JsonObject().apply {
            add("parms", JsonObject().apply {
                addProperty("devUuid", devUuid)
            })
        }
        
        Logger.i("delete device request: \n$jsBody")
        
        val call = service.devDelete(
            RetrofitUtil.getHeader(context),
            RetrofitUtil.buildReqBody(jsBody)
        )
        
        
        return try {
            val response = call.execute()
            if (response.isSuccessful) {
                val responseStr = response.body()!!.string()
                
                Logger.i("delete device response: \n$responseStr")
                
                Gson().fromJson(responseStr, ServerResponse::class.java) ?: null
            } else
                null
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } catch (e: RuntimeException) {
            e.printStackTrace()
            null
        }
    }
    
    override fun getSceneImage(imgUrl: String): ByteArray? {
        val service: APIService.Scene = retrofit.create(APIService.Scene::class.java)
        val reqUrl = "https://intgr.sttptech.com$imgUrl"
        val call = service.downloadSceneImage(reqUrl)
        
        return try {
            val res = call.execute()
            return if (res.isSuccessful && res.body() != null) {
                res.body()!!.bytes()
            } else {
                null
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } catch (e: java.lang.NullPointerException) {
            e.printStackTrace()
            null
        }
    }
    
    override fun insertScene(seqCode: Int, name: String, devList: List<Device>): ServerResponse? {
        val service = retrofit.create(APIService.Scene::class.java)
        
        val jsBody = JsonObject().apply {
            add("parms", JsonObject().apply {
                addProperty("grsituationName", name)
                addProperty("grsituationSeq", seqCode)
                add("devUuids", JsonArray().apply {
                    for (dev in devList) {
                        this.add(dev.uId)
                    }
                })
            })
        }
        
        Logger.i("insert scene request: $jsBody")
        
        val call =
            service.sceneAdd(RetrofitUtil.getHeader(context), RetrofitUtil.buildReqBody(jsBody))
        
        return try {
            val response = call.execute()
            if (response.isSuccessful) {
                val resStr = response.body()!!.string()
                
                Logger.i("insert scene response: \n$resStr")
                
                Gson().fromJson(resStr, ServerResponse::class.java)
            } else
                null
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } catch (e: RuntimeException) {
            e.printStackTrace()
            null
        }
    }
    
    override fun deleteScene(uUid: String): ServerResponse? {
        
        val jsBody = JsonObject().apply {
            add("parms", JsonObject().apply {
                add("grsituationUuid", JsonArray().apply {
                    add(uUid)
                })
            })
        }
        
        Logger.i("delete scene request: \n$jsBody")
        
        val call =
            retrofit.create(APIService.Scene::class.java)
                .sceneDelete(RetrofitUtil.getHeader(context), RetrofitUtil.buildReqBody(jsBody))
        
        return try {
            val response = call.execute()
            if (response.isSuccessful) {
                val resStr = response.body()!!.string()
                
                Logger.i("delete scene response: \n$resStr")
                
                return Gson().fromJson(resStr, ServerResponse::class.java)
            }
            
            else null
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } catch (e: NullPointerException) {
            e.printStackTrace()
            null
        }
    }
    
    override fun updateSceneImage(uUid: String, img: File): ServerResponse? {
        
        val requestFile: RequestBody = RequestBody.create(MediaType.parse("image/jpg"), img)
        val requestBody = MultipartBody.Part.createFormData("file", img.name, requestFile)
        
        val call = retrofit
            .create(APIService.Scene::class.java)
            .sceneImgUpdate(RetrofitUtil.getHeader(context), uUid, requestBody)
        
        return try {
            val response = call.execute()
            if (response.isSuccessful) {
                val resStr = response.body()!!.string()
                
                Logger.i("update image response: \n$resStr")
    
                Gson().fromJson(resStr, ServerResponse::class.java)
            } else {
                Logger.e(
                    "image update error response: \n${
                        response.errorBody()?.string().toString()
                    }"
                )
                null
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } catch (e: NullPointerException) {
            e.printStackTrace()
            null
        }
    
    }
    
    override fun updateScene(
        sceneUid: String,
        addDevs: List<String>?,
        removeDevs: List<String>?,
        name: String?
    ): ServerResponse? {
        
        val jsBody = JsonObject().apply {
            add("parms", JsonObject().apply {
                
                // add dev
                if (addDevs != null) {
                    add("addDevUuids", JsonArray().apply {
                        for (devUuid in addDevs) {
                            add(devUuid)
                        }
                    })
                }
                
                // remove dev
                if (removeDevs != null) {
                    add("removeDevUuids", JsonArray().apply {
                        for (devUuid in removeDevs) {
                            add(devUuid)
                        }
                    })
                }
                
                // name
                if (name != null)
                    addProperty("grsituationName", name)
                
                // uid
                addProperty("grsituationUuid", sceneUid)
            })
        }
        
        Logger.i("update scene request: $jsBody")
        
        val call = retrofit
            .create(APIService.Scene::class.java)
            .sceneModify(RetrofitUtil.getHeader(context), RetrofitUtil.buildReqBody(jsBody))
        
        return try {
            val response = call.execute()
            
            if (response.isSuccessful) {
                val resStr = response.body()!!.string()
                
                Logger.i("update scene response: $resStr")
                
                return Gson().fromJson(resStr, ServerResponse::class.java)
            }
            
            null
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } catch (e: NullPointerException) {
            e.printStackTrace()
            null
        }
    }
    
    override fun sortScene(sceneList: List<Scene>): ServerResponse? {
        
        val jsBody = JsonObject().apply {
            add("parms", JsonObject().apply {
                add("grsituationUuids", JsonArray().apply {
                    for (scene in sceneList) {
                        add(scene.uId)
                    }
                })
            })
        }
        
        Logger.i("sort scene request: $jsBody")
        
        val call = retrofit
            .create(APIService.Scene::class.java)
            .sceneSort(RetrofitUtil.getHeader(context), RetrofitUtil.buildReqBody(jsBody))
        
        return try {
            val response = call.execute()
            if (response.isSuccessful) {
                val resStr = response.body()!!.string()
                
                Logger.i("sort scene response: $resStr")
                
                return Gson().fromJson(resStr, ServerResponse::class.java)
            }
            null
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } catch (e: NullPointerException) {
            e.printStackTrace()
            null
        }
    }
    
    override fun getSchedule(): ServerResponse? {
        
        val call = retrofit
            .create(APIService.SceneSchedule::class.java)
            .sceneScheduleInfo(
                RetrofitUtil.getHeader(context),
                RetrofitUtil.buildReqBody(JsonObject())
            )
        
        Logger.i("schedule list request start")
        
        return try {
            val response = call.execute()
            if (response.isSuccessful) {
                val resStr = response.body()!!.string()
        
                Logger.i("schedule list response: $resStr")
        
                return Gson().fromJson(resStr, ServerResponse::class.java)
            }
    
            null
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } catch (e: NullPointerException) {
            e.printStackTrace()
            null
        }
    }
    
    override fun scheduleOnOff(taskId: String, onOff: Boolean): ServerResponse? {
        
        val jsBody = JsonObject().apply {
            add("parms", JsonObject().apply {
                addProperty("onoff", if (onOff) "Y" else "N")
                addProperty("taskId", taskId)
            })
        }
        
        val call = retrofit
            .create(APIService.SceneSchedule::class.java)
            .sceneScheduleOnOff(RetrofitUtil.getHeader(context), RetrofitUtil.buildReqBody(jsBody))
        
        Logger.i("schedule on/off request: $jsBody")
        
        return try {
            val response = call.execute()
            if (response.isSuccessful) {
                val resStr = response.body()!!.string()
                
                Logger.i("schedule on/off response: $resStr")
                
                return Gson().fromJson(resStr, ServerResponse::class.java)
            }
            
            null
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } catch (e: NullPointerException) {
            e.printStackTrace()
            null
        }
    }
    
    override fun addSchedule(
        sceneUuid: String,
        weekList: List<Int>,
        minOfDay: Int
    ): ServerResponse? {
        
        val jsBody = JsonObject().apply {
            add("parms", JsonObject().apply {
                addProperty("taskSeq", 0)
                addProperty("status", "Y")
                add("addPayload", JsonArray().apply {
                    for (week in weekList) {
                        add(JsonObject().apply {
                            addProperty("act", "ON")
                            addProperty("dayOfWeek", week)
                            addProperty("grsituationUuid", sceneUuid)
                            addProperty("minuteOfDay", minOfDay)
                        })
                    }
                })
            })
        }
        
        Logger.i("add schedule request: $jsBody")
        
        val call = retrofit
            .create(APIService.SceneSchedule::class.java)
            .sceneScheduleAdd(RetrofitUtil.getHeader(context), RetrofitUtil.buildReqBody(jsBody))
        
        return try {
            val response = call.execute()
            if (response.isSuccessful) {
                val resStr = response.body()!!.string()
                
                Logger.i("add schedule response: $resStr")
                
                return Gson().fromJson(resStr, ServerResponse::class.java)
            }
            
            null
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } catch (e: NullPointerException) {
            e.printStackTrace()
            null
        }
    }
    
    override fun modifySchedule(
        scheUuid: String,
        sceneUuid: String,
        weekList: List<Int>,
        minOfDay: Int
    ): ServerResponse? {
        
        val jsBody = JsonObject().apply {
            add("parms", JsonObject().apply {
                addProperty("taskId", scheUuid)
                addProperty("taskSeq", 0)
                addProperty("status", "Y")
                add("modPayload", JsonArray().apply {
                    for (week in weekList) {
                        add(JsonObject().apply {
                            addProperty("act", "ON")
                            addProperty("dayOfWeek", week)
                            addProperty("grsituationUuid", sceneUuid)
                            addProperty("minuteOfDay", minOfDay)
                        })
                    }
                })
            })
        }
        
        Logger.i("modify schedule request: $jsBody")
        
        val call = retrofit
            .create(APIService.SceneSchedule::class.java)
            .sceneScheduleModify(RetrofitUtil.getHeader(context), RetrofitUtil.buildReqBody(jsBody))
        
        return try {
            val response = call.execute()
            if (response.isSuccessful) {
                val resStr = response.body()!!.string()
                
                Logger.i("modify schedule response: $resStr")
                
                return Gson().fromJson(resStr, ServerResponse::class.java)
            }
            
            null
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } catch (e: NullPointerException) {
            e.printStackTrace()
            null
        }
    }
    
    override fun deleteSchedule(taskId: String): ServerResponse? {
        
        val jsBody = JsonObject().apply {
            add("parms", JsonObject().apply {
                addProperty("taskId", taskId)
            })
        }
        
        val call = retrofit
            .create(APIService.SceneSchedule::class.java)
            .sceneScheduleDelete(RetrofitUtil.getHeader(context), RetrofitUtil.buildReqBody(jsBody))
        
        Logger.i("schedule delete request: $jsBody")
        
        return try {
            val response = call.execute()
            if (response.isSuccessful) {
                val resStr = response.body()!!.string()
                
                Logger.i("schedule delete response: $resStr")
                
                return Gson().fromJson(resStr, ServerResponse::class.java)
            }
            
            null
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } catch (e: NullPointerException) {
            e.printStackTrace()
            null
        }
    }
    
    /* Share */
    
    override fun inviteMember(account: String): ServerResponse? {
        
        val jsBody = JsonObject().apply {
            add("parms", JsonObject().apply {
                addProperty("email", account)
            })
        }
        
        Logger.i("invite member request: \n$jsBody")
        
        val call = retrofit.create(APIService.Share::class.java)
            .inviteMember(RetrofitUtil.getHeader(context), RetrofitUtil.buildReqBody(jsBody))
        
        return try {
            val response = call.execute()
            if (response.isSuccessful) {
                val resStr = response.body()!!.string()
                
                Logger.i("invite member response: $resStr")
                
                return Gson().fromJson(resStr, ServerResponse::class.java)
            }
            
            null
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } catch (e: NullPointerException) {
            e.printStackTrace()
            null
        }
    }
    
    override fun bindShare(verifyCode: String): ServerResponse? {
        val jsBody = JsonObject().apply {
            add("parms", JsonObject().apply {
                addProperty("verificationCode", verifyCode)
            })
        }
    
        Logger.i("bind share request: \n$jsBody")
    
        val call = retrofit.create(APIService.Share::class.java)
            .bindShare(RetrofitUtil.getHeader(context), RetrofitUtil.buildReqBody(jsBody))
    
        return try {
            val response = call.execute()
            if (response.isSuccessful) {
                val resStr = response.body()!!.string()
            
                Logger.i("bind share response: $resStr")
            
                return Gson().fromJson(resStr, ServerResponse::class.java)
            }
        
            null
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } catch (e: NullPointerException) {
            e.printStackTrace()
            null
        }
    }
    
    override fun unbindShare(): ServerResponse? {
        val jsBody = JsonObject()
    
        Logger.i("unbind share request: \n$jsBody")
    
        val call = retrofit.create(APIService.Share::class.java)
            .unbindShare(RetrofitUtil.getHeader(context), RetrofitUtil.buildReqBody(jsBody))
    
        return try {
            val response = call.execute()
            if (response.isSuccessful) {
                val resStr = response.body()!!.string()
            
                Logger.i("unbind share response: $resStr")
            
                return Gson().fromJson(resStr, ServerResponse::class.java)
            }
        
            null
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } catch (e: NullPointerException) {
            e.printStackTrace()
            null
        }
    }
    
    override fun verifyCode(vCode: String): ServerResponse? {
        val jsBody = JsonObject().apply {
            add("parms", JsonObject().apply {
                addProperty("verificationCode", vCode)
            })
        }
    
        Logger.i("verify code request: \n$jsBody")
    
        val call = retrofit.create(APIService.Share::class.java)
            .bindShare(RetrofitUtil.getHeader(context), RetrofitUtil.buildReqBody(jsBody))
    
        return try {
            val response = call.execute()
            if (response.isSuccessful) {
                val resStr = response.body()!!.string()
            
                Logger.i("verify code response: $resStr")
            
                return Gson().fromJson(resStr, ServerResponse::class.java)
            }
        
            null
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } catch (e: NullPointerException) {
            e.printStackTrace()
            null
        }
    }
}