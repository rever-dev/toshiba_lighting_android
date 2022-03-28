package com.sttptech.toshiba_lighting.DialogFragment.PairDevice

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.sttptech.toshiba_lighting.Application.BaseApplication
import com.sttptech.toshiba_lighting.Data.Bean.CeilingLight
import com.sttptech.toshiba_lighting.Data.Bean.Device
import com.sttptech.toshiba_lighting.Mqtt.MqttTopic
import com.sttptech.toshiba_lighting.Mqtt.MqttTopicTag

class PairDeviceViewModel(application: Application) : AndroidViewModel(application) {

    companion object {

        private const val TAG = "PairDevice"
    }

    private val mainHandler = Handler(Looper.getMainLooper())

    // Status
    /**
     * 0 : default
     * 1 : finish
     * 2 : fail
     * */
    val signupStatus = MutableLiveData(0)

    // Data
    val espList = MutableLiveData<MutableList<Device>>(mutableListOf())
    val pairList = MutableLiveData<MutableList<Device>>(mutableListOf())
    val sucList = MutableLiveData<MutableList<Device>>(mutableListOf())


    fun mqttIdentify(dev: Device) {
        var json: JsonObject = JsonObject()
        json.addProperty("config", "IDENTIFY")
        BaseApplication.mqttClient
            .sendMsg(
                Gson().toJson(json),
                MqttTopic.DEVICE_CONFIG,
                dev.macId,
                MqttTopicTag.CONFIG
            )
    }


    /**
     * If device checked or unchecked
     * */
    fun selDev(checked: Boolean, dev: Device) {
        if (checked)
            pairList.value?.add(dev)
        else
            for (remDev in pairList.value!!) {
                if (dev.macId == remDev.macId) {
                    pairList.value!!.remove(remDev)
                    break
                }
            }

        Log.d(
            TAG,
            "＊＊＊＊＊PAIR DEVICE LIST＊＊＊＊＊" +
                    "\n${pairList.value!!}" +
                    "\n＊＊＊＊＊pair device list＊＊＊＊＊"
        )
    }

    fun startPair() {

        if (pairList.value!!.size == 0) {
            signupStatus.value = 2 // Fail
            return
        }

        Thread {
            // Sign up every pair device
            for (dev in pairList.value!!) {
                val response = BaseApplication.repository
                    .remoteS.devSignup(dev.macId, dev.group!!.groupName, dev.name!!, dev.model!!)

                // If sign up fail, reset device network status
                if (response == null || !response.isSuccess())
                    networkReset(dev)
                else {
                    // If success sucList add it
                    val ceilingLight = CeilingLight(dev.macId)
                    ceilingLight.uId = response.getDatum()?.info?.devUuid
                    ceilingLight.model = dev.model
                    ceilingLight.name = dev.name
                    ceilingLight.group = response.getDatum()?.group!!

                    // Save to local DB
                    BaseApplication.repository
                        .localS.insertCeilingLight(ceilingLight)

                    mainHandler.post {
                        sucList.value!!.add(dev)
                    }
                }

                // Remove from espList
                for (remove in espList.value!!) {
                    if (remove.macId == dev.macId) {
                        espList.value!!.remove(remove)
                        break
                    }
                }
            }

            // Clear network status from espList
            mqttNetworkResetAll()
        }.start()
    }

    fun mqttNetworkResetAll() {
        for (dev in espList.value!!) {
            networkReset(dev)
        }

        mainHandler.post { signupStatus.value = 1 }
    }

    private fun networkReset(dev: Device) {
        val json = JsonObject()
        json.addProperty("config", "NETWORKRESET")

        BaseApplication.mqttClient
            .sendMsg(
                Gson().toJson(json),
                MqttTopic.DEVICE_CONFIG,
                dev.macId,
                MqttTopicTag.CONFIG
            )

        Log.d(
            TAG,
            "＊＊＊＊＊Network reset mac id : ${dev.macId}＊＊＊＊＊"
        )
    }
}