package ru.femirion.telegram.number4bot.telegram.commands.service;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.femirion.telegram.number4bot.telegram.Bot;
import ru.femirion.telegram.number4bot.telegram.nonCommand.Settings;
import ru.femirion.telegram.number4bot.utils.UserUtils;

@Slf4j
public class CancelCommand extends ServiceCommand {

    public CancelCommand(String identifier, String description) {
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
            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                    "Исследование объекта=" + player.getExploringObjectId() + " отменено!");
            player.setExploringObjectId(null);
            player.setStartExploringTime(null);
        } else {
            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                    "Вы сейчас не ведете исследование");
        }
    }
}