package com.sttptech.toshiba_lighting.Activity.Main

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.espressif.iot.esptouch.EsptouchTask
import com.espressif.iot.esptouch.IEsptouchResult
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.orhanobut.logger.Logger
import com.sttptech.toshiba_lighting.AppUtil.KeyOfShp
import com.sttptech.toshiba_lighting.AppUtil.PermissionUtil
import com.sttptech.toshiba_lighting.Application.BaseApplication
import com.sttptech.toshiba_lighting.Data.Bean.CeilingLight
import com.sttptech.toshiba_lighting.Data.Bean.Group
import com.sttptech.toshiba_lighting.DialogFragment.PairDevice.PairDeviceDialogFragment
import com.sttptech.toshiba_lighting.Fragment.Device.DeviceList.DeviceListFragment
import com.sttptech.toshiba_lighting.Mqtt.MqttClient
import com.sttptech.toshiba_lighting.Mqtt.MqttTopic
import com.sttptech.toshiba_lighting.Mqtt.MqttTopicAgreement
import com.sttptech.toshiba_lighting.R
import com.sttptech.toshiba_lighting.RetrofitUtil.InfoListRes
import org.eclipse.paho.client.mqttv3.MqttException
import java.sql.Timestamp


class MainActivity : AppCompatActivity(),
    MqttClient.MqttCallbackListener,
    PairDeviceDialogFragment.PairFinishCallback {
    
    companion object {
        
        private const val TAG: String = "MainActivity"
    }
    
    private fun showLoading() =
        BaseApplication.loadingView.show(supportFragmentManager, null)
    
    private fun dismissLoading() =
        try {
            BaseApplication.loadingView.dismiss()
        } catch (e: Exception) {
        }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        val bottomNav = findViewById<BottomNavigationView>(R.id.main_bottomNav)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_fragContainer) as NavHostFragment?
        val navController = navHostFragment!!.navController
        NavigationUI.setupWithNavController(bottomNav, navController)
        
        PermissionUtil.requestPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
        
        checkLoginStatus()
        getDataFromServer()
    }
    
    private fun checkLoginStatus(): Boolean {
        applicationContext.getSharedPreferences(KeyOfShp.SHP_NAME, MODE_PRIVATE).apply {
            Logger.i(
                "\nLogin: ${getBoolean(KeyOfShp.SHP_LOGIN, false)}" +
                        "\nAccount: ${getString(KeyOfShp.SHP_ACCOUNT, null)}" +
                        "\nToken: ${getString(KeyOfShp.SHP_TOKEN, null)}"
            )
            
            return getBoolean(KeyOfShp.SHP_LOGIN, false) &&
                    getString(KeyOfShp.SHP_ACCOUNT, null) != null &&
                    getString(KeyOfShp.SHP_TOKEN, null) != null
        }
    }
    
    private fun getDataFromServer() {
        showLoading()
        Thread {
            val remoteService = BaseApplication.repository.remoteS
            val info = remoteService.getInfoList()
            if (info != null) {
                syncDevice2DB(info)
            }
            runOnUiThread { dismissLoading() }
        }.start()
    }
    
    /**
     * sync device data to DB
     */
    private fun syncDevice2DB(response: InfoListRes) {
        // Clear DB data
        BaseApplication.repository.localS.clearCeilingLightTable()
        
        for (own in response.datum.owns) {
            val device = CeilingLight(own.info.devSn)
            
            device.uId = own.info.devUuid
            
            device.name = (
                    if (own.infoD.parseConfPC.devName.isEmpty())
                        "吸頂燈"
                    else
                        own.infoD.parseConfPC.devName
                    )
            device.model = own.info.prodScode
            
            device.wakeUp =
                Timestamp.valueOf(own.infoD.parseConfSW.rcuSetting.dailyWakeUp).time
            
            device.wakeUpS =
                own.infoD.parseConfSW.rcuSetting.dailyWakeUpS == "O"
            
            device.dailyOn =
                Timestamp.valueOf(own.infoD.parseConfSW.rcuSetting.dailyOpen).time
            
            device.dailyOnS =
                own.infoD.parseConfSW.rcuSetting.dailyOpenS == "O"
            
            device.dailyOff =
                Timestamp.valueOf(own.infoD.parseConfSW.rcuSetting.dailyClose).time
            
            device.dailyOffS =
                own.infoD.parseConfSW.rcuSetting.dailyCloseS == "O"
            
            device.cusMode1 = own.infoD.parseConfSW.rcuModes[0].cn
            device.cusMode2 = own.infoD.parseConfSW.rcuModes[1].cn
            device.cusMode3 = own.infoD.parseConfSW.rcuModes[2].cn
            device.cusMode4 = own.infoD.parseConfSW.rcuModes[3].cn
            
            // 找到 device 所屬群組，將其設定
            for (ownGroups in response.datum.ownGroups) {
                if (ownGroups.devUuids.contains(device.uId)) {
                    device.group =
                        Group(ownGroups.groupUuid, ownGroups.groupName, "N", null)
                    break
                }
            }
            
            BaseApplication.mqttClient.subscribeTopic(
                device.model!!,
                device.macId,
                MqttTopicAgreement.STATUS
            )
            
            BaseApplication.repository.localS.insertCeilingLight(device)
        }
    }
    
    override fun onStop() {
        super.onStop()
        esptouchStop()
    }
    
    override fun pairFinish() {
        pairPage = null
        
        var fragment = (supportFragmentManager.primaryNavigationFragment as NavHostFragment)
            .childFragmentManager.primaryNavigationFragment
        
        if (fragment!!::class.java == DeviceListFragment::class.java) {
            (fragment as DeviceListFragment).refreshList()
        }
    }

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * TOUCH TASK WORK - ESP TOUCH TASK WORK - ESP TOUCH TASK WORK - ESP TOUCH TASK  *
 *                                                                               *
 * WORK ESP TOUCH TASK WORK - ESP TOUCH TASK WORK - ESP TOUCH TASK WORK - ESP TO *
 *                                                                               *
 * P TOUCH TASK- ESP TOUCH TASK WORK - ESP TOUCH TASK WORK - ESP TOUCH TASK WORK *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

