package com.example.boardpick.list.repository;

import com.example.boardpick.game.domain.Game;
import com.example.boardpick.list.domain.GameList;
import com.example.boardpick.list.domain.ListGame;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListGameRepository extends JpaRepository<ListGame, Long> {
    List<ListGame> findByGameList(GameList gameList);
    boolean existsByGameListAndGame(GameList gameList, Game game);
    void deleteByGameListAndGame(GameList gameList, Game game);
}
