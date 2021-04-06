package ru.femirion.telegram.number4bot.telegram.commands.service;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.femirion.telegram.number4bot.telegram.Bot;
import ru.femirion.telegram.number4bot.utils.SendUtils;
import ru.femirion.telegram.number4bot.utils.UserUtils;

@Slf4j
public class ShareInfoCommand extends ServiceCommand {

    public ShareInfoCommand(String identifier, String description) {
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

        if (args.length != 2) {
            SendUtils.sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                    "Чтобы поделиться информацией, нужно вызвать команду с указанием идентификатора игрока и кода объекта" +
                            " Например: /share player-id 1234");
            return;
        }

        var anotherPlayerId = args[0];
        var anotherPlayerOpt = Bot.findPlayer(anotherPlayerId);
        if (anotherPlayerOpt.isEmpty()) {
            SendUtils.sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                    "Игрок с таким идентификатором не найден");
            return;
        }

        var anotherPlayer = anotherPlayerOpt.get();
        var objectId = args[1];
        var objectOptional = Bot.findObject(objectId);
        if (objectOptional.isEmpty()) {
            sendObjectNotFoundMessage(absSender, chatId, userName, objectId);
            return;
        }

        boolean playerKnowThisObject = player.getObjects().stream().anyMatch(objectId::equals);
        if (playerKnowThisObject) {
            var object = objectOptional.get();
            anotherPlayer.getObjects().add(object.getObjectId());

            SendUtils.sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                    "Вы отправили игроку " + anotherPlayer.getName() + " информацию об объекте" + objectId);
        } else {
            SendUtils.sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                    "Вы еще не изучили объект. Чтобы начать изучение вызовите команду /explore " + objectId);
        }
    }
}