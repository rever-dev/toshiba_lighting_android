package com.sttptech.toshibalight.Activity.MainActivity

import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.espressif.iot.esptouch.EsptouchTask
import com.espressif.iot.esptouch.IEsptouchResult
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.sttptech.toshiba_lighting.Application.BaseApplication
import com.sttptech.toshiba_lighting.Application.BaseApplication.Companion.mqttClient
import com.sttptech.toshiba_lighting.Mqtt.MqttClient
import com.sttptech.toshiba_lighting.Mqtt.MqttTopic
import com.sttptech.toshiba_lighting.Mqtt.MqttTopicTag
import org.eclipse.paho.client.mqttv3.MqttException

object EsptouchWork {

    interface EsptouchCallback {
        fun findDevice(result: IEsptouchResult)
    }

    const val KEY_SSID = "KEY_SSID"
    const val KEY_BSSID = "KEY_BSSID"
    const val KEY_PWD = "KEY_PWD"
    const val KEY_TIME = "KEY_TIME"
    const val KEY_RESULT = "KEY_RESULT"
    var esptouchTask: EsptouchTask? = null
    var esptouchCallback: EsptouchCallback? = null
    
    class StartWork(context: Context, workerParams: WorkerParameters) :
        Worker(context, workerParams) {
        override fun doWork(): Result {
            val mqttClient: MqttClient = BaseApplication.mqttClient
            esptouchTask = EsptouchTask(
                inputData.getByteArray(KEY_SSID),
                inputData.getByteArray(KEY_BSSID),
                inputData.getByteArray(KEY_PWD),
                applicationContext
            )
            esptouchTask!!.setEsptouchListener { result: IEsptouchResult ->
                esptouchCallback?.findDevice(result)
                resultMqttEvent(result)
            }
            val result = esptouchTask!!.executeForResults(-1)
            val data: Data = Data.Builder()
                .putBoolean(KEY_RESULT, result[0].isSuc)
                .build()
            return Result.success(data)
        }
    }

    class StopWork(context: Context, workerParams: WorkerParameters) :
        Worker(context, workerParams) {
        override fun doWork(): Result {
            return if (esptouchTask != null) {
                esptouchTask?.interrupt()
                esptouchCallback = null
                Result.success()
            } else {
                esptouchCallback = null
                Result.failure()
            }
        }
    }

    fun resultMqttEvent(result: IEsptouchResult) {

        val json = JsonObject()
        json.addProperty("config", "CONFIGURATION")

        try {
            mqttClient.subscribeTopic(
                MqttTopic.DEVICE_CONFIG,
                result.bssid.uppercase(),
                MqttTopicTag.CONFIG
            )
        } catch (e: MqttException) {
            e.printStackTrace()
        }
        try {
            synchronized(this) { Thread.sleep(2_000) }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        mqttClient.sendMsg(
            Gson().toJson(json),
            MqttTopic.DEVICE_CONFIG,
            result.bssid.uppercase(),
            MqttTopicTag.CONFIG
        )
    }
}