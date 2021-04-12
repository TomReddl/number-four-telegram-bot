package ru.femirion.telegram.number4bot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Player {
  private String playerId;
  private String password;
  private Boolean isMaster;
  private Long chatId;
  private String name;
  private String desc;
  private List<String> objects = new ArrayList<>();
  @JsonIgnore
  private LocalDateTime startExploringTime;
  @JsonIgnore
  private String exploringObjectId;
  @JsonIgnore
  private Boolean sendExploreNotises;
  private long money;
  private boolean canStartAuction;
}
