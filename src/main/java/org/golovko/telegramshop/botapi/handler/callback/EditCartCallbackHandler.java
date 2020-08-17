package org.golovko.telegramshop.botapi.handler.callback;

import org.golovko.telegramshop.botapi.BotState;
import org.golovko.telegramshop.botapi.handler.CallbackHandler;
import org.golovko.telegramshop.botapi.handler.CallbackType;
import org.golovko.telegramshop.cache.UserDataCache;
import org.golovko.telegramshop.domain.model.CartItem;
import org.golovko.telegramshop.service.KeyboardService;
import org.golovko.telegramshop.service.ReplyMessageService;
import org.golovko.telegramshop.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;

@Component
public class EditCartCallbackHandler implements CallbackHandler {

    @Autowired
    private ReplyMessageService messageService;

    @Autowired
    private ShoppingCartService cartService;

    @Autowired
    private KeyboardService keyboardService;

    @Autowired
    private UserDataCache userDataCache;

    @Override
    public BotApiMethod<?> handleCallbackQuery(CallbackQuery callbackQuery) {
        return processUsersCallback(callbackQuery);
    }

    @Override
    public CallbackType getHandlerQueryType() {
        return CallbackType.EDIT_CART;
    }

    private BotApiMethod<?> processUsersCallback(CallbackQuery buttonQuery) {
        final Long chatId = buttonQuery.getMessage().getChatId();
        final Integer messageId = buttonQuery.getMessage().getMessageId();

        CallbackType callbackType = CallbackType.valueOf(buttonQuery.getData().split("=")[1]);
        userDataCache.setNewBotState(chatId, BotState.SHOW_MAIN_MENU);

        switch (callbackType) {
            case DELETE_PRODUCT:
                return deleteProduct(chatId, messageId);
            case MINUS_PRODUCT:
                return minusProduct(chatId, messageId);
            case PLUS_PRODUCT:
                return plusProduct(chatId, messageId);
            case PREVIOUS_PRODUCT:
                return previousProduct(chatId, messageId);
            case NEXT_PRODUCT:
                return nextProduct(chatId, messageId);
            default:
                return startEditing(chatId);
        }
    }

    private EditMessageText deleteProduct(Long chatId, Integer messageId) {
        List<CartItem> cartItems = cartService.findAllCartItemsByChatId(chatId);
        int currentCartPage = cartService.findPageNumberByChatId(chatId);

        if (!cartItems.isEmpty()) {
            CartItem cartItem = cartItems.get(currentCartPage);
            if (cartItem != null) {
                cartItems.remove(cartItem);
                cartService.deleteCartItem(chatId, cartItem.getId());
            }
        }

        if (cartItems.isEmpty()) {
            return getEmptyCardMessage(chatId, messageId);
        }

        if (cartItems.size() == currentCartPage) {
            currentCartPage -= 1;
            cartService.setPageNumber(chatId, currentCartPage);
        }

        String textReply = getItemText(cartItems.get(currentCartPage));

        return messageService.getEditMessageText(chatId, messageId, textReply,
                keyboardService.getEditCartKeyboard(cartItems, currentCartPage));
    }

    private EditMessageText minusProduct(Long chatId, Integer messageId) {
        List<CartItem> cartItems = cartService.findAllCartItemsByChatId(chatId);
        int currentCartPage = cartService.findPageNumberByChatId(chatId);

        if (cartItems.isEmpty()) {
            return getEmptyCardMessage(chatId, messageId);
        }

        CartItem cartItem = cartItems.get(currentCartPage);
        EditMessageText editMessageText = null;

        if (cartItem != null && cartItem.getQuantity() > 1) {
            cartItem.setQuantity(cartItem.getQuantity() - 1);
            cartService.updateCartItem(chatId, cartItem);

            String textReply = getItemText(cartItem);

            editMessageText = messageService.getEditMessageText(chatId, messageId, textReply,
                    keyboardService.getEditCartKeyboard(cartItems, currentCartPage));
        }

        return editMessageText;
    }

