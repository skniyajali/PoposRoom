package com.niyaj.poposroom.features.employee_payment.presentation.add_edit

import com.niyaj.poposroom.features.common.utils.toMilliSecond
import com.niyaj.poposroom.features.employee.domain.utils.PaymentMode
import com.niyaj.poposroom.features.employee.domain.utils.PaymentType
import java.time.LocalDate

data class AddEditPaymentState(
    val employeeId: Int = 0,
    val paymentAmount: String = "",
    val paymentNote: String = "",
    val paymentDate: String = LocalDate.now().toMilliSecond,
    val paymentType: PaymentType = PaymentType.Advanced,
    val paymentMode: PaymentMode = PaymentMode.Cash,
)
