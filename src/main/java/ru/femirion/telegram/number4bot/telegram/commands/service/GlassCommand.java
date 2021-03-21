package ru.femirion.telegram.number4bot.telegram.commands.service;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.femirion.telegram.number4bot.telegram.Bot;
import ru.femirion.telegram.number4bot.utils.UserUtils;

@Slf4j
public class GlassCommand extends ServiceCommand {

    public GlassCommand(String identifier, String description) {
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

        if (args.length != 1) {
            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                    "Вызовите эту команду с идентификатором объекта, например: /очки ABCD");
            return;
        }

        var objectId = args[0];
        var objectOptional = Bot.findObject(objectId);
        if (objectOptional.isEmpty()) {
            sendObjectNotFoundMessage(absSender, chatId, userName, objectId);
            return;
        }

        var object = objectOptional.get();
        if (object.getGlassId() != null) {
            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                    "Вы используете очки к объекту=" + objectId + ". Доступная информация: "
                            + object.getGlassDesc());
            return;
        }

        // если объект пустышка
        if (object.isFake()) {
            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName, object.getDesc());
            return;
        }

        sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                    "К этому объекту нельзя применить очки");

    }
}