package com.niyaj.data.mapper

import com.niyaj.common.utils.toDate
import com.niyaj.database.model.AddressEntity
import com.niyaj.model.Address

fun Address.toEntity(): AddressEntity {
    return AddressEntity(
        addressId = this.addressId,
        addressName = this.addressName,
        shortName = this.shortName,
        createdAt = this.createdAt.toDate,
        updatedAt = this.updatedAt?.toDate
    )
}