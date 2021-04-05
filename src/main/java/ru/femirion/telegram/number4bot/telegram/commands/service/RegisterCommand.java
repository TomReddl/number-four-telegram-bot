package ru.femirion.telegram.number4bot.telegram.commands.service;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.femirion.telegram.number4bot.telegram.Bot;
import ru.femirion.telegram.number4bot.telegram.nonCommand.Settings;
import ru.femirion.telegram.number4bot.utils.SendUtils;
import ru.femirion.telegram.number4bot.utils.UserUtils;

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

        if (args.length != 2) {
            SendUtils.sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                    "Команда регистрации должна содержать только идентификатор игрока." +
                            " Если у вас есть вопросы, то подойдите к мастеру.\n\n" +
                            " Пример команды: /registration my-game-player-id my-password");
            return;
        }

        var playerId = args[0];
        var password = args[1];
        var playerOptional = Bot.findPlayer(playerId);
        if (playerOptional.isEmpty()) {
            SendUtils.sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                    "Персонаж с идентификатором=" + playerId + " не найден. Подойдите к мастеру");
            return;
        }

        var player = playerOptional.get();
        if (!password.equals(player.getPassword())) {
            SendUtils.sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                    "Вы ввели неверный логин/пароль. Проверьте ввод или подойдите к мастеру");
            return;
        }

        // TODO revert before prod
//        if (settings != null) {
//            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
//                    "Вы уже зарегистрированы в системе");
//            return;
//        }


        settings = new Settings(playerId);
        Bot.getUserSettings().put(chatId, settings);


        player.setChatId(chatId);

        settings.setPlayerId(args[0]);
        settings.setPlayer(player);

        SendUtils.sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                String.format("*Регистрация прошла успешно!*\n" +
                                "   payerId: %s\n" +
                                "   имя: %s\n",
                        playerId,
                        player.getName())
        );
    }
}