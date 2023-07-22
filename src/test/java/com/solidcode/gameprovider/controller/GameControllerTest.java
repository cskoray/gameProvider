package com.solidcode.gameprovider.controller;

import static com.solidcode.gameprovider.exception.ErrorType.GAME_NOT_FOUND;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solidcode.gameprovider.dto.request.GameRequest;
import com.solidcode.gameprovider.dto.response.GameResponse;
import com.solidcode.gameprovider.exception.GameNotFoundException;
import com.solidcode.gameprovider.mapper.GameMapper;
import com.solidcode.gameprovider.repository.entity.Game;
import com.solidcode.gameprovider.service.GameService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(GameController.class)
class GameControllerTest {

  @MockBean
  private GameService gameService;

  @MockBean
  private GameMapper gameMapper;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  public void getGame() throws Exception {

    Game game = Game.builder().id(1L).name("some name").active(true).build();
    when(gameService.getGame(any(Long.class))).thenReturn(game);
    GameResponse response = GameResponse.builder().id(1L).name("some name").active(true).build();
    when(gameMapper.toGameResponse(game)).thenReturn(response);

    mockMvc.perform(get("/v1/api/games/game/{id}", 1L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.name").value("some name"))
        .andExpect(jsonPath("$.active").value(true));
  }

  @Test
  public void getGames() throws Exception {

    Game game = Game.builder().id(1L).name("some name").active(true).build();
    when(gameService.getGames()).thenReturn(singletonList(game));
    List<GameResponse> list = asList(
        GameResponse.builder().id(1L).name("some name").active(true).build());
    when(gameMapper.toGames(singletonList(game))).thenReturn(list);

    mockMvc.perform(get("/v1/api/games"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.[0].id").value(1L))
        .andExpect(jsonPath("$.[0].name").value("some name"))
        .andExpect(jsonPath("$.[0].active").value(true));
  }

  @Test
  public void saveGame() throws Exception {

    GameRequest request = GameRequest.builder().name("some name").build();
    Game game = Game.builder().name("some name").active(true).build();
    when(gameMapper.toGame(any(GameRequest.class))).thenReturn(game);
    when(gameService.saveGame(any(Game.class))).thenReturn(game);

    mockMvc.perform(post("/v1/api/games")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$").value("Game saved"));
  }

  @Test
  public void saveGameShouldReturnBadRequest() throws Exception {

    GameRequest request = GameRequest.builder().name("some name").build();
    when(gameService.saveGame(any(Game.class))).thenThrow(
        new DataIntegrityViolationException("Game exists."));
    Game game = Game.builder().name("some name").active(true).build();
    when(gameMapper.toGame(any(GameRequest.class))).thenReturn(game);

    mockMvc.perform(post("/v1/api/games")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void updateGame() throws Exception {

    GameRequest request = GameRequest.builder().name("new name").build();
    Game game = Game.builder().name("new name").active(true).build();
    when(gameService.updateGame(any(Long.class), any(Game.class))).thenReturn(game);
    when(gameMapper.toGame(any(GameRequest.class))).thenReturn(game);

    mockMvc.perform(put("/v1/api/games/game/{id}", 1L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").value("Game updated"));
  }

  @Test
  public void updateGame_NotFound() throws Exception {

    GameRequest request = GameRequest.builder().name("new name").build();
    Game game = Game.builder().name("new name").active(true).build();
    when(gameService.updateGame(any(Long.class), any(Game.class))).thenThrow(
        new GameNotFoundException(GAME_NOT_FOUND, "id"));
    when(gameMapper.toGame(any(GameRequest.class))).thenReturn(game);

    mockMvc.perform(put("/v1/api/games/game/{id}", 1L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors.[0].description").value("Game cannot be found."));
  }

  @Test
  public void deleteGame() throws Exception {

    Game game = Game.builder().name("new name").active(true).build();
    when(gameService.deleteGame(any(Long.class))).thenReturn("Game deleted");
    when(gameMapper.toGame(any(GameRequest.class))).thenReturn(game);

    mockMvc.perform(delete("/v1/api/games/game/{id}", 1L)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").value("Game deleted"));
  }

  @Test
  public void deleteGame_NotFound() throws Exception {

    Game game = Game.builder().name("new name").active(true).build();
    when(gameService.deleteGame(any(Long.class))).thenThrow(
        new GameNotFoundException(GAME_NOT_FOUND, "id"));
    when(gameMapper.toGame(any(GameRequest.class))).thenReturn(game);

    mockMvc.perform(delete("/v1/api/games/game/{id}", 1L)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors.[0].description").value("Game cannot be found."));
  }

}