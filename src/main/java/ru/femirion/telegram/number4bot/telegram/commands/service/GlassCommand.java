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
                    "Вызовите эту команду с идентификатором объекта, например: /очки ABCD");
            return;
        }

        var objectId = args[0];
        var staffOptional = Bot.findStaff(objectId);
        if (staffOptional.isEmpty()) {
            sendObjectNotFoundMessage(absSender, chatId, userName, objectId);
            return;
        }

        if (Bot.findObject(objectId).isPresent()) {
            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                    "К этому предмету нельзя использовать очки");
            return;
        }

        var staff = staffOptional.get();
        var specialDesc = staff.getSpecialDesc();
        var desc = staff.getDesc();
        if (!specialDesc.isEmpty()) {
            var special = specialDesc.stream()
                    .filter(s -> s.getPlayerId().equals(player.getPlayerId()))
                    .map(SpecialStaffDesc::getSpecialDesc);
            desc = desc + special;
        }

        sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                "Вы используете очки к объекту=" + objectId + ". Доступная информация: "
                        + desc);
    }
}