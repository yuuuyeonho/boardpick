package com.example.boardpick.list.dto;

import com.example.boardpick.list.domain.GameList;
import java.time.LocalDateTime;

public record GameListResponse(Long id, Long memberId, String name, boolean isPublic,
                               LocalDateTime createdAt, LocalDateTime updatedAt) {
    public static GameListResponse from(GameList list) {
        return new GameListResponse(list.getId(), list.getOwner().getId(), list.getName(), list.isPublic(),
                list.getCreatedAt(), list.getUpdatedAt());
    }
}
