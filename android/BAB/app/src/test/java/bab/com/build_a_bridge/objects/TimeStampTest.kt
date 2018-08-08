package bab.com.build_a_bridge.objects

import org.junit.Assert.*
import org.junit.Test


class TimeStampTest{
    @Test
    fun returnTimeString1(){
        val expected = "8:30" // AM
        val timestamp = TimeStamp(hour = 8, minute = 30)
        val actual = timestamp.getTimeDisplay()
        assertEquals(expected, actual)
    }

    @Test
    fun returnTimeString2(){
        val expected = "1:30" // PM
        val timestamp = TimeStamp(hour = 13, minute = 30)
        val actual = timestamp.getTimeDisplay()
        assertEquals(expected, actual)
    }
}