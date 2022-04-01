package com.sttptech.toshiba_lighting

import com.sttptech.toshiba_lighting.Data.Bean.Scene
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
    fun sort() {
        val scene1 = Scene("1").apply { seq = 1 }
        val scene2 = Scene("2").apply { seq = 2 }
        val scene3 = Scene("3").apply { seq = 3 }
        val scene4 = Scene("4").apply { seq = 4 }
        val scene5 = Scene("5").apply { seq = 5 }
        val scene6 = Scene("6").apply { seq = 6 }
        val scene7 = Scene("7").apply { seq = 7 }
        val list = listOf<Scene>(scene4, scene5, scene3, scene1, scene7, scene2, scene6)
        println(list)
        println(list.sortedBy { scene -> scene.seq })
    }
}