//    private var espWork: OneTimeWorkRequest? = null
//
//    fun startEsp(infoData: Data) {
//        showLoading()
//        espWork = OneTimeWorkRequest.Builder(EsptouchWork.StartWork::class.java)
//            .setInputData(infoData)
//            .build()
//        WorkManager.getInstance(this).getWorkInfoByIdLiveData(espWork!!.id)
//            .observe(this, observer)
//        BaseApplication.mqttClient.mqttCallbackListener = this
//        WorkManager.getInstance(this).enqueue(espWork!!)
//    }
//
//    fun cancelEsp() {
//        if (espWork != null) {
//            WorkManager.getInstance(this)
//                .getWorkInfoByIdLiveData(espWork!!.id).removeObserver(observer)
//            espWork = OneTimeWorkRequest.Builder(StopWork::class.java)
//                .build()
//            WorkManager.getInstance(this).enqueue(espWork!!)
//            BaseApplication.mqttClient.mqttCallbackListener = null
//            espWork = null
//        }
//    }
//
//    private val observer = Observer { workInfo: WorkInfo? ->
//        if (workInfo != null && workInfo.state.isFinished) {
//            dismissLoading()
//        }
//    }
//
    
    private var espTask: EsptouchTask? = null
    
    fun esptouchStart(ssid: ByteArray, bssid: ByteArray, pwd: ByteArray, context: Context) {
        showLoading()
        BaseApplication.mqttClient.mqttCallbackListener = this
        if (espTask == null) {
            Thread {
                espTask = EsptouchTask(ssid, bssid, pwd, context)
                espTask?.setEsptouchListener { result: IEsptouchResult -> resultMqttEvent(result) }
                // TODO: 2022/1/13 result not used
                var result = espTask?.executeForResults(-1)
                
                BaseApplication.mqttClient.mqttCallbackListener = null
                espTask = null
                
                if (!result!![0].isSuc) {
                    dismissLoading()
                    // TODO: 2022/1/13 change to AlertDialog 
                    runOnUiThread { Toast.makeText(this, "no search any device.", Toast.LENGTH_SHORT).show() }
                }
            }.start()
        }
        
        Logger.i("EspTouch Start")
    }
    
    fun esptouchStop() {
        if (espTask != null && !espTask!!.isCancelled) {
            espTask?.interrupt()
            dismissLoading()
        }
        
        Logger.i("EspTouch interrupt")
    }
    
    private fun resultMqttEvent(result: IEsptouchResult) {
        
        val json = JsonObject()
        json.addProperty("config", "CONFIGURATION")
        
        try {
            BaseApplication.mqttClient.subscribeTopic(
                MqttTopic.DEVICE_CONFIG,
                result.bssid.uppercase(),
                MqttTopicAgreement.CONFIG
            )
        } catch (e: MqttException) {
            e.printStackTrace()
        }
        try {
            synchronized(this) { Thread.sleep(2_000) }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        BaseApplication.mqttClient.sendMsg(
            Gson().toJson(json),
            MqttTopic.DEVICE_CONFIG,
            result.bssid.uppercase(),
            MqttTopicAgreement.CONFIG
        )
    }
    
    private var pairPage: PairDeviceDialogFragment? = null
    
    /**
     * Mqtt callback
     * */
    @Synchronized
    override fun msgArrived(topic: String, msg: String) {
        if (topic.contains("AD_CONFIG")) {
            val dev = CeilingLight(topic.substring(34, 46))
            dev.model =
                Gson().fromJson(msg, JsonObject::class.java).get("devicename").asString
            
            runOnUiThread {
                if (pairPage == null) {
                    pairPage = PairDeviceDialogFragment(dev)
                    pairPage?.addCallback(this)
                    pairPage?.show(supportFragmentManager, null)
                } else {
                    pairPage!!.addDevice(dev)
                }
            }
        }
    }
}
