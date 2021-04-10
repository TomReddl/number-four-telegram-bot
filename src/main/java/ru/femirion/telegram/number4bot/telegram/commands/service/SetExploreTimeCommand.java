package ru.femirion.telegram.number4bot.telegram.commands.service;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.femirion.telegram.number4bot.telegram.Bot;
import ru.femirion.telegram.number4bot.utils.SendUtils;
import ru.femirion.telegram.number4bot.utils.UserUtils;

@Slf4j
public class SetExploreTimeCommand extends ServiceCommand {
    public SetExploreTimeCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] args) {
        var userName = UserUtils.getUserName(user);
        var chatId = chat.getId();
        var settings = Bot.getUserSettings(chatId);
        if (settings == null) {
            sendNotAutMessage(absSender, chatId, userName);
            return;
        }

        var player = settings.getPlayer();
        // не мастер не может задавать время исследования
        if (player == null || !player.getIsMaster()) {
            sendNotAutMessage(absSender, chatId, userName);
            return;
        }
        // задаем время исследования
        Bot.setExploreTime(Integer.parseInt(args[0]));

        int fastExploreTime = args[1].isEmpty() ? 0 : Integer.parseInt(args[1]);
        Bot.setFastExploreTime(fastExploreTime != 0 ? fastExploreTime : Bot.getExploreTime() / 2);

        SendUtils.sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName,
                "Задано новое время исследования: " + Bot.getExploreTime() + "мин.\n" +
                "Задано новое время ускоренного исследования: " + Bot.getFastExploreTime() + "мин.\n");
    }
}