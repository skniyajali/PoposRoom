package com.niyaj.data.repository

import com.niyaj.common.result.Resource
import com.niyaj.model.Charges
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