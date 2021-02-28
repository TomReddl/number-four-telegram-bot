package ru.femirion.telegram.number4bot.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.femirion.telegram.number4bot.utils.EntityGenerator;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PlayerSerializationTest {

  @Test
  void onePlayerSerializationTest() throws JsonProcessingException {
    var player = EntityGenerator.createPlayer("player1", 123L, "Vasia");

    var result = new ObjectMapper().writeValueAsString(player);

    var expectedResult = "{\"playerId\":\"player1\",\"chatId\":123,\"name\":\"Vasia\",\"desc\":\"desc Vasia\"," +
            "\"objects\":[\"fist-object\",\"second-object\"],\"actions\":[\"first-action\",\"second-action\"]," +
            "\"timeNextNotification\":\"2021-02-28T18:36:44+0300\",\"textNextNotification\":\"text of notification\"}";
    assertThat(expectedResult).isEqualTo(result);
  }

  @Test
  void listOfPlayersSerializationTest() throws JsonProcessingException {
    var player1 = EntityGenerator.createPlayer("player1", 121L, "Vasia");
    var player2 = EntityGenerator.createPlayer("player2", 122L, "Petya");
    var player3 = EntityGenerator.createPlayer("player3", 123L, "Natasha");

    var result = new ObjectMapper().writeValueAsString(List.of(player1, player2, player3));

    var expectedResult = "[{\"playerId\":\"player1\",\"chatId\":121,\"name\":\"Vasia\",\"desc\":\"desc Vasia\"," +
            "\"objects\":[\"fist-object\",\"second-object\"],\"actions\":[\"first-action\",\"second-action\"]," +
            "\"timeNextNotification\":\"2021-02-28T18:36:44+0300\",\"textNextNotification\":\"text of notification\"}" +
            ",{\"playerId\":\"player2\",\"chatId\":122,\"name\":\"Petya\",\"desc\":\"desc Petya\"," +
            "\"objects\":[\"fist-object\",\"second-object\"],\"actions\":[\"first-action\",\"second-action\"]," +
            "\"timeNextNotification\":\"2021-02-28T18:36:44+0300\",\"textNextNotification\":\"text of notification\"}," +
            "{\"playerId\":\"player3\",\"chatId\":123,\"name\":\"Natasha\",\"desc\":\"desc Natasha\"," +
            "\"objects\":[\"fist-object\",\"second-object\"],\"actions\":[\"first-action\",\"second-action\"]," +
            "\"timeNextNotification\":\"2021-02-28T18:36:44+0300\",\"textNextNotification\":\"text of notification\"}]";
    assertThat(expectedResult).isEqualTo(result);
  }

}