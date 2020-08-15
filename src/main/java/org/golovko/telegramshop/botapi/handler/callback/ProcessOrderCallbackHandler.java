package org.golovko.telegramshop.botapi.handler.callback;

import org.golovko.telegramshop.botapi.BotState;
import org.golovko.telegramshop.botapi.handler.CallbackHandler;
import org.golovko.telegramshop.botapi.handler.CallbackType;
import org.golovko.telegramshop.cache.UserDataCache;
import org.golovko.telegramshop.service.KeyboardService;
import org.golovko.telegramshop.service.ReplyMessageService;
import org.golovko.telegramshop.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class ProcessOrderCallbackHandler implements CallbackHandler {

    @Autowired
    private UserDataCache userDataCache;

    @Autowired
    private ShoppingCartService cartService;

    @Autowired
    private ReplyMessageService messageService;

    @Autowired
    private KeyboardService keyboardService;

    @Override
    public BotApiMethod<?> handleCallbackQuery(CallbackQuery callbackQuery) {
        return processUsersCallback(callbackQuery);
    }

    @Override
    public CallbackType getHandlerQueryType() {
        return CallbackType.PROCESS_ORDER;
    }

    private BotApiMethod<?> processUsersCallback(CallbackQuery buttonQuery) {
        final long chatId = buttonQuery.getMessage().getChatId();

        SendMessage replyToUser = messageService.getReplyMessage(chatId, "reply.enterFullName");

        userDataCache.setNewBotState(chatId, BotState.ENTER_FULL_NAME);

        return replyToUser;
    }
}
