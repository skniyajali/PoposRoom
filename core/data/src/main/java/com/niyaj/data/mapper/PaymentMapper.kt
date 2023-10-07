package com.niyaj.data.mapper

import com.niyaj.common.utils.toDate
import com.niyaj.database.model.PaymentEntity
import com.niyaj.model.Payment

fun Payment.toEntity(): PaymentEntity {
    return PaymentEntity(
        paymentId = this.paymentId,
        employeeId = this.employeeId,
        paymentAmount = this.paymentAmount,
        paymentDate = this.paymentDate,
        paymentType = this.paymentType,
        paymentMode = this.paymentMode,
        paymentNote = this.paymentNote,
        createdAt = this.createdAt.toDate,
        updatedAt = this.updatedAt?.toDate
    )
}