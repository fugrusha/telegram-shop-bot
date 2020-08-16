package org.golovko.telegramshop.botapi.handler;

import org.golovko.telegramshop.botapi.BotState;
import org.golovko.telegramshop.cache.UserDataCache;
import org.golovko.telegramshop.service.KeyboardService;
import org.golovko.telegramshop.service.ReplyMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class CancelButtonHandler implements InputMessageHandler {

    @Autowired
    private ReplyMessageService messageService;

    @Autowired
    private KeyboardService keyboardService;

    @Autowired
    private UserDataCache userDataCache;

    @Override
    public SendMessage handle(Message message) {
        return processUserInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.CANCEL_BUTTON;
    }

    private SendMessage processUserInput(Message inputMsg) {
        long chatId = inputMsg.getChatId();

        SendMessage reply = messageService.getReplyMessage(chatId, "reply.orderIsCancelled");
        reply.setReplyMarkup(keyboardService.getMainMenuKeyboard());

        userDataCache.setNewBotState(chatId, BotState.SHOW_MAIN_MENU);

        return reply;
    }
}
