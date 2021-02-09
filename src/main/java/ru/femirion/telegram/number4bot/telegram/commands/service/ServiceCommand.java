package ru.femirion.telegram.number4bot.telegram.commands.service;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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
}