package org.golovko.telegramshop.botapi.handler.menu;

import org.golovko.telegramshop.botapi.BotState;
import org.golovko.telegramshop.botapi.handler.CallbackType;
import org.golovko.telegramshop.botapi.handler.InputMessageHandler;
import org.golovko.telegramshop.cache.UserDataCache;
import org.golovko.telegramshop.domain.Category;
import org.golovko.telegramshop.service.CategoryService;
import org.golovko.telegramshop.service.ReplyMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class CatalogMenuButtonHandler implements InputMessageHandler {

    @Autowired
    private ReplyMessageService messageService;

    @Autowired
    private UserDataCache userDataCache;

    @Autowired
    private CategoryService categoryService;

    @Override
    public SendMessage handle(Message message) {
        return processUsersInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_CATALOG;
    }

    private SendMessage processUsersInput(Message inputMsg) {
        long chatId = inputMsg.getChatId();

        SendMessage replyToUser = messageService.getReplyMessage(chatId, "reply.showCatalog");
        replyToUser.setParseMode("HTML");
        replyToUser.setReplyMarkup(createCategoriesKeyboard());

        userDataCache.setNewBotState(chatId, BotState.SHOW_MAIN_MENU);

        return replyToUser;
    }

    private InlineKeyboardMarkup createCategoriesKeyboard() {
        List<Category> categories = categoryService.getAllCategories();

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        for (Category category : categories) {
            String categoryName = category.getName();

            buttons.add(new ArrayList<>() {{
                add(new InlineKeyboardButton(categoryName)
                        .setCallbackData(CallbackType.CATEGORY + "=" + categoryName + "=1"));
            }});
        }

        markup.setKeyboard(buttons);
        return markup;
    }
}
