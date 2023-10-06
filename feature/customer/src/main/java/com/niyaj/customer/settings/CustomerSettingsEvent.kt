package com.niyaj.customer.settings

import com.niyaj.model.Customer

sealed class CustomerSettingsEvent {

    data object GetExportedItems: CustomerSettingsEvent()

    data class OnImportCustomerItemsFromFile(val data: List<Customer>): CustomerSettingsEvent()

    data object ImportCustomerItemsToDatabase: CustomerSettingsEvent()
}