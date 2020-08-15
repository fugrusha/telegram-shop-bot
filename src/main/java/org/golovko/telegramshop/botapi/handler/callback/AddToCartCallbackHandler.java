package org.golovko.telegramshop.botapi.handler.callback;

import org.golovko.telegramshop.botapi.BotState;
import org.golovko.telegramshop.botapi.handler.CallbackHandler;
import org.golovko.telegramshop.botapi.handler.CallbackType;
import org.golovko.telegramshop.cache.UserDataCache;
import org.golovko.telegramshop.domain.Product;
import org.golovko.telegramshop.domain.model.CartItem;
import org.golovko.telegramshop.service.ProductService;
import org.golovko.telegramshop.service.ReplyMessageService;
import org.golovko.telegramshop.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.UUID;

@Component
public class AddToCartCallbackHandler implements CallbackHandler {

    @Autowired
    private UserDataCache userDataCache;

    @Autowired
    private ProductService productService;

    @Autowired
    private ShoppingCartService cartService;

    @Autowired
    private ReplyMessageService messageService;

    @Override
    public BotApiMethod<?> handleCallbackQuery(CallbackQuery callbackQuery) {
        return processUsersCallback(callbackQuery);
    }

    @Override
    public CallbackType getHandlerQueryType() {
        return CallbackType.ADD_TO_CART;
    }

    private BotApiMethod<?> processUsersCallback(CallbackQuery buttonQuery) {
        final long chatId = buttonQuery.getMessage().getChatId();

        UUID productId = UUID.fromString(buttonQuery.getData().split("=")[1]);
        CartItem cartItem = cartService.findCartItemByChatIdAndProductId(chatId, productId);
        Product product = cartItem != null ? cartItem.getProduct() : productService.getProductById(productId);

        if (product == null) {
            return messageService.getReplyMessage(chatId, "product.productNotFound");
        }

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            cartService.updateCartItem(chatId, cartItem);
        } else {
            cartService.saveCartItem(chatId, new CartItem(product, 1));
        }

        BotApiMethod<?> callbackAnswer = getAnswerCallbackQuery(
                messageService.getReplyText("reply.productWasAdded"),true, buttonQuery);

        userDataCache.setNewBotState(chatId, BotState.SHOW_MAIN_MENU);

        return callbackAnswer;
    }

    private AnswerCallbackQuery getAnswerCallbackQuery(String text, boolean alert, CallbackQuery callbackquery) {
        AnswerCallbackQuery answer = new AnswerCallbackQuery();
        answer.setCallbackQueryId(callbackquery.getId());
        answer.setShowAlert(alert);
        answer.setText(text);
        return answer;
    }
}
