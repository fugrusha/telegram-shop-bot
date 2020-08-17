package org.golovko.telegramshop.botapi.handler.callback;

import org.golovko.telegramshop.botapi.handler.CallbackHandler;
import org.golovko.telegramshop.botapi.handler.CallbackType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CallbackQueryFacade {

    private final Map<CallbackType, CallbackHandler> callbackHandlersMap = new HashMap<>();

    @Autowired
    public CallbackQueryFacade(List<CallbackHandler> callbackQueryHandlers) {
        callbackQueryHandlers.forEach(handler ->
                        this.callbackHandlersMap.put(handler.getHandlerQueryType(), handler));

    }

    public BotApiMethod<?> processCallbackQuery(CallbackQuery usersQuery) {
        CallbackType usersQueryType = CallbackType.valueOf(usersQuery.getData().split("=")[0]);

        CallbackHandler currentHandler = findCallbackHandler(usersQueryType);
        return currentHandler.handleCallbackQuery(usersQuery);
    }

    private CallbackHandler findCallbackHandler(CallbackType callbackType) {
        if (isFillingOrderInfoType(callbackType)) {
            return callbackHandlersMap.get(CallbackType.FILLING_ORDER_INFO);
        }

        if (isConfirmOrderType(callbackType)) {
            return callbackHandlersMap.get(CallbackType.CREATING_ORDER);
        }

        return callbackHandlersMap.get(callbackType);
    }

    private boolean isFillingOrderInfoType(CallbackType callbackType) {
        switch (callbackType) {
            case PREPAYMENT:
            case NP_PAYMENT:
                return true;
            default:
                return false;
        }
    }

    private boolean isConfirmOrderType(CallbackType callbackType) {
        switch (callbackType) {
            case CANCEL_ORDER:
            case CONFIRM_ORDER:
                return true;
            default:
                return false;
        }
    }
}
