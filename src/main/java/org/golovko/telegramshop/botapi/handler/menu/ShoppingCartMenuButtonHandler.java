package org.golovko.telegramshop.botapi.handler.menu;

import org.golovko.telegramshop.botapi.BotState;
import org.golovko.telegramshop.botapi.handler.InputMessageHandler;
import org.golovko.telegramshop.cache.UserDataCache;
import org.golovko.telegramshop.domain.model.CartItem;
import org.golovko.telegramshop.service.KeyboardService;
import org.golovko.telegramshop.service.ReplyMessageService;
import org.golovko.telegramshop.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Component
public class ShoppingCartMenuButtonHandler implements InputMessageHandler {

    @Autowired
    private ReplyMessageService messageService;

    @Autowired
    private UserDataCache userDataCache;

    @Autowired
    private ShoppingCartService cartService;

    @Autowired
    private KeyboardService keyboardService;

    @Override
    public SendMessage handle(Message message) {
        return processUsersInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_SHOPPING_CART;
    }

    private SendMessage processUsersInput(Message inputMsg) {
        long chatId = inputMsg.getChatId();

        List<CartItem> allCartItems = cartService.findAllCartItemsByChatId(chatId);

        if(allCartItems.isEmpty()) {
            return messageService.getReplyMessage(chatId, "reply.emptyShoppingCart");
        }

        String cartText = createCartText(allCartItems);

        SendMessage replyToUser = messageService.getReplyMessage(chatId,
                "reply.shoppingCartText", cartText);
        replyToUser.setReplyMarkup(keyboardService.getShoppingCartKeyboard());

        userDataCache.setNewBotState(chatId, BotState.SHOW_MAIN_MENU);

        return replyToUser;
    }

    private String createCartText(List<CartItem> allCartItems) {
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
}
