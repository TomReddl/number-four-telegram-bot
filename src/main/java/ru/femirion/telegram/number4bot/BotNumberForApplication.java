package ru.femirion.telegram.number4bot;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.femirion.telegram.number4bot.entity.Player;
import ru.femirion.telegram.number4bot.telegram.Bot;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class BotNumberForApplication {
    private static final Map<String, String> ENV = System.getenv();
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public static void main(String[] args) {
        try {
            var botsApi = new TelegramBotsApi(DefaultBotSession.class);
            var bot = new Bot(ENV.get("BOT_NAME"), ENV.get("TOKEN"));
            botsApi.registerBot(bot);
            log.info("start bot");

            var notificationThread = creatNotificationThread(bot);
            executor.submit(notificationThread);
        } catch (TelegramApiException ex) {
            log.error("bot initialization error", ex);
        }
        executor.shutdown();
    }

    private static Runnable creatNotificationThread(Bot bot) {
        return () -> {
            while (!executor.isShutdown()) {
                var now = ZonedDateTime.now();
                var players = Bot.getPlayers();
                for (Player player : players) {
                    if (player.getTimeNextNotification() != null
                            && player.getTimeNextNotification().isBefore(now)
                            && player.getTextNextNotification() != null) {
                        bot.sendToPlayer(player.getChatId(), player.getName(), player.getTextNextNotification());
                        player.setTimeNextNotification(null);
                        player.setTextNextNotification(null);
                    }
                }
            }
        };
    }
}