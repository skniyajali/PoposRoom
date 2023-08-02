package com.niyaj.customer.add_edit


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