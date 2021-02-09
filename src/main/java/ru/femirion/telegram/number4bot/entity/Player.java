package ru.femirion.telegram.number4bot.entity;

import lombok.Data;

import java.util.List;

@Data
public class Player {
  private String playerId;
  private String name;
  private String desc;
  private List<GameObject> objects;
  private List<String> actions;
}
