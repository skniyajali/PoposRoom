package com.niyaj.data.mapper

import com.niyaj.database.model.ProfileEntity
import com.niyaj.model.Profile

fun Profile.toEntity(): ProfileEntity {
    return ProfileEntity(
        restaurantId = this.restaurantId,
        name = this.name,
        email = this.email,
        primaryPhone = this.primaryPhone,
        secondaryPhone = this.secondaryPhone,
        tagline = this.tagline,
        description = this.description,
        address = this.address,
        logo = this.logo,
        printLogo = this.printLogo,
        paymentQrCode = this.paymentQrCode,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}