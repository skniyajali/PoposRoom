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
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.niyaj.model.AddOnItem
import java.util.Date

@Entity(tableName = "addonitem")
data class AddOnItemEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true)
    val itemId: Int,

    val itemName: String,

    val itemPrice: Int,

    val isApplicable: Boolean,

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: Date,

    @ColumnInfo(defaultValue = "NULL")
    val updatedAt: Date? = null,
)

fun AddOnItemEntity.asExternalModel(): AddOnItem {
    return AddOnItem(
        itemId = this.itemId,
        itemName = this.itemName,
        itemPrice = this.itemPrice,
        isApplicable = this.isApplicable,
        createdAt = this.createdAt.time,
        updatedAt = this.updatedAt?.time,
    )
}
