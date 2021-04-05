package ru.femirion.telegram.number4bot.telegram.commands.service;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.femirion.telegram.number4bot.telegram.Bot;
import ru.femirion.telegram.number4bot.utils.SendUtils;
import ru.femirion.telegram.number4bot.utils.UserUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
public class ExploringCommand extends ServiceCommand {

    private static String fastExploringId = "pv60";
    private static int fastExploringTime = 1; // TODO не забыть поменять на настоящее время для игры (5 минут)
    private static int exploringTime = 2;

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
            var restTime = getRestExploringTime(player.getPlayerId(), startExploringTime);
            if (restTime > 0 ) {
                SendUtils.sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                        "Вы начали изучать объект objectId=" + player.getExploringObjectId()
                                + ". Осталось всего " + restTime + " минут(ы). Не забудьте обратиться к боту за результатом");
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
                // если объект пустышка
                if (object.isFake()) {
                    SendUtils.sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName, object.getDesc());
                    player.setExploringObjectId(null);
                    player.setStartExploringTime(null);
                    return;
                }

                SendUtils.sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                        "Вы изучили объект идентификатором=" + player.getExploringObjectId());
                player.setExploringObjectId(null);
                player.setStartExploringTime(null);
                player.getObjects().add(object.getObjectId());
                // обработка того, что пользователь знает несколько объектов
                SendUtils.exploringSeveralObjectsHandler(object, player, absSender, chatId, userName);
            }
            return;
        }

        // исследования еще не было, надо начать
        if (args.length != 1) {
            SendUtils.sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
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
            SendUtils.sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName, object.getDesc());
            return;
        }

        boolean playerKnowThisObject = player.getObjects().stream().anyMatch(objectId::equals);
        if (playerKnowThisObject) {
            SendUtils.sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                    "Вы уже изучили объект c идентификатором=" + objectId
                            + ". Чтобы получить информацию об объекте выполните команду /object " + objectId);
            return;
        }

        player.setStartExploringTime(LocalDateTime.now());
        player.setExploringObjectId(objectId);

        int time = player.getPlayerId().equals(fastExploringId) ? fastExploringTime : exploringTime;
        SendUtils.sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName, "Вы начали изучать объект с идентификатором="
                + objectId + ". Это займет у вас " + time + " минут. Не забудьте обратиться к боту за результатом");
        log.info("player has begun to exporer objectId={}, objectId={}", settings.getPlayerId(), objectId);
    }

    private int getRestExploringTime(String playerId, LocalDateTime startExploringTime) {
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
        int time = playerId.equals(fastExploringId) ? fastExploringTime : exploringTime;
        return time - (int) minutes;
    }
}
