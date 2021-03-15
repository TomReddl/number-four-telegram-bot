package ru.femirion.telegram.number4bot;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.femirion.telegram.number4bot.telegram.Bot;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

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
    } catch (TelegramApiException ex) {
      log.error("bot initialization error", ex);
    }
    executor.shutdown();
  }

}