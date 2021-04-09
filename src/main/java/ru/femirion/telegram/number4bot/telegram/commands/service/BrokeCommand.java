package ru.femirion.telegram.number4bot.telegram.commands.service;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.femirion.telegram.number4bot.telegram.Bot;
import ru.femirion.telegram.number4bot.utils.SendUtils;
import ru.femirion.telegram.number4bot.utils.UserUtils;

@Slf4j
public class BrokeCommand extends ServiceCommand {
    public BrokeCommand(String identifier, String description) {
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
        // не мастер не может сломать комнату
        if (player == null || !player.getIsMaster()) {
            sendNotAutMessage(absSender, chatId, userName);
            return;
        }
        // ломаем или чиним комнату
        Bot.setIsRoomBroke(!Bot.getIsRoomBroke());

        SendUtils.sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName,
                Bot.getIsRoomBroke() ? "Комната сломана" : "Комната починена");
    }
}