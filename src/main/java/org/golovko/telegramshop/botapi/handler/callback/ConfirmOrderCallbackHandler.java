package org.golovko.telegramshop.botapi.handler.callback;

import org.golovko.telegramshop.botapi.BotState;
import org.golovko.telegramshop.botapi.handler.CallbackHandler;
import org.golovko.telegramshop.botapi.handler.CallbackType;
import org.golovko.telegramshop.cache.UserDataCache;
import org.golovko.telegramshop.domain.Customer;
import org.golovko.telegramshop.domain.OrderCart;
import org.golovko.telegramshop.domain.model.CartItem;
import org.golovko.telegramshop.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;

@Component
public class ConfirmOrderCallbackHandler implements CallbackHandler {

    @Autowired
    private ReplyMessageService messageService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private UserDataCache userDataCache;

    @Autowired
    private KeyboardService keyboardService;

    @Autowired
    private ShoppingCartService cartService;

    @Autowired
    private OrderService orderService;

    @Override
    public BotApiMethod<?> handleCallbackQuery(CallbackQuery callbackQuery) {
        return processUsersCallback(callbackQuery);
    }

    @Override
    public CallbackType getHandlerQueryType() {
        return CallbackType.CREATING_ORDER;
    }

    private BotApiMethod<?> processUsersCallback(CallbackQuery buttonQuery) {
        final long chatId = buttonQuery.getMessage().getChatId();

        CallbackType callbackType = CallbackType.valueOf(buttonQuery.getData().split("=")[0]);

        SendMessage replyToUser = null;

        if (callbackType.equals(CallbackType.CANCEL_ORDER)) {
            replyToUser = messageService.getReplyMessage(chatId,
                    "reply.orderIsCancelled");
        }

        if (callbackType.equals(CallbackType.CONFIRM_ORDER)) {
            String orderNumber = createNewOrder(chatId);

            replyToUser = messageService.getReplyMessage(chatId,
                    "reply.orderWasCreated", orderNumber);
        }

        replyToUser.setParseMode("HTML");
        replyToUser.setReplyMarkup(keyboardService.getMainMenuKeyboard());
        userDataCache.setNewBotState(chatId, BotState.SHOW_MAIN_MENU);
        return replyToUser;
    }

    private String createNewOrder(long chatId) {
        Customer customer = customerService.getByChatId(chatId);
        List<CartItem> allCartItems = cartService.findAllCartItemsByChatId(chatId);

        OrderCart orderCart = orderService.createOrder(customer, allCartItems);

        cartService.deleteAllCartItemsByChatId(chatId);

        return orderCart.getOrderNumber();
    }
}
