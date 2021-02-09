package ru.femirion.telegram.number4bot.telegram.commands.service;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.femirion.telegram.number4bot.Utils;
import ru.femirion.telegram.number4bot.telegram.Bot;

@Slf4j
public class RegisterCommand extends ServiceCommand {

    public RegisterCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        var userName = Utils.getUserName(user);

        log.debug(String.format("Пользователь %s. Начато выполнение команды %s", userName, this.getCommandIdentifier()));

        var chatId = chat.getId();
        var settings = Bot.getUserSettings(chatId);
        sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                String.format("*Текущие настройки*\n" +
                                "- payerId: %s\n" +
                                "- userName: %s",
                        settings.getPlayerId(),
                        user)
        );

        log.info("current settings, playerId={}, userName={}", settings.getPlayerId(), user);


        log.debug(String.format("Пользователь %s. Завершено выполнение команды %s", userName,
                this.getCommandIdentifier()));
    }
}