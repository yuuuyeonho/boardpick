package com.example.boardpick.dto;

import com.example.boardpick.entity.CollectionType;
import com.example.boardpick.entity.GameCollection;

public record CollectionResponse(
        Long id,
        String name,
        String slug,
        CollectionType type,
        Long ownerId,
        String ownerName,
        int gameCount
) {
    public static CollectionResponse from(GameCollection collection) {
        return new CollectionResponse(
                collection.getId(),
                collection.getName(),
                collection.getSlug(),
                collection.getType(),
                collection.getOwner() == null ? null : collection.getOwner().getId(),
                collection.getOwner() == null ? null : collection.getOwner().getDisplayName(),
                collection.getGames().size()
        );
    }
}
