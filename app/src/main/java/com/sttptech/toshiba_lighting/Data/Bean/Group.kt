package com.sttptech.toshiba_lighting.Data.Bean

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.sttptech.toshiba_lighting.Data.Room.Converters
import com.sttptech.toshiba_lighting.Data.Room.DataBase

@Entity(tableName = DataBase.GROUPS_TABLE_NAME)
@TypeConverters(Converters.ListConverters::class)
data class Group(
    var groupUuid: String?,
    @PrimaryKey
    var groupName: String,
    var groupDef: String?,
    var devUuids: List<String>?,
) {
    constructor(name: String) : this(
        null,
        name,
        "N",
        null
    )
}
