package com.sttptech.toshiba_lighting.Data.Bean

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sttptech.toshiba_lighting.Data.Room.DataBase

@Entity(tableName = DataBase.SCENE_TABLE_NAME)
data class Scene(
    @PrimaryKey
    var uId:String,
) {
}