package org.golovko.telegramshop.botapi.handler.callback;

import org.golovko.telegramshop.botapi.BotState;
import org.golovko.telegramshop.botapi.handler.CallbackHandler;
import org.golovko.telegramshop.botapi.handler.CallbackType;
import org.golovko.telegramshop.cache.UserDataCache;
import org.golovko.telegramshop.domain.Product;
import org.golovko.telegramshop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class ShowCategoryCallbackHandler implements CallbackHandler {

    @Autowired
    private UserDataCache userDataCache;

    @Autowired
    private ProductService productService;

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
        int orderNumber = 1;

        String categoryName = buttonQuery.getData().split("=")[1];
        Product product = productService.getProductByCategoryNameAndOrderNumber(categoryName, orderNumber);

        BotApiMethod<?> callbackAnswer = null;

        userDataCache.setNewBotState(chatId, BotState.SHOW_MAIN_MENU);

        return callbackAnswer;
    }
}
