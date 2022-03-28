package com.sttptech.toshiba_lighting.Application

import android.content.Context
import android.content.SharedPreferences
import com.sttptech.toshiba_lighting.AppUtil.AppKey
import com.sttptech.toshiba_lighting.Data.Bean.CeilingLight
import com.sttptech.toshiba_lighting.Data.Bean.Group
import com.sttptech.toshiba_lighting.Data.Bean.Scene
import com.sttptech.toshiba_lighting.Data.Room.DataBase
import com.sttptech.toshiba_lighting.Data.Room.DataDao

class LocalService(var context: Context) : RepositoryService.LocalData {

    private val dao: DataDao =
        DataBase.getInstance(context).dataUao
    
    private val shp: SharedPreferences =
        context.getSharedPreferences(AppKey.SHP_NAME, Context.MODE_PRIVATE)
    
    override fun getAccount(): String? {
        return shp.getString(AppKey.SHP_ACCOUNT, null)
    }

    override fun clearCeilingLightTable() {
        dao.deleteCeilingLightTable()
    }

    override fun clearGroupTable() {
        dao.deleteGroupsTable()
    }
    
    override fun clearSceneTable() {
        dao.deleteSceneTable()
    }
    
    override fun insertCeilingLight(data: CeilingLight) {
        dao.insertCeilingLight(data)
    }
    
    override fun updateCeilingLight(data: CeilingLight) {
        dao.updateCeilingLight(data)
    }
    
    override fun deleteCeilingLight(macId: String) {
        dao.deleteCeilingLightByUId(macId)
    }
    
    override fun insertGroup(data: Group) {
        dao.insertGroup(data)
    }
    
    override fun allCeilingLights(): List<CeilingLight>? {
        return dao.allCeilingLights()
    }
    
    override fun allGroups(): List<Group>? {
        return dao.allGroups()
    }
    
    override fun allScene(): List<Scene>? {
        return dao.allScene()
    }
    
    override fun getCeilingLightById(uUid: String): CeilingLight? {
        return dao.getCeilingLightByUuid(uUid)
    }
    
    override fun insertScene(data: Scene) {
        dao.insertScene(data)
    }
    
    override fun deleteSceneByUuid(uUid: String) {
        dao.deleteSceneByUId(uUid)
    }
}