package com.niyaj.poposroom.features.product.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.niyaj.poposroom.features.category.domain.model.Category
import com.niyaj.poposroom.features.common.utils.getAllCapitalizedLetters
import java.util.Date

@Entity(
    tableName = "product",
    foreignKeys = [ForeignKey(
        entity = Category::class,
        parentColumns = arrayOf("categoryId"),
        childColumns = arrayOf("categoryId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Product(
    @PrimaryKey(autoGenerate = true)
    val productId: Int = 0,

    @ColumnInfo(index = true)
    val categoryId: Int = 0,

    val productName: String = "",

    val productPrice: Int = 0,

    val productDescription: String = "",

    val productAvailability : Boolean = true,

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: Date,

    @ColumnInfo(defaultValue = "NULL")
    val updatedAt: Date? = null

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