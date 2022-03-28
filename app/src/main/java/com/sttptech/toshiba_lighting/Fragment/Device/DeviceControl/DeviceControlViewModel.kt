package com.sttptech.toshiba_lighting.Fragment.Device.DeviceControl

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.sttptech.toshiba_lighting.Application.BaseApplication
import com.sttptech.toshiba_lighting.Application.BaseApplication.Companion.repository
import com.sttptech.toshiba_lighting.Data.Bean.DeviceStatus
import com.sttptech.toshiba_lighting.Mqtt.MqttClient
import com.sttptech.toshiba_lighting.Mqtt.MqttPublish
import com.sttptech.toshiba_lighting.Mqtt.MqttTopicTag

class DeviceControlViewModel(application: Application) : AndroidViewModel(application) {
    
    companion object {
        
        const val TAG = "DeviceControl"
    }
    
    val mqtt = BaseApplication.mqttClient
    val uiThread = Handler(Looper.getMainLooper())
    
    /** isSubscribe? */
    val isSubscribe: MutableLiveData<Boolean> = MutableLiveData(false)
    
    /** text */
    val devName: MutableLiveData<String?> = MutableLiveData(null)
    val devCusMode1: MutableLiveData<String?> = MutableLiveData(null)
    val devCusMode2: MutableLiveData<String?> = MutableLiveData(null)
    val devCusMode3: MutableLiveData<String?> = MutableLiveData(null)
    val devCusMode4: MutableLiveData<String?> = MutableLiveData(null)
    
    /** value */
    val devModel: MutableLiveData<String?> = MutableLiveData(null)
    val devBssId: MutableLiveData<String?> = MutableLiveData(null)
    val model get() = devModel.value!!
    val bssid get() = devBssId.value!!
    
    
    val switchStatus: MutableLiveData<Boolean> = MutableLiveData(false)
    val opMode: MutableLiveData<Int> = MutableLiveData(1)
    val selectMode: MutableLiveData<Int> = MutableLiveData(0)
    val mBr: MutableLiveData<Int> = MutableLiveData(0)
    val mC: MutableLiveData<Int> = MutableLiveData(1)
    val nBr: MutableLiveData<Int> = MutableLiveData(0)
    val rgbBr: MutableLiveData<Int> = MutableLiveData(0)
    val rgbValue: MutableLiveData<IntArray> = MutableLiveData(intArrayOf(0, 0, 0))
    
    private lateinit var devUuid: String
    private var rgbModeCounter = 10
    
    fun initDeviceData(uUid: String?) {
        if (uUid != null) {
            val ceilingLight = repository.localS.getCeilingLightById(uUid)
            devUuid = uUid
            devModel.postValue(ceilingLight?.model)
            devBssId.postValue(ceilingLight?.macId)
            devName.postValue(ceilingLight?.name)
            devCusMode1.postValue(ceilingLight?.cusMode1)
            devCusMode2.postValue(ceilingLight?.cusMode2)
            devCusMode3.postValue(ceilingLight?.cusMode3)
            devCusMode4.postValue(ceilingLight?.cusMode4)
        }
    }
    
    fun listenTopic() {
        mqtt.subscribeTopic(model, bssid, MqttTopicTag.STATUS)
        mqtt.mqttCallbackListener = object : MqttClient.MqttCallbackListener {
            override fun msgArrived(topic: String, msg: String) {
                
                val bssId = topic.split("/")[4]
                
                if (bssId == (bssid)) {
                    val status = Gson().fromJson(msg, DeviceStatus::class.java)
                    
                    switchStatus.postValue(
                        (status.payload.opMode.toInt() == 1 &&
                                status.payload.selectMode.toInt() == 0 &&
                                status.payload.mBr.toInt() == 0).not()
                    )
                    
                    selectMode.postValue(status.payload.selectMode.toInt())
                    opMode.postValue(status.payload.opMode.toInt())
                    mBr.postValue(status.payload.mBr.toInt())
                    mC.postValue(status.payload.mC.toInt())
                    nBr.postValue(status.payload.nBr.toInt())
                    rgbBr.postValue(status.payload.rgbBr.toInt())
                    rgbValue.postValue(
                        intArrayOf(
                            status.payload.rgbR.toInt(),
                            status.payload.rgbG.toInt(),
                            status.payload.rgbB.toInt()
                        )
                    )
                }
                
            }
        }
        isSubscribe.value = true
    }
    
    fun unListenTopic() {
        mqtt.mqttCallbackListener = null
        isSubscribe.value = false
    }
    
    fun getStatus() {
        MqttPublish.getStatus(mqtt, model, bssid)
    }
    
    fun turnOn() {
        MqttPublish.triggerAll(mqtt, true, model, bssid)
    }
    
    fun turnOff() {
        MqttPublish.triggerAll(mqtt, false, model, bssid)
    }
    
    fun mainChange(mBr: Int, mC: Int) {
        if (opMode.value == 1)
            MqttPublish.triggerMainMode(mqtt, 50, mBr, mC, model, bssid)
        else if (opMode.value == 3) {
            MqttPublish.mainBrCAdjust(mqtt, selectMode.value!!, 1, mBr, mC, model, bssid)
        }
    }
    
