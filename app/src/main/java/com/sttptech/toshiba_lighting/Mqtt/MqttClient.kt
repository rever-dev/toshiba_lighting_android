package com.sttptech.toshiba_lighting.Mqtt

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.orhanobut.logger.Logger
import com.sttptech.toshiba_lighting.AppUtil.AppKey
import com.sttptech.toshiba_lighting.Mqtt.MqttTopic.CONFIG
import com.sttptech.toshiba_lighting.Mqtt.MqttTopic.CONTROL
import com.sttptech.toshiba_lighting.Mqtt.MqttTopic.DEVICE_CONFIG
import com.sttptech.toshiba_lighting.Mqtt.MqttTopic.GET_STATUS
import com.sttptech.toshiba_lighting.Mqtt.MqttTopic.TO_APP
import com.sttptech.toshiba_lighting.Mqtt.MqttTopic.TO_DEVICE
import com.sttptech.toshiba_lighting.Mqtt.MqttTopic.TO_SERVER_APP
import com.sttptech.toshiba_lighting.Mqtt.MqttTopicTag.STATUS
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import java.text.SimpleDateFormat
import java.util.*

class MqttClient private constructor(val context: Context) {
    
    companion object {
        private const val TAG = "MqttClient"
        
        private const val HOST = "tcp://intgr.sttptech.com:11883"
        
        private const val MQTT_USER_NAME = "GTOEM900"
        private const val MQTT_PASSWORD = "GTOEM900123"
        
        private const val BASE_TOPIC = "GWE/GWETSBLOEM/GTOEM900/"
        
        
        /**
         * 獲取MQTT Client，並初始化
         */
        fun getMqttClient(context: Context): MqttClient {
            val mqttClient = MqttClient(context)
            mqttClient.init()
            return mqttClient
        }
    }
    
    private var _client: MqttAndroidClient? = null
    private val client get() = _client!!
    
    // Mqtt action listener
    private val iMqttActionListener: IMqttActionListener = object : IMqttActionListener {
        override fun onSuccess(arg0: IMqttToken) {
            Logger.i("Mqtt connection successfully")
        }
        
        override fun onFailure(arg0: IMqttToken, arg1: Throwable) {
            Logger.e("Mqtt connection fail")
        }
    }
    private var conOpt: MqttConnectOptions? = null
    
    @SuppressLint("SimpleDateFormat")
    private fun getMqttClientID(context: Context): String {
        val bizPCode = "GTOEM900_113"
        val format = SimpleDateFormat("yyyyMMddHHmmss.sss")
        val timestamp = format.format(Date())
        val account = context.getSharedPreferences(AppKey.SHP_NAME, Context.MODE_PRIVATE)
            .getString(AppKey.SHP_ACCOUNT, null)
        
        val clientID = "$bizPCode:$account:$timestamp"
        
        Logger.i("Mqtt client id: $clientID")
        
        return clientID
    }
    
    /**
     * 初始化
     */
    private fun init() {
        
        val uri = HOST
        val clientID = getMqttClientID(context)
        _client = MqttAndroidClient(context, uri, clientID)
        // 設置Mqtt監聽並接收消息
        client.setCallback(mqttCallback)
        
        conOpt = MqttConnectOptions()
        // 清除緩存
        conOpt!!.isCleanSession = false
        // 設置超時時間，單位：秒
        conOpt!!.connectionTimeout = 0
        // 心跳包發送間隔，單位：秒
        conOpt!!.keepAliveInterval = 30
        
        conOpt!!.isAutomaticReconnect = true
        
        conOpt!!.maxInflight = 1000
        // 用戶名
        conOpt!!.userName = MQTT_USER_NAME
        // 密碼
        conOpt!!.password = MQTT_PASSWORD.toCharArray()
        
        Logger.i("Mqtt initialized successfully")
        
        // last will message
        var doConnect = true
        val message = "terminal_uid:$clientID"
        val topic: String = BASE_TOPIC
        val qos = 0
        val retained = false
        // 最後遺囑
        try {
            conOpt!!.setWill(topic, message.toByteArray(), qos, retained)
        } catch (e: Exception) {
            doConnect = false
            iMqttActionListener.onFailure(null, e)
        }
    }
    
    /**
     * 訂閱 Topic
     */
    fun subscribeTopic(model: String, bssid: String, tag: MqttTopicTag) {
        if (!client.isConnected) {
            client.connect(conOpt, context, iMqttActionListener)
        } else {
            when (tag) {
                MqttTopicTag.CONFIG -> {
                    client.subscribe(
                        "$BASE_TOPIC$DEVICE_CONFIG/${bssid.uppercase()}$TO_APP$CONFIG", 2
                    )
                    
                    Logger.i(
                        "Mqtt subscribe topic: \n$BASE_TOPIC$DEVICE_CONFIG/$bssid$TO_APP$CONFIG"
                    )
                }
                MqttTopicTag.STATUS -> {
                    client.subscribe(
                        "$BASE_TOPIC$model/$bssid$TO_SERVER_APP$GET_STATUS", 2
                    )
                    
                    Logger.i(
                        "Mqtt subscribe topic: \n$BASE_TOPIC$model/$bssid$TO_SERVER_APP$GET_STATUS"
                    )
                }
                else -> {}
            }
        }
    }
    
