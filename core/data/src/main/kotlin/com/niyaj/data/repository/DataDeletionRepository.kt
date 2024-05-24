/*
 *      Copyright 2024 Sk Niyaj Ali
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

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