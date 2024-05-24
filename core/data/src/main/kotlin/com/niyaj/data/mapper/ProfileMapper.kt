/*
 *      Copyright 2024 Sk Niyaj Ali
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

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