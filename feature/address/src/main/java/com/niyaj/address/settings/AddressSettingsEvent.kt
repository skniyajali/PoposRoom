package com.niyaj.address.settings

import com.niyaj.model.Address

sealed class AddressSettingsEvent {
    data object GetExportedItems: AddressSettingsEvent()

    data class OnImportAddressItemsFromFile(val data: List<Address>): AddressSettingsEvent()

    data object ImportAddressItemsToDatabase: AddressSettingsEvent()
}