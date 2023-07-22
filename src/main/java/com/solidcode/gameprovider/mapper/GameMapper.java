package com.solidcode.gameprovider.mapper;

import com.solidcode.gameprovider.dto.request.GameRequest;
import com.solidcode.gameprovider.dto.response.GameResponse;
import com.solidcode.gameprovider.repository.entity.Game;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GameMapper {

  Game toGame(GameRequest gameRequest);

  GameResponse toGameResponse(Game game);

  List<GameResponse> toGames(List<Game> games);
}
