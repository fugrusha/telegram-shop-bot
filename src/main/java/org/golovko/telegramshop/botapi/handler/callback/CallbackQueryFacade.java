package org.golovko.telegramshop.botapi.handler.callback;

import org.golovko.telegramshop.botapi.handler.CallbackHandler;
import org.golovko.telegramshop.botapi.handler.CallbackType;
import org.golovko.telegramshop.service.ReplyMessageService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;
import java.util.Optional;

@Component
public class CallbackQueryFacade {

    private final ReplyMessageService messageService;
    private List<CallbackHandler> callbackQueryHandlers;

    public CallbackQueryFacade(
            ReplyMessageService messagesService,
            List<CallbackHandler> callbackQueryHandlers
    ) {
        this.messageService = messagesService;
        this.callbackQueryHandlers = callbackQueryHandlers;
    }

    public BotApiMethod<?> processCallbackQuery(CallbackQuery usersQuery) {

        CallbackType usersQueryType = CallbackType.valueOf(usersQuery.getData().split("=")[0]);

        Optional<CallbackHandler> queryHandler = callbackQueryHandlers
                .stream()
                .filter(callbackQuery -> callbackQuery.getHandlerQueryType().equals(usersQueryType))
                .findFirst();

        BotApiMethod<?> replyToUser = queryHandler.map(handler -> handler.handleCallbackQuery(usersQuery))
                .orElse(messageService.getWarningReplyMessage(usersQuery.getMessage().getChatId(),
                        "reply.query.failed"));

        return replyToUser;
    }
}
