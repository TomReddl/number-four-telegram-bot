package ru.femirion.telegram.number4bot.telegram.commands.service;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.femirion.telegram.number4bot.entity.GameObject;
import ru.femirion.telegram.number4bot.entity.Player;
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

        if (settings == null) {
            sendNotAutMessage(absSender, chatId, userName);
            return;
        }

        var player = settings.getPlayer();
        if (player == null) {
            sendNotAutMessage(absSender, chatId, userName);
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
                // по какой-то причине объект не найден. Такого быть не должно, но обработаем всеравно!
                if (objectOptional.isEmpty()) {
                    sendObjectNotFoundMessage(absSender, chatId, userName, player.getExploringObjectId());
                    player.setExploringObjectId(null);
                    player.setStartExploringTime(null);
                    log.error("ERROR!!! objectId={}, playerId={}", player.getExploringObjectId(), player.getPlayerId());
                    return;
                }
                var object = objectOptional.get();
                sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                        "Вы изучили объект идентификатором=" + player.getExploringObjectId());
                player.setExploringObjectId(null);
                player.setStartExploringTime(null);
                player.getObjects().add(object.getObjectId());
                // обработка того, что пользователь знает несколько объектов
                exploringSeveralObjectsHandler(object, player, absSender, chatId, userName);
            }
            return;
        }

        // исследования еще не было, надо начать
        if (args.length != 1) {
            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                    "Вы сейчас не изучаете объект. Чтобы начать изучение, пошлите команду /explore с указанием идентификатора объекта");
            return;
        }

        var objectId = args[0];
        var objectOptional = Bot.findObject(objectId);
        if (objectOptional.isEmpty()) {
            sendObjectNotFoundMessage(absSender, chatId, userName, objectId);
            return;
        }

        var object = objectOptional.get();
        if (!object.isCanBeExploring()) {
            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                    "Этот объект нельзя изучить. Описание объекта: " + object.getDesc());
            return;
        }

        boolean playerKnowThisObject = player.getObjects().stream().anyMatch(objectId::equals);
        if (playerKnowThisObject) {
            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                    "Вы уже изучили объект c идентификатором=" + objectId
                            + ". Чтобы получить информацию об объекте выполните команду /object " + objectId);
            return;
        }

        // если объект пустышка
        if (object.isFake()) {
            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName, object.getDesc());
            return;
        }

        player.setStartExploringTime(LocalDateTime.now());
        player.setExploringObjectId(objectId);
        sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName, "Вы начали изучать объект с идентификатором="
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

        if (days > 1 || hours > 1) {
            return -1;
        }

        // todo increase to 20 minutes!!!
        return 5 - (int) minutes;
    }
}