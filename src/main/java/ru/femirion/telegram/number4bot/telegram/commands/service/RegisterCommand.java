package ru.femirion.telegram.number4bot.telegram.commands.service;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.femirion.telegram.number4bot.Utils;
import ru.femirion.telegram.number4bot.telegram.Bot;
import ru.femirion.telegram.number4bot.telegram.nonCommand.Settings;

import java.util.Arrays;

@Slf4j
public class RegisterCommand extends ServiceCommand {

    public RegisterCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        var userName = Utils.getUserName(user);

        var chatId = chat.getId();
        var settings = Bot.getUserSettings().get(chatId);
        log.info("current settings, playerId={}, userName={}, Strings={}", settings.getPlayerId(), user, Arrays.toString(strings));
        savePlayer(chatId, strings[0]);

        sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                String.format("*Регистрация прошла успешно!*\n" +
                                "- payerId: %s\n" +
                                "- userName: %s",
                        settings.getPlayerId(),
                        user)
        );
    }


    private void savePlayer(Long chatId, String playerId) {
        var settings = Bot.getUserSettings().get(chatId);
        if (settings == null) {
            settings = new Settings("");
        }
        settings.setPlayerId(playerId);
    }
}