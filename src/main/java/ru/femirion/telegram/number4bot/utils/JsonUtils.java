package ru.femirion.telegram.number4bot.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import ru.femirion.telegram.number4bot.entity.GameObject;
import ru.femirion.telegram.number4bot.entity.Player;

import java.io.File;
import java.util.List;

@UtilityClass
public class JsonUtils {
  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static List<Player> getPlayers() {
    try {
      return objectMapper.readValue(new File("players.json"), new TypeReference<>(){});
    } catch (Exception ex) {
      throw new RuntimeException("can not read 'players.json', cause=%s" + ex.getMessage());
    }
  }

  public static List<GameObject> getObjects() {
    try {
      return objectMapper.readValue(new File("objects.json"), new TypeReference<>(){});
    } catch (Exception ex) {
      throw new RuntimeException("can not read 'objects.json', cause=%s" + ex.getMessage());
    }
  }
}
