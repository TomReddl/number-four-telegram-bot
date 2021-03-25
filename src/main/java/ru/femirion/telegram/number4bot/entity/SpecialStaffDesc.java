package ru.femirion.telegram.number4bot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SpecialStaffDesc {
  private String playerId;
  private String specialDesc;
}
