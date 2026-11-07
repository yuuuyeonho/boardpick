package com.example.boardpick.game.dto;

import com.example.boardpick.game.domain.GameCategory;
import com.example.boardpick.game.domain.Game;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GameRequest(
        @NotBlank String name,
        @Min(1) int minPlayer,
        @Min(1) int maxPlayer,
        @NotNull GameCategory category,
        @Min(1) int playTimeMinutes,
        @Min(1) @Max(5) int difficulty
) {
    @AssertTrue(message = "최소 인원은 최대 인원보다 클 수 없습니다.")
    public boolean isPlayerRangeValid() {
        return minPlayer <= maxPlayer;
    }

    public Game toEntity() {
        return new Game(name, minPlayer, maxPlayer, category, playTimeMinutes, difficulty);
    }
}
