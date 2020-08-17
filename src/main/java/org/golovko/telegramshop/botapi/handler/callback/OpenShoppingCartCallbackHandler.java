package org.golovko.telegramshop.botapi.handler.callback;

import org.golovko.telegramshop.botapi.handler.CallbackHandler;
import org.golovko.telegramshop.botapi.handler.CallbackType;
import org.golovko.telegramshop.botapi.handler.menu.ShoppingCartMenuButtonHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class OpenShoppingCartCallbackHandler implements CallbackHandler {

    @Autowired
    private ShoppingCartMenuButtonHandler shoppingCartMenuButtonHandler;

    @Override
    public BotApiMethod<?> handleCallbackQuery(CallbackQuery callbackQuery) {
        return shoppingCartMenuButtonHandler.handle(callbackQuery.getMessage());
    }

    @Override
    public CallbackType getHandlerQueryType() {
        return CallbackType.OPEN_SHOPPING_CART;
    }
}
