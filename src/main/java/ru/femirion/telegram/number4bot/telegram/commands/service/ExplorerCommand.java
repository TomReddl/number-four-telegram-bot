package ru.femirion.telegram.number4bot.telegram.commands.service;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.femirion.telegram.number4bot.telegram.Bot;
import ru.femirion.telegram.number4bot.utils.UserUtils;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
public class ExplorerCommand extends ServiceCommand {

    public ExplorerCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] args) {
        var userName = UserUtils.getUserName(user);

        var chatId = chat.getId();
        var settings = Bot.getUserSettings().get(chatId);

        var player = settings.getPlayer();
        if (player == null) {
            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                    "Вы не зарегистрированы!!! Для регистрации необходимо выполнить команду" +
                            " /register и указать ваш playerId");
            return;
        }

        var now = LocalDateTime.now();
        var startExploringTime = player.getStartExploringTime();
        // значит сейчас идет исследование, надо определить есть закончилось ли оно
        if (startExploringTime != null) {
            long minutes = startExploringTime.until(now, ChronoUnit.MINUTES );
            // todo increase to 30 minutes!!!
            // еще не закончилось
            if (minutes > 0) {
                sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                        "Вы начали изучать объект objectId=" + player.getExploringObjectId()
                                + ". Осталось всего " + minutes + " минут(ы). Не забудьте обратится к боту за результатом");
            } else {
                // закончилось
                var objectOptional = Bot.findObject(player.getExploringObjectId());
                if (objectOptional.isEmpty()) {
                    sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                            "Объект с таким objectId=" + player.getExploringObjectId() + " не найден. Проверьте правильность кода");

                    player.setExploringObjectId(null);
                    player.setStartExploringTime(null);
                    return;
                }
                var object = objectOptional.get();
                sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                        "Вы изучили объект objectId=" + player.getExploringObjectId()
                                + ". Информация об объекте: " + object.getDesc());
                player.setExploringObjectId(null);
                player.setStartExploringTime(null);
            }
            return;
        }

        // исследования еще не было, надо начать
        if (args.length != 1) {
            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                    "Команда должна содержать objectId");
            return;
        }

        var objectId = args[0];
        var objectOptional = Bot.findObject(objectId);
        if (objectOptional.isEmpty()) {
            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                    "Объект с таким objectId=" + objectId + " не найден. Проверьте правильность кода");
            return;
        }


        player.setStartExploringTime(now);
        player.setExploringObjectId(objectId);
        sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName, "Вы начали изучать объект objectId="
                + objectId + ". Это займет у вас 30 минут. Не забудьте обратится к боту за результатом");
        log.info("player has begun to exporer objectId={}, objectId={}", settings.getPlayerId(), objectId);
    }
}