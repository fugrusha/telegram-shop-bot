package org.golovko.telegramshop.botapi;

import lombok.extern.slf4j.Slf4j;
import org.golovko.telegramshop.cache.UserDataCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
public class TelegramFacade {

    @Autowired
    private UserDataCache userDataCache;

    @Autowired
    private BotStateContext botStateContext;

    public BotApiMethod<?> handleUpdate(Update update) {

//        if (update.getMessage() != null && update.getMessage().hasContact()) {
//            return phoneNumberHandler.handle(update.getMessage());
//        }

//        if (update.hasCallbackQuery()) {
//            CallbackQuery callbackQuery = update.getCallbackQuery();
//            log.info("New callbackQuery from User: {}, userId: {}, with data: {}", update.getCallbackQuery().getFrom().getUserName(),
//                    callbackQuery.getFrom().getId(), update.getCallbackQuery().getData());
//
//            return callbackQueryFacade.processCallbackQuery(callbackQuery);
//        }

        BotApiMethod<?> replyMessage = null;
        Message message = update.getMessage();

        if (message != null && message.hasText()) {
            log.info("New message from user:{}, chatId:{}, with text:{}",
                    message.getFrom().getUserName(), message.getChatId(), message.getText());

            replyMessage = handleInputMessage(message);
        }

//        replyMessage = new SendMessage(message.getChatId(), message.getText());

        return replyMessage;
    }

    private BotApiMethod<?> handleInputMessage(Message message) {
        String inputMsg = message.getText();
        long chatId = message.getFrom().getId();

        BotState botState;
        BotApiMethod<?> replyMessage = null;

        switch (inputMsg) {
            case "/start":
                botState = BotState.START;
                break;
            case "Контакты":
                botState = BotState.SHOW_CONTACTS;
                break;
            case "Частые вопросы":
                botState = BotState.QUESTIONS;
                break;
            case "Мои заказы":
                botState = BotState.SHOW_MY_ORDERS;
                break;
            case "Каталог товара":
                botState = BotState.SHOW_CATALOG;
                break;
            case "Моя корзина":
                botState = BotState.SHOW_SHOPPING_CART;
                break;
            default:
                botState = userDataCache.getCurrentBotState(chatId);
                break;
        }

        userDataCache.setNewBotState(chatId, botState);

        replyMessage = botStateContext.processInputMessage(botState, message);

        return replyMessage;
    }
}
