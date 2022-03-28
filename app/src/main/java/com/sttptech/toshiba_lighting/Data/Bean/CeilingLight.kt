package com.sttptech.toshiba_lighting.Data.Bean

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.sttptech.toshiba_lighting.Data.Room.Converters
import com.sttptech.toshiba_lighting.Data.Room.DataBase

@Entity(tableName = DataBase.CEILING_LIGHT_TABLE_NAME)
@TypeConverters(Converters.ListConverters::class, Converters.GroupConverters::class)
data class CeilingLight(
    // info
    override var uId: String?,
    @PrimaryKey
    override var macId: String,
    override var model: String?,
    override var name: String?,
    override var group: Group?,
    
    // status
    var opMode: Int = 1,
    var selectMode: Int = 0,
    var mBr: Int = 0,
    var mC: Int = 15,
    var nBr: Int = 10,
    var rgbBr: Int = 10,
    var rVal: Int = 0,
    var gVal: Int = 0,
    var bVal: Int = 0,
    var status: Int = 1,
    
    // Setting
    var wakeUp: Long = 1577833200L,
    var wakeUpS: Boolean = false,
    var dailyOn: Long = 1577872800L,
    var dailyOnS: Boolean = false,
    var dailyOff: Long = 1577883600L,
    var dailyOffS: Boolean = false,
    var cdtTime: Long = 0,
    var buzzer: Int = 0,
    var cdtStatus: Int = 0,
    var sleep: Int = 0,
    
    // Custom mode names
    var cusMode1: String?,
    var cusMode2: String?,
    var cusMode3: String?,
    var cusMode4: String?,

) : Device(uId, macId, model, name, group) {

    constructor(macId: String) : this(
        null,
        macId,
        null,
        "吸頂燈",
        null,
        1,
        0,
        0,
        15,
        10,
        10,
        0,
        0,
        0,
        1,
        1577833200000L,
        false,
        1577833200000L,
        false,
        1577833200000L,
        false,
        0,
        0,
        0,
        0,
        "自訂模式1",
        "自訂模式2",
        "自訂模式3",
        "自訂模式4"
    )
    
    fun reset() {
        this.opMode = 1
        this.selectMode = 0
        this.mBr = 0
        this.mC = 15
        this.nBr = 10
        this.rgbBr = 10
        this.rVal = 0
        this.gVal = 0
        this.bVal = 0
        this.status = 1
        
        // Setting
        this.wakeUp= 1577833200L
        this.wakeUpS = false
        this.dailyOn = 1577872800L
        this.dailyOnS = false
        this.dailyOff = 1577883600L
        this.dailyOffS = false
        this.cdtTime = 0
        this.buzzer = 0
        this.cdtStatus = 0
        this.sleep = 0
        
        // Custom mode names
        this.cusMode1 = "自訂模式1"
        this.cusMode2 = "自訂模式2"
        this.cusMode3 = "自訂模式3"
        this.cusMode4 = "自訂模式4"
    }
}
