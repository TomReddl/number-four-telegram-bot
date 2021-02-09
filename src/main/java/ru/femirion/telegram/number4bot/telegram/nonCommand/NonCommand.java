package ru.femirion.telegram.number4bot.telegram.nonCommand;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NonCommand {
    public String nonCommandExecute(Long chatId, String userName, String text) {
        return  "Простите, я не понимаю Вас. \n" +
                "Возможно, Вам поможет /help";
    }
}