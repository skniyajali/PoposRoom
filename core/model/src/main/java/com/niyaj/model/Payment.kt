package com.niyaj.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Payment(
    val paymentId: Int,

    val employeeId: Int,

    val paymentAmount: String = "",

    val paymentDate: String = "",

    val paymentType: PaymentType = PaymentType.Advanced,

    val paymentMode: PaymentMode = PaymentMode.Cash,

    val paymentNote: String = "",

    val createdAt: Long = System.currentTimeMillis(),

    val updatedAt: Long? = null,
)


fun List<Payment>.searchPayment(searchText: String): List<Payment> {
    return if (searchText.isNotEmpty()) {
        this.filter {
            it.paymentAmount.contains(searchText, true) ||
                    it.paymentType.name.contains(searchText, true) ||
                    it.paymentDate.contains(searchText, true) ||
                    it.paymentMode.name.contains(searchText, true) ||
                    it.paymentNote.contains(searchText, true)
        }
    } else this
}

fun Payment.filterPayment(searchText: String): Boolean {
    return if (searchText.isNotEmpty()) {
        this.paymentAmount.contains(searchText, true) ||
                this.paymentType.name.contains(searchText, true) ||
                this.paymentDate.contains(searchText, true) ||
                this.paymentMode.name.contains(searchText, true) ||
                this.paymentNote.contains(searchText, true)
    } else true
}