    /**
     * 取消訂閱
     */
    fun unSubscribe(model: String, bssid: String, tag: MqttTopicTag) {
        if (!client.isConnected) {
            client.connect(conOpt, context, iMqttActionListener)
        } else {
            when (tag) {
                MqttTopicTag.CONFIG -> {
                    client.unsubscribe(
                        "$BASE_TOPIC$DEVICE_CONFIG/${bssid.uppercase()}$TO_APP$CONFIG"
                    )
                    
                    Logger.i(
                        "Mqtt unsubscribe topic: \n$BASE_TOPIC$DEVICE_CONFIG/${bssid.uppercase()}$TO_APP$CONFIG"
                    )
                }
                STATUS -> {
                    client.unsubscribe(
                        "$BASE_TOPIC$model/${bssid.uppercase()}$TO_SERVER_APP$GET_STATUS"
                    )
                    
                    Logger.i(
                        "Mqtt unsubscribe topic: \n$BASE_TOPIC$model/${bssid.uppercase()}$TO_SERVER_APP$GET_STATUS"
                    )
                }
                else -> {}
            }
        }
    }
    
    /**
     * publish message to topic
     * */
    fun sendMsg(
        payload: String,
        model: String,
        bssid: String,
        tag: MqttTopicTag
    ) {
        try {
            if (!client.isConnected) {
                client.connect(conOpt, context, iMqttActionListener)
            } else {
                Thread {
                    val message = MqttMessage(payload.toByteArray())
                    message.qos = 2
                    val action: String = when (tag) {
                        MqttTopicTag.CONFIG -> CONFIG
                        MqttTopicTag.CONTROL -> CONTROL
                        STATUS -> GET_STATUS
                    }
    
                    client.publish(
                        "$BASE_TOPIC$model/$bssid${TO_DEVICE}$action", message, context,
                        object : IMqttActionListener {
                            override fun onSuccess(asyncActionToken: IMqttToken) {
                                Logger.i(
                                    "Mqtt publish message" +
                                            "\ntopic: $BASE_TOPIC$model/$bssid${TO_DEVICE}$action" +
                                            "\npayload: $message"
                                )
                            }
            
                            override fun onFailure(
                                asyncActionToken: IMqttToken,
                                exception: Throwable
                            ) {
                                Logger.i(
                                    "Mqtt publish fail" +
                                            "\n$exception"
                                )
                            }
                        })
                }.start()
            }
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }
    
    /**
     * 判斷網絡是否連接
     */
    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        
        when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                Logger.i("Mqtt transport type: Wi-Fi")
                return true
            }
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                Logger.i(TAG, "Mqtt transport type: Cellular-Net")
                return true
            }
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                Logger.i(TAG, "Mqtt transport type: Ethernet")
                return true
            }
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> {
                Logger.i(TAG, "Mqtt transport type: Bluetooth")
                return true
            }
        }
        
        Logger.i("Mqtt no network available")
        return false
    }
    
    /**
     * 連接 MQTT Server
     */
    fun doClientConnection(): Boolean {
        if (!client.isConnected && isNetworkAvailable(context)) {
            return try {
                client.connect(conOpt, context, iMqttActionListener)
                true
            } catch (e: MqttException) {
                e.printStackTrace()
                false
            }
        }
        return false
    }
    
    /**
     * 中斷 Mqtt 連線
     */
    fun disClientConnection(): Boolean {
        return try {
            if (client.isConnected) {
                client.disconnect()
                return true
            }
            false
        } catch (e: MqttException) {
            e.printStackTrace()
            false
        }
    }
    
    interface MqttCallbackListener {
        fun msgArrived(topic: String, msg: String)
    }
    
    var mqttCallbackListener: MqttCallbackListener? = null
    
    /**
     * MQTT 監聽並接收回傳
     */
    private val mqttCallback: MqttCallback = object : MqttCallback {
        override fun messageArrived(topic: String, message: MqttMessage) {
    
            if (mqttCallbackListener != null) {
                Thread {
                    Logger.i("Mqtt message arrived: $topic")
                    Logger.json(message.toString())
    
                    mqttCallbackListener!!.msgArrived(topic, message.toString())
                }.start()
            }
        }
        
        override fun deliveryComplete(arg0: IMqttDeliveryToken) {
            // 交付完成
        }
        
        override fun connectionLost(arg0: Throwable) {
            // 失去連接 重連
        }
    }
}