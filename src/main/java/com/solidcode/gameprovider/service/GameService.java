package com.solidcode.gameprovider.service;

import static com.solidcode.gameprovider.exception.ErrorType.GAME_NOT_FOUND;
import static java.time.LocalDateTime.now;

import com.solidcode.gameprovider.exception.GameNotFoundException;
import com.solidcode.gameprovider.repository.GameRepository;
import com.solidcode.gameprovider.repository.entity.Game;
import java.sql.Timestamp;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@CacheConfig(cacheNames = "games")
public class GameService {

  private GameRepository gameRepository;

  @Autowired
  public GameService(GameRepository gameRepository) {
    this.gameRepository = gameRepository;
  }

  public Game saveGame(Game game) {

    game.setCreatedDate(Timestamp.valueOf(now()));
    return gameRepository.save(game);
  }

  @Cacheable(value = "game", key = "#id")
  public Game getGame(Long id) {

    log.info("GameService: findById: {}", id);
    return gameRepository.findById(id).orElseThrow();
  }

  @Cacheable("games")
  public List<Game> getGames() {

    log.info("GameService: findAll");
    return gameRepository.findAll();
  }

  @Caching(evict = {
      @CacheEvict(value = "games", allEntries = true),
      @CacheEvict(value = "game", key = "#id")})
  public Game updateGame(Long id, Game game) {

    log.info("GameService: saveOrUpdate, {}", id);
    Game gameDb = gameRepository.findById(id)
        .orElseThrow(() -> new GameNotFoundException(GAME_NOT_FOUND, "id"));
    gameDb.setName(game.getName());
    gameDb.setActive(game.isActive());
    return gameRepository.save(gameDb);
  }

  @Caching(evict = {
      @CacheEvict(value = "games", allEntries = true),
      @CacheEvict(value = "game", key = "#id")})
  public String deleteGame(Long id) {

    log.info("GameService: delete, {}", id);
    Game game = gameRepository.findById(id)
        .orElseThrow(() -> new GameNotFoundException(GAME_NOT_FOUND, "id"));
    gameRepository.deleteById(game.getId());
    return "Game deleted";
  }
}
