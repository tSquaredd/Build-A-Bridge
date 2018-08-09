package bab.com.build_a_bridge.objects

import java.util.*
import kotlin.math.min

/**
 * Models a value of time.
 * Primarily used for attaching a time
 * to a message or conversation.
 */
data class TimeStamp(val day: Int = 0,
                     val month: Int = 0,
                     val year: Int = 2018,
                     val hour: Int = 0,
                     val minute: Int = 0,
                     val second: Int = 0) :
        Comparable<TimeStamp> {

    override fun compareTo(other: TimeStamp): Int {
        return when {
            other.year == year ->
                when {
                    other.month == month ->
                        when {
                            other.day == day ->
                                when {
                                    other.hour == hour ->
                                        when {
                                            other.minute == minute ->
                                                when {
                                                    (other.second < second) -> 1
                                                    (other.second > second) -> -1
                                                    else -> 0
                                                }
                                            other.minute < minute -> 1
                                            else -> -1
                                        }
                                    other.hour < hour -> 1
                                    else -> -1
                                }
                            other.day < day -> 1
                            else -> -1
                        }
                    other.month < month -> 1
                    else -> -1
                }
            other.year < year -> 1
            else -> -1
        }
    }

    fun getTimeDisplay(): String {
        val hour = when {
            (this.hour == 0) -> 12
            (this.hour < 13) -> this.hour
            else -> this.hour - 12
        }

        val minute = when {
            (this.minute < 10) -> "0${this.minute}"
            else -> "${this.minute}"
        }

        return "$hour:$minute"
    }

    companion object {
        fun getInstance(): TimeStamp {
            val cal = Calendar.getInstance()
            return TimeStamp(day = cal.get(Calendar.DAY_OF_MONTH),
                    month = cal.get(Calendar.MONTH),
                    year = cal.get(Calendar.YEAR),
                    hour = cal.get(Calendar.HOUR_OF_DAY),
                    minute = cal.get(Calendar.MINUTE),
                    second = cal.get(Calendar.SECOND))
        }

    }
}