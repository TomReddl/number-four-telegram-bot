package ru.femirion.telegram.number4bot.telegram;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.femirion.telegram.number4bot.entity.GameObject;
import ru.femirion.telegram.number4bot.entity.Player;
import ru.femirion.telegram.number4bot.telegram.commands.service.*;
import ru.femirion.telegram.number4bot.utils.JsonUtils;
import ru.femirion.telegram.number4bot.utils.UserUtils;
import ru.femirion.telegram.number4bot.telegram.nonCommand.NonCommand;
import ru.femirion.telegram.number4bot.telegram.nonCommand.Settings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public final class Bot extends TelegramLongPollingCommandBot {
    private static final String NO_NAME = "no_name";
    private static final String NOT_COMMAND = "Ошибка %s. Сообщение, не являющееся командой. Пользователь: %s";
    private final String BOT_NAME;
    private final String BOT_TOKEN;

    @Getter
    private static final Settings defaultSettings = new Settings(NO_NAME);
    private final NonCommand nonCommand;
    @Getter
    private static Map<Long, Settings> userSettings;
    @Getter
    private static List<Player> players;
    @Getter
    private static List<GameObject> gameObjects;

    public Bot(String botName, String botToken) {
        super();
        this.BOT_NAME = botName;
        this.BOT_TOKEN = botToken;
        this.nonCommand = new NonCommand();
        register(new StartCommand("start", "Старт"));
        register(new HelpCommand("help","Помощь"));
        register(new InfoCommand("info", "Моя информация"));
        register(new RegisterCommand("register", "Войти с id игрока"));
        register(new ExploringCommand("exploring", "Изучить объект"));
        userSettings = new HashMap<>();
        players = JsonUtils.getPlayers();
        gameObjects = JsonUtils.getObjects();
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        Message msg = update.getMessage();
        Long chatId = msg.getChatId();
        String userName = UserUtils.getUserName(msg);

        String answer = nonCommand.nonCommandExecute(chatId, userName, msg.getText());
        sendToPlayer(chatId, userName, answer);
    }

    public static Optional<Player> findPlayer(String playerId) {
        return players.stream()
                .filter(p -> playerId.equals(p.getPlayerId()))
                .findAny();
    }

    public static Optional<GameObject> findObject(String objectId) {
        return gameObjects.stream()
                .filter(p -> objectId.equals(p.getObjectId()))
                .findAny();
    }

    public static Settings getUserSettings(Long chatId) {
        var userSettings = Bot.getUserSettings();
        var settings = userSettings.get(chatId);
        if (settings == null) {
            return defaultSettings;
        }
        return settings;
    }

    public void sendToPlayer(Long chatId, String userName, String text) {
        SendMessage answer = new SendMessage();
        answer.setText(text);
        answer.setChatId(chatId.toString());
        try {
            execute(answer);
        } catch (TelegramApiException ex) {
            log.error(String.format(NOT_COMMAND, ex.getMessage(), userName), ex);
        }
    }
}