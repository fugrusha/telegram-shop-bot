package org.golovko.telegramshop.botapi.handler;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface CallbackHandler {

    BotApiMethod<?> handleCallbackQuery(CallbackQuery callbackQuery);

    CallbackType getHandlerQueryType();
}
