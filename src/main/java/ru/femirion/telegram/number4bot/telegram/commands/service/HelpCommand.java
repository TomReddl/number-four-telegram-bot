package ru.femirion.telegram.number4bot.telegram.commands.service;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.femirion.telegram.number4bot.utils.UserUtils;

/**
 * Команда "Помощь"
 */
@Slf4j
public class HelpCommand extends ServiceCommand {
    public HelpCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        String userName = UserUtils.getUserName(user);
        sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName,
                "Я игровой бот. Я умею многое, но не все((\n\n " +
                        "Вот что я могу\n" +
                        "регистрация /registration\n " +
                        "ваша текущая информация /info\n" +
                        "исследование объектов /explore\n" +
                        "отмена текущего исследования /cancel\n" +
                        "информация об объекте /object\n" +
                        "применение очков /glass\n" +
                        "аукцион /auction\n" +
                        "ставка /bet\n" +
                        "объект продан на аукционе /sold\n" +
                        "денежный перед другому игроку /transfer\n" +
                        "Если у тебя остались вопросы," +
                        " то подойди к мастерам. Они тебе помогут. \n\n Желаю удачи\uD83D\uDE42");
    }
}