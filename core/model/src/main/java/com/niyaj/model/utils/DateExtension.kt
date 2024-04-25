package com.niyaj.model.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal val String.toJoinedDate
    get() = SimpleDateFormat(
        "dd-MM-yyyy",
        Locale.getDefault(),
    ).format(this.toLong()).toString()

internal val String.toTime
    get() = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(this.toLong()).toString()

internal val Date.toTime
    get() = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(this).toString()

internal val Long.toDateString
    get() = SimpleDateFormat("dd", Locale.getDefault()).format(this).toString()

internal val String.capitalizeWords
    get() = this.lowercase(Locale.ROOT).split(" ").joinToString(" ") { char ->
        char.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
        }
    }

internal fun String.getCapitalWord(): String {
    var capitalizeLetters = ""

    this.capitalizeWords.forEach {
        if (it.isUpperCase()) {
            capitalizeLetters += it.toString()
        }
    }

    return capitalizeLetters
}