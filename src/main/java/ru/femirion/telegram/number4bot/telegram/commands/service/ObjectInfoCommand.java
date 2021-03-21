package ru.femirion.telegram.number4bot.telegram.commands.service;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.femirion.telegram.number4bot.telegram.Bot;
import ru.femirion.telegram.number4bot.utils.UserUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
public class ObjectInfoCommand extends ServiceCommand {

    public ObjectInfoCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] args) {
        var userName = UserUtils.getUserName(user);

        var chatId = chat.getId();
        var settings = Bot.getUserSettings().get(chatId);

        if (settings == null) {
            sendNotAutMessage(absSender, chatId, userName);
            return;
        }

        var player = settings.getPlayer();
        if (player == null) {
            sendNotAutMessage(absSender, chatId, userName);
            return;
        }

        // исследования еще не было, надо начать
        if (args.length != 1) {
            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                    "Вызовите эту команду с идентификатором объекта, например: /объект ABCD");
            return;
        }

        var objectId = args[0];
        var objectOptional = Bot.findObject(objectId);
        if (objectOptional.isEmpty()) {
            sendObjectNotFoundMessage(absSender, chatId, userName, objectId);
            return;
        }

        boolean playerKnowThisObject = player.getObjects().stream().anyMatch(objectId::equals);
        if (playerKnowThisObject) {
            var object = objectOptional.get();
            // обработка того, что пользователь знает несколько объектов
            exploringSeveralObjectsHandler(object, player, absSender, chatId, userName);
        } else {
            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                    "Вы еще не изучили объект. Чтобы начать изучение вызовите команду /изучить " + objectId);
        }
    }
}