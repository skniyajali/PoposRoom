package com.niyaj.data.data.repository

import com.niyaj.common.result.Resource
import com.niyaj.data.repository.DataDeletionRepository

class DataDeletionRepositoryImpl : DataDeletionRepository {

    override suspend fun deleteData(): Resource<Boolean> {
        return Resource.Success(true)
    }

    override suspend fun deleteAllRecords(): Resource<Boolean> {
        return Resource.Success(false)
    }
}