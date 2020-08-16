package org.golovko.telegramshop.botapi;

import lombok.extern.slf4j.Slf4j;
import org.golovko.telegramshop.botapi.handler.PhoneNumberHandler;
import org.golovko.telegramshop.botapi.handler.callback.CallbackQueryFacade;
import org.golovko.telegramshop.cache.UserDataCache;
import org.golovko.telegramshop.service.AdminService;
import org.golovko.telegramshop.service.ReplyMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
public class TelegramFacade {

    @Autowired
    private UserDataCache userDataCache;

    @Autowired
    private BotStateContext botStateContext;

    @Autowired
    private CallbackQueryFacade callbackQueryFacade;

    @Autowired
    private AdminService adminService;

    @Autowired
    private ReplyMessageService messageService;

    @Autowired
    private PhoneNumberHandler phoneNumberHandler;

    public BotApiMethod<?> handleUpdate(Update update) {

        if (update.hasCallbackQuery() || update.hasInlineQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            log.info("New callbackQuery from User: {}, userId: {}, with data: {}",
                    update.getCallbackQuery().getFrom().getUserName(),
                    callbackQuery.getFrom().getId(), update.getCallbackQuery().getData());

            return callbackQueryFacade.processCallbackQuery(callbackQuery);
        }

        BotApiMethod<?> replyMessage = null;

        if (update.hasMessage()) {
            Message message = update.getMessage();

            if (message.hasContact()) {
                return phoneNumberHandler.handle(update.getMessage());
            }

            if (message.hasPhoto()) {
                log.info("New photo from user:{}, chatId:{}",
                        message.getFrom().getUserName(), message.getChatId());

                adminService.getAndSendPhotoId(update.getMessage());
            }

            if (message.hasText()) {
                log.info("New message from user:{}, chatId:{}, with text:{}",
                        message.getFrom().getUserName(), message.getChatId(), message.getText());

                replyMessage = handleInputMessage(message);
            }
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
            case "\u260E Контакты":
                botState = BotState.SHOW_CONTACTS;
                break;
            case "\u2753 Частые вопросы":
                botState = BotState.QUESTIONS;
                break;
            case "\uD83D\uDCE6 Мои заказы":
                botState = BotState.SHOW_MY_ORDERS;
                break;
            case "\uD83D\uDDC2 Каталог товара":
                botState = BotState.SHOW_CATALOG;
                break;
            case "\uD83D\uDED2 Моя корзина":
                botState = BotState.SHOW_SHOPPING_CART;
                break;
            case "Отмена":
                botState = BotState.CANCEL_BUTTON;
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
