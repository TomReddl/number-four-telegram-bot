package ru.femirion.telegram.number4bot.telegram.commands.service;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.femirion.telegram.number4bot.telegram.Bot;
import ru.femirion.telegram.number4bot.utils.SendUtils;
import ru.femirion.telegram.number4bot.utils.UserUtils;

@Slf4j
public class MoneyTransferCommand extends ServiceCommand {

    public MoneyTransferCommand(String identifier, String description) {
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

        if (args.length != 2) {
            SendUtils.sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                    "Чтобы сделать перевод нужно вызвать команду нужно указать идентификатор игрока и сумму перевода." +
                            " Например: /transfer player-id 5000");
            return;
        }

        var anotherPlayerId = args[0];
        var anotherPlayerOpt = Bot.findPlayer(anotherPlayerId);
        if (anotherPlayerOpt.isEmpty()) {
            SendUtils.sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                    "Игрок с таким идентификатором не найден");
            return;
        }

        var anotherPlayer = anotherPlayerOpt.get();
        var count = Long.parseLong(args[1]);
        if (count <= 0) {
            SendUtils.sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                    "Вы не можете перевести сумму меньше 1");
            return;
        } else if (player.getMoney() < count) {
            SendUtils.sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                    "На вашем счету недостаточно денег. У вас всего " + player.getMoney() + ", а вы хотите перевести " + count);
            return;
        }

        anotherPlayer.setMoney(anotherPlayer.getMoney() + count);
        player.setMoney(player.getMoney() - count);

        SendUtils.sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                "Перевод игроку " + anotherPlayer.getName() + " совершен успешно!");
    }
}