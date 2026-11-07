package com.example.boardpick.game.domain;

import com.example.boardpick.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Game extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private int minPlayer;
    private int maxPlayer;
    @Enumerated(EnumType.STRING)
    private GameCategory category;
    private int playTime;
    private int difficulty;

    public Game(String name, int minPlayer, int maxPlayer, GameCategory category,
                int playTime, int difficulty) {
        update(name, minPlayer, maxPlayer, category, playTime, difficulty);
    }

    public void update(String name, int minPlayer, int maxPlayer, GameCategory category,
                       int playTimeMinutes, int difficulty) {
        this.name = name;
        this.minPlayer = minPlayer;
        this.maxPlayer = maxPlayer;
        this.category = category;
        this.playTime = playTimeMinutes;
        this.difficulty = difficulty;
    }
}
