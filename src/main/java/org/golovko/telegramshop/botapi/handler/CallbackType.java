package org.golovko.telegramshop.botapi.handler;

public enum CallbackType {
    IGNORE,
    ADD_TO_CART,
    EDIT_CART,
    CLEAN_CART,
    PROCESS_ORDER,
    CATEGORY,
    FILLING_ORDER_INFO,
    PREPAYMENT,
    NP_PAYMENT,
    CONFIRM_ORDER,
    CANCEL_ORDER
}
