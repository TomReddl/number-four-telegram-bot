package ru.femirion.telegram.number4bot;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.femirion.telegram.number4bot.telegram.Bot;

import java.util.Map;

@Slf4j
public class BotNumberForApplication {
    private static final Map<String, String> getenv = System.getenv();

    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new Bot(getenv.get("BOT_NAME"), getenv.get("TOKEN")));
            log.info("start bot");
        } catch (TelegramApiException ex) {
            log.error("bot initialization error", ex);
        }
    }
}