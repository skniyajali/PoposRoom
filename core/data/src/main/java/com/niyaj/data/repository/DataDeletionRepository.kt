package com.niyaj.data.repository

import com.niyaj.common.result.Resource

interface DataDeletionRepository {

    /**
     * ## Prerequisites
    -  Delete **CartOrder** data before today date.
    -  Delete **Cart** data before today start date.
    -  Generate Report Before Deleting Data
     * @return [Resource] of [Boolean] type
     */
    suspend fun deleteData(): Resource<Boolean>

    /**
     * This method will clean up all database records including today data.
     * @return [Resource] of [Boolean] type
     */
    suspend fun deleteAllRecords(): Resource<Boolean>
}