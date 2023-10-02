package com.niyaj.model

import com.niyaj.common.utils.getAllCapitalizedLetters
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class Product(
    val productId: Int = 0,

    val categoryId: Int = 0,

    val productName: String = "",

    val productPrice: Int = 0,

    val productDescription: String = "",

    val productAvailability : Boolean = true,

    val createdAt: Long,

    val updatedAt: Long? = null

)


/**
 * Filter products
 */
fun List<Product>.filterProducts(searchText : String) : List<Product> {
    return if (searchText.isNotEmpty()) {
        this.filter {
            it.productName.contains(searchText, true) ||
                    it.productPrice.toString().contains(searchText, true) ||
                    it.productAvailability.toString().contains(searchText, true) ||
                    getAllCapitalizedLetters(it.productName).contains(searchText, true)
        }
    } else this

}

/**
 * Filter product by category
 */
fun Product.filterByCategory(categoryId : String) : Boolean {
    return if (categoryId.isNotEmpty()) {
        this.categoryId == categoryId.toInt()
    }else true
}