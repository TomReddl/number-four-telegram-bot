package ru.femirion.telegram.number4bot.telegram.commands.service;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.femirion.telegram.number4bot.utils.UserUtils;
import ru.femirion.telegram.number4bot.telegram.Bot;
import ru.femirion.telegram.number4bot.telegram.nonCommand.Settings;

import java.util.Arrays;

@Slf4j
public class RegisterCommand extends ServiceCommand {

    public RegisterCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] args) {
        var userName = UserUtils.getUserName(user);

        var chatId = chat.getId();
        var settings = Bot.getUserSettings().get(chatId);

        if (args.length != 1) {
            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                    "команда должна содержать playerId");
            return;
        }

        var playerId = args[0];
        var playerOptional = Bot.findPlayer(playerId);
        if (playerOptional.isEmpty()) {
            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                    "персонаж с таким playerId=" + playerId + " не найден. Подойдите к мастеру");
            return;
        }
        var player = playerOptional.get();
        if (settings == null) {
            settings = new Settings(playerId);
            Bot.getUserSettings().put(chatId, settings);
        }
        player.setChatId(chatId);
        settings.setPlayerId(args[0]);
        settings.setPlayer(player);

        sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                String.format("*Регистрация прошла успешно!*\n" +
                                "   payerId: %s\n" +
                                "   userInfo: %s\n" +
                                "   playerName: %s",
                        playerId,
                        user,
                        player.getName())
        );
        log.info("player registration, playerId={}, userName={}, playerId={}", settings.getPlayerId(), user, playerId);
    }
}