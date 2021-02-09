package ru.femirion.telegram.number4bot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Player {
  private String playerId;
  private String name;
  private String desc;
  private List<String> objects;
  private List<String> actions;
}
