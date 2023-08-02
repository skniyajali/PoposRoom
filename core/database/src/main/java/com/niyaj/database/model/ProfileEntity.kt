package com.niyaj.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.niyaj.model.Profile

@Entity(tableName = "profile")
data class ProfileEntity(
    @PrimaryKey
    val restaurantId: Int,

    val name: String,

    val email: String,

    val primaryPhone: String,

    val secondaryPhone: String = "",

    val tagline: String,

    val description: String = "",

    val address: String,

    val logo: String,

    val printLogo: String = "",

    val paymentQrCode: String = "",

    val createdAt: String = System.currentTimeMillis().toString(),

    val updatedAt: String? = null,
)


fun ProfileEntity.asExternalModel(): Profile {
    return Profile(
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