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
  private String superObjectDesc;
  private String secondSuperObjectDesc;
  private String photoId;
  private String superPhotoId;
  private String activationPhotoId;
  private String secondSuperPhotoId;
  private String secondActivationPhotoId;
  private boolean canBeExploring;
  private List<String> dependedObjects;
  private List<String> secondDependedObjects;
  private boolean fake;
}
