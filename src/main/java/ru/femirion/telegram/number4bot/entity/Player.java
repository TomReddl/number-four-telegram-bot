package ru.femirion.telegram.number4bot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Player {
  private String playerId;
  private Long chatId;
  private String name;
  private String desc;
  private List<String> objects;
  private List<String> actions;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
  @JsonSerialize(using = ZonedDateTimeSerializer.class)
  private ZonedDateTime timeNextNotification;
  private String textNextNotification;
}
