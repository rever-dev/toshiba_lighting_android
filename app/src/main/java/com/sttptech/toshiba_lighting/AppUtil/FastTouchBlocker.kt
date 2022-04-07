package com.sttptech.toshiba_lighting.AppUtil

class FastTouchBlocker {
    
    private var lastTouchTamps: Long
    
    init {
        lastTouchTamps = System.currentTimeMillis() - 1000
    }
    
    fun onTouch(): Boolean {
        if ((System.currentTimeMillis() - lastTouchTamps) >= 1000) {
            lastTouchTamps = System.currentTimeMillis()
            return true
        }
        return false
    }
}