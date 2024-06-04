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

package com.niyaj.print

sealed interface PrintEvent {

    data class PrintOrder(val orderId: Int) : PrintEvent

    data class PrintOrders(val orderIds: List<Int>) : PrintEvent

    data class PrintDeliveryReport(val date: String, val partnerId: Int? = null) : PrintEvent

    data class PrintAllExpenses(val date: String) : PrintEvent
}
