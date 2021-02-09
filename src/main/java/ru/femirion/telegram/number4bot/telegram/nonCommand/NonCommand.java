package ru.femirion.telegram.number4bot.telegram.nonCommand;

import lombok.extern.slf4j.Slf4j;
import ru.femirion.telegram.number4bot.exceptions.IllegalSettingsException;
import ru.femirion.telegram.number4bot.telegram.Bot;

@Slf4j
public class NonCommand {

    public String nonCommandExecute(Long chatId, String userName, String text) {
        log.debug(String.format("Пользователь %s. Начата обработка сообщения \"%s\", не являющегося командой",
                userName, text));

        Settings settings;
        String answer;
        try {

            settings = createSettings(text);
            saveUserSettings(chatId, settings);
            answer = "Настройки обновлены. Вы всегда можете их посмотреть с помощью /settings";
        } catch (IllegalSettingsException e) {
            answer = e.getMessage() +
                    "\n\n❗ Настройки не были изменены. Вы всегда можете их посмотреть с помощью /settings";
        } catch (Exception e) {
            answer = "Простите, я не понимаю Вас. \n" +
                    "Возможно, Вам поможет /help";
        }

        log.debug(String.format("Пользователь %s. Завершена обработка сообщения \"%s\", не являющегося командой",
                    userName, text));
        return answer;
    }

    private Settings createSettings(String text) {
        //отсекаем файлы, стикеры, гифки и прочий мусор
        if (text == null) {
            throw new IllegalArgumentException("Сообщение не является текстом");
        }

        // todo check that text contains playerId
        return new Settings(text);
    }

    private void saveUserSettings(Long chatId, Settings settings) {
        if (!settings.equals(Bot.getDefaultSettings())) {
            Bot.getUserSettings().put(chatId, settings);
        } else {
            Bot.getUserSettings().remove(chatId);
        }
    }
}