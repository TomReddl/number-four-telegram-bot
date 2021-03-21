package ru.femirion.telegram.number4bot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GameObject {
  private String objectId;
  private String name;
  private String desc;
  private String photoId;
  private boolean canBeExploring;
  private String superObjectId;
  private List<String> dependedObjects;
  private List<String> activationOrder;
  private String glassId;
  private String glassDesc;
  private boolean fake;
}
