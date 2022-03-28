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
        
        fun deleteSceneByUuid(uUid: String)
        
    }
    
    interface RemoteData {
        
        fun memberLogin(account: String, password: String): ServerResponse?
        
        fun getInfoList(): InfoListRes?
        
        fun devSignup(mac: String, group: String, name: String, model: String): ServerResponse?
        
        fun modifyDeviceName(uUid: String, newName: String): Boolean
    
        enum class ModifyDeviceGroup {
            ADD_DEVICE, REMOVE_DEVICE
        }
        
        fun modifyDeviceGroup(devUuid: String, groupUuid: String, action: ModifyDeviceGroup): ServerResponse?
        
        enum class ModifyDeviceSettings {
            Wakeup, DailyOn, DailyOff
        }
        
        fun modifyDeviceSetting(devUuid: String, action: ModifyDeviceSettings, time: String, status: String): ServerResponse?
        
        fun createGroup(devUuid: String, groupName: String): ServerResponse?
        
        fun modifyCustomModeName(modeNum: Int, name: String, devUuid: String): Boolean
        
        fun deleteDevice(devUuid: String): ServerResponse?
        
        fun getSceneImage(imageUrl: String): ByteArray?
        
        fun insertScene(seqCode: Int, name: String, devList: List<Device>): ServerResponse?
        
        fun deleteScene(uUid: String): ServerResponse?
        
        fun updateSceneImage(uUid: String, img: File): ServerResponse?
    }
}