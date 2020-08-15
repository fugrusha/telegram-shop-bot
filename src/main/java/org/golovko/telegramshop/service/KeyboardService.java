package org.golovko.telegramshop.service;

import org.golovko.telegramshop.botapi.handler.CallbackType;
import org.golovko.telegramshop.domain.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Service
public class KeyboardService {

    @Autowired
    private ReplyMessageService messageService;

    public ReplyKeyboardMarkup getMainMenuKeyboard() {

        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton(messageService.getReplyText("button.catalog")));
        row1.add(new KeyboardButton(messageService.getReplyText("button.shoppingCart")));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton(messageService.getReplyText("button.myOrders")));
        row2.add(new KeyboardButton(messageService.getReplyText("button.contacts")));

        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton(messageService.getReplyText("button.faq")));

        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);

        replyKeyboardMarkup.setKeyboard(keyboard);

        return replyKeyboardMarkup;
    }

    public InlineKeyboardMarkup getProductKeyboard(Product product, int totalProducts) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        firstRow.add(new InlineKeyboardButton(messageService.getReplyText("button.addToShoppingCart"))
                .setCallbackData(CallbackType.ADD_TO_CART + "=" + product.getId().toString()));

        List<InlineKeyboardButton> secondRow = new ArrayList<>();

        if (product.getOrderNumber() == 1) {
            secondRow.add(new InlineKeyboardButton("\u25c0")
                    .setCallbackData(CallbackType.CATEGORY + "="
                            + product.getCategory().getName() + "=" + totalProducts));
        } else {
            int prevNumber = product.getOrderNumber() - 1;
            secondRow.add(new InlineKeyboardButton("\u25c0")
                    .setCallbackData(CallbackType.CATEGORY + "="
                            + product.getCategory().getName() + "=" + prevNumber));
        }

        secondRow.add(new InlineKeyboardButton((product.getOrderNumber()) + "/" + totalProducts)
                .setCallbackData(CallbackType.IGNORE.name()));

        int nextNumber = product.getOrderNumber() + 1;

        if (nextNumber > totalProducts) {
            secondRow.add(new InlineKeyboardButton("\u25b6")
                    .setCallbackData(CallbackType.CATEGORY + "="
                            + product.getCategory().getName() + "=1"));
        } else {
            secondRow.add(new InlineKeyboardButton("\u25b6")
                    .setCallbackData(CallbackType.CATEGORY + "="
                            + product.getCategory().getName() + "=" + nextNumber));
        }

        keyboard.add(firstRow);
        keyboard.add(secondRow);

        return new InlineKeyboardMarkup().setKeyboard(keyboard);
    }

    public InlineKeyboardMarkup getShoppingCartKeyboard() {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        firstRow.add(new InlineKeyboardButton(messageService.getReplyText("button.editCart"))
                .setCallbackData(CallbackType.EDIT_CART.name()));

        firstRow.add(new InlineKeyboardButton(messageService.getReplyText("button.cleanCart"))
                .setCallbackData(CallbackType.CLEAN_CART.name()));

        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        secondRow.add(new InlineKeyboardButton(messageService.getReplyText("button.processOrder"))
                .setCallbackData(CallbackType.PROCESS_ORDER.name()));

        keyboard.add(firstRow);
        keyboard.add(secondRow);

        return new InlineKeyboardMarkup().setKeyboard(keyboard);
    }

    public ReplyKeyboard getPaymentTypeKeyboard() {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        firstRow.add(new InlineKeyboardButton(messageService.getReplyText("button.novaPoshtaPayment"))
                .setCallbackData(CallbackType.NP_PAYMENT.name()));

        firstRow.add(new InlineKeyboardButton(messageService.getReplyText("button.prepayment"))
                .setCallbackData(CallbackType.PREPAYMENT.name()));

        keyboard.add(firstRow);

        return new InlineKeyboardMarkup().setKeyboard(keyboard);
    }

    public ReplyKeyboardMarkup getRequestContactKeyboard() {

        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();

        row1.add(new KeyboardButton(messageService.getReplyText("button.sendMyPhone"))
                .setRequestContact(true));

        keyboard.add(row1);

        replyKeyboardMarkup.setKeyboard(keyboard);

        return replyKeyboardMarkup;
    }

    public ReplyKeyboard getConfirmOrderKeyboard() {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        firstRow.add(new InlineKeyboardButton(messageService.getReplyText("button.confirm"))
                .setCallbackData(CallbackType.CONFIRM_ORDER.name()));

        firstRow.add(new InlineKeyboardButton(messageService.getReplyText("button.cancel"))
                .setCallbackData(CallbackType.CANCEL_ORDER.name()));

        keyboard.add(firstRow);

        return new InlineKeyboardMarkup().setKeyboard(keyboard);
    }
}
