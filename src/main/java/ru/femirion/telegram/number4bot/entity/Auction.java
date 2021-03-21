package ru.femirion.telegram.number4bot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Auction {
  private String hostPlayerId;
  private long currentSum;
  private long startFrom;
  private long step;
  private String currentPlayerId;
}
