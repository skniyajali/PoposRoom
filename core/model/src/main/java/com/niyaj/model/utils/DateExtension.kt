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

package com.niyaj.model.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal val String.toJoinedDate
    get() = SimpleDateFormat(
        "dd-MM-yyyy",
        Locale.getDefault(),
    ).format(this.toLong()).toString()

internal val Long.toJoinedDate
    get() = SimpleDateFormat(
        "dd-MM-yyyy",
        Locale.getDefault(),
    ).format(this.toLong()).toString()

internal val String.toTime
    get() = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(this.toLong()).toString()

internal val Date.toTime
    get() = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(this).toString()

val Long.toTime
    get() = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(this).toString()

internal val Long.toDateString
    get() = SimpleDateFormat("dd", Locale.getDefault()).format(this).toString()

internal val String.capitalizeWords
    get() = this.lowercase(Locale.ROOT).split(" ").joinToString(" ") { char ->
        char.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
        }
    }

val String.toDate
    get() = SimpleDateFormat("dd", Locale.getDefault()).format(this.toLong()).toString()

internal fun String.getCapitalWord(): String {
    var capitalizeLetters = ""

    this.capitalizeWords.forEach {
        if (it.isUpperCase()) {
            capitalizeLetters += it.toString()
        }
    }

    return capitalizeLetters
}
