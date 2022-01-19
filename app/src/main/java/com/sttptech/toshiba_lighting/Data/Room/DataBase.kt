package com.sttptech.toshiba_lighting.Data.Room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sttptech.toshiba_lighting.Data.Bean.CeilingLight
import com.sttptech.toshiba_lighting.Data.Bean.Device
import com.sttptech.toshiba_lighting.Data.Bean.Group
import com.sttptech.toshiba_lighting.Data.Bean.Scene
import com.sttptech.toshiba_lighting.Data.Room.DataBase.Companion.DB_VERSION

@Database(
    entities = [CeilingLight::class, Group::class, Scene::class],
    version = DB_VERSION,
    exportSchema = false
) //資料綁定的Getter-Setter,資料庫版本,是否將資料導出至文件
abstract class DataBase : RoomDatabase() {
    //設置對外接口
    abstract val dataUao: DataDao

    companion object {
        private const val DB_NAME = "data_base" // 資料庫名稱
        const val DB_VERSION = 3 // 資料庫版本

        const val CEILING_LIGHT_TABLE_NAME = "CEILING_LIGHT_TABLE"
        const val GROUPS_TABLE_NAME = "GROUPS_TABLE"
        const val SCENE_TABLE_NAME = "SCENES_TABLE"

        @Volatile
        private var instance: DataBase? = null

        @Synchronized
        fun getInstance(context: Context): DataBase {
            if (instance == null) {
                instance = create(context) //創立新的資料庫
            }
            return instance!!
        }

        private fun create(context: Context): DataBase {
            return Room.databaseBuilder(
                context,
                DataBase::class.java,
                DB_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}