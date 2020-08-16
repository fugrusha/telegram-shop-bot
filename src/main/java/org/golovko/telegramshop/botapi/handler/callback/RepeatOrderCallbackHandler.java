package org.golovko.telegramshop.botapi.handler.callback;

import org.golovko.telegramshop.botapi.handler.CallbackHandler;
import org.golovko.telegramshop.botapi.handler.CallbackType;
import org.golovko.telegramshop.botapi.handler.menu.ShoppingCartMenuButtonHandler;
import org.golovko.telegramshop.domain.OrderCart;
import org.golovko.telegramshop.service.OrderService;
import org.golovko.telegramshop.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.UUID;

@Component
public class RepeatOrderCallbackHandler implements CallbackHandler {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ShoppingCartService cartService;

    @Autowired
    private ShoppingCartMenuButtonHandler shoppingCartMenuButtonHandler;

    @Override
    public BotApiMethod<?> handleCallbackQuery(CallbackQuery callbackQuery) {
        return processUsersCallback(callbackQuery);
    }

    @Override
    public CallbackType getHandlerQueryType() {
        return CallbackType.REPEAT_ORDER;
    }

    private BotApiMethod<?> processUsersCallback(CallbackQuery buttonQuery) {
        final long chatId = buttonQuery.getMessage().getChatId();

        UUID orderId = UUID.fromString(buttonQuery.getData().split("=")[1]);

        OrderCart order = orderService.getOrderById(orderId);
        cartService.copyOrderToShoppingCart(order, chatId);

        return shoppingCartMenuButtonHandler.handle(buttonQuery.getMessage());
    }
}
