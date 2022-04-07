package com.sttptech.toshiba_lighting.Application

import com.sttptech.toshiba_lighting.Data.Bean.CeilingLight
import com.sttptech.toshiba_lighting.Data.Bean.Device
import com.sttptech.toshiba_lighting.Data.Bean.Group
import com.sttptech.toshiba_lighting.Data.Bean.Scene
import com.sttptech.toshiba_lighting.RetrofitUtil.InfoListRes
import com.sttptech.toshiba_lighting.RetrofitUtil.ServerResponse
import java.io.File

interface RepositoryService {
    
    interface LocalData {
        
        fun getAccount(): String?
        
        fun clearCeilingLightTable()
        
        fun clearGroupTable()
        
        fun clearSceneTable()
        
        fun insertCeilingLight(data: CeilingLight)
        
        fun updateCeilingLight(data: CeilingLight)
        
        fun deleteCeilingLight(uId: String)
        
        fun insertGroup(data: Group)
    
        fun allCeilingLights(): List<CeilingLight>?
    
        fun allGroups(): List<Group>?
    
        fun allScene(): List<Scene>?
    
        fun getCeilingLightById(uUid: String): CeilingLight?
    
        fun insertScene(data: Scene)
    
        fun insertAllScene(dataList: List<Scene>)
    
        fun getSceneBySeq(seq: Int): Scene?
        
        fun getSceneByUuid(uUid: String): Scene?
    
        fun deleteSceneByUuid(uUid: String)
    
    }
    
    interface RemoteData {
    
        /* member */
        fun memberLogin(account: String, password: String): ServerResponse?
    
        /* info list */
        fun getInfoList(): InfoListRes?
    
    
        /* device */
        fun devSignup(mac: String, group: String, name: String, model: String): ServerResponse?
    
        fun modifyDeviceName(uUid: String, newName: String): Boolean
    
        enum class ModifyDeviceGroup {
            ADD_DEVICE, REMOVE_DEVICE
        }
    
        fun modifyDeviceGroup(
            devUuid: String,
            groupUuid: String,
            action: ModifyDeviceGroup
        ): ServerResponse?
    
        enum class ModifyDeviceSettings {
            Wakeup, DailyOn, DailyOff
        }
    
        fun modifyDeviceSetting(
            devUuid: String,
            action: ModifyDeviceSettings,
            time: String,
            status: String
        ): ServerResponse?
    
        fun modifyCustomModeName(modeNum: Int, name: String, devUuid: String): Boolean
    
        fun deleteDevice(devUuid: String): ServerResponse?
        
        
        /* group */
        fun createGroup(devUuid: String, groupName: String): ServerResponse?
        
    
        /* scene */
        fun getSceneImage(imageUrl: String): ByteArray?
    
        fun insertScene(seqCode: Int, name: String, devList: List<Device>): ServerResponse?
    
        fun deleteScene(uUid: String): ServerResponse?
    
        fun updateSceneImage(uUid: String, img: File): ServerResponse?
    
        fun updateScene(
            sceneUid: String,
            addDevs: List<String>?,
            removeDevs: List<String>?,
            name: String?
        ): ServerResponse?
    
        fun sortScene(sceneList: List<Scene>): ServerResponse?
    
        /* schedule */
        fun getSchedule(): ServerResponse?
    
        fun scheduleOnOff(taskId: String, onOff: Boolean): ServerResponse?
    
        fun addSchedule(sceneUuid: String, weekList: List<Int>, minOfDay: Int): ServerResponse?
    
        fun modifySchedule(
            scheUuid: String,
            sceneUuid: String,
            weekList: List<Int>,
            minOfDay: Int
        ): ServerResponse?
    
        fun deleteSchedule(taskId: String): ServerResponse?
    
        /* Share */
        fun inviteMember(account: String): ServerResponse?
        
        fun bindShare(verifyCode: String): ServerResponse?
        
        fun unbindShare(): ServerResponse?
        
        fun verifyCode(vCode: String): ServerResponse?
    }
}