package com.example.boardpick.list.domain;

import com.example.boardpick.game.domain.Game;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "list_game", uniqueConstraints = @UniqueConstraint(columnNames = {"list_id", "game_id"}))
@Getter
@NoArgsConstructor
public class ListGame {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "list_id", nullable = false)
    private GameList gameList;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    public ListGame(GameList gameList, Game game) {
        this.gameList = gameList;
        this.game = game;
    }
}
