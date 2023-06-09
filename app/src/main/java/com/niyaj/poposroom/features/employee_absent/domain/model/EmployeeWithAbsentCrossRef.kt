package com.niyaj.poposroom.features.employee_absent.domain.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Junction
import androidx.room.Relation
import com.niyaj.poposroom.features.employee.domain.model.Employee

@Entity(
    primaryKeys = ["employeeId", "absentId"],
    foreignKeys = [
        ForeignKey(
            entity = Employee::class,
            parentColumns = arrayOf("employeeId"),
            childColumns = arrayOf("employeeId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = Absent::class,
            parentColumns = arrayOf("absentId"),
            childColumns = arrayOf("absentId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        )
    ]
)
data class EmployeeWithAbsentCrossRef(
    @ColumnInfo(index = true)
    val employeeId: Int,

    @ColumnInfo(index = true)
    val absentId: Int
)



data class EmployeeWithAbsent(
    @Embedded
    val employee: Employee,

    @Relation(
        parentColumn = "employeeId",
        entity = Absent::class,
        entityColumn = "absentId",
        associateBy = Junction(EmployeeWithAbsentCrossRef::class)
    )
    val absents: List<Absent> = emptyList()
)