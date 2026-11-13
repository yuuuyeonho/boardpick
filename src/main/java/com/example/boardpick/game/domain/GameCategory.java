package com.example.boardpick.game.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GameCategory {
    STRATEGY("전략"),
    FAMILY("가족"),
    ABSTRACT_STRATEGY("추상전략"),
    PARTY("파티"),
    DEDUCTION("추리"),
    BLUFFING("심리"),
    COOPERATIVE("협력"),
    COMPETITIVE("경쟁"),
    DEXTERITY("순발력");

    private final String displayName;
}
