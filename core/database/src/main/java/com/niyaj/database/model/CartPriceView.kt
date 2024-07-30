/*
 * Copyright 2024 Sk Niyaj Ali
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.niyaj.database.model

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import com.niyaj.model.OrderPrice

@DatabaseView(
    value = """
        WITH OrderPrices AS (
            SELECT DISTINCT
                o.orderId,
                COALESCE(SUM(ci.quantity * p.productPrice), 0) AS productTotal,
                COALESCE((
                    SELECT SUM(CASE WHEN ai.isApplicable = 0 THEN ai.itemPrice ELSE 0 END)
                    FROM cart_addon_items cai
                    JOIN addonitem ai ON cai.itemId = ai.itemId
                    WHERE cai.orderId = o.orderId
                ), 0) AS nonApplicableAddons,
                COALESCE((
                    SELECT SUM(CASE WHEN ai.isApplicable = 1 THEN ai.itemPrice ELSE 0 END)
                    FROM cart_addon_items cai
                    JOIN addonitem ai ON cai.itemId = ai.itemId
                    WHERE cai.orderId = o.orderId
                ), 0) AS applicableAddons,
                COALESCE((
                    SELECT SUM(ch.chargesPrice)
                    FROM cart_charges cc
                    JOIN charges ch ON cc.chargesId = ch.chargesId
                    WHERE cc.orderId = o.orderId
                ), 0) AS chargesTotal,
                CASE WHEN o.doesChargesIncluded THEN 
                    COALESCE((
                        SELECT SUM(ch.chargesPrice)
                        FROM charges ch WHERE ch.isApplicable = 1
                    ), 0)
                ELSE 0 END AS includedCharges
            FROM cartorder o
            LEFT JOIN cart ci ON o.orderId = ci.orderId
            LEFT JOIN product p ON ci.productId = p.productId
            GROUP BY o.orderId
        )
        SELECT
            orderId,
            (productTotal + applicableAddons + nonApplicableAddons + includedCharges + chargesTotal) AS basePrice,
            nonApplicableAddons AS discountPrice,
            ((productTotal + applicableAddons + nonApplicableAddons + includedCharges + chargesTotal) - nonApplicableAddons) AS totalPrice
        FROM OrderPrices
        ORDER BY orderId DESC
    """,
)
data class CartPriceView(
    @ColumnInfo(index = true)
    val orderId: Int,

    val basePrice: Long = 0,

    val discountPrice: Long = 0,

    val totalPrice: Long = 0,

    val createdAt: String = System.currentTimeMillis().toString(),
)

fun CartPriceView.toExternalModel(): OrderPrice {
    return OrderPrice(
        orderId = this.orderId,
        basePrice = this.basePrice,
        discountPrice = this.discountPrice,
        totalPrice = this.totalPrice,
    )
}
