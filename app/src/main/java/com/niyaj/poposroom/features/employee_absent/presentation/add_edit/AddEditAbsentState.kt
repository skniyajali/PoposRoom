package com.niyaj.poposroom.features.employee_absent.presentation.add_edit

import com.niyaj.poposroom.features.common.utils.toMilliSecond
import java.time.LocalDate

data class AddEditAbsentState(
    val absentDate: String = LocalDate.now().toMilliSecond,

    val absentReason: String = "",
)
