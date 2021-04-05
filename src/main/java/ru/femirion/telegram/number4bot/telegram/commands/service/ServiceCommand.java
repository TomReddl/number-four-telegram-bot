package ru.femirion.telegram.number4bot.telegram.commands.service;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.femirion.telegram.number4bot.utils.SendUtils;

@Slf4j
abstract class ServiceCommand extends BotCommand {
    private static final String ERROR_MSG = "Ошибка %s. Команда %s. Пользователь: %s";

    ServiceCommand(String identifier, String description) {
        super(identifier, description);
    }

    void sendNotAutMessage(AbsSender absSender, Long chatId, String userName) {
        SendUtils.sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                "Вы не зарегистрированы!!! Для регистрации необходимо выполнить команду" +
                        " /registration и указать ваш идентификатор игрока");
    }

    void sendObjectNotFoundMessage(AbsSender absSender, Long chatId, String userName, String objectId) {
        SendUtils.sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                "Объект с идентификатором=" + objectId + " не найден. Проверьте правильность кода");
    }
}