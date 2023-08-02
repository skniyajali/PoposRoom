package com.niyaj.employee_payment.add_edit

import com.niyaj.common.utils.toMilliSecond
import com.niyaj.model.PaymentMode
import com.niyaj.model.PaymentType
import java.time.LocalDate

data class AddEditPaymentState(
    val paymentAmount: String = "",
    val paymentNote: String = "",
    val paymentDate: String = LocalDate.now().toMilliSecond,
    val paymentType: PaymentType = PaymentType.Advanced,
    val paymentMode: PaymentMode = PaymentMode.Cash,
)
