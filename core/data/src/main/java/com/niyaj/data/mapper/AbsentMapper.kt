package com.niyaj.data.mapper

import com.niyaj.common.utils.toDate
import com.niyaj.database.model.AbsentEntity
import com.niyaj.model.Absent

fun Absent.toEntity(): AbsentEntity {
    return AbsentEntity(
        absentId = this.absentId,
        employeeId = this.employeeId,
        absentReason = this.absentReason,
        absentDate = this.absentDate,
        createdAt = this.createdAt.toDate,
        updatedAt = this.updatedAt?.toDate
    )
}