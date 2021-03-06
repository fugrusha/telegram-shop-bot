package org.golovko.telegramshop.botapi.handler.callback;

import org.golovko.telegramshop.botapi.BotState;
import org.golovko.telegramshop.botapi.handler.CallbackHandler;
import org.golovko.telegramshop.botapi.handler.CallbackType;
import org.golovko.telegramshop.cache.UserDataCache;
import org.golovko.telegramshop.domain.Customer;
import org.golovko.telegramshop.domain.PaymentType;
import org.golovko.telegramshop.domain.model.CartItem;
import org.golovko.telegramshop.service.CustomerService;
import org.golovko.telegramshop.service.KeyboardService;
import org.golovko.telegramshop.service.ReplyMessageService;
import org.golovko.telegramshop.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;

@Component
public class FillingOrderInfoCallbackHandler implements CallbackHandler {

    @Autowired
    private ReplyMessageService messageService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private UserDataCache userDataCache;

    @Autowired
    private KeyboardService keyboardService;

    @Autowired
    private ShoppingCartService cartService;

    @Override
    public BotApiMethod<?> handleCallbackQuery(CallbackQuery callbackQuery) {
        return processUsersCallback(callbackQuery);
    }

    @Override
    public CallbackType getHandlerQueryType() {
        return CallbackType.FILLING_ORDER_INFO;
    }

    private BotApiMethod<?> processUsersCallback(CallbackQuery buttonQuery) {
        final long chatId = buttonQuery.getMessage().getChatId();

        Customer customer = customerService.getByChatId(chatId);

        CallbackType callbackType = CallbackType.valueOf(buttonQuery.getData().split("=")[0]);

        if (callbackType.equals(CallbackType.NP_PAYMENT)) {
            customer.setPaymentType(PaymentType.NP_PAYMENT);
        }

        if (callbackType.equals(CallbackType.PREPAYMENT)) {
            customer.setPaymentType(PaymentType.PREPAYMENT);
        }

        customerService.saveCustomer(customer);
        userDataCache.setNewBotState(chatId, BotState.CONFIRM_ORDER);

        List<CartItem> allCartItems = cartService.findAllCartItemsByChatId(chatId);

        SendMessage replyToUser = messageService.getReplyMessage(chatId,
                "reply.confirmOrder", getCustomerData(customer), getOrderData(allCartItems));
        replyToUser.setReplyMarkup(keyboardService.getConfirmOrderKeyboard());

        return replyToUser;
    }

    private String getOrderData(List<CartItem> allCartItems) {
        double totalSum = 0.0;
        StringBuilder sb = new StringBuilder();

        for (CartItem item : allCartItems) {
            String productName = item.getProduct().getName();
            Double productPrice = item.getProduct().getPrice();
            int quantity = item.getQuantity();
            double totalPrice = item.getTotalPrice();
            totalSum += totalPrice;

            sb.append(messageService.getReplyText("reply.shoppingCartItemText",
                    productName, quantity, productPrice, totalPrice));
        }

        sb.append(messageService.getReplyText("reply.shoppingCartTotalSum", totalSum));
        return sb.toString();
    }

    private String getCustomerData(Customer customer) {
        String fullName = customer.getName() + " " + customer.getSurname();
        String phone = customer.getPhone();
        String city = customer.getCity();
        String address = customer.getAddress();
        String paymentType = getPaymentType(customer.getPaymentType());

        return messageService.getReplyText("reply.receiverData",
                fullName, phone, city, address, paymentType);
    }

    private String getPaymentType(PaymentType paymentType) {
        if (paymentType.equals(PaymentType.NP_PAYMENT)) {
            return messageService.getReplyText("button.novaPoshtaPayment");
        } else if (paymentType.equals(PaymentType.PREPAYMENT)) {
            return messageService.getReplyText("button.prepayment");
        }

        return "undefined";
    }
}
