package com.solidcode.gameprovider.repository;

import com.solidcode.gameprovider.repository.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

}
