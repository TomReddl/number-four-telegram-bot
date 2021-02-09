package ru.femirion.telegram.number4bot.telegram.commands.service;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.femirion.telegram.number4bot.Utils;
import ru.femirion.telegram.number4bot.telegram.Bot;

/**
 * Команда получения текущих настроек
 */
@Slf4j
public class SettingsCommand extends ServiceCommand {
    public SettingsCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        var userName = Utils.getUserName(user);
        var chatId = chat.getId();
        var settings = Bot.getUserSettings(chatId);
        sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                String.format("Текущие настройки:\n" +
                                "    playerId: %s\n",
                        settings.getPlayerId())
        );
    }
}