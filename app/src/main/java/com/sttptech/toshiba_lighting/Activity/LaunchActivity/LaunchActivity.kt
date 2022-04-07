package com.sttptech.toshiba_lighting.Activity.LaunchActivity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.orhanobut.logger.Logger
import com.sttptech.toshiba_lighting.Activity.Main.MainActivity
import com.sttptech.toshiba_lighting.Activity.Member.MemberActivity
import com.sttptech.toshiba_lighting.AppUtil.AppKey
import com.sttptech.toshiba_lighting.Application.BaseApplication
import com.sttptech.toshiba_lighting.Application.RepositoryService
import com.sttptech.toshiba_lighting.Data.Bean.CeilingLight
import com.sttptech.toshiba_lighting.Data.Bean.Group
import com.sttptech.toshiba_lighting.Data.Bean.Scene
import com.sttptech.toshiba_lighting.Mqtt.MqttTopicTag
import com.sttptech.toshiba_lighting.R
import com.sttptech.toshiba_lighting.RetrofitUtil.InfoListRes
import java.sql.Timestamp

class LaunchActivity : AppCompatActivity() {
    
    lateinit var remService: RepositoryService.RemoteData
    lateinit var locService: RepositoryService.LocalData
    lateinit var shp: SharedPreferences
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)
        
        remService = BaseApplication.repository.remoteS
        locService = BaseApplication.repository.localS
        shp = applicationContext.getSharedPreferences(AppKey.SHP_NAME, MODE_PRIVATE)
        
        val logStatus = checkLogin()
        
        Thread {
            if (logStatus) {
                getDataFromServer()
                runOnUiThread { startMainActivity() }
            } else {
                Thread.sleep(1500)
                runOnUiThread { startMemberActivity() }
            }
        }.start()
    }
    
    private fun checkLogin(): Boolean {
        shp.run {
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
    
    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }
    
    private fun startMemberActivity() {
        val intent = Intent(this, MemberActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }
    
    private fun getDataFromServer() {
        val remoteService = BaseApplication.repository.remoteS
        val info = remoteService.getInfoList()
        if (info != null) {
            shp.edit()
                .putBoolean(AppKey.SHP_SHARE, info.datum.share)
                .putString(AppKey.SHP_SHARE_EMAIL, info.datum.shareMail)
                .apply()
            syncDevice2DB(info)
            syncGroup2DB(info)
            syncScene2DB(info)
        }
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
    
}