    fun nightChange(br: Int) {
        if (opMode.value == 2) {
            MqttPublish.triggerNightMode(mqtt, br, model, bssid)
        } else if (opMode.value == 3) {
            MqttPublish.triggerMode(
                mqtt,
                selectMode.value!!,
                br,
                devModel.value!!,
                devBssId.value!!
            )
        }
    }
    
    fun rgbChange(br: Int, rgb: IntArray) {
        if (rgbModeCounter >= 5) {
            MqttPublish.triggerRGBMode(mqtt, br, rgb, model, bssid)
            rgbModeCounter = 0
        } else {
            rgbModeCounter++
        }
    }
    
    fun rgbBrChange(br: Int, rgb: IntArray) {
        MqttPublish.triggerRGBMode(mqtt, br, rgb, model, bssid)
    }
    
    /** 節能 */
    fun mode50() {
        MqttPublish.triggerMainMode(mqtt, 50, 15, 15, devModel.value!!, devBssId.value!!)
    }
    
    /** 全光 */
    fun mode51() {
        MqttPublish.triggerMainMode(mqtt, 51, 20, 15, devModel.value!!, devBssId.value!!)
    }
    
    /** 夜燈 */
    fun mode53() {
        MqttPublish.triggerNightMode(mqtt, 10, devModel.value!!, devBssId.value!!)
    }
    
    /** RGB mode */
    fun mode12() {
        MqttPublish.triggerRGBMode(
            mqtt,
            3,
            intArrayOf(22, 0, 0),
            devModel.value!!,
            devBssId.value!!
        )
    }
    
    fun mode13() {
        MqttPublish.triggerMode(mqtt, 13, 5, devModel.value!!, devBssId.value!!)
    }
    
    fun mode14() {
        MqttPublish.triggerMode(mqtt, 14, 5, devModel.value!!, devBssId.value!!)
    }
    
    fun mode15() {
        MqttPublish.triggerMode(mqtt, 15, 5, devModel.value!!, devBssId.value!!)
    }
    
    fun callCustomMode(mode: Int) {
        MqttPublish.triggerCustomMode(mqtt, mode, 2, model, bssid)
    }
    
    fun saveCustomMode(mode: Int) {
        MqttPublish.triggerCustomMode(mqtt, mode, 1, model, bssid)
    }
    
    fun rAdjust(addLess: Int) {
        MqttPublish.mainRGBAdjust(mqtt, 13, addLess, devModel.value!!, devBssId.value!!)
    }
    
    fun gAdjust(addLess: Int) {
        MqttPublish.mainRGBAdjust(mqtt, 14, addLess, devModel.value!!, devBssId.value!!)
    }
    
    fun bAdjust(addLess: Int) {
        MqttPublish.mainRGBAdjust(mqtt, 15, addLess, devModel.value!!, devBssId.value!!)
    }
    
    fun modifyCustomModName(mode: Int, name: String): Boolean {
        val result = repository.remoteS.modifyCustomModeName(mode, name, devUuid)
        if (result) {
            val ceilingLight = repository.localS.getCeilingLightById(devUuid)
            if (ceilingLight != null) {
                when (mode) {
                    61 -> {
                        ceilingLight.cusMode1 = name
                    }
                    62 -> {
                        ceilingLight.cusMode2 = name
                    }
                    63 -> {
                        ceilingLight.cusMode3 = name
                    }
                    64 -> {
                        ceilingLight.cusMode4 = name
                    }
                }
                repository.localS.insertCeilingLight(ceilingLight)
            }
        }
        return result
    }
    
    fun mode01() {
        MqttPublish.triggerMode(mqtt, 1, 10, model, bssid)
    }
    
    fun mode02() {
        MqttPublish.triggerMode(mqtt, 2, 10, model, bssid)
    }
    
    fun mode03() {
        MqttPublish.triggerMode(mqtt, 3, 10, model, bssid)
    }
    
    fun mode04() {
        MqttPublish.triggerMode(mqtt, 4, 10, model, bssid)
    }
    
    fun mode05() {
        MqttPublish.triggerMode(mqtt, 5, 10, model, bssid)
    }
    
    fun mode06() {
//        MqttPublish.triggerMorningMode(mqtt, 1, 10, model, bssid)
    }
    
    fun mode07() {
        MqttPublish.triggerMode(mqtt, 7, 10, model, bssid)
    }
    
    fun mode08() {
        MqttPublish.triggerMode(mqtt, 8, 10, model, bssid)
    }
    
    fun mode09() {
        MqttPublish.triggerMode(mqtt, 9, 10, model, bssid)
    }
    
    /** 彩色 */
    fun mode10() {
        MqttPublish.triggerColorMode(mqtt, 10, 0, model, bssid)
    }
    
    fun colorModePlay() {
        MqttPublish.triggerColorMode(mqtt, 10, 2, model, bssid)
    }
    
    fun colorModePause() {
        MqttPublish.triggerColorMode(mqtt, 10, 1, model, bssid)
    }
    
    fun mode11() {
        MqttPublish.triggerMode(mqtt, 11, 10, model, bssid)
    }
}
