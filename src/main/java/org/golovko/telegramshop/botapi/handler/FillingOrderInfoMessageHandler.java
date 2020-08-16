package org.golovko.telegramshop.botapi.handler;

import org.golovko.telegramshop.botapi.BotState;
import org.golovko.telegramshop.cache.UserDataCache;
import org.golovko.telegramshop.domain.Customer;
import org.golovko.telegramshop.service.CustomerService;
import org.golovko.telegramshop.service.KeyboardService;
import org.golovko.telegramshop.service.ReplyMessageService;
import org.golovko.telegramshop.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class FillingOrderInfoMessageHandler implements InputMessageHandler {

    @Autowired
    private ReplyMessageService messageService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private UserDataCache userDataCache;

    @Autowired
    private KeyboardService keyboardService;

    @Autowired
    private ValidationService validationService;

    @Override
    public BotApiMethod<?> handle(Message message) {
        return processUsersInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.FILLING_ORDER_INFO;
    }

    // todo add validation to cyrillic letters and for fullName input
    private SendMessage processUsersInput(Message inputMsg) {
        long chatId = inputMsg.getChatId();
        String usersAnswer = inputMsg.getText();

        Customer customer = customerService.getByChatId(chatId);
        BotState botState = userDataCache.getCurrentBotState(chatId);

        SendMessage replyToUser = messageService.getReplyMessage(chatId, "reply.default");

        if (botState.equals(BotState.ENTER_FULL_NAME)) {
            if (validationService.isValidText(usersAnswer)) {
                customer.setName(validationService.getFirstName(usersAnswer));
                customer.setSurname(validationService.getLastName(usersAnswer));

                replyToUser = messageService.getReplyMessage(chatId, "reply.enterCity");
                userDataCache.setNewBotState(chatId, BotState.ENTER_CITY);
            } else {
                replyToUser = messageService.getReplyMessage(chatId, "reply.repeatFullName");
                userDataCache.setNewBotState(chatId, BotState.ENTER_FULL_NAME);
            }
        }

        if (botState.equals(BotState.ENTER_CITY)) {
            if (validationService.isValidText(usersAnswer)) {
                customer.setCity(usersAnswer);

                replyToUser = messageService.getReplyMessage(chatId, "reply.enterAddress");
                userDataCache.setNewBotState(chatId, BotState.ENTER_ADDRESS);
            } else {
                replyToUser = messageService.getReplyMessage(chatId, "reply.repeatCity");
                userDataCache.setNewBotState(chatId, BotState.ENTER_CITY);
            }
        }

        if (botState.equals(BotState.ENTER_ADDRESS)) {
            customer.setAddress(usersAnswer);

            replyToUser = messageService.getReplyMessage(chatId, "reply.enterPhone");
            replyToUser.setReplyMarkup(keyboardService.getRequestContactKeyboard());

            userDataCache.setNewBotState(chatId, BotState.ENTER_PHONE);
        }

        if (botState.equals(BotState.ENTER_PHONE)) {
            if (validationService.isValidPhoneNumber(usersAnswer)) {
                customer.setPhone(usersAnswer);

                replyToUser = messageService.getReplyMessage(chatId, "reply.paymentType");
                replyToUser.setParseMode("HTML");
                replyToUser.setReplyMarkup(keyboardService.getPaymentTypeKeyboard());

                userDataCache.setNewBotState(chatId, BotState.CHOOSE_PAYMENT_TYPE);
            } else {
                replyToUser = messageService.getReplyMessage(chatId, "reply.askRepeatPhone");
                replyToUser.setReplyMarkup(keyboardService.getRequestContactKeyboard());

                userDataCache.setNewBotState(chatId, BotState.ENTER_PHONE);
            }
        }

        customerService.saveCustomer(customer);

        return replyToUser;
    }
}
