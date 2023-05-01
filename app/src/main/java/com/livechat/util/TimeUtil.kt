package com.livechat.util

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
}