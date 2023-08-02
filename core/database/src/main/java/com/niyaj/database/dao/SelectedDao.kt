package com.niyaj.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.niyaj.database.model.CartOrderEntity
import com.niyaj.database.model.SelectedEntity
import com.niyaj.model.OrderStatus
import com.niyaj.model.Selected
import kotlinx.coroutines.flow.Flow

@Dao
interface SelectedDao {

    @Query(
        value = """
        SELECT * FROM selected LIMIT 1
    """
    )
    fun getSelectedCartOrder(): Flow<SelectedEntity?>

    @Query(
        value = """
        SELECT orderId FROM selected LIMIT 1
    """
    )
    fun getSelectedCartOrderId(): Int?

    @Upsert
    suspend fun insertOrUpdateSelectedOrder(selected: SelectedEntity): Long

    @Query(
        value = """
        DELETE FROM selected WHERE selectedId = :id
    """
    )
    suspend fun deleteSelectedOrder(id: String)

    @Query(
        value = """
        SELECT * FROM cartorder WHERE orderStatus = :orderStatus ORDER BY orderId DESC
    """
    )
    fun getAllProcessingCartOrders(orderStatus: OrderStatus = OrderStatus.PROCESSING): Flow<List<CartOrderEntity>>
}