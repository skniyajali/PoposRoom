package com.niyaj.poposroom.features.common.utils

import android.text.format.DateUtils
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.Year
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale

val Int.safeString: String
    get() = if (this == 0) "" else this.toString()


val LazyListState.isScrolled: Boolean
    get() = derivedStateOf { firstVisibleItemIndex > 0 || firstVisibleItemScrollOffset > 0 }.value

@Composable
fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableIntStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableIntStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}

@Composable
fun LazyGridState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableIntStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableIntStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}

val LazyGridState.isScrolled: Boolean
    get() = derivedStateOf { firstVisibleItemIndex > 0 || firstVisibleItemScrollOffset > 0 }.value

val String.toRupee
    get() = DecimalFormat
        .getCurrencyInstance(Locale("en", "IN"))
        .format(this.toLong())
        .substringBefore(".")

val Int.toRupee
    get() = DecimalFormat
        .getCurrencyInstance(Locale("en", "IN"))
        .format(this.toLong())
        .substringBefore(".")


val String.isContainsArithmeticCharacter: Boolean
    get() = this.any { str ->
        (str == '%' || str == '/' || str == '*' || str == '+' || str == '-')
    }

val String.capitalizeWords
    get() = this.lowercase(Locale.ROOT).split(" ").joinToString(" ") { char ->
        char.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
        }
    }

fun getAllCapitalizedLetters(string: String): String {
    var capitalizeLetters = ""

    string.capitalizeWords.forEach {
        if (it.isUpperCase()) {
            capitalizeLetters += it.toString()
        }
    }

    return capitalizeLetters
}

fun String.getCapitalWord(): String {
    var capitalizeLetters = ""

    this.capitalizeWords.forEach {
        if (it.isUpperCase()) {
            capitalizeLetters += it.toString()
        }
    }

    return capitalizeLetters
}

val zoneId: ZoneId = ZoneId.of("Asia/Kolkata")

val LocalDate.toMilliSecond: String
    get() = this.atStartOfDay(zoneId)
        .toLocalDateTime()
        .atZone(zoneId)
        .toInstant().toEpochMilli()
        .toString()

val LocalDate.toCurrentMilliSecond: String
    get() = this.atTime(LocalTime.now().hour, LocalTime.now().minute)
        .atZone(zoneId)
        .toInstant()
        .toEpochMilli()
        .toString()

val String.toJoinedDate
    get() = SimpleDateFormat(
        "dd-MM-yyyy",
        Locale.getDefault()
    ).format(this.toLong()).toString()

fun toMonthAndYear(date: String): String {
    val currentYear = Year.now().value.toString()
    val format = SimpleDateFormat("yyyy", Locale.getDefault()).format(date.toLong()).toString()

    return if (currentYear == format) {
        SimpleDateFormat("MMMM", Locale.getDefault()).format(date.toLong()).toString()
    } else {
        SimpleDateFormat("MMMM yy", Locale.getDefault()).format(date.toLong()).toString()
    }
}

val String.toDate
    get() = SimpleDateFormat("dd", Locale.getDefault()).format(this.toLong()).toString()

val String.toTime
    get() = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(this.toLong()).toString()


val Date.toTime
    get() = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(this).toString()

fun String.toPrettyDate(): String {
    val nowTime = Calendar.getInstance()
    val neededTime = Calendar.getInstance()
    neededTime.timeInMillis = this.toLong()

    return if (neededTime[Calendar.YEAR] == nowTime[Calendar.YEAR]) {
        if (neededTime[Calendar.MONTH] == nowTime[Calendar.MONTH]) {
            when {
                neededTime[Calendar.DATE] - nowTime[Calendar.DATE] == 1 -> {
                    //here return like "Tomorrow at 12:00"
                    "Tomorrow"
                }

                nowTime[Calendar.DATE] == neededTime[Calendar.DATE] -> {
                    //here return like "Today at 12:00"
                    "Today"
                }

                nowTime[Calendar.DATE] - neededTime[Calendar.DATE] == 1 -> {
                    //here return like "Yesterday at 12:00"
                    "Yesterday"
                }

                else -> {
                    //here return like "May 31, 12:00"
                    SimpleDateFormat("MMMM dd", Locale.getDefault()).format(Date(this.toLong()))
                }
            }
        } else {
            //here return like "May 31, 12:00"
            SimpleDateFormat("MMMM dd", Locale.getDefault()).format(Date(this.toLong()))
        }
    } else {
        //here return like "May 31 2022, 12:00" - it's a different year we need to show it
        SimpleDateFormat("MMMM dd yyyy", Locale.getDefault()).format(Date(this.toLong()))
    }
}

