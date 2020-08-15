package org.golovko.telegramshop.botapi.handler;

import org.golovko.telegramshop.botapi.BotState;
import org.golovko.telegramshop.cache.UserDataCache;
import org.golovko.telegramshop.domain.Customer;
import org.golovko.telegramshop.service.CustomerService;
import org.golovko.telegramshop.service.KeyboardService;
import org.golovko.telegramshop.service.ReplyMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class PhoneNumberHandler {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ReplyMessageService messageService;

    @Autowired
    private KeyboardService keyboardService;

    @Autowired
    private UserDataCache userDataCache;

    public BotApiMethod<?> handle(Message inputMsg) {
        Contact contact = inputMsg.getContact();
        long chatId = inputMsg.getChatId();

        Customer customer = customerService.getByChatId(chatId);

        customer.setPhone(contact.getPhoneNumber());

        SendMessage replyToUser = messageService.getReplyMessage(chatId, "reply.paymentType");
        replyToUser.setParseMode("HTML");
        replyToUser.setReplyMarkup(keyboardService.getPaymentTypeKeyboard());

        userDataCache.setNewBotState(chatId, BotState.CHOOSE_PAYMENT_TYPE);
        customerService.saveCustomer(customer);

        return replyToUser;
    }
}
