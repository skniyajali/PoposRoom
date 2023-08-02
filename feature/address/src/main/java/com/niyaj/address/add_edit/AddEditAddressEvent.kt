package com.niyaj.address.add_edit

sealed interface AddEditAddressEvent {
    
    data class ShortNameChanged(val shortName: String) : AddEditAddressEvent

    data class AddressNameChanged(val addressName: String) : AddEditAddressEvent

    data class CreateOrUpdateAddress(val addressId: Int = 0) : AddEditAddressEvent
}