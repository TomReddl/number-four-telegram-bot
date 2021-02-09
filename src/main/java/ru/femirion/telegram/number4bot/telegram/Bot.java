package ru.femirion.telegram.number4bot.telegram;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.femirion.telegram.number4bot.Utils;
import ru.femirion.telegram.number4bot.telegram.commands.service.HelpCommand;
import ru.femirion.telegram.number4bot.telegram.commands.service.RegisterCommand;
import ru.femirion.telegram.number4bot.telegram.commands.service.SettingsCommand;
import ru.femirion.telegram.number4bot.telegram.commands.service.StartCommand;
import ru.femirion.telegram.number4bot.telegram.nonCommand.NonCommand;
import ru.femirion.telegram.number4bot.telegram.nonCommand.Settings;

import java.util.HashMap;
import java.util.Map;

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

    public Bot(String botName, String botToken) {
        super();
        this.BOT_NAME = botName;
        this.BOT_TOKEN = botToken;
        this.nonCommand = new NonCommand();
        register(new StartCommand("start", "Старт"));
        register(new HelpCommand("help","Помощь"));
        register(new SettingsCommand("settings", "Мои настройки"));
        register(new RegisterCommand("register", "Войти с id игрока"));
        userSettings = new HashMap<>();
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
        String userName = Utils.getUserName(msg);

        String answer = nonCommand.nonCommandExecute(chatId, userName, msg.getText());
        setAnswer(chatId, userName, answer);
    }

    public static Settings getUserSettings(Long chatId) {
        var userSettings = Bot.getUserSettings();
        var settings = userSettings.get(chatId);
        if (settings == null) {
            return defaultSettings;
        }
        return settings;
    }

    private void setAnswer(Long chatId, String userName, String text) {
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