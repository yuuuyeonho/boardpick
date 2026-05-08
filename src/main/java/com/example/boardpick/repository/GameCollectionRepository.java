package com.example.boardpick.repository;

import com.example.boardpick.entity.GameCollection;
import com.example.boardpick.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GameCollectionRepository extends JpaRepository<GameCollection, Long> {
    Optional<GameCollection> findBySlug(String slug);
    List<GameCollection> findByOwner(User owner);
}
