package ru.femirion.telegram.number4bot.telegram.nonCommand;

import lombok.Data;
import ru.femirion.telegram.number4bot.entity.Player;

@Data
public class Settings {
    private String playerId;
    private Player player;

    public Settings(String playerId) {
        this.playerId = playerId;
    }
}