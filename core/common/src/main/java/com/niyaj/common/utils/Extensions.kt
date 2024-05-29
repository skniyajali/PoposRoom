/*
 * Copyright 2024 Sk Niyaj Ali
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.niyaj.common.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.format.DateUtils
import com.niyaj.common.result.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.math.RoundingMode
import java.nio.ByteBuffer
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.Year
import java.time.YearMonth
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale

val API_KEY = "WQBvKOC4PBQ8EZUqOrVpslYSavvudON84Q5RyZavmPMHhZ00VvgfeIpXL2C5jFyM"

fun isValidPassword(password: String): Boolean {
    if (password.length < 8) return false
    if (password.firstOrNull { it.isDigit() } == null) return false
    if (password.firstOrNull { it.isLetter() } == null) return false
    return password.firstOrNull { !it.isLetterOrDigit() } != null
}

val Int.safeString: String
    get() = if (this == 0) "" else this.toString()

val Double.safeString: String
    get() = if (this == 0.0) "0" else this.toString()

val String.toRupee
    get() = DecimalFormat
        .getCurrencyInstance(Locale("en", "IN"))
        .format(this.toLong())
        .substringBefore(".")

val Long.toRupee
    get() = DecimalFormat
        .getCurrencyInstance(Locale("en", "IN"))
        .format(this)
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

val Date.toMillis: String
    get() = this.time.toString()

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
        Locale.getDefault(),
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

val Date.toDate
    get() = SimpleDateFormat("dd", Locale.getDefault()).format(this).toString()

val Long.toDateString
    get() = SimpleDateFormat("dd", Locale.getDefault()).format(this).toString()

val String.toTime
    get() = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(this.toLong()).toString()

val Date.toTime
    get() = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(this).toString()

val String.toBarDate
    get() = SimpleDateFormat(
        "dd MMM",
        Locale.getDefault(),
    ).format(this.toLong()).toString()

val Date.toBarDate
    get() = SimpleDateFormat(
        "dd MMM",
        Locale.getDefault(),
    ).format(this).toString()

val Long.toBarDate
    get() = SimpleDateFormat(
        "dd MMM",
        Locale.getDefault(),
    ).format(this).toString()

val String.toFormattedDate
    get() = SimpleDateFormat(
        "dd MMM yy",
        Locale.getDefault(),
    ).format(this.toLong()).toString()

val Long.toFormattedDate
    get() = SimpleDateFormat(
        "dd MMM yy",
        Locale.getDefault(),
    ).format(this.toLong()).toString()

val String.toFormattedTime
    get() = SimpleDateFormat(
        "hh:mm a",
        Locale.getDefault(),
    ).format(this.toLong()).toString()

val String.toFormattedDateAndTime
    get() = SimpleDateFormat(
        "dd MMM, hh:mm a",
        Locale.getDefault(),
    ).format(this.toLong()).toString()

val Date.toFormattedDateAndTime
    get() = SimpleDateFormat(
        "dd MMM, hh:mm a",
        Locale.getDefault(),
    ).format(this).toString()

val Long.toFormattedDateAndTime
    get() = SimpleDateFormat(
        "dd MMM, hh:mm a",
        Locale.getDefault(),
    ).format(this).toString()

val String.toYearAndMonth
    get() = SimpleDateFormat(
        "MMM yyyy",
        Locale.getDefault(),
    ).format(this.toLong()).toString()

val Long.toDate
    get() = Date(this)

fun String.toPrettyDate(): String {
    val nowTime = Calendar.getInstance()
    val neededTime = Calendar.getInstance()
    neededTime.timeInMillis = this.toLong()

    return if (neededTime[Calendar.YEAR] == nowTime[Calendar.YEAR]) {
        if (neededTime[Calendar.MONTH] == nowTime[Calendar.MONTH]) {
            when {
                neededTime[Calendar.DATE] - nowTime[Calendar.DATE] == 1 -> {
                    // here return like "Tomorrow at 12:00"
                    "Tomorrow"
                }

                nowTime[Calendar.DATE] == neededTime[Calendar.DATE] -> {
                    // here return like "Today at 12:00"
                    "Today"
                }

                nowTime[Calendar.DATE] - neededTime[Calendar.DATE] == 1 -> {
                    // here return like "Yesterday at 12:00"
                    "Yesterday"
                }

                else -> {
                    // here return like "May 31, 12:00"
                    SimpleDateFormat("MMMM dd", Locale.getDefault()).format(Date(this.toLong()))
                }
            }
        } else {
            // here return like "May 31, 12:00"
            SimpleDateFormat("MMMM dd", Locale.getDefault()).format(Date(this.toLong()))
        }
    } else {
        // here return like "May 31 2022, 12:00" - it's a different year we need to show it
        SimpleDateFormat("MMMM dd yyyy", Locale.getDefault()).format(Date(this.toLong()))
    }
}

fun Long.toPrettyDate(): String {
    val nowTime = Calendar.getInstance()
    val neededTime = Calendar.getInstance()
    neededTime.timeInMillis = this

    return if (neededTime[Calendar.YEAR] == nowTime[Calendar.YEAR]) {
        if (neededTime[Calendar.MONTH] == nowTime[Calendar.MONTH]) {
            when {
                neededTime[Calendar.DATE] - nowTime[Calendar.DATE] == 1 -> {
                    // here return like "Tomorrow at 12:00"
                    "Tomorrow"
                }

                nowTime[Calendar.DATE] == neededTime[Calendar.DATE] -> {
                    // here return like "Today at 12:00"
                    "Today"
                }

                nowTime[Calendar.DATE] - neededTime[Calendar.DATE] == 1 -> {
                    // here return like "Yesterday at 12:00"
                    "Yesterday"
                }

                else -> {
                    // here return like "May 31, 12:00"
                    SimpleDateFormat("MMMM dd", Locale.getDefault()).format(Date(this))
                }
            }
        } else {
            // here return like "May 31, 12:00"
            SimpleDateFormat("MMMM dd", Locale.getDefault()).format(Date(this))
        }
    } else {
        // here return like "May 31 2022, 12:00" - it's a different year we need to show it
        SimpleDateFormat("MMMM dd yyyy", Locale.getDefault()).format(Date(this))
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
                    // here return like "Tomorrow at 12:00"
                    "Tomorrow"
                }

                nowTime[Calendar.DATE] == neededTime[Calendar.DATE] -> {
                    // here return like "Today at 12:00"
                    "Today"
                }

                nowTime[Calendar.DATE] - neededTime[Calendar.DATE] == 1 -> {
                    // here return like "Yesterday at 12:00"
                    "Yesterday"
                }

                else -> {
                    // here return like "May 31, 12:00"
                    SimpleDateFormat("MMMM dd", Locale.getDefault()).format(this)
                }
            }
        } else {
            // here return like "May 31, 12:00"
            SimpleDateFormat("MMMM dd", Locale.getDefault()).format(this)
        }
    } else {
        // here return like "May 31 2022, 12:00" - it's a different year we need to show it
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

fun String.safeString(): String {
    return if (this.isEmpty()) {
        "0"
    } else {
        try {
            this
        } catch (e: NumberFormatException) {
            "0"
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

fun String.safeFloat(): Float {
    return if (this.isEmpty()) {
        0f
    } else {
        try {
            this.toFloat()
        } catch (e: NumberFormatException) {
            0f
        }
    }
}

fun String.safeDouble(): Double {
    return if (this.isEmpty()) {
        0.0
    } else {
        try {
            this.toDouble()
        } catch (e: NumberFormatException) {
            0.0
        }
    }
}

val Boolean.toSafeString: String
    get() = if (this) "Yes" else "No"

val startOfDayTime = LocalDate.now().toMilliSecond
val endOfDayTime = calculateEndOfDayTime(startOfDayTime)

fun startTime(): Calendar {
    val startTime = Calendar.getInstance()
    startTime[Calendar.HOUR_OF_DAY] = 0
    startTime[Calendar.MINUTE] = 0
    startTime[Calendar.SECOND] = 0
    startTime[Calendar.MILLISECOND] = 0

    return startTime
}

fun endTime(): Calendar {
    val endTime = startTime().clone() as Calendar
    endTime[Calendar.HOUR_OF_DAY] = 23
    endTime[Calendar.MINUTE] = 59
    endTime[Calendar.SECOND] = 59
    endTime[Calendar.MILLISECOND] = 0

    return endTime
}

fun calculateStartDate(date: String, days: String = ""): Long {
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

    return calendar.timeInMillis
}

fun calculateEndDate(date: String, days: String = ""): Long {
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

    return calendar.timeInMillis
}

val getStartTime: String = startTime().timeInMillis.toString()
val getEndTime: String = endTime().timeInMillis.toString()

val getStartDate: Date = startTime().time
val getEndDate: Date = endTime().time

val getStartDateLong: Long = startTime().timeInMillis
val getEndDateLong: Long = endTime().timeInMillis

val Date.toTimeSpan
    get() = DateUtils.getRelativeTimeSpanString(this.time).toString()

val Long.toTimeSpan
    get() = DateUtils.getRelativeTimeSpanString(this).toString()

fun createDottedString(name: String, limit: Int): String {
    if (name.length > limit) {
        var wordLength = 0
        var firstWordLength = 0
        val splitName = name.split(' ')

        splitName.forEachIndexed { index, word ->
            if (index != 0) {
                wordLength += word.length.plus(1)
            } else {
                firstWordLength = word.length
            }
        }

        val remainingLength = limit.minus(firstWordLength)

        val whiteSpace = splitName.size - 1

        val remLength =
            wordLength.plus(whiteSpace).minus(remainingLength).div(splitName.size.minus(1))

        var newName = ""

        splitName.forEachIndexed { index, name1 ->
            if (index != 0) {
                val wordLen = name1.length.minus(remLength.plus(1))
                val dottedName =
                    if (wordLen <= 0) {
                        name1.substring(0, 1)
                    } else {
                        name1.substring(0, wordLen)
                            .plus(".")
                    }
                newName += " $dottedName"
            } else {
                newName = name1
            }
        }

        return newName
    } else {
        return name
    }
}

fun createLineString(name: String, limit: Int): String {
    var text = ""

    val remainingSpace = limit.minus(name.length + 2).div(2)

    for (i in 0 until remainingSpace) {
        text += "--"
    }

    text += " $name "

    for (i in 0 until remainingSpace) {
        text += "--"
    }

    return text
}

fun getSalaryDates(joinedDate: String): List<Pair<String, String>> {
    val currentYearAndMonth = YearMonth.now()

    val salaryDates = mutableListOf<Pair<String, String>>()

    val formatDate = SimpleDateFormat("dd", Locale.getDefault())
    val formattedDate = formatDate.format(joinedDate.toLong()).toInt()

    for (i in 0 until 5) {
        var previousMonth = 0
        var previousYear = 0

        val subtractMonth = currentYearAndMonth.minusMonths(i.toLong())

        for (j in 1 until 2) {
            previousMonth = subtractMonth.minusMonths(j.toLong()).month.value
            previousYear = subtractMonth.minusMonths(j.toLong()).year
        }

        val currentMonth = subtractMonth.month.value
        val currentYear = subtractMonth.year

        val comparePreDate = compareSalaryDates(
            getStartDate(formattedDate, previousMonth, previousYear),
            Calendar.getInstance().timeInMillis.toString(),
        )

        if (comparePreDate) {
            salaryDates.add(
                getStartAndEndDate(
                    date = formattedDate,
                    currentMonth = currentMonth,
                    currentYear = currentYear,
                    previousMonth = previousMonth,
                    previousYear = previousYear,
                ),
            )
        }
    }

    return salaryDates
}

private fun getStartAndEndDate(
    date: Int,
    currentMonth: Int,
    currentYear: Int,
    previousMonth: Int,
    previousYear: Int,
): Pair<String, String> {
    val startCalender = Calendar.getInstance()
    startCalender[Calendar.DATE] = date
    startCalender[Calendar.YEAR] = previousYear
    startCalender[Calendar.MONTH] = previousMonth
    startCalender[Calendar.HOUR_OF_DAY] = 0
    startCalender[Calendar.MINUTE] = 0
    startCalender[Calendar.SECOND] = 0
    startCalender[Calendar.MILLISECOND] = 0

    val endCalender = Calendar.getInstance()
    endCalender[Calendar.DATE] = date
    endCalender[Calendar.YEAR] = currentYear
    endCalender[Calendar.MONTH] = currentMonth
    endCalender[Calendar.HOUR_OF_DAY] = 23
    endCalender[Calendar.MINUTE] = 59
    endCalender[Calendar.SECOND] = 59

    return Pair(startCalender.timeInMillis.toString(), endCalender.timeInMillis.toString())
}

fun compareSalaryDates(joinedDate: String, comparableDate: String): Boolean {
    val calendar = Calendar.getInstance()

    calendar.timeInMillis = joinedDate.toLong()
    val firstDate = calendar.time

    calendar.timeInMillis = comparableDate.toLong()
    val secondDate = calendar.time

    val cmp = firstDate.compareTo(secondDate)

    return when {
        cmp < 0 -> true
        cmp > 0 -> false
        else -> true
    }
}

private fun getStartDate(date: Int, currentMonth: Int, currentYear: Int): String {
    val startCalender = Calendar.getInstance()
    startCalender[Calendar.DATE] = date
    startCalender[Calendar.YEAR] = currentYear
    startCalender[Calendar.MONTH] = currentMonth
    startCalender[Calendar.HOUR_OF_DAY] = 0
    startCalender[Calendar.MINUTE] = 0
    startCalender[Calendar.SECOND] = 0
    startCalender[Calendar.MILLISECOND] = 0

    return startCalender.timeInMillis.toString()
}

fun String.toDailySalaryAmount(): String {
    val dailyAmount = this.toLong().div(30).toInt()
    val numberFormat = NumberFormat.getInstance()
    numberFormat.roundingMode = RoundingMode.CEILING
    numberFormat.format(dailyAmount)

    return dailyAmount.toString().toRupee
}

val Pair<String, String>.isSameDay: Boolean
    get() = this.first.toBarDate == this.second.toBarDate

val String.isToday: Boolean
    get() = DateUtils.isToday(this.toLong())

// Create a list of all measure units.
val measureUnitLists = listOf(
    "kg", "gm", "li", "bottle", "packet",
    "tsp", "tbsp", "cup", "pcs",
)

fun List<Int>.toListString(): String {
    val sb = StringBuilder()
    for (item in this) {
        sb.append(item).append(",")
    }
    return sb.toString()
}

fun String.toListString(): MutableList<Int> {
    if (this.isEmpty()) return mutableListOf()

    val items = this.trimEnd().split(",")

    return items.map { it.trim() } // Trim to remove leading/trailing whitespace
        .filter { it.isNotEmpty() } // Filter out empty strings
        .map { it.toInt() }.toMutableList()
}

fun Double.toSafeString(): String {
    return when {
        this == 0.0 -> "0"
        this == this.toInt().toDouble() -> this.toInt().toString()
        else -> this.toString()
    }
}

fun Uri.toBitmap(context: Context): Bitmap? {
    val inputStream = context.contentResolver.openInputStream(this)

    val bitmap: Bitmap? = BitmapFactory.decodeStream(inputStream)

    inputStream?.close()

    return bitmap
}

suspend fun Context.saveImageToInternalStorage(
    uri: Uri,
    fileName: String,
    oldFileName: String = "",
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
): Resource<String> {
    val contentResolver = this.contentResolver
    val targetFile = File(this.filesDir, fileName)

    if (oldFileName.isNotEmpty()) {
        val oldFile = File(this.filesDir, oldFileName)
        if (oldFile.exists()) {
            oldFile.delete()
        }
    }

    return withContext(dispatcher) {
        try {
            targetFile.createNewFile()
            contentResolver.openInputStream(uri)?.use { inputStream ->
                targetFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream, bufferSize = 1024)
                }
            }
            Resource.Success(fileName)
        } catch (e: Exception) {
            Resource.Error(e.message.toString())
        }
    }
}

private fun InputStream.copyTo(
    outputStream: OutputStream,
    bufferSize: Int = 1024,
) {
    val buffer = ByteArray(bufferSize)
    var bytesRead: Int
    while (read(buffer).also { bytesRead = it } > 0) {
        outputStream.write(buffer, 0, bytesRead)
    }
}

fun drawableToByteArray(context: Context, imageRes: Int): ByteArray {
    // Get the drawable from the resources
    val drawable: Drawable? = context.getDrawable(imageRes)

    // Convert the drawable to a Bitmap
    val bitmap = Bitmap.createBitmap(
        drawable?.intrinsicWidth ?: 0,
        drawable?.intrinsicHeight ?: 0,
        Bitmap.Config.ARGB_8888,
    )
    val canvas = Canvas(bitmap)
    drawable?.setBounds(0, 0, canvas.width, canvas.height)
    drawable?.draw(canvas)

    // Convert the Bitmap to a byte array
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
    val byteArray = stream.toByteArray()

    // Clean up resources
    stream.close()
    bitmap.recycle()

    // Now you have the byte array representing the drawable

    return byteArray
}

/**
 * Convert bitmap to byte array using ByteBuffer.
 */
fun Bitmap.convertToByteArray(): ByteArray {
    // minimum number of bytes that can be used to store this bitmaps pixels
    val size = this.byteCount

    // allocate new instances which will hold bitmap
    val buffer = ByteBuffer.allocate(size)
    val bytes = ByteArray(size)

    // copy the bitmap's pixels into the specified buffer
    this.copyPixelsToBuffer(buffer)

    // rewinds buffer (buffer position is set to zero and the mark is discarded)
    buffer.rewind()

    // transfer bytes from buffer into the given destination array
    buffer.get(bytes)

    // return bitmaps pixels
    return bytes
}

fun Bitmap.toByteArray(): ByteArray {
    val stream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}

fun ByteArray.toBitmap(): Bitmap {
    return BitmapFactory.decodeByteArray(this, 0, this.size)
}
