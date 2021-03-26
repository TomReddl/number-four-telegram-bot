package ru.femirion.telegram.number4bot.telegram.commands.service;

import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.femirion.telegram.number4bot.entity.Auction;
import ru.femirion.telegram.number4bot.telegram.Bot;
import ru.femirion.telegram.number4bot.utils.UserUtils;

@Slf4j
public class AuctionCommand extends ServiceCommand {

    public AuctionCommand(String identifier, String description) {
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

        // хозяин аукциона должен перед ставку и шаг для начала торгов
        if (args.length == 2) {
            if (!player.isCanStartAuction()) {
                sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                        "Вы не имеет прав начинать аукцион.");
                return;
            }

            var startFrom = Long.parseLong(args[0]);
            var step = Long.parseLong(args[1]);
            Bot.setAuction(new Auction(player.getPlayerId(), startFrom - step, startFrom, step, null));
            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                    "Вы начали аукцион. Начальная ставка=" + startFrom + ". Шаг аукциона=" + step);
            return;
        }

        var auction = Bot.getAuction();
        if (auction == null) {
            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName, "Аукцион еще не начался!");
            return;
        }

        if (args.length == 0) {
            String msg = "Аукцион в самом разгаре! Текущая ставка=" + auction.getCurrentSum();
            var currentAuctionPlayerId = auction.getCurrentPlayerId();
            if (player.getPlayerId().equals(currentAuctionPlayerId) || player.isCanStartAuction()) {
                var currentAuctionPlayer = Bot.findPlayer(currentAuctionPlayerId);
                if (currentAuctionPlayer.isEmpty()) {
                    sendNotAutMessage(absSender, chatId, userName);
                    return;
                }
                msg = msg + ". владелец ставки:" + currentAuctionPlayer.get().getName();
            }
            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName, msg);
        }
    }
}