package com.example.boardpick.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class GameCollection {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Column(unique = true)
    private String slug;

    @Enumerated(EnumType.STRING)
    private CollectionType type;

    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;

    @ManyToMany
    @JoinTable(
            name = "collection_game",
            joinColumns = @JoinColumn(name = "collection_id"),
            inverseJoinColumns = @JoinColumn(name = "game_id")
    )
    private List<Game> games = new ArrayList<>();
}
