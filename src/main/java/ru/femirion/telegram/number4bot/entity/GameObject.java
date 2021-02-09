package ru.femirion.telegram.number4bot.entity;

import lombok.Data;

import java.util.List;

@Data
public class GameObject {
  private String objectId;
  private String name;
  private String desc;
  private List<String> actions;
}
