package bab.com.build_a_bridge.objects

import java.util.*

data class TimeStamp(val day: Int = 0, val month: Int = 0, val year: Int = 2018){
    companion object {
        fun getInstance(): TimeStamp {
            val cal = Calendar.getInstance()
            return TimeStamp(day = cal.get(Calendar.DAY_OF_MONTH),
                    month = cal.get(Calendar.MONTH),
                    year = cal.get(Calendar.YEAR))
        }
    }
}