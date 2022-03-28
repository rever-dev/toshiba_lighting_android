package com.sttptech.toshiba_lighting.Activity.Main

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.espressif.iot.esptouch2.provision.EspProvisioner
import com.espressif.iot.esptouch2.provision.EspProvisioningListener
import com.espressif.iot.esptouch2.provision.EspProvisioningRequest
import com.espressif.iot.esptouch2.provision.EspProvisioningResult
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.orhanobut.logger.Logger
import com.sttptech.toshiba_lighting.AppUtil.AppKey
import com.sttptech.toshiba_lighting.AppUtil.PermissionUtil
import com.sttptech.toshiba_lighting.Application.BaseApplication
import com.sttptech.toshiba_lighting.Data.Bean.CeilingLight
import com.sttptech.toshiba_lighting.Data.Bean.Group
import com.sttptech.toshiba_lighting.Data.Bean.Scene
import com.sttptech.toshiba_lighting.DialogFragment.PairDevice.PairDeviceDialogFragment
import com.sttptech.toshiba_lighting.Fragment.Device.DeviceList.DeviceListFragment
import com.sttptech.toshiba_lighting.Mqtt.MqttClient
import com.sttptech.toshiba_lighting.Mqtt.MqttTopic
import com.sttptech.toshiba_lighting.Mqtt.MqttTopicTag
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
    
    
        // 檢查 如果是在以下頁面無法使用Navigation
        val dcl =
            NavController.OnDestinationChangedListener { controller: NavController?, destination: NavDestination, arguments: Bundle? ->
                val idList: MutableList<Int> = ArrayList()
                idList.add(R.id.deviceControlFragment)
                idList.add(R.id.deviceSettingsFragment)
                idList.add(R.id.sceneCreateFragment)
                if (idList.contains(destination.id)) {
                    bottomNav.visibility = View.GONE
                } else {
                    bottomNav.visibility = View.VISIBLE
                }
            }
        navController.addOnDestinationChangedListener(dcl)
    
        PermissionUtil.requestPermission(
            this,
            PermissionUtil.PERMISSION_REQUEST_FINE_LOCATION
        )
    
        if (checkLoginStatus())
            getDataFromServer()
    }
    
    private fun checkLoginStatus(): Boolean {
        applicationContext.getSharedPreferences(AppKey.SHP_NAME, MODE_PRIVATE).apply {
            Logger.i(
                "\nLogin: ${getBoolean(AppKey.SHP_LOGIN, false)}" +
                        "\nAccount: ${getString(AppKey.SHP_ACCOUNT, null)}" +
                        "\nToken: ${getString(AppKey.SHP_TOKEN, null)}"
            )
        
            return getBoolean(AppKey.SHP_LOGIN, false) &&
                    getString(AppKey.SHP_ACCOUNT, null) != null &&
                    getString(AppKey.SHP_TOKEN, null) != null
        }
    }
    
    private fun getDataFromServer() {
        showLoading()
        Thread {
            val remoteService = BaseApplication.repository.remoteS
            val info = remoteService.getInfoList()
            if (info != null) {
                syncDevice2DB(info)
                syncGroup2DB(info)
                syncScene2DB(info)
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
                MqttTopicTag.STATUS
            )
    
            BaseApplication.repository.localS.insertCeilingLight(device)
        }
    }
    
    /**
     * sync groups data to DB
     */
    private fun syncGroup2DB(response: InfoListRes) {
        // Clear DB data
        BaseApplication.repository.localS.clearGroupTable()
        
        for (ownGroups in response.datum.ownGroups) {
            val group = Group(ownGroups.groupName)
            
            group.groupUuid = ownGroups.groupUuid
            group.groupDef = ownGroups.groupDef
            group.devUuids = ownGroups.devUuids
            
            BaseApplication.repository.localS.insertGroup(group)
        }
    }
    
    private fun syncScene2DB(info: InfoListRes) {
        // Clear DB data
        BaseApplication.repository.localS.clearSceneTable()
        
        for (ownScene in info.datum.ownGrsituations) {
            val scene = Scene(ownScene.grsituationUuid)
            scene.name = ownScene.grsituationName
            scene.seq = ownScene.grsituationSeq
            scene.def = ownScene.grsituationDef
            scene.devUuids = ownScene.devUuids
            scene.order = ownScene.grsituationOrder
            scene.imageUrl = ownScene.grsituationImage
            
            if (scene.imageUrl != null && scene.imageUrl!!.isNotEmpty())
                scene.image = BaseApplication.repository.remoteS.getSceneImage(scene.imageUrl!!)
            
            BaseApplication.repository.localS.insertScene(scene)
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
    
    private var espV2Task: EspProvisioner? = null
    
    fun esptouchStart(ssid: ByteArray, bssid: ByteArray, pwd: ByteArray, context: Context) {
        showLoading()
        BaseApplication.mqttClient.mqttCallbackListener = this
        if (espV2Task == null) {
            Thread {
                espV2Task = EspProvisioner(this)
                val espReq = EspProvisioningRequest.Builder(this)
                    .setSSID(ssid)
                    .setBSSID(bssid)
                    .setPassword(pwd)
                    .setReservedData(null)
                    .build()
            
                espV2Task!!.startProvisioning(espReq, object : EspProvisioningListener {
                    override fun onStart() {
                    
                    }
                
                    override fun onResponse(result: EspProvisioningResult?) {
                        if (result == null) return
                    
                        val bssid = result.bssid.uppercase().replace(":", "")
                    
                        val json = JsonObject()
                        json.addProperty("config", "CONFIGURATION")
                    
                        try {
                            BaseApplication.mqttClient.subscribeTopic(
                                MqttTopic.DEVICE_CONFIG,
                                bssid,
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
                        BaseApplication.mqttClient.sendMsg(
                            Gson().toJson(json),
                            MqttTopic.DEVICE_CONFIG,
                            bssid,
                            MqttTopicTag.CONFIG
                        )
                    
                    }
                
                    override fun onStop() {
                    
                    }
                
                    override fun onError(e: java.lang.Exception?) {
                    
                    }
                })
            
            
            }.start()
        }
    
    }
    
    fun esptouchStop() {
        if (espV2Task != null) {
            espV2Task!!.stopProvisioning()
            espV2Task = null
        }
    }

//    private var espTask: EsptouchTask? = null
//
//    fun esptouchStart(ssid: ByteArray, bssid: ByteArray, pwd: ByteArray, context: Context) {
//        showLoading()
//        BaseApplication.mqttClient.mqttCallbackListener = this
//        if (espTask == null) {
//            Thread {
//                espTask = EsptouchTask(ssid, bssid, pwd, context)
//                espTask?.setEsptouchListener { result: IEsptouchResult -> resultMqttEvent(result) }
//                // TODO: 2022/1/13 result not used
//                var result = espTask?.executeForResults(-1)
//
//                BaseApplication.mqttClient.mqttCallbackListener = null
//                espTask = null
//
//                if (!result!![0].isSuc) {
//                    dismissLoading()
//                    // TODO: 2022/1/13 change to AlertDialog
//                    runOnUiThread { Toast.makeText(this, "no search any device.", Toast.LENGTH_SHORT).show() }
//                }
//            }.start()
//        }
//
//        Logger.i("EspTouch Start")
//    }
//
//    fun esptouchStop() {
//        if (espTask != null && !espTask!!.isCancelled) {
//            espTask?.interrupt()
//            dismissLoading()
//
//            Logger.i("EspTouch interrupt")
//        }
//
//    }
//
//    private fun resultMqttEvent(result: IEsptouchResult) {
//
//        val json = JsonObject()
//        json.addProperty("config", "CONFIGURATION")
//
//        try {
//            BaseApplication.mqttClient.subscribeTopic(
//                MqttTopic.DEVICE_CONFIG,
//                result.bssid.uppercase(),
//                MqttTopicTag.CONFIG
//            )
//        } catch (e: MqttException) {
//            e.printStackTrace()
//        }
//        try {
//            synchronized(this) { Thread.sleep(2_000) }
//        } catch (e: InterruptedException) {
//            e.printStackTrace()
//        }
//        BaseApplication.mqttClient.sendMsg(
//            Gson().toJson(json),
//            MqttTopic.DEVICE_CONFIG,
//            result.bssid.uppercase(),
//            MqttTopicTag.CONFIG
//        )
//    }
    
    private var pairPage: PairDeviceDialogFragment? = null
    
    /**
     * Mqtt callback
     * */
    @Synchronized
    override fun msgArrived(topic: String, msg: String) {
        if (
            topic.contains("AD_CONFIG") &&
            Gson().fromJson(msg, JsonObject::class.java).get("devicename") != null
        ) {
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
