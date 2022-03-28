package com.sttptech.toshiba_lighting

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun calculate() {
        val total = 93784
        val sec = total % 60
        val min = total / 60 % 60
        val hour = total / 60 / 60 % 24
        val day = total / 60 / 60 / 24
        print("$day 天 $hour 時 $min 分 $sec 秒")
    }
    
    @Test
    fun toStr() {
        val list: List<String?> = listOf("1", "2", "3", null)
        println("\"${list.toString()}\"")
    }
    
    @Test
    fun currentTime() {
        println(System.currentTimeMillis())
    }
}