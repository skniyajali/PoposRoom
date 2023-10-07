package com.niyaj.employee_payment.settings

import com.niyaj.model.EmployeeWithPayments

sealed class PaymentSettingsEvent {

    data object GetExportedItems: PaymentSettingsEvent()

    data class OnImportPaymentsFromFile(val data: List<EmployeeWithPayments>): PaymentSettingsEvent()

    data object ImportPaymentsToDatabase: PaymentSettingsEvent()
}