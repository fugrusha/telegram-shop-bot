package org.golovko.telegramshop.botapi.handler.callback;

import org.golovko.telegramshop.MyTelegramBot;
import org.golovko.telegramshop.botapi.BotState;
import org.golovko.telegramshop.botapi.handler.CallbackHandler;
import org.golovko.telegramshop.botapi.handler.CallbackType;
import org.golovko.telegramshop.cache.UserDataCache;
import org.golovko.telegramshop.domain.Product;
import org.golovko.telegramshop.service.KeyboardService;
import org.golovko.telegramshop.service.ProductService;
import org.golovko.telegramshop.service.ReplyMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;

@Component
public class ShowCategoryCallbackHandler implements CallbackHandler {

    @Autowired
    private UserDataCache userDataCache;

    @Autowired
    private ProductService productService;

    @Autowired
    private ReplyMessageService messageService;

    @Autowired
    private KeyboardService keyboardService;

    @Autowired
    private MyTelegramBot myTelegramBot;

    @Override
    public BotApiMethod<?> handleCallbackQuery(CallbackQuery callbackQuery) {
        return processUsersCallback(callbackQuery);
    }

    @Override
    public CallbackType getHandlerQueryType() {
        return CallbackType.CATEGORY;
    }

    private BotApiMethod<?> processUsersCallback(CallbackQuery buttonQuery) {
        final long chatId = buttonQuery.getMessage().getChatId();

        String categoryName = buttonQuery.getData().split("=")[1];
        int orderNumber = Integer.parseInt(buttonQuery.getData().split("=")[2]);

        List<Product> products = productService.getProductByCategoryName(categoryName);
        BotApiMethod<?> callbackAnswer = null;

        if (products.isEmpty()) {
            return new SendMessage(chatId, messageService.getReplyText("reply.emptyCategory", categoryName));
        }

        SendPhoto sendPhoto = createSendPhoto(chatId, products.get(orderNumber - 1), products.size());
        myTelegramBot.send(sendPhoto);

        userDataCache.setNewBotState(chatId, BotState.SHOW_MAIN_MENU);

        return callbackAnswer;
    }

    private SendPhoto createSendPhoto(long chatId, Product product, int totalSize) {
        return new SendPhoto()
                .setChatId(chatId)
                .setCaption(getDescription(product))
                .setPhoto(product.getPhotoUrl())
                .setParseMode("HTML")
                .setReplyMarkup(keyboardService.getProductKeyboard(product, totalSize));
    }

    private String getDescription(Product product) {
        return messageService.getReplyText(
                "product.description",
                product.getName(),
                product.getDescription(),
                product.getPrice());
    }
}
