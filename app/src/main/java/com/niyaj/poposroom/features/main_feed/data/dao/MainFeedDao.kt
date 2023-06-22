package com.niyaj.poposroom.features.main_feed.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.niyaj.poposroom.features.category.domain.model.Category
import com.niyaj.poposroom.features.product.domain.model.Product
import com.niyaj.poposroom.features.selected.domain.model.Selected
import kotlinx.coroutines.flow.Flow

@Dao
interface MainFeedDao {

    @Query(value = """
        SELECT * FROM category WHERE isAvailable = :isAvailable ORDER BY categoryId ASC
    """)
    fun getAllCategories(isAvailable: Boolean = true): Flow<List<Category>>


    @Query(value = """
        SELECT * FROM product WHERE productAvailability = :isAvailable ORDER BY categoryId ASC, productPrice ASC
    """)
    fun getAllProduct(isAvailable: Boolean = true): Flow<List<Product>>


    @Query(
        value = """
        SELECT * FROM selected LIMIT 1
    """
    )
    fun getSelectedOrder(): Flow<Selected?>
}