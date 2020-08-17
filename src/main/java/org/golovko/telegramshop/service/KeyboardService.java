package org.golovko.telegramshop.service;

import org.golovko.telegramshop.domain.OrderCart;
import org.golovko.telegramshop.domain.Product;
import org.golovko.telegramshop.domain.model.CartItem;
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
import java.util.UUID;

import static org.golovko.telegramshop.botapi.handler.CallbackType.*;

@Service
public class KeyboardService {

    @Autowired
    private ReplyMessageService messageService;

    @Autowired
    private OrderService orderService;

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
                .setCallbackData(ADD_TO_CART + "=" + product.getId().toString()));

        List<InlineKeyboardButton> secondRow = new ArrayList<>();

        if (product.getOrderNumber() == 1) {
            secondRow.add(new InlineKeyboardButton("\u25c0")
                    .setCallbackData(CATEGORY + "="
                            + product.getCategory().getName() + "=" + totalProducts));
        } else {
            int prevNumber = product.getOrderNumber() - 1;
            secondRow.add(new InlineKeyboardButton("\u25c0")
                    .setCallbackData(CATEGORY + "="
                            + product.getCategory().getName() + "=" + prevNumber));
        }

        secondRow.add(new InlineKeyboardButton((product.getOrderNumber()) + "/" + totalProducts)
                .setCallbackData(IGNORE.name()));

        int nextNumber = product.getOrderNumber() + 1;

        if (nextNumber > totalProducts) {
            secondRow.add(new InlineKeyboardButton("\u25b6")
                    .setCallbackData(CATEGORY + "="
                            + product.getCategory().getName() + "=1"));
        } else {
            secondRow.add(new InlineKeyboardButton("\u25b6")
                    .setCallbackData(CATEGORY + "="
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
                .setCallbackData(EDIT_CART + "=" + EDIT_CART));

        firstRow.add(new InlineKeyboardButton(messageService.getReplyText("button.cleanCart"))
                .setCallbackData(CLEAN_CART.name()));

        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        secondRow.add(new InlineKeyboardButton(messageService.getReplyText("button.processOrder"))
                .setCallbackData(PROCESS_ORDER.name()));

        keyboard.add(firstRow);
        keyboard.add(secondRow);

        return new InlineKeyboardMarkup().setKeyboard(keyboard);
    }

    public ReplyKeyboard getPaymentTypeKeyboard() {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        firstRow.add(new InlineKeyboardButton(messageService.getReplyText("button.novaPoshtaPayment"))
                .setCallbackData(NP_PAYMENT.name()));

        firstRow.add(new InlineKeyboardButton(messageService.getReplyText("button.prepayment"))
                .setCallbackData(PREPAYMENT.name()));

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

        row1.add(new KeyboardButton(messageService.getReplyText("button.cancel")));

        keyboard.add(row1);

        replyKeyboardMarkup.setKeyboard(keyboard);

        return replyKeyboardMarkup;
    }

    public ReplyKeyboard getConfirmOrderKeyboard() {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        firstRow.add(new InlineKeyboardButton(messageService.getReplyText("button.confirm"))
                .setCallbackData(CONFIRM_ORDER.name()));

        firstRow.add(new InlineKeyboardButton(messageService.getReplyText("button.cancel"))
                .setCallbackData(CANCEL_ORDER.name()));

        keyboard.add(firstRow);

        return new InlineKeyboardMarkup().setKeyboard(keyboard);
    }

    public ReplyKeyboard getOrderListKeyboard(List<OrderCart> orders) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (OrderCart order : orders) {
            UUID orderId = order.getId();
            String orderNumber = order.getOrderNumber();

            keyboard.add(new ArrayList<>() {{
                add(new InlineKeyboardButton(orderNumber)
                        .setCallbackData(MY_ORDERS + "=" + orderId));
            }});
        }

        return new InlineKeyboardMarkup().setKeyboard(keyboard);
    }

    public ReplyKeyboard getRepeatOrderKeybord(UUID orderId) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        keyboard.add(new ArrayList<>() {{
            add(new InlineKeyboardButton(messageService.getReplyText("button.repeatOrder"))
                    .setCallbackData(REPEAT_ORDER + "=" + orderId));
        }});

        return new InlineKeyboardMarkup().setKeyboard(keyboard);
    }

    public InlineKeyboardMarkup getEditCartKeyboard(List<CartItem> cartItems, int currentPage) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        firstRow.add(new InlineKeyboardButton("\u2716")
                .setCallbackData(EDIT_CART + "=" + DELETE_PRODUCT));
        firstRow.add(new InlineKeyboardButton("\u2796")
                .setCallbackData(EDIT_CART + "=" + MINUS_PRODUCT));
        firstRow.add(new InlineKeyboardButton("\u2795")
                .setCallbackData(EDIT_CART + "=" + PLUS_PRODUCT));

        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        secondRow.add(new InlineKeyboardButton("\u25c0")
                .setCallbackData(EDIT_CART + "=" + PREVIOUS_PRODUCT));
        secondRow.add(new InlineKeyboardButton((currentPage + 1) + "/" + cartItems.size())
                .setCallbackData(IGNORE.name()));
        secondRow.add(new InlineKeyboardButton("\u25b6")
                .setCallbackData(EDIT_CART + "=" + NEXT_PRODUCT));

        List<InlineKeyboardButton> thirdRow = new ArrayList<>();
        thirdRow.add(new InlineKeyboardButton()
                .setCallbackData(OPEN_SHOPPING_CART.name())
                .setText(messageService.getReplyText("button.checkout",
                        orderService.calculateTotalPrice(cartItems))));

        keyboard.add(firstRow);
        keyboard.add(secondRow);
        keyboard.add(thirdRow);

        return new InlineKeyboardMarkup().setKeyboard(keyboard);
    }
}
