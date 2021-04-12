package ru.femirion.telegram.number4bot.telegram.commands.service;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.femirion.telegram.number4bot.telegram.Bot;
import ru.femirion.telegram.number4bot.utils.SendUtils;
import ru.femirion.telegram.number4bot.utils.UserUtils;

@Slf4j
// Включение или отключение уведомлений об исследований объектов (для мастеров)
public class SwitchExploreNoticesCommand extends ServiceCommand {
    public SwitchExploreNoticesCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        var userName = UserUtils.getUserName(user);
        var chatId = chat.getId();
        var settings = Bot.getUserSettings(chatId);
        if (settings == null) {
            sendNotAutMessage(absSender, chatId, userName);
            return;
        }

        var player = settings.getPlayer();
        // команда только для мастера
        if (player == null || !player.getIsMaster()) {
            sendNotAutMessage(absSender, chatId, userName);
            return;
        }
        // включить/выключить уведомления
        player.setSendExploreNotises(!player.getSendExploreNotises());

        SendUtils.sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName,
                player.getSendExploreNotises() ? "Уведомления включены" : "уведомления отключены");
    }
}