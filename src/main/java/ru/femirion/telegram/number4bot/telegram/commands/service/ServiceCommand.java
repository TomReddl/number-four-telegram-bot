package ru.femirion.telegram.number4bot.telegram.commands.service;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.femirion.telegram.number4bot.entity.GameObject;
import ru.femirion.telegram.number4bot.entity.Player;
import ru.femirion.telegram.number4bot.telegram.Bot;
import ru.femirion.telegram.number4bot.utils.PhotoUtils;

@Slf4j
abstract class ServiceCommand extends BotCommand {
    private static final String ERROR_MSG = "Ошибка %s. Команда %s. Пользователь: %s";

    ServiceCommand(String identifier, String description) {
        super(identifier, description);
    }

    void exploringSeveralObjectsHandler(GameObject object, Player player, AbsSender absSender, long chatId, String userName) {
        var countOfDependsObject = player.getObjects().stream()
                .filter(id -> object.getDependedObjects().contains(id))
                .count();

        sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                "Информация об объекте: " + object.getDesc());
        sendPhotoAnswer(absSender, chatId, this.getCommandIdentifier(), userName, object.getPhotoId());

        // если 1 - надо выслать пиктограммы
        if (countOfDependsObject == 0) {
            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName, "Пиктограммы связанных объектов: ");
            sendPhotoAnswer(absSender, chatId, this.getCommandIdentifier(), userName, object.getSuperPhotoId());
        }
        // если 2 - надо выслать порядок активации
        if (countOfDependsObject == 1) {
            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName, "Порядок активации: ");
            sendPhotoAnswer(absSender, chatId, this.getCommandIdentifier(), userName, object.getActivationPhotoId());
        }
        // если 3 - надо выслать описание супер-объекта
        if (countOfDependsObject == 2) {
            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName, object.getSuperObjectDesc());
        }

        if (object.getSecondDependedObjects() == null ) {
            return;
        }

        var countOfSecondDependsObject = player.getObjects().stream()
                .filter(id -> object.getSecondDependedObjects().contains(id))
                .count();

        // если 1 - надо выслать пиктограммы
        if (countOfSecondDependsObject == 0) {
            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName, "Пиктограммы связанных объектов второй схемы: ");
            sendPhotoAnswer(absSender, chatId, this.getCommandIdentifier(), userName, object.getSecondSuperPhotoId());
            return;
        }
        // если 2 - надо выслать порядок активации
        if (countOfSecondDependsObject == 1) {
            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName, "Порядок активации второй схемы: ");
            sendPhotoAnswer(absSender, chatId, this.getCommandIdentifier(), userName, object.getSecondActivationPhotoId());
            return;
        }
        // если 3 - надо выслать описание супер-объекта
        if (countOfSecondDependsObject == 2) {
            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName, object.getSecondSuperObjectDesc());
        }

    }

    void sendNotAutMessage(AbsSender absSender, Long chatId, String userName) {
        sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                "Вы не зарегистрированы!!! Для регистрации необходимо выполнить команду" +
                        " /registration и указать ваш идентификатор игрока");
    }

    void sendObjectNotFoundMessage(AbsSender absSender, Long chatId, String userName, String objectId) {
        sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                "Объект с идентификатором=" + objectId + " не найден. Проверьте правильность кода");
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