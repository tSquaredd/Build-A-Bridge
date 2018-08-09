package bab.com.build_a_bridge.objects

import org.junit.Assert.*
import org.junit.Test


class TimeStampTest {
    @Test
    fun returnTimeString1() {
       runTest("8:30", 8, 30)
    }

    @Test
    fun returnTimeString2() {
       runTest("1:30", 13, 30)
    }

    @Test
    fun returnTimeStamp3() {
        runTest("3:05", 3, 5)
    }

    @Test
    fun returnTimeStamp4(){
        runTest("12:35", 0, 35)
    }

    private fun runTest(expected: String, hour: Int, minute: Int){
        val timeStamp = TimeStamp(hour = hour, minute = minute)
        val actual = timeStamp.getTimeDisplay()
        assertEquals(expected, actual)
    }
}