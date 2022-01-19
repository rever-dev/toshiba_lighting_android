package com.sttptech.toshiba_lighting.Mqtt

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.sttptech.toshiba_lighting.AppUtil.KeyOfShp
import com.sttptech.toshiba_lighting.Mqtt.MqttTopic.CONFIG
import com.sttptech.toshiba_lighting.Mqtt.MqttTopic.CONTROL
import com.sttptech.toshiba_lighting.Mqtt.MqttTopic.DEVICE_CONFIG
import com.sttptech.toshiba_lighting.Mqtt.MqttTopic.GET_STATUS
import com.sttptech.toshiba_lighting.Mqtt.MqttTopic.TO_APP
import com.sttptech.toshiba_lighting.Mqtt.MqttTopic.TO_DEVICE
import com.sttptech.toshiba_lighting.Mqtt.MqttTopicAgreement.STATUS
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
            val mqttClient: MqttClient = MqttClient(context)
            mqttClient.init()
            return mqttClient
        }
    }

    private var _client: MqttAndroidClient? = null
    private val client get() = _client!!

    // Mqtt action listener
    private val iMqttActionListener: IMqttActionListener = object : IMqttActionListener {
        override fun onSuccess(arg0: IMqttToken) {
            Log.d(TAG, "＊＊＊＊＊MQTT CONNECTED SUCCESSFULLY＊＊＊＊＊")
        }

        override fun onFailure(arg0: IMqttToken, arg1: Throwable) {
            arg1.printStackTrace()
            Log.d(TAG, "＊＊＊＊＊MQTT CONNECTION FAILED＊＊＊＊＊")
        }
    }
    private var conOpt: MqttConnectOptions? = null

    @SuppressLint("SimpleDateFormat")
    private fun getMqttClientID(context: Context): String {
        val bizPCode = "GTOEM900_113"
        val format = SimpleDateFormat("yyyyMMddHHmmss.sss")
        val timestamp = format.format(Date())
        val account = context.getSharedPreferences(KeyOfShp.SHP_NAME, Context.MODE_PRIVATE)
            .getString(KeyOfShp.SHP_ACCOUNT, null)

        val clientID = "$bizPCode:$account:$timestamp"
        Log.d(
            TAG, "＊＊＊＊＊MQTT CLIENT＊＊＊＊＊" +
                    "\n$clientID" +
                    "\n＊＊＊＊＊mqtt client＊＊＊＊＊"
        )

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
        // 用戶名
        conOpt!!.userName = MQTT_USER_NAME
        // 密碼
        conOpt!!.password = MQTT_PASSWORD.toCharArray()

        Log.d(TAG, "＊＊＊＊＊MQTT INITIALIZED SUCCESSFULLY＊＊＊＊＊")

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

            Log.d(TAG, "Exception Occurred", e)

            doConnect = false
            iMqttActionListener.onFailure(null, e)
        }
    }

    /**
     * 訂閱 Topic
     */
    fun subscribeTopic(model: String, bssid: String, agreement: MqttTopicAgreement) {
        if (!client.isConnected) {
            client.connect(conOpt, null, iMqttActionListener)
        }
        when (agreement) {
            MqttTopicAgreement.CONFIG -> {
                client.subscribe(
                    "$BASE_TOPIC$DEVICE_CONFIG/${bssid.uppercase()}$TO_APP$CONFIG", 2
                )
                Log.d(
                    TAG,
                    "＊＊＊＊＊SUBSCRIBE TOPIC＊＊＊＊＊" +
                            "\n$BASE_TOPIC$DEVICE_CONFIG/${bssid.uppercase()}$TO_APP$CONFIG" +
                            "\n＊＊＊＊＊subscribe topic＊＊＊＊＊"
                )
            }
            MqttTopicAgreement.STATUS -> {
                client.subscribe(
                    "$BASE_TOPIC$model/${bssid.uppercase()}$TO_APP$GET_STATUS", 2
                )
                Log.d(
                    TAG,
                    "＊＊＊＊＊SUBSCRIBE TOPIC＊＊＊＊＊" +
                            "\n$BASE_TOPIC$model/${bssid.uppercase()}$TO_APP$GET_STATUS" +
                            "\n＊＊＊＊＊subscribe topic＊＊＊＊＊"
                )
            }
            else -> {}
        }
    }

    /**
     * 取消訂閱
     */
    fun unSubscribe(model: String, bssid: String, agreement: MqttTopicAgreement) {
        if (!client.isConnected) {
            client.connect(conOpt, null, iMqttActionListener)
        }
        when (agreement) {
            MqttTopicAgreement.CONFIG -> {
                client.unsubscribe(
                    "$BASE_TOPIC$DEVICE_CONFIG/${bssid.uppercase()}$TO_APP$CONFIG"
                )
                Log.d(
                    TAG,
                    "＊＊＊＊＊UNSUBSCRIBE TOPIC＊＊＊＊＊" +
                            "\n$BASE_TOPIC$DEVICE_CONFIG/${bssid.uppercase()}$TO_APP$CONFIG" +
                            "\n＊＊＊＊＊unsubscribe topic＊＊＊＊＊"
                )
            }
            STATUS -> {
                client.unsubscribe(
                    "$BASE_TOPIC$model/${bssid.uppercase()}$TO_APP$GET_STATUS"
                )
                Log.d(
                    TAG,
                    "＊＊＊＊＊UNSUBSCRIBE TOPIC＊＊＊＊＊" +
                            "\n$BASE_TOPIC$model/${bssid.uppercase()}$TO_APP$GET_STATUS" +
                            "\n＊＊＊＊＊unsubscribe topic＊＊＊＊＊"
                )
            }
            else -> {}
        }
    }

    fun sendMsg(
        payload: String,
        model: String,
        bssid: String,
        agreement: MqttTopicAgreement
    ) {
        try {
            if (!client.isConnected) {
                client.connect(conOpt, null, iMqttActionListener)
            }
            val message = MqttMessage(payload.toByteArray())
            message.qos = 2
            val action: String = when (agreement) {
                MqttTopicAgreement.CONFIG -> CONFIG
                MqttTopicAgreement.CONTROL -> CONTROL
                STATUS -> GET_STATUS
            }

            client.publish(
                "$BASE_TOPIC$model/$bssid${TO_DEVICE}$action", message, context,
                object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken) {
                        Log.d(
                            TAG, "＊＊＊＊＊PUBLISH MSG＊＊＊＊＊" +
                                    "\nTOPIC : $BASE_TOPIC$model/$bssid${TO_DEVICE}$action" +
                                    "\nMSG : $message" +
                                    "\n＊＊＊＊＊publish msg＊＊＊＊＊"
                        )
                    }

                    override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                        Log.e(
                            TAG,
                            "ＸＸＸＸＸPUBLISH FAILＸＸＸＸＸ" +
                                    "\n$exception"
                        )
                    }
                })
        } catch (e: MqttException) {

            Log.e(TAG, e.toString())

            e.printStackTrace()
        }
    }

    fun connection() {
        if (!client.isConnected && isNetworkAvailable(context)) {
            try {
                client.connect(conOpt, null, iMqttActionListener)
            } catch (e: MqttException) {
                e.printStackTrace()
            }
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
                Log.d(TAG, "＊＊＊＊＊MQTT TRANSPORT : WI-FI＊＊＊＊＊")
                return true
            }
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                Log.d(TAG, "＊＊＊＊＊MQTT TRANSPORT : CELLULAR-NET＊＊＊＊＊")
                return true
            }
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                Log.d(TAG, "＊＊＊＊＊MQTT TRANSPORT : ETHER-NET＊＊＊＊＊")
                return true
            }
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> {
                Log.d(TAG, "＊＊＊＊＊MQTT TRANSPORT : WIFI＊＊＊＊＊")
                return true
            }
        }

        Log.d(TAG, "ＸＸＸＸＸMQTT NO NETWORK AVAILABLEＸＸＸＸＸ")
        return false
    }

    /**
     * 連接 MQTT Server
     */
    fun doClientConnection(): Boolean {
        if (!client.isConnected && isNetworkAvailable(context)) {
            return try {
                client.connect(conOpt, null, iMqttActionListener)
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

            mqttCallbackListener?.msgArrived(topic, message.toString())

            Log.d(
                TAG, "＊＊＊＊＊MSG ARRIVED＊＊＊＊＊" +
                        "\nTOPIC : $topic" +
                        "\nMSG : $message" +
                        "\n＊＊＊＊＊msg arrived＊＊＊＊＊"
            )
        }

        override fun deliveryComplete(arg0: IMqttDeliveryToken) {
            // 交付完成
        }

        override fun connectionLost(arg0: Throwable) {
            // 失去連接 重連
        }
    }
}