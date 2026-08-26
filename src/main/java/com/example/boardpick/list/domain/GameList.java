package com.example.boardpick.list.domain;

import com.example.boardpick.global.entity.BaseEntity;
import com.example.boardpick.member.domain.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "game_list")
@Getter
@NoArgsConstructor
public class GameList extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Column(name = "is_public", nullable = false)
    private boolean isPublic;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member owner;

    public GameList(Member owner, String name, boolean isPublic) {
        this.owner = owner;
        this.name = name;
        this.isPublic = isPublic;
    }
}
