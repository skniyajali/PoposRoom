/*
 * Copyright 2024 Sk Niyaj Ali
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.niyaj.customer.createOrUpdate

sealed interface AddEditCustomerEvent {
    /**
     * Customer Name Changed Event Class
     * @param customerName [String]
     * @return [AddEditCustomerEvent]
     * @see AddEditCustomerEvent
     */
    data class CustomerNameChanged(val customerName: String) : AddEditCustomerEvent

    /**
     * Customer Email Changed Event Class
     * @param customerEmail [String]
     * @return [AddEditCustomerEvent]
     * @see AddEditCustomerEvent
     */
    data class CustomerEmailChanged(val customerEmail: String) : AddEditCustomerEvent

    /**
     * Customer Phone Changed Event Class
     * @param customerPhone [String]
     */
    data class CustomerPhoneChanged(val customerPhone: String) : AddEditCustomerEvent

    data class CreateOrUpdateCustomer(val customerId: Int = 0) : AddEditCustomerEvent
}
