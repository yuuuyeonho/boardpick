package com.example.boardpick.game.dto;

import com.example.boardpick.game.domain.Game;

public record GameResponse(Long id, String name, int minPlayer, int maxPlayer, String category,
                           int playTimeMinutes, int difficulty) {
    public static GameResponse from(Game game) {
        return new GameResponse(game.getId(), game.getName(), game.getMinPlayer(), game.getMaxPlayer(),
                game.getCategory(), game.getPlayTime(), game.getDifficulty());
    }
}
