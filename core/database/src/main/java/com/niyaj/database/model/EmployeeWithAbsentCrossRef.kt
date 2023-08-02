package com.niyaj.database.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Junction
import androidx.room.Relation
import com.niyaj.model.EmployeeWithAbsents

@Entity(
    primaryKeys = ["employeeId", "absentId"],
    foreignKeys = [
        ForeignKey(
            entity = EmployeeEntity::class,
            parentColumns = arrayOf("employeeId"),
            childColumns = arrayOf("employeeId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = AbsentEntity::class,
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


data class EmployeeWithAbsentsDto(
    @Embedded
    val employee: EmployeeEntity,

    @Relation(
        parentColumn = "employeeId",
        entity = AbsentEntity::class,
        entityColumn = "absentId",
        associateBy = Junction(EmployeeWithAbsentCrossRef::class)
    )
    val absents: List<AbsentEntity> = emptyList()
)

fun EmployeeWithAbsentsDto.asExternalModel(): EmployeeWithAbsents {
    return EmployeeWithAbsents(
        employee = this.employee.asExternalModel(),
        absents = this.absents.map { it.asExternalModel() }
    )
}