package ru.femirion.telegram.number4bot.telegram.commands.service;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.femirion.telegram.number4bot.utils.UserUtils;
import ru.femirion.telegram.number4bot.telegram.Bot;

@Slf4j
public class SettingsCommand extends ServiceCommand {
    public SettingsCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        var userName = UserUtils.getUserName(user);
        var chatId = chat.getId();
        var settings = Bot.getUserSettings(chatId);
        if (settings == null) {
            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                    "Вы не зарегистрированы в системе. Если у вас есть проблемы с регистрацией, то подойдите к мастеру");
            return;
        }

        var player = settings.getPlayer();
        if (player == null) {
            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                    "Вы не зарегистрированы в системе. Если у вас есть проблемы с регистрацией, то подойдите к мастеру");
            return;
        }

        sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                String.format("Информация о персонаже:\n" +
                                "    playerId: %s\n",
                                "    playerName: %s\n",
                                "    desc: %s\n",
                                "    objects: %s\n",
                                "    actions: %s\n",
                        settings.getPlayerId(),
                        settings.getPlayer().getName(),
                        settings.getPlayer().getDesc(),
                        settings.getPlayer().getObjects(),
                        settings.getPlayer().getActions())
        );
    }
}