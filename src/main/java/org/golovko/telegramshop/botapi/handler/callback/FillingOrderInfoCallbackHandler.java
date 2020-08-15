package org.golovko.telegramshop.botapi.handler.callback;

import org.golovko.telegramshop.botapi.BotState;
import org.golovko.telegramshop.botapi.handler.CallbackHandler;
import org.golovko.telegramshop.botapi.handler.CallbackType;
import org.golovko.telegramshop.cache.UserDataCache;
import org.golovko.telegramshop.domain.Customer;
import org.golovko.telegramshop.domain.PaymentType;
import org.golovko.telegramshop.service.CustomerService;
import org.golovko.telegramshop.service.KeyboardService;
import org.golovko.telegramshop.service.ReplyMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class FillingOrderInfoCallbackHandler implements CallbackHandler {

    @Autowired
    private ReplyMessageService messageService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private UserDataCache userDataCache;

    @Autowired
    private KeyboardService keyboardService;

    @Override
    public BotApiMethod<?> handleCallbackQuery(CallbackQuery callbackQuery) {
        return processUsersCallback(callbackQuery);
    }

    @Override
    public CallbackType getHandlerQueryType() {
        return CallbackType.FILLING_ORDER_INFO;
    }

    private BotApiMethod<?> processUsersCallback(CallbackQuery buttonQuery) {
        final long chatId = buttonQuery.getMessage().getChatId();

        Customer customer = customerService.getByChatId(chatId);

        CallbackType callbackType = CallbackType.valueOf(buttonQuery.getData().split("=")[0]);

        if (callbackType.equals(CallbackType.NP_PAYMENT)) {
            customer.setPaymentType(PaymentType.NP_PAYMENT);
        }

        if (callbackType.equals(CallbackType.PREPAYMENT)) {
            customer.setPaymentType(PaymentType.PREPAYMENT);
        }

        customerService.saveCustomer(customer);
        userDataCache.setNewBotState(chatId, BotState.CONFIRM_ORDER);

        SendMessage replyToUser = messageService.getReplyMessage(chatId, "reply.confirmOrder");
        replyToUser.setParseMode("HTML");
        replyToUser.setReplyMarkup(keyboardService.getConfirmOrderKeyboard());

        return replyToUser;
    }
}
