package com.niyaj.model

import com.niyaj.common.utils.toJoinedDate
import java.util.Date

data class Absent(
    val absentId: Int = 0,

    val employeeId: Int,

    val absentReason: String = "",

    val absentDate: String = "",

    val createdAt: Date,

    val updatedAt: Date? = null,
)

/**
 * Filter absent employee by date and absent reason
 */
fun Absent.filterAbsent(searchText: String): Boolean {
    return this.absentDate.toJoinedDate.contains(searchText, true) ||
            this.absentReason.contains(searchText, true)
}