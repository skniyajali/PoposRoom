package com.niyaj.poposroom.features.employee_payment.presentation.add_edit

import com.niyaj.poposroom.features.employee.domain.model.Employee
import com.niyaj.poposroom.features.employee.domain.utils.PaymentMode
import com.niyaj.poposroom.features.employee.domain.utils.PaymentType


sealed interface AddEditPaymentEvent {

    data class OnSelectEmployee(val employee: Employee) : AddEditPaymentEvent

    data class PaymentAmountChanged(val paymentAmount: String) : AddEditPaymentEvent

    data class PaymentTypeChanged(val paymentType: PaymentType) : AddEditPaymentEvent

    data class PaymentDateChanged(val paymentDate: String) : AddEditPaymentEvent

    data class PaymentModeChanged(val paymentMode: PaymentMode) : AddEditPaymentEvent

    data class PaymentNoteChanged(val paymentNote: String) : AddEditPaymentEvent

    data class CreateOrUpdatePayment(val paymentId: Int = 0) : AddEditPaymentEvent
}