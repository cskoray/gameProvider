package com.solidcode.gameprovider.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.ok;

import com.solidcode.gameprovider.dto.request.GameRequest;
import com.solidcode.gameprovider.dto.response.GameResponse;
import com.solidcode.gameprovider.mapper.GameMapper;
import com.solidcode.gameprovider.repository.entity.Game;
import com.solidcode.gameprovider.service.GameService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v1/api/games")
@Validated
public class GameController {

  private GameService gameService;
  private GameMapper gameMapper;

  @Autowired
  public GameController(GameService gameService, GameMapper gameMapper) {
    this.gameService = gameService;
    this.gameMapper = gameMapper;
  }

  @PostMapping
  public ResponseEntity<String> saveGame(@Valid @RequestBody GameRequest request) {

    log.info("GameController: saveGame");
    Game game = gameMapper.toGame(request);
    gameService.saveGame(game);
    return new ResponseEntity<>("Game saved", CREATED);
  }

  @GetMapping
  @ResponseStatus(OK)
  public List<GameResponse> getGames() {

    log.info("GameController: getAllGame");
    List<Game> games = gameService.getGames();
    return gameMapper.toGames(games);
  }

  @GetMapping("/game/{id}")
  @ResponseStatus(OK)
  public GameResponse getGame(@PathVariable("id") Long id) {

    log.info("GameController: getGame id: {}", id);
    Game game = gameService.getGame(id);
    return gameMapper.toGameResponse(game);
  }

  @PutMapping("/game/{id}")
  public ResponseEntity<String> updateGame(@PathVariable("id") Long id,
      @Valid @RequestBody GameRequest request) {

    log.info("GameController: updateGame id: {}", id);
    Game game = gameMapper.toGame(request);
    gameService.updateGame(id, game);
    return ok("Game updated");
  }

  @DeleteMapping("/game/{id}")
  public ResponseEntity<String> deleteGame(@PathVariable("id") Long id) {

    log.info("GameController: deleteGame id: {}", id);
    gameService.deleteGame(id);
    return ok("Game deleted");
  }
}
