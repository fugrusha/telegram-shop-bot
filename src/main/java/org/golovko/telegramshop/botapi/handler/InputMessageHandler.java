package org.golovko.telegramshop.botapi.handler;

import org.golovko.telegramshop.botapi.BotState;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface InputMessageHandler {

    BotApiMethod<?> handle(Message message);

    BotState getHandlerName();
}
