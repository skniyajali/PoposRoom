package com.niyaj.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.niyaj.database.model.CartItemDto
import com.niyaj.database.model.DeliveryReportDto
import com.niyaj.database.model.ProfileEntity
import com.niyaj.model.ChargesNameAndPrice
import com.niyaj.model.OrderStatus
import com.niyaj.model.OrderType
import kotlinx.coroutines.flow.Flow

@Dao
interface PrintDao {

    @Transaction
    @Query(
        value = """
            SELECT orderId, createdAt, updatedAt, addressId FROM cartorder WHERE orderStatus = :orderStatus
            AND orderType = :orderType AND updatedAt BETWEEN :startDate 
            AND :endDate ORDER BY createdAt DESC
        """
    )
    suspend fun getDeliveryReports(
        startDate: Long,
        endDate: Long,
        orderType: OrderType = OrderType.DineOut,
        orderStatus: OrderStatus = OrderStatus.PLACED
    ): List<DeliveryReportDto>

    @Transaction
    @Query(
        value = """
        SELECT * FROM cartorder WHERE orderId = :orderId
    """
    )
    suspend fun getOrderDetails(orderId: Int): CartItemDto

    @Query(
        value = """
        SELECT chargesName, chargesPrice FROM charges WHERE isApplicable = :isApplicable ORDER BY createdAt DESC
    """
    )
    suspend fun getAllCharges(isApplicable: Boolean = true): List<ChargesNameAndPrice>

    @Query(
        value = """
        SELECT * FROM profile WHERE restaurantId = :restaurantId
    """
    )
    fun getProfileInfo(restaurantId: Int): Flow<ProfileEntity?>
}