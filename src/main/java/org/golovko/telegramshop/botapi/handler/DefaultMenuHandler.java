package org.golovko.telegramshop.botapi.handler;

import org.golovko.telegramshop.botapi.BotState;
import org.golovko.telegramshop.service.ReplyMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class DefaultMenuHandler implements InputMessageHandler {

    @Autowired
    private ReplyMessageService messageService;

    @Override
    public SendMessage handle(Message message) {
        return processUserInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_MAIN_MENU;
    }

    private SendMessage processUserInput(Message inputMsg) {
        long chatId = inputMsg.getChatId();

        return messageService.getReplyMessage(chatId, "reply.default");
    }
}
