package org.golovko.telegramshop.service;

import org.golovko.telegramshop.MyTelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Service
public class ReplyMessageService {

    @Autowired
    private LocaleMessageService localeMessageService;

    @Autowired
    @Lazy
    private MyTelegramBot myTelegramBot;

    public SendMessage getReplyMessage(long chatId, String replyMessage) {
        return new SendMessage()
                .setParseMode("HTML")
                .setChatId(chatId)
                .setText(localeMessageService.getMessage(replyMessage))
                .enableMarkdown(true);
    }

    public SendMessage getReplyMessage(long chatId, String replyMessage, Object... args) {
        return new SendMessage()
                .setParseMode("HTML")
                .setChatId(chatId)
                .setText(localeMessageService.getMessage(replyMessage, args));
    }

    public void sendPhoto(long chatId, String imageCaption, String photoId) {
        SendPhoto sendPhoto = new SendPhoto()
                .setParseMode("HTML")
                .setChatId(chatId)
                .setCaption(imageCaption)
                .setPhoto(photoId);

        myTelegramBot.send(sendPhoto);
    }

    public void sendPhoto(long chatId, String imageCaption, String photoId, ReplyKeyboard keyboard) {
        SendPhoto sendPhoto = new SendPhoto()
                .setParseMode("HTML")
                .setChatId(chatId)
                .setCaption(imageCaption)
                .setPhoto(photoId)
                .setReplyMarkup(keyboard);

        myTelegramBot.send(sendPhoto);
    }

    public void sendMessage(String chatId, String message) {
        SendMessage sendMessage = new SendMessage()
                .setParseMode("HTML")
                .setChatId(chatId)
                .setText(message)
                .enableMarkdown(true);

        myTelegramBot.send(sendMessage);
    }

    public String getReplyText(String replyText) {
        return localeMessageService.getMessage(replyText);
    }

    public String getReplyText(String replyText, Object... args) {
        return localeMessageService.getMessage(replyText, args);
    }

    public EditMessageText getEditMessageText(
            Long chatId, Integer messageId, String text, InlineKeyboardMarkup keyboard) {

        EditMessageText editMessageText = new EditMessageText()
                .setChatId(chatId)
                .setMessageId(messageId)
                .setText(text)
                .setParseMode("HTML");

        if (keyboard != null) {
            editMessageText.setReplyMarkup(keyboard);
        }

        return editMessageText;
    }

    public void sendEditMessageText(
            Long chatId, Integer messageId, String text, InlineKeyboardMarkup keyboard) {

        EditMessageText editMessageText = new EditMessageText()
                .setChatId(chatId)
                .setMessageId(messageId)
                .setText(text)
                .setParseMode("HTML");

        if (keyboard != null) {
            editMessageText.setReplyMarkup(keyboard);
        }

        myTelegramBot.send(editMessageText);
    }
}
