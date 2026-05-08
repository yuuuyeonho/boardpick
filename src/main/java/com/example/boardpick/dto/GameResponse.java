package com.example.boardpick.dto;

import com.example.boardpick.entity.Game;

public record GameResponse(
        Long id,
        String name,
        int minPlayer,
        int maxPlayer,
        String category,
        int playTimeMinutes,
        int difficulty,
        String description
) {
    public static GameResponse from(Game game) {
        return new GameResponse(
                game.getId(),
                game.getName(),
                game.getMinPlayer(),
                game.getMaxPlayer(),
                game.getCategory(),
                game.getPlayTimeMinutes(),
                game.getDifficulty(),
                game.getDescription()
        );
    }
}
