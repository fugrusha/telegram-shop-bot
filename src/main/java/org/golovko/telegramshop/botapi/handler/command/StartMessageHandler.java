package org.golovko.telegramshop.botapi.handler.command;

import org.golovko.telegramshop.botapi.BotState;
import org.golovko.telegramshop.botapi.handler.InputMessageHandler;
import org.golovko.telegramshop.cache.UserDataCache;
import org.golovko.telegramshop.service.CustomerService;
import org.golovko.telegramshop.service.KeyboardService;
import org.golovko.telegramshop.service.ReplyMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class StartMessageHandler implements InputMessageHandler {

    @Autowired
    private ReplyMessageService messageService;

    @Autowired
    private KeyboardService keyboardService;

    @Autowired
    private UserDataCache userDataCache;

    @Autowired
    private CustomerService customerService;

    @Override
    public SendMessage handle(Message message) {
        return processUsersInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.START;
    }

    private SendMessage processUsersInput(Message inputMsg) {

        customerService.checkUserIfExists(inputMsg);

        long chatId = inputMsg.getChatId();
        String name = inputMsg.getFrom().getFirstName();

        SendMessage replyToUser = messageService.getReplyMessage(chatId, "reply.startMessage", name);
        replyToUser.setReplyMarkup(keyboardService.getMainMenuKeyboard());

        userDataCache.setNewBotState(chatId, BotState.SHOW_MAIN_MENU);

        return replyToUser;
    }
}
