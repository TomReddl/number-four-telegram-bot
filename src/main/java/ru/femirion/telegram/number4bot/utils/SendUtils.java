package ru.femirion.telegram.number4bot.utils;

import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.femirion.telegram.number4bot.entity.GameObject;
import ru.femirion.telegram.number4bot.entity.Player;
import ru.femirion.telegram.number4bot.telegram.Bot;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class SendUtils {
    private static final String ERROR_MSG = "Ошибка %s. Команда %s. Пользователь: %s";

    public static void sendAnswerWithKeyboard(AbsSender absSender, Long chatId, String text, InlineKeyboardMarkup keyboard) {
        var message = new SendMessage();
        message.enableMarkdown(true);
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setReplyMarkup(keyboard);
        try {
            absSender.execute(message);
        } catch (TelegramApiException ex) {
            // log.error(String.format(ERROR_MSG, ex.getMessage(), commandName, userName), ex);
        }
    }

    public static void sendAnswer(AbsSender absSender, Long chatId, String commandName, String userName, String text) {
        var message = new SendMessage();
        message.enableMarkdown(true);
        message.setChatId(chatId.toString());
        message.setText(text);
        try {
            absSender.execute(message);
        } catch (TelegramApiException ex) {
            // log.error(String.format(ERROR_MSG, ex.getMessage(), commandName, userName), ex);
        }
    }

    public static void sendPhotoAnswer(AbsSender absSender, Long chatId, String commandName, String userName, String photoId) {
        try {
            var message = new SendPhoto();
            message.setChatId(chatId.toString());
            message.setPhoto(new InputFile(PhotoUtils.getImage(photoId)));
            absSender.execute(message);
        } catch (TelegramApiException ex) {
            // log.error(String.format(ERROR_MSG, ex.getMessage(), commandName, userName), ex);
        }
    }

    public static void exploringSeveralObjectsHandler(String commandIdentifier, GameObject object, Player player, AbsSender absSender, long chatId, String userName) {
        var countOfDependsObject = player.getObjects().stream()
                .filter(id -> object.getDependedObjects().contains(id))
                .count();

        sendAnswer(absSender, chatId, commandIdentifier, userName,
                "Информация об объекте: " + object.getDesc() +
                        (Bot.getIsRoomBroke() && !object.getBrokenDesk().isEmpty() ?
                                "\n\n" + "*НОВОЕ СВОЙСТВО:* " + object.getBrokenDesk() : ""));
        sendPhotoAnswer(absSender, chatId, commandIdentifier, userName, object.getPhotoId());

        // если 0 - надо выслать пиктограммы
        if (countOfDependsObject == 0 && !object.getSuperPhotoId().isEmpty()) {
            sendAnswer(absSender, chatId, commandIdentifier, userName, "Пиктограммы связанных объектов: ");
            sendPhotoAnswer(absSender, chatId, commandIdentifier, userName, object.getSuperPhotoId());
        }
        // если 1 - надо выслать порядок активации
        if (countOfDependsObject == 1) {
            sendAnswer(absSender, chatId, commandIdentifier, userName, "Порядок активации: ");
            sendPhotoAnswer(absSender, chatId, commandIdentifier, userName, object.getActivationPhotoId());
        }
        // если 2 или 3 - надо выслать описание супер-объекта
        if (countOfDependsObject == 2 || countOfDependsObject == 3) {
            sendAnswer(absSender, chatId, commandIdentifier, userName, object.getSuperObjectDesc());
            sendAnswer(absSender, chatId, commandIdentifier, userName, "Порядок активации: ");
            sendPhotoAnswer(absSender, chatId, commandIdentifier, userName, object.getActivationPhotoId());
        }

        if (object.getSecondDependedObjects() == null) {
            return;
        }

        var countOfSecondDependsObject = player.getObjects().stream()
                .filter(id -> object.getSecondDependedObjects().contains(id))
                .count();

        // если 0 - надо выслать пиктограммы
        if (countOfSecondDependsObject == 0) {
            sendAnswer(absSender, chatId, commandIdentifier, userName, "Пиктограммы связанных объектов второй схемы: ");
            sendPhotoAnswer(absSender, chatId, commandIdentifier, userName, object.getSecondSuperPhotoId());
            return;
        }
        // если 1 - надо выслать порядок активации
        if (countOfSecondDependsObject == 1) {
            sendAnswer(absSender, chatId, commandIdentifier, userName, "Порядок активации второй схемы: ");
            sendPhotoAnswer(absSender, chatId, commandIdentifier, userName, object.getSecondActivationPhotoId());
            return;
        }
        // если 2 - надо выслать описание супер-объекта
        if (countOfSecondDependsObject == 2 || countOfSecondDependsObject == 3) {
            sendAnswer(absSender, chatId, commandIdentifier, userName, object.getSecondSuperObjectDesc());
            sendAnswer(absSender, chatId, commandIdentifier, userName, "Порядок активации второй схемы: ");
            sendPhotoAnswer(absSender, chatId, commandIdentifier, userName, object.getSecondActivationPhotoId());
        }
    }

    public static InlineKeyboardMarkup getKeyBoard() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Информация");
        inlineKeyboardButton1.setCallbackData("/info");
        inlineKeyboardButton2.setText("Исследование");
        inlineKeyboardButton2.setCallbackData("/explore");
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow1.add(inlineKeyboardButton1);
        // keyboardButtonsRow1.add(new InlineKeyboardButton().setText("Fi4a").setCallbackData("CallFi4a"));
        keyboardButtonsRow2.add(inlineKeyboardButton2);
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }
}
