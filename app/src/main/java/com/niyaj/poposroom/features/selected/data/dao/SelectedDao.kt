package com.niyaj.poposroom.features.selected.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.niyaj.poposroom.features.cart_order.domain.model.CartOrderEntity
import com.niyaj.poposroom.features.cart_order.domain.utils.OrderStatus
import com.niyaj.poposroom.features.selected.domain.model.Selected
import com.niyaj.poposroom.features.selected.domain.utils.SelectedTestTag.SELECTED_ID
import kotlinx.coroutines.flow.Flow

@Dao
interface SelectedDao {

    @Query(
        value = """
        SELECT * FROM selected LIMIT 1
    """
    )
    fun getSelectedCartOrder(): Flow<Selected?>

    @Query(
        value = """
        SELECT orderId FROM selected LIMIT 1
    """
    )
    fun getSelectedCartOrderId(): Int?

    @Upsert
    suspend fun insertOrUpdateSelectedOrder(selected: Selected): Long

    @Query(
        value = """
        DELETE FROM selected WHERE selectedId = :id
    """
    )
    suspend fun deleteSelectedOrder(id: String = SELECTED_ID)

    @Query(
        value = """
        SELECT * FROM cartorder WHERE orderStatus = :orderStatus ORDER BY orderId DESC
    """
    )
    fun getAllProcessingCartOrders(orderStatus: OrderStatus = OrderStatus.PROCESSING): Flow<List<CartOrderEntity>>
}