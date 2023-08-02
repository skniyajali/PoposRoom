package com.niyaj.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.niyaj.common.utils.Constants.RESTAURANT_ID
import com.niyaj.database.model.ProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {

    @Query(value = """
        SELECT * FROM profile WHERE restaurantId = :restaurantId
    """)
    fun getProfileInfo(restaurantId: Int = RESTAURANT_ID): Flow<ProfileEntity?>

    @Query(value = """
        SELECT * FROM profile WHERE restaurantId = :restaurantId
    """)
    fun getProfileById(restaurantId: Int = RESTAURANT_ID): ProfileEntity?

//    @Query(
//        value = """
//
//        """
//    )
//    suspend fun updateProfileLogo(resLogo: String, restaurantId: Int = RESTAURANT_ID): Long

    @Upsert
    suspend fun insertOrUpdateProfile(profile: ProfileEntity): Long
}