package org.golovko.telegramshop.service;

import org.golovko.telegramshop.MyTelegramBot;
import org.golovko.telegramshop.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class AdminService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    @Lazy
    private MyTelegramBot myTelegramBot;

    public void getAndSendPhotoId(Message message) {
        Long chatId = message.getChatId();

        if (!appUserRepository.existsByChatId(chatId)) {
            SendMessage sendMessage = new SendMessage(chatId, "Access denied!");
            myTelegramBot.send(sendMessage);
            return;
        }

        List<PhotoSize> photos = message.getPhoto();

        // Know file_id
        String f_id = Objects.requireNonNull(photos
                .stream()
                .max(Comparator.comparing(PhotoSize::getFileSize))
                .orElse(null))
                .getFileId();

        // Set photo caption
        String caption = "photo_id: " + f_id;

        SendPhoto msg = new SendPhoto()
                .setChatId(chatId)
                .setPhoto(f_id)
                .setCaption(caption);

        myTelegramBot.send(msg);
    }
}
