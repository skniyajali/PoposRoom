package com.niyaj.poposroom.features.common.utils

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.derivedStateOf
import java.text.DecimalFormat
import java.util.Locale

fun safeInt(price: String): Int{
    return if(price.isEmpty()){
        0
    } else{
        try {
            price.toInt()
        }catch (e: NumberFormatException) {
            0
        }
    }
}

val Int.safeString: String
    get() = if (this == 0) "" else this.toString()


val LazyListState.isScrolled: Boolean
    get() = derivedStateOf { firstVisibleItemIndex > 0 || firstVisibleItemScrollOffset > 0 }.value

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