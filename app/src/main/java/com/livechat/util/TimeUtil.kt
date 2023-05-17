package com.livechat.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

/**
 * User: Quang Phúc
 * Date: 2023-05-01
 * Time: 10:42 PM
 */
object TimeUtil {

    fun formatMilliSecToHour(milliSec: Int): String {
        var mMilliSec = milliSec
        val hour = mMilliSec / 3600000
        mMilliSec %= 3600000
        val minute = mMilliSec / 60000
        mMilliSec %= 60000
        val second = mMilliSec / 1000
        mMilliSec %= 1000
        return "${NumberUtil.numberTo2DigitsNumber(hour)}:${NumberUtil.numberTo2DigitsNumber(minute)}:${
            NumberUtil.numberTo2DigitsNumber(
                second
            )
        }.${mMilliSec / 100}"
    }

    fun getCurrentTimestamp(): Long {
        return System.currentTimeMillis()
    }

    fun formatTimestampToString(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp * 1000
        val now = Calendar.getInstance()
        now.timeInMillis = getCurrentTimestamp()
        val format = if (calendar.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH)
            && calendar.get(Calendar.MONTH) == now.get(Calendar.MONTH)
            && calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR)
        ) {
            SimpleDateFormat("HH:mm")
        } else {
            SimpleDateFormat("dd/MM/yyyy")
        }

        val date = Date(timestamp * 1000)
        return format.format(date)
    }
}