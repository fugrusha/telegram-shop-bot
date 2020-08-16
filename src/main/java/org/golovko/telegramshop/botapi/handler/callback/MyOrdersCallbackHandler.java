package org.golovko.telegramshop.botapi.handler.callback;

import org.golovko.telegramshop.botapi.handler.CallbackHandler;
import org.golovko.telegramshop.botapi.handler.CallbackType;
import org.golovko.telegramshop.domain.OrderCart;
import org.golovko.telegramshop.domain.OrderItem;
import org.golovko.telegramshop.service.KeyboardService;
import org.golovko.telegramshop.service.OrderService;
import org.golovko.telegramshop.service.ReplyMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;
import java.util.UUID;

@Component
public class MyOrdersCallbackHandler implements CallbackHandler {

    @Autowired
    private ReplyMessageService messageService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private KeyboardService keyboardService;

    @Override
    public BotApiMethod<?> handleCallbackQuery(CallbackQuery callbackQuery) {
        return processUsersCallback(callbackQuery);
    }

    @Override
    public CallbackType getHandlerQueryType() {
        return CallbackType.MY_ORDERS;
    }

    private BotApiMethod<?> processUsersCallback(CallbackQuery buttonQuery) {
        final long chatId = buttonQuery.getMessage().getChatId();

        UUID orderId = UUID.fromString(buttonQuery.getData().split("=")[1]);

        OrderCart order = orderService.getOrderById(orderId);

        SendMessage replyToUser = messageService.getReplyMessage(chatId,
                "reply.orderText", order.getOrderNumber(), createOrderText(order));
        replyToUser.setParseMode("HTML");
        replyToUser.setReplyMarkup(keyboardService.getRepeatOrderKeybord(order.getId()));

        return replyToUser;
    }

    private String createOrderText(OrderCart order) {
        double totalSum = 0.0;
        StringBuilder sb = new StringBuilder();

        List<OrderItem> items = order.getItems();

        for (OrderItem item : items) {
            String productName = item.getProduct().getName();
            Double productPrice = item.getProduct().getPrice();
            int quantity = item.getQuantity();
            double totalPrice = productPrice * quantity;
            totalSum += totalPrice;

            sb.append(messageService.getReplyText("reply.orderItemText",
                    productName, quantity, productPrice, totalPrice));
        }

        sb.append(messageService.getReplyText("reply.orderTotalSum", totalSum));
        sb.append(messageService.getReplyText("reply.orderStatus", order.getStatus()));
        return sb.toString();
    }
}
