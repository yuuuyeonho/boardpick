package com.example.boardpick.game.repository;

import com.example.boardpick.game.domain.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {
}
