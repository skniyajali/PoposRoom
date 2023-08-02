package com.niyaj.data.mapper

import com.niyaj.database.model.AddressEntity
import com.niyaj.model.Address

fun Address.toEntity(): AddressEntity {
    return AddressEntity(
        addressId = this.addressId,
        addressName = this.addressName,
        shortName = this.shortName,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}