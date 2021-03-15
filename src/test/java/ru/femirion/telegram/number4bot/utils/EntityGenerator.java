package ru.femirion.telegram.number4bot.utils;

import lombok.experimental.UtilityClass;
import ru.femirion.telegram.number4bot.entity.Player;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@UtilityClass
public class EntityGenerator {

  public Player createPlayer(String playerId, Long chatId, String name) {
    var player = new Player();
    player.setPlayerId(playerId);
    player.setChatId(chatId);
    player.setName(name);
    player.setDesc("desc " + name);

    var objects = List.of("fist-object", "second-object");
    player.setObjects(objects);

    var actions = List.of("first-action", "second-action");
    player.setActions(actions);

    var time = LocalDateTime.parse("2021-02-28T18:36:44");
    player.setStartExploringTime(time);
    player.setExploringObjectId("1234-444");

    return player;
  }
}
