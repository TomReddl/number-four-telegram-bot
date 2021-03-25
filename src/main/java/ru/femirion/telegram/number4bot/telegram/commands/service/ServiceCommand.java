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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

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

        // если 1 - надо выслать пиктограммы
        if (countOfDependsObject == 1) {
            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                    "Информация об объкте: " + object.getDesc());
            sendPhotoAnswer(absSender, chatId, this.getCommandIdentifier(), userName, object.getPhotoId());
            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName, "Пиктограммы связанных объектов: ");
            var dependedObjectIds = object.getDependedObjects();
            for (var id : dependedObjectIds) {
                var dependedObject = Bot.findObject(id);
                if (dependedObject.isEmpty()) {
                    sendObjectNotFoundMessage(absSender, chatId, userName, id);
                    return;
                }
                var o = dependedObject.get();
                sendPhotoAnswer(absSender, chatId, this.getCommandIdentifier(), userName, o.getPhotoId());
            }
            return;
        }
        // если 2 - надо выслать порядок активации
        if (countOfDependsObject == 2) {
            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName, "Порядок активации: ");
//            var activationOrder = object.getActivationOrder();
//            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName, "Порядок активации объектов: "  + activationOrder);
            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName, "Порядок активации объектов: НО КОЛЯ НЕ ДАЛ МНЕ ПОРЯДОК АКТИВАЦИИ!!!!" );
            return;
        }
        // если 3 - надо выслать описание супер-объекта
        if (countOfDependsObject == 3) {
//            var superObject = Bot.findObject(object.getSuperObjectId());
//            if (superObject.isEmpty()) {
//                sendObjectNotFoundMessage(absSender, chatId, userName, object.getSuperObjectId());
//                return;
//            }
//            var o = superObject.get();
//            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName, "Описание эффекта при совмещении объектов: "  + o.getDesc());
            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName, "Описание эффекта при совмещении объектов: НО КОЛЯ НЕ ДАЛ МНЕ superObjectId!!!!");
        }

        var countOfSecondDependsObject = player.getObjects().stream()
                .filter(id -> object.getSecondDependedObjects().contains(id))
                .count();

        // если 1 - надо выслать пиктограммы
        if (countOfSecondDependsObject == 1) {
            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                    "Информация об объкте: " + object.getDesc());
            sendPhotoAnswer(absSender, chatId, this.getCommandIdentifier(), userName, object.getPhotoId());
            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName, "Пиктограммы связанных объектов: ");
            var dependedObjectIds = object.getSecondDependedObjects();
            for (var id : dependedObjectIds) {
                var dependedObject = Bot.findObject(id);
                if (dependedObject.isEmpty()) {
                    sendObjectNotFoundMessage(absSender, chatId, userName, id);
                    return;
                }
                var o = dependedObject.get();
                sendPhotoAnswer(absSender, chatId, this.getCommandIdentifier(), userName, o.getPhotoId());
            }
            return;
        }
        // если 2 - надо выслать порядок активации
        if (countOfSecondDependsObject == 2) {
            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName, "Порядок активации: ");
//            var activationOrder = object.getActivationOrder();
//            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName, "Порядок активации объектов: "  + activationOrder);
            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName, "Порядок активации объектов: НО КОЛЯ НЕ ДАЛ МНЕ ПОРЯДОК АКТИВАЦИИ!!!!" );
            return;
        }
        // если 3 - надо выслать описание супер-объекта
        if (countOfSecondDependsObject == 3) {
//            var superObject = Bot.findObject(object.getSuperObjectId());
//            if (superObject.isEmpty()) {
//                sendObjectNotFoundMessage(absSender, chatId, userName, object.getSuperObjectId());
//                return;
//            }
//            var o = superObject.get();
//            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName, "Описание эффекта при совмещении объектов: "  + o.getDesc());
            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName, "Описание эффекта при совмещении объектов: НО КОЛЯ НЕ ДАЛ МНЕ superObjectId!!!!");
        }

    }

    void sendNotAutMessage(AbsSender absSender, Long chatId, String userName) {
        sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                "Вы не зарегистрированы!!! Для регистрации необходимо выполнить команду" +
                        " /регистрация и указать ваш идентификатор игрока");
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