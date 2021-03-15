package ru.femirion.telegram.number4bot.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import lombok.experimental.UtilityClass;
import ru.femirion.telegram.number4bot.entity.GameObject;
import ru.femirion.telegram.number4bot.entity.Player;

import java.io.File;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@UtilityClass
public class JsonUtils {
  private static final ObjectMapper objectMapper = createMapper();

  private static ObjectMapper createMapper() {
    JavaTimeModule javaTimeModule = new JavaTimeModule();
    javaTimeModule.addSerializer(ZonedDateTime.class, new ZonedDateTimeSerializer(DateTimeFormatter.ISO_DATE_TIME));
    javaTimeModule.addDeserializer(ZonedDateTime.class, InstantDeserializer.ZONED_DATE_TIME);

    var mapper = new ObjectMapper();
    mapper.registerModule(javaTimeModule);
    return mapper;
  }

  public static List<Player> getPlayers() {
    try {
      var path = "/" + JsonUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath()  + "players.json";
      return objectMapper.readValue(new File(path), new TypeReference<>(){});
    } catch (Exception ex) {
      throw new RuntimeException("can not read 'players.json', cause=%s" + ex.getMessage());
    }
  }

  public static List<GameObject> getObjects() {
    try {
      var path = "/" + JsonUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath()  + "objects.json";
      return objectMapper.readValue(new File(path), new TypeReference<>(){});
    } catch (Exception ex) {
      throw new RuntimeException("can not read 'objects.json', cause=%s" + ex.getMessage());
    }
  }
}
