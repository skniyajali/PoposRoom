package com.niyaj.employee_payment.add_edit

import com.niyaj.model.Employee
import com.niyaj.model.PaymentMode
import com.niyaj.model.PaymentType


sealed interface AddEditPaymentEvent {

    data class OnSelectEmployee(val employee: Employee) : AddEditPaymentEvent

    data class PaymentAmountChanged(val paymentAmount: String) : AddEditPaymentEvent

    data class PaymentTypeChanged(val paymentType: PaymentType) : AddEditPaymentEvent

    data class PaymentDateChanged(val paymentDate: String) : AddEditPaymentEvent

    data class PaymentModeChanged(val paymentMode: PaymentMode) : AddEditPaymentEvent

    data class PaymentNoteChanged(val paymentNote: String) : AddEditPaymentEvent

    data class CreateOrUpdatePayment(val paymentId: Int = 0) : AddEditPaymentEvent
}