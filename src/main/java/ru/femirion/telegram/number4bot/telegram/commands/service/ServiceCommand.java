package ru.femirion.telegram.number4bot.telegram.commands.service;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.femirion.telegram.number4bot.utils.PhotoUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@Slf4j
abstract class ServiceCommand extends BotCommand {
    private static final String ERROR_MSG = "Ошибка %s. Команда %s. Пользователь: %s";

    ServiceCommand(String identifier, String description) {
        super(identifier, description);
    }


    void sendAnswer(AbsSender absSender, Long chatId, String commandName, String userName, String text) {
        var message = new SendMessage();
        message.enableMarkdown(true);
        message.setChatId(chatId.toString());
        message.setText(text);
        try {
            absSender.execute(message);
        } catch (TelegramApiException ex) {
            log.error(String.format(ERROR_MSG, ex.getMessage(), commandName, userName), ex);
        }
    }

    void sendPhotoAnswer(AbsSender absSender, Long chatId, String commandName, String userName, String photoId) {
        try {
            var message = new SendPhoto();
            message.setChatId(chatId.toString());
            message.setPhoto(new InputFile(PhotoUtils.getImage(photoId)));
            absSender.execute(message);
        } catch (TelegramApiException ex) {
            log.error(String.format(ERROR_MSG, ex.getMessage(), commandName, userName), ex);
        }
    }
}