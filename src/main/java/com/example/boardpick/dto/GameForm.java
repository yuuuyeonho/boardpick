package com.example.boardpick.dto;

import com.example.boardpick.entity.Game;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter @Setter
public class GameForm {
    private Long id;
    private String name;

    private int minPlayer;
    private int maxPlayer;

    private String category;

    public Game toEntity(){
        Game game = new Game();
        game.setName(name);
        game.setMinPlayer(minPlayer);
        game.setMaxPlayer(maxPlayer);
        game.setCategory(category);

        return game;
    }

    public Game updateToEntity(){
        Game game = new Game();
        game.setId(id);
        game.setName(name);
        game.setMinPlayer(minPlayer);
        game.setMaxPlayer(maxPlayer);
        game.setCategory(category);

        return game;
    }
}
