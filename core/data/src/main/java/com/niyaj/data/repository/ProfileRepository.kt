package com.niyaj.data.repository

import com.niyaj.common.result.Resource
import com.niyaj.model.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {

    fun getProfileInfo(): Flow<Profile>

    suspend fun updateRestaurantLogo(imageName: String): Resource<Boolean>

    suspend fun updatePrintLogo(imageName: String): Resource<Boolean>

    suspend fun insertOrUpdateProfile(profile: Profile): Resource<Boolean>
}