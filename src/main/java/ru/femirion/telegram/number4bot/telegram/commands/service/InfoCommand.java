package ru.femirion.telegram.number4bot.telegram.commands.service;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.femirion.telegram.number4bot.utils.UserUtils;
import ru.femirion.telegram.number4bot.telegram.Bot;

@Slf4j
public class InfoCommand extends ServiceCommand {
    public InfoCommand(String identifier, String description) {
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
        if (player == null) {
            sendNotAutMessage(absSender, chatId, userName);
            return;
        }

        sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                String.format("*Информация о персонаже:*\n" +
                                "    playerId: %s\n" +
                                "    имя: %s\n" +
                                "    известные объекты: %s\n\n" +
                                "    квента: %s\n",
                        settings.getPlayerId(),
                        settings.getPlayer().getName(),
                        settings.getPlayer().getObjects(),
                        settings.getPlayer().getDesc())
        );
    }
}