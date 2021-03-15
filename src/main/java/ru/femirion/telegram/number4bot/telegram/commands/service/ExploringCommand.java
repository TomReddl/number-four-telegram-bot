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
public class ExploringCommand extends ServiceCommand {

    public ExploringCommand(String identifier, String description) {
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

        var startExploringTime = player.getStartExploringTime();
        // значит сейчас идет исследование, надо определить есть закончилось ли оно
        if (startExploringTime != null) {
            // еще не закончилось
            var restTime = getRestExploringTime(startExploringTime);
            if (restTime > 0 ) {
                sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                        "Вы начали изучать объект objectId=" + player.getExploringObjectId()
                                + ". Осталось всего " + restTime + " минут(ы). Не забудьте обратится к боту за результатом");
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
                    "Вы сейчас не изучаете объект. Чтобы начать изучение, пошлите команду /exploring с указанием objectId");
            return;
        }

        var objectId = args[0];
        var objectOptional = Bot.findObject(objectId);
        if (objectOptional.isEmpty()) {
            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                    "Объект с таким objectId=" + objectId + " не найден. Проверьте правильность кода");
            return;
        }

        player.setStartExploringTime(LocalDateTime.now());
        player.setExploringObjectId(objectId);
        sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName, "Вы начали изучать объект objectId="
                + objectId + ". Это займет у вас 30 минут. Не забудьте обратится к боту за результатом");
        log.info("player has begun to exporer objectId={}, objectId={}", settings.getPlayerId(), objectId);
    }

    private int getRestExploringTime(LocalDateTime startExploringTime) {
        var now = LocalDateTime.now();

        var tempDateTime = LocalDateTime.from(startExploringTime);
        var days = tempDateTime.until(now, ChronoUnit.DAYS);
        tempDateTime = tempDateTime.plusDays(days);
        var hours = tempDateTime.until(now, ChronoUnit.HOURS);
        tempDateTime = tempDateTime.plusHours(hours);
        var minutes = tempDateTime.until(now, ChronoUnit.MINUTES);

        log.info("TIME!!!! days=" + days + " hours=" + hours + " munutes=" + minutes);


        // todo increase to 30 minutes!!!
        if (minutes > 2) {
            return -1;
        }

        if (days > 1 || hours > 1) {
            return -1;
        }

        return (int) minutes;
    }
}