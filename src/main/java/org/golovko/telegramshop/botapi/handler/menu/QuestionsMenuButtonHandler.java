package org.golovko.telegramshop.botapi.handler.menu;

import org.golovko.telegramshop.botapi.BotState;
import org.golovko.telegramshop.botapi.handler.InputMessageHandler;
import org.golovko.telegramshop.cache.UserDataCache;
import org.golovko.telegramshop.service.ReplyMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class QuestionsMenuButtonHandler implements InputMessageHandler {

    @Autowired
    private ReplyMessageService messageService;

    @Autowired
    private UserDataCache userDataCache;

    @Override
    public SendMessage handle(Message message) {
        return processUsersInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.QUESTIONS;
    }

    private SendMessage processUsersInput(Message inputMsg) {
        long chatId = inputMsg.getChatId();

        userDataCache.setNewBotState(chatId, BotState.SHOW_MAIN_MENU);

        return messageService.getReplyMessage(chatId, "reply.questionsMenuButton");
    }
}
