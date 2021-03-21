package ru.femirion.telegram.number4bot.telegram.commands.service;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.femirion.telegram.number4bot.telegram.Bot;
import ru.femirion.telegram.number4bot.utils.UserUtils;

@Slf4j
public class CloseAuctionCommand extends ServiceCommand {

    public CloseAuctionCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] args) {
        synchronized (Bot.class) {
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

            var auction = Bot.getAuction();
            if (auction == null) {
                sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName, "Аукцион еще не начался!");
                return;
            }

            if (!player.isCanStartAuction()) {
                sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                        "Вы не имеет права завершеть аукцион.");
                return;
            }

            var customerOptional = Bot.findPlayer(auction.getCurrentPlayerId());
            if (customerOptional.isEmpty()) {
                sendNotAutMessage(absSender, chatId, userName);
                return;
            }

            var customer = customerOptional.get();
            player.setMoney(player.getMoney() + auction.getCurrentSum());
            customer.setMoney(player.getMoney() - auction.getCurrentSum());
            sendAnswer(absSender, chatId, this.getCommandIdentifier(), userName,
                    "Аукцион закончен. Товар продан игроку=" + customer.getName() + " за " + auction.getCurrentSum());

            Bot.setAuction(null);
        }
    }
}