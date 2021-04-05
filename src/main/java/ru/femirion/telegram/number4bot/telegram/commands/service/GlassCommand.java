package ru.femirion.telegram.number4bot.telegram.commands.service;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.femirion.telegram.number4bot.entity.SpecialStaffDesc;
import ru.femirion.telegram.number4bot.telegram.Bot;
import ru.femirion.telegram.number4bot.utils.UserUtils;

@Slf4j
public class GlassCommand extends ServiceCommand {

    public GlassCommand(String identifier, String description) {
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

        if (args.length != 1) {
            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                    "Вызовите эту команду с идентификатором объекта, например: /glass ABCD");
            return;
        }

        if (!player.getObjects().contains("ЗР8ЬЬ")) {
            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                    "Изучите очки перед использованием");
            return;
        }

        var objectId = args[0];
        var glassInfoOptional = Bot.findGlassObject(objectId);
        if (glassInfoOptional.isEmpty()) {
            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                    "К этому предмету нельзя использовать очки");
            return;
        }

        var glassInfo = glassInfoOptional.get();
        var specialDesc = glassInfo.getSpecialDesc();
        var desc = glassInfo.getDesc();
        if (!specialDesc.isEmpty()) {
            var special = specialDesc.stream()
                    .filter(s -> s.getPlayerId().equals(player.getPlayerId()))
                    .map(SpecialStaffDesc::getSpecialDesc)
                    .findAny()
                    .orElse("");
            desc = desc + special;
        }

        sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                "Вы используете очки к объекту=" + objectId + ". Доступная информация: "
                        + desc);
    }
}