fun Date.toPrettyDate(): String {
    val nowTime = Calendar.getInstance()
    val neededTime = Calendar.getInstance()
    neededTime.timeInMillis = this.time

    return if (neededTime[Calendar.YEAR] == nowTime[Calendar.YEAR]) {
        if (neededTime[Calendar.MONTH] == nowTime[Calendar.MONTH]) {
            when {
                neededTime[Calendar.DATE] - nowTime[Calendar.DATE] == 1 -> {
                    //here return like "Tomorrow at 12:00"
                    "Tomorrow"
                }

                nowTime[Calendar.DATE] == neededTime[Calendar.DATE] -> {
                    //here return like "Today at 12:00"
                    "Today"
                }

                nowTime[Calendar.DATE] - neededTime[Calendar.DATE] == 1 -> {
                    //here return like "Yesterday at 12:00"
                    "Yesterday"
                }

                else -> {
                    //here return like "May 31, 12:00"
                    SimpleDateFormat("MMMM dd", Locale.getDefault()).format(this)
                }
            }
        } else {
            //here return like "May 31, 12:00"
            SimpleDateFormat("MMMM dd", Locale.getDefault()).format(this)
        }
    } else {
        //here return like "May 31 2022, 12:00" - it's a different year we need to show it
        SimpleDateFormat("MMMM dd yyyy", Locale.getDefault()).format(this)
    }
}

fun calculateStartOfDayTime(date: String = "", days: String = ""): String {
    val calendar = Calendar.getInstance()
    val s = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    try {
        if (date.isNotEmpty()) {
            calendar.time = s.parse(date) as Date
        }
    } catch (e: Exception) {
        calendar.timeInMillis = date.toLong()
    }

    val day = try {
        if (days.isNotEmpty()) days.toInt() else 0
    } catch (e: Exception) {
        0
    }
    calendar.add(Calendar.DAY_OF_YEAR, day)
    calendar[Calendar.HOUR_OF_DAY] = 0
    calendar[Calendar.MINUTE] = 0
    calendar[Calendar.SECOND] = 0
    calendar[Calendar.MILLISECOND] = 0

    return calendar.timeInMillis.toString()
}

fun calculateEndOfDayTime(date: String = "", days: String = ""): String {
    val calendar = Calendar.getInstance()
    val s = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    try {
        if (date.isNotEmpty()) {
            calendar.time = s.parse(date) as Date
        }
    } catch (e: Exception) {
        calendar.timeInMillis = date.toLong()
    }

    val day = try {
        if (days.isNotEmpty()) days.toInt() else 0
    } catch (e: Exception) {
        0
    }

    calendar.add(Calendar.DAY_OF_YEAR, day)
    calendar[Calendar.HOUR_OF_DAY] = 23
    calendar[Calendar.MINUTE] = 59
    calendar[Calendar.SECOND] = 59
    calendar[Calendar.MILLISECOND] = 0

    return calendar.timeInMillis.toString()
}

fun safeString(price: String): Int {
    return if (price.isEmpty()) {
        0
    } else {
        try {
            price.toInt()
        } catch (e: NumberFormatException) {
            0
        }
    }
}

fun String.safeInt(): Int {
    return if (this.isEmpty()) {
        0
    } else {
        try {
            this.toInt()
        } catch (e: NumberFormatException) {
            0
        }
    }
}

val startOfDayTime = LocalDate.now().toMilliSecond
val endOfDayTime = calculateEndOfDayTime(startOfDayTime)

val Date.toTimeSpan
    get() = DateUtils.getRelativeTimeSpanString(this.time).toString()