    private EditMessageText plusProduct(Long chatId, Integer messageId) {
        List<CartItem> cartItems = cartService.findAllCartItemsByChatId(chatId);
        int currentCartPage = cartService.findPageNumberByChatId(chatId);

        if (cartItems.isEmpty()) {
            return getEmptyCardMessage(chatId, messageId);
        }

        CartItem cartItem = cartItems.get(currentCartPage);
        EditMessageText editMessageText = null;

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            cartService.updateCartItem(chatId, cartItem);

            String textReply = getItemText(cartItem);

            editMessageText = messageService.getEditMessageText(chatId, messageId, textReply,
                    keyboardService.getEditCartKeyboard(cartItems, currentCartPage));
        }

        return editMessageText;
    }

    private EditMessageText previousProduct(Long chatId, Integer messageId) {
        List<CartItem> cartItems = cartService.findAllCartItemsByChatId(chatId);
        int currentCartPage = cartService.findPageNumberByChatId(chatId);

        EditMessageText editMessageText = null;

        if (cartItems.isEmpty()) {
            return getEmptyCardMessage(chatId, messageId);
        }

        if (cartItems.size() == 1) {
            return editMessageText;
        }

        if (currentCartPage <= 0) {
            currentCartPage = cartItems.size() - 1;
        } else {
            currentCartPage -= 1;
        }

        cartService.setPageNumber(chatId, currentCartPage);

        CartItem cartItem = cartItems.get(currentCartPage);
        String textReply = getItemText(cartItem);

        editMessageText = messageService.getEditMessageText(chatId, messageId, textReply,
                keyboardService.getEditCartKeyboard(cartItems, currentCartPage));

        return editMessageText;
    }

    private EditMessageText nextProduct(Long chatId, Integer messageId) {
        List<CartItem> cartItems = cartService.findAllCartItemsByChatId(chatId);
        int currentCartPage = cartService.findPageNumberByChatId(chatId);
        EditMessageText editMessageText = null;

        if (cartItems.isEmpty()) {
            return getEmptyCardMessage(chatId, messageId);
        }

        if (cartItems.size() == 1) {
            return editMessageText;
        }
        if (currentCartPage >= cartItems.size() - 1) {
            currentCartPage = 0;
        } else {
            currentCartPage += 1;
        }

        cartService.setPageNumber(chatId, currentCartPage);

        CartItem cartItem = cartItems.get(currentCartPage);
        String textReply = getItemText(cartItem);

        editMessageText = messageService.getEditMessageText(chatId, messageId, textReply,
                keyboardService.getEditCartKeyboard(cartItems, currentCartPage));

        return editMessageText;
    }

    private SendMessage startEditing(long chatId) {
        List<CartItem> cartItems = cartService.findAllCartItemsByChatId(chatId);
        cartService.setPageNumber(chatId, 0);

        SendMessage replyMessage = messageService.getReplyMessage(chatId,
                "reply.editCartText", getItemText(cartItems.get(0)));

        replyMessage.setReplyMarkup(keyboardService.getEditCartKeyboard(cartItems, 0));

        return replyMessage;
    }

    private String getItemText(CartItem cartItem) {
        String productName = cartItem.getProduct().getName();
        Double productPrice = cartItem.getProduct().getPrice();
        int quantity = cartItem.getQuantity();
        double totalPrice = cartItem.getTotalPrice();

        String itemDescription = messageService.getReplyText("reply.shoppingCartItemText",
                productName, quantity, productPrice, totalPrice);

        return itemDescription;
    }

    private EditMessageText getEmptyCardMessage(Long chatId, Integer messageId) {
        String textReply = messageService.getReplyText("reply.emptyShoppingCart");
        return messageService.getEditMessageText(chatId, messageId, textReply, null);
    }
}