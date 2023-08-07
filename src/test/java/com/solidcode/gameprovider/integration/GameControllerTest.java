package com.solidcode.gameprovider.integration;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.solidcode.gameprovider.exception.ErrorType.GAME_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.web.util.UriComponentsBuilder.fromUriString;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.solidcode.gameprovider.dto.request.GameRequest;
import com.solidcode.gameprovider.dto.response.GameResponse;
import com.solidcode.gameprovider.exception.Error;
import com.solidcode.gameprovider.exception.ErrorList;
import com.solidcode.gameprovider.repository.GameRepository;
import com.solidcode.gameprovider.repository.entity.Game;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class GameControllerTest {

  private WireMockServer wm;
  private static TestRestTemplate restTemplate;
  private String baseUrl = "http://localhost";
  private static GameRequest gameRequest;

  @Autowired
  private GameRepository gameRepository;

  @LocalServerPort
  private int port;

  @BeforeAll
  public static void beforeClass() {
    restTemplate = new TestRestTemplate();
  }

  @BeforeEach
  public void setUp() {
    gameRequest = GameRequest.builder().active(true).name("game").build();
    baseUrl = baseUrl + ":" + port + "/v1/api/games";
  }

  @AfterEach
  public void tearDown() {
    wm = new WireMockServer(options().port(8080));
    wm.start();
    gameRepository.deleteAll();
  }

  @Test
  public void getGame() {

    restTemplate.postForObject(baseUrl, gameRequest, String.class);
    ResponseEntity<Game> response = restTemplate.getForEntity(baseUrl.concat("/game/1"),
        Game.class);
    Game game = response.getBody();

    assertAll(
        () -> assertNotNull(game),
        () -> assertEquals("game", game.getName()),
        () -> assertEquals(1, game.getId())
    );
  }

  @Test
  public void getGames() {

    restTemplate.postForObject(baseUrl, gameRequest, String.class);
    ResponseEntity<GameResponse[]> response = restTemplate.getForEntity(baseUrl,
        GameResponse[].class);
    GameResponse[] games = response.getBody();

    assertAll(
        () -> assertNotNull(games),
        () -> assertEquals(1, games.length)
    );
  }

  @Test
  public void addGame() {

    String response = restTemplate.postForObject(baseUrl, gameRequest, String.class);
    Game first = gameRepository.findAll().stream().findFirst().get();

    assertEquals("Game saved", response);
    assertEquals("game", first.getName());
    assertEquals(1, gameRepository.findAll().size());
  }

  @Test
  public void addGame_Duplicate() {

    restTemplate.postForObject(baseUrl, gameRequest, String.class);
    assertThrows(RuntimeException.class,
        () -> restTemplate.postForObject(baseUrl, gameRequest, ResponseEntity.class));
  }

  @Test
  public void updateGame() {

    restTemplate.postForObject(baseUrl, gameRequest, String.class);
    gameRequest.setName("new name");

    HttpEntity<GameRequest> request = new HttpEntity<>(gameRequest);
    Map<String, String> urlParams = new HashMap<>();
    urlParams.put("id", "1");
    URI uri = fromUriString(baseUrl.concat("/game/{id}")).buildAndExpand(urlParams).toUri();
    ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.PUT, request,
        String.class);

    Game gameDb = gameRepository.findAll().stream().findFirst().get();
    assertAll(() -> assertNotNull(gameDb),
        () -> assertEquals(gameRequest.getName(), gameDb.getName()),
        () -> assertEquals("Game updated", response.getBody()));
  }

  @Test
  public void updateGame_NotFound() {

    restTemplate.postForObject(baseUrl, gameRequest, String.class);
    gameRequest.setName("new name");

    HttpEntity<GameRequest> request = new HttpEntity<>(gameRequest);
    Map<String, String> urlParams = new HashMap<>();
    urlParams.put("id", "0");
    URI uri = fromUriString(baseUrl.concat("/game/{id}")).buildAndExpand(urlParams).toUri();

    ResponseEntity<ErrorList> response = restTemplate.exchange(uri, HttpMethod.PUT, request,
        ErrorList.class);

    Error error = response.getBody().getErrors().get(0);

    assertEquals(GAME_NOT_FOUND.getMessage(), error.getDescription());
  }

  @Test
  public void deleteGame() {

    restTemplate.postForObject(baseUrl, gameRequest, String.class);
    HttpEntity<GameRequest> request = new HttpEntity<>(gameRequest);
    Map<String, String> urlParams = new HashMap<>();
    urlParams.put("id", "1");
    URI uri = fromUriString(baseUrl.concat("/game/{id}")).buildAndExpand(urlParams).toUri();
    ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.DELETE, request,
        String.class);

    List<Game> games = gameRepository.findAll();
    assertAll(() -> assertEquals(0, games.size()),
        () -> assertEquals("Game deleted", response.getBody()));
  }

  @Test
  public void deleteGame_NotFound() {

    restTemplate.postForObject(baseUrl, gameRequest, String.class);
    HttpEntity<GameRequest> request = new HttpEntity<>(gameRequest);
    Map<String, String> urlParams = new HashMap<>();
    urlParams.put("id", "0");
    URI uri = fromUriString(baseUrl.concat("/game/{id}")).buildAndExpand(urlParams).toUri();
    ResponseEntity<ErrorList> response = restTemplate.exchange(uri, HttpMethod.DELETE, request,
        ErrorList.class);

    Error error = response.getBody().getErrors().get(0);
    assertEquals(GAME_NOT_FOUND.getMessage(), error.getDescription());
  }
}