package com.niyaj.common.utils

object Constants {

    const val UPDATE_MANAGER_REQUEST_CODE = 123
    const val NOTIFICATION_PERMISSION_REQUEST_CODE = 888
    const val NETWORK_PERMISSION_REQUEST_CODE = 777

    // github login token - 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

    const val SPLASH_SCREEN_DURATION = 100

    const val PRINTER_ID = "PRINTER11"

    const val PRINTER_DPI = 176

    const val PRINTER_WIDTH_MM = 58f

    const val PRINTER_NBR_LINE = 31

    const val PAYMENT_QR_DATA = "upi://pay?pa=paytmqr281005050101zry6uqipmngr@paytm&pn=Paytm%20Merchant&paytmqr=281005050101ZRY6UQIPMNGR"

    const val PRODUCT_NAME_LENGTH = 18

    const val PRINT_PRODUCT_WISE_REPORT_LIMIT = 30

    const val PRINT_ADDRESS_WISE_REPORT_LIMIT = 15

    const val PRINT_CUSTOMER_WISE_REPORT_LIMIT = 15

    const val JSON_FILE_TYPE = "application/json"

    const val JSON_FILE_EXTENSION = ".json"

    const val BACKUP_REALM_NAME = "backup.realm"

    const val SAVEABLE_FILE_NAME = "popos"

    const val PAID = "Paid"

    const val NOT_PAID = "Not Paid"

    const val SELECTED_CART_ORDER_ID = "33333333"
    const val SETTINGS_ID = "11111111"
    const val RESTAURANT_ID = 2222

    const val RESTAURANT_NAME = "Popos Highlight"
    const val RESTAURANT_TAGLINE = "- Pure And Tasty -"
    const val RESTAURANT_DESCRIPTION = "Multi Cuisine Veg & Non-Veg Restaurant"
    const val RESTAURANT_EMAIL = "poposhighlight@gmail.com"
    const val RESTAURANT_SECONDARY_PHONE: String = "9597185001"
    const val RESTAURANT_PRIMARY_PHONE: String = "9500825077"
    const val RESTAURANT_ADDRESS = "Chinna Seeragapadi, Salem, TamilNadu, India 636308, Opp. of VIMS Hospital"

    const val RESTAURANT_LOGO_NAME = "reslogo.png"
    const val RESTAURANT_PRINT_LOGO_NAME = "printlogo.png"

//    const val RESTAURANT_LOGO = R.drawable.logo_new.toString()
//    const val PRINT_LOGO = R.drawable.reslogo.toString()

    //Dialog positive button test tag
    const val POSITIVE_BUTTON = "positive"
    const val NEGATIVE_BUTTON = "negative"

    const val STANDARD_BOTTOM_SHEET = "BottomSheet"
    const val STANDARD_BOTTOM_SHEET_CLOSE_BTN = "Standard Bottom Close Button"

    const val SORT_ASCENDING = "Sort Ascending"
    const val SORT_DESCENDING = "Sort Descending"

    const val ABSENT_REMINDER_NOTE = "Selected employees will be mark as absent."
    const val DAILY_SALARY_REMINDER_NOTE = "Selected employees will be mark as paid."

    const val TEXT_FIELD_LEADING_ICON = "TextFieldLeadingIcon"
    const val TEXT_FIELD_TRAILING_ICON = "TextFieldLeadingIcon"

    const val SEARCH_ITEM_NOT_FOUND = "Searched Item Not Found."
    const val SEARCH_ITEM_PLACEHOLDER = "Search for items..."

    const val STANDARD_BACK_BUTTON = "StandardBackButton"
    const val SEARCH_BAR_CLEAR_BUTTON = "SearchBarClearButton"
    const val STANDARD_SEARCH_BAR = "StandardSearchBar"
    const val FAB_TEXT = "Create New"
    const val LOADING_INDICATION = "loadingIndicator"
    const val SEARCH_ICON = "SearchIcon"
    const val SEARCH_PLACEHOLDER = "SearchPlaceHolder"
    const val SETTINGS_ICON = "SettingsIcon"
    const val EDIT_ICON = "EditIcon"
    const val DELETE_ICON = "DeleteIcon"
    const val CLEAR_ICON = "ClearIcon"
    const val DRAWER_ICON = "DrawerIcon"
    const val SELECTALL_ICON = "SelectAllIcon"
    const val PASSWORD_HIDDEN_ICON = "Password Hidden"
    const val PASSWORD_SHOWN_ICON = "Password Shown"
}


enum class ImportExportType {
    IMPORT,
    EXPORT
}