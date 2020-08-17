package org.golovko.telegramshop.botapi.handler.callback;

import org.golovko.telegramshop.botapi.BotState;
import org.golovko.telegramshop.botapi.handler.CallbackHandler;
import org.golovko.telegramshop.botapi.handler.CallbackType;
import org.golovko.telegramshop.cache.UserDataCache;
import org.golovko.telegramshop.service.ReplyMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class IgnoreCallbackHandler implements CallbackHandler {
    @Autowired
    private UserDataCache userDataCache;

    @Autowired
    private ReplyMessageService messageService;

    @Override
    public BotApiMethod<?> handleCallbackQuery(CallbackQuery callbackQuery) {
        return processUsersCallback(callbackQuery);
    }

    @Override
    public CallbackType getHandlerQueryType() {
        return CallbackType.IGNORE;
    }

    private BotApiMethod<?> processUsersCallback(CallbackQuery callbackQuery) {
        final long chatId = callbackQuery.getMessage().getChatId();
        userDataCache.setNewBotState(chatId, BotState.SHOW_MAIN_MENU);

        return getAnswerCallbackQuery(
                messageService.getReplyText("reply.buttonDoesNotWork"), callbackQuery);
    }

    private AnswerCallbackQuery getAnswerCallbackQuery(String text, CallbackQuery callbackquery) {
        AnswerCallbackQuery answer = new AnswerCallbackQuery();
        answer.setCallbackQueryId(callbackquery.getId());
        answer.setShowAlert(false);
        answer.setText(text);
        return answer;
    }
}
