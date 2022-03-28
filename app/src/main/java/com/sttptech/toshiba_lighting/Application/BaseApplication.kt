package com.sttptech.toshiba_lighting.Application

import android.app.Application
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.FormatStrategy
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
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
    
        val formatStrategy: FormatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(true) // (Optional) Whether to show thread info or not. Default true
            .methodCount(0) // (Optional) How many method line to show. Default 2
            .methodOffset(6) // (Optional) Hides internal method calls up to offset. Default 5
            .tag("logger_debugger") // (Optional) Global tag for every log. Default PRETTY_LOGGER
            .build()

        Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))
    }
}