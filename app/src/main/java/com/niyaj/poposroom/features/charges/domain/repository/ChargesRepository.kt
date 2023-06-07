package com.niyaj.poposroom.features.charges.domain.repository

import com.niyaj.poposroom.features.charges.domain.model.Charges
import com.niyaj.poposroom.features.common.utils.Resource
import kotlinx.coroutines.flow.Flow

interface ChargesRepository {

    suspend fun getAllCharges(searchText: String): Flow<List<Charges>>

    suspend fun getChargesById(chargesId: Int): Resource<Charges?>

    suspend fun addOrIgnoreCharges(newCharges: Charges): Resource<Boolean>

    suspend fun updateCharges(newCharges: Charges): Resource<Boolean>

    suspend fun upsertCharges(newCharges: Charges): Resource<Boolean>

    suspend fun deleteCharges(chargesId: Int): Resource<Boolean>

    suspend fun deleteCharges(chargesIds: List<Int>): Resource<Boolean>
}