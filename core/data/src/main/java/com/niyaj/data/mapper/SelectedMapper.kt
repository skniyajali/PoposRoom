package com.niyaj.data.mapper

import com.niyaj.database.model.SelectedEntity
import com.niyaj.model.Selected

fun Selected.toEntity(): SelectedEntity {
    return SelectedEntity(
        selectedId = this.selectedId,
        orderId = this.orderId
    )
}