package com.sttptech.toshiba_lighting.Application

import android.content.Context
import android.content.SharedPreferences
import com.sttptech.toshiba_lighting.AppUtil.KeyOfShp
import com.sttptech.toshiba_lighting.Data.Bean.CeilingLight
import com.sttptech.toshiba_lighting.Data.Bean.Device
import com.sttptech.toshiba_lighting.Data.Room.DataBase
import com.sttptech.toshiba_lighting.Data.Room.DataDao

class LocalService(var context: Context) : DataSource.LocalData {

    val dao: DataDao
    val shp: SharedPreferences

    init {
        dao = DataBase.getInstance(context).dataUao
        shp = context.getSharedPreferences(KeyOfShp.SHP_NAME, Context.MODE_PRIVATE)
    }

    override fun getAccount(): String? {
        return shp.getString(KeyOfShp.SHP_ACCOUNT, null)
    }

    override fun clearCeilingLightTable() {
        dao.deleteCeilingLightTable()
    }

    override fun clearGroupTable() {
        dao.deleteGroupsTable()
    }

    override fun insertCeilingLight(data: CeilingLight) {
        dao.insertCeilingLight(data)
    }

    override fun allCeilingLights(): List<CeilingLight>? {
        return dao.allCeilingLights()
    }
}