package ru.femirion.telegram.number4bot;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.femirion.telegram.number4bot.entity.Player;
import ru.femirion.telegram.number4bot.telegram.Bot;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class BotNumberForApplication {
  private static final Map<String, String> ENV = System.getenv();
  private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

  public static void main(String[] args) {
    try {
      var botsApi = new TelegramBotsApi(DefaultBotSession.class);
      var bot = new Bot(ENV.get("BOT_NAME"), ENV.get("TOKEN"));
      botsApi.registerBot(bot);
      log.info("start bot");

      var notificationThread = creatNotificationThread(bot);
      executor.scheduleAtFixedRate(notificationThread, 5, 10, TimeUnit.SECONDS);
    } catch (TelegramApiException ex) {
      log.error("bot initialization error", ex);
    }
    executor.shutdown();
  }

  private static Runnable creatNotificationThread(Bot bot) {
    return () -> {
      log.info("start monitoring notification");
      try {
        var now = ZonedDateTime.now();
        var players = Bot.getPlayers();
        for (Player player : players) {
          if (player.getTimeNextNotification() != null
                  && player.getTimeNextNotification().isBefore(now)
                  && player.getTextNextNotification() != null) {
            bot.sendToPlayer(player.getChatId(), player.getName(), player.getTextNextNotification());
            log.info("send notification to player={}, text={}", player.getName(), player.getTextNextNotification());
            player.setTimeNextNotification(null);
            player.setTextNextNotification(null);
          }
        }
      } catch (Exception ex) {
        log.error("error in notification thread", ex);
      }
      log.info("end monitoring notification");
    };
  }
}