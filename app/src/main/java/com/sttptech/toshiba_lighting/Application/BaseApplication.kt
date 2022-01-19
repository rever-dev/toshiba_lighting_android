package com.sttptech.toshiba_lighting.Application

import android.app.Application
import com.sttptech.toshiba_lighting.CustomView.LoadingView
import com.sttptech.toshiba_lighting.Mqtt.MqttClient

class BaseApplication : Application() {

    companion object {
        lateinit var mqttClient: MqttClient
        lateinit var repository: Repository
        lateinit var loadingView: LoadingView
    }

    override fun onCreate() {
        super.onCreate()
        mqttClient = MqttClient.getMqttClient(baseContext)
        mqttClient.doClientConnection()
        repository = Repository(baseContext)
        loadingView = LoadingView()
    }
}