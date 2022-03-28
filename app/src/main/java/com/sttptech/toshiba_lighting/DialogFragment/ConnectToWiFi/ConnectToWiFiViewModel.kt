package com.sttptech.toshiba_lighting.DialogFragment.ConnectToWiFi

import android.app.Application
import android.content.Context
import android.net.wifi.WifiManager
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.espressif.iot.esptouch.util.ByteUtil
import com.espressif.iot.esptouch.util.TouchNetUtil
import com.sttptech.toshiba_lighting.Activity.Main.MainActivity
import com.sttptech.toshiba_lighting.Application.BaseApplication

class ConnectToWiFiViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val TAG: String = "ConnectToWiFi"
    }

    var wifiName: MutableLiveData<String?> = MutableLiveData(null)
    var ssid: MutableLiveData<ByteArray?> = MutableLiveData(null)
    var bssidByte: MutableLiveData<ByteArray?> = MutableLiveData(null)
    var pwdVisi: MutableLiveData<Boolean> = MutableLiveData(false)

    /**獲取 WiFi 資訊 */
    fun getWifiInfo(): String? {
        val manager =
            getApplication<BaseApplication>().applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return if (manager.isWifiEnabled) {
            val info = manager.connectionInfo
            wifiName.value = info.ssid.substring(1, info.ssid.length - 1)
            val bssid = info.bssid
            ssid.value = ByteUtil.getBytesByString(wifiName.value)

            // TODO: 2022/1/13 If wifi status changing -> NullPointerException
            bssidByte.value = TouchNetUtil.parseBssid2bytes(bssid)
            Log.d(
                TAG,
                "SSID: ${wifiName.value}" +
                        "\nBSSID: $bssid" +
                        "\nWiFi Name: ${wifiName.value}"
            )
            wifiName.value!!
        } else
            null
    }

    fun startESP(activity: FragmentActivity, pwd: String?) {
        if (ssid.value != null)
            if (bssidByte.value != null)
                (activity as MainActivity).esptouchStart(
                    ssid.value!!,
                    bssidByte.value!!,
                    (if (pwd == null) "" else pwd).toByteArray(),
                    activity
                )
    }
}