package com.sttptech.toshiba_lighting.Data.Bean

abstract class Device
    (
    open var uId: String?,
    open var macId: String,
    open var model: String?,
    open var name: String?,
    open var group: Group?
)
