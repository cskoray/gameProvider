package com.solidcode.gameprovider.service;

import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.solidcode.gameprovider.repository.GameRepository;
import com.solidcode.gameprovider.repository.entity.Game;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

  @InjectMocks
  private GameService unit;

  @Mock
  private GameRepository gameRepository;

  @Test
  public void addGame() {

    Game game = Game.builder().name("some name").build();
    unit.saveGame(game);

    ArgumentCaptor<Game> userCapture = ArgumentCaptor.forClass(Game.class);
    verify(gameRepository).save(userCapture.capture());

    assertEquals(game.getName(), userCapture.getValue().getName());
    verify(gameRepository, times(1)).save(game);
  }

  @Test
  public void addGame_DuplicateGame() {

    Game game = Game.builder().name("some name").build();

    when(gameRepository.save(any(Game.class))).thenThrow(
        new DataIntegrityViolationException(""));

    assertThrows(DataIntegrityViolationException.class, () -> unit.saveGame(game));
  }

  @Test
  public void updateGame() {

    Game game = Game.builder().name("some name").active(false).build();
    when(gameRepository.findById(any(Long.class))).thenReturn(
        ofNullable(game));

    String newName = "new name";
    game.setName(newName);
    unit.updateGame(1l, game);

    ArgumentCaptor<Game> userCapture = ArgumentCaptor.forClass(Game.class);
    verify(gameRepository).save(userCapture.capture());
    assertEquals(newName, userCapture.getValue().getName());
  }

  @Test
  public void updateGame_NotFound() {

    Game game = Game.builder().name("some name").build();
    when(gameRepository.findById(any(Long.class))).thenThrow(
        new IllegalArgumentException(""));

    assertThrows(IllegalArgumentException.class, () -> {
      String newName = "new name";
      game.setName(newName);
      unit.updateGame(1L, game);
    });
  }

  @Test
  public void deleteGame() throws Exception {

    Game game = Game.builder().name("some name").active(false).build();
    when(gameRepository.findById(any(Long.class))).thenReturn(
        ofNullable(game));

    unit.deleteGame(1l);
    verify(gameRepository, times(1)).deleteById(any(Long.class));
  }
}