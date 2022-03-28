package com.sttptech.toshiba_lighting.Data.Bean

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.sttptech.toshiba_lighting.Data.Room.Converters
import com.sttptech.toshiba_lighting.Data.Room.DataBase

@Entity(tableName = DataBase.SCENE_TABLE_NAME)
@TypeConverters(Converters.ListConverters::class)
data class Scene(
    @PrimaryKey
    val uId: String,
) {
    var name: String? = null
    var seq: Int? = null
    var def: String? = null
    var image: ByteArray? = null
    var imageUrl: String? = null
    var devUuids: List<String>? = null
    var order: Int? = null
}
