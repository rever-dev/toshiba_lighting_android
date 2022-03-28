package com.sttptech.toshiba_lighting.Data.Bean

data class DeviceStatus(
    val mac: String,
    val payload: Payload
)

data class Payload(
    val mBr: String,
    val mC: String,
    val nBr: String,
    val opMode: String,
    val rgbB: String,
    val rgbBr: String,
    val rgbG: String,
    val rgbR: String,
    val selectMode: String,
    val setStatus: String
)