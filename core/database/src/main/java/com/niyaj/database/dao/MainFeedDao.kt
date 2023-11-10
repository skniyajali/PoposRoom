package com.niyaj.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.niyaj.database.model.CategoryEntity
import com.niyaj.database.model.ProductEntity
import com.niyaj.database.model.SelectedEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MainFeedDao {

    @Query(value = """
        SELECT * FROM category WHERE isAvailable = :isAvailable ORDER BY categoryId ASC
    """)
    fun getAllCategories(isAvailable: Boolean = true): Flow<List<CategoryEntity>>


    @Query(value = """
        SELECT * FROM product WHERE productAvailability = :isAvailable ORDER BY categoryId ASC, productPrice ASC
    """)
    fun getAllProducts(isAvailable: Boolean = true): Flow<List<ProductEntity>>


    @Query(
        value = """
        SELECT * FROM selected LIMIT 1
    """
    )
    fun getSelectedOrder(): Flow<SelectedEntity?>


    @Query(
        value = """
        SELECT addressName FROM address INNER JOIN cartorder ON address.addressId is cartorder.addressId WHERE cartorder.orderId = :orderId
    """
    )
    fun getSelectedOrderAddress(orderId: Int): String?


    @Query(value = """
        SELECT quantity FROM cart WHERE orderId = :orderId AND productId = :productId
    """
    )
    fun getProductQuantity(orderId: Int, productId: Int): Flow<Int>
}