package com.example.boardpick.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class Game {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private int minPlayer;
    private int maxPlayer; // 인원수 필드가 범위값이라 어떻게 써야할지 고민중

    private String category;

    /* 나중에 추가하면 좋을 필터
    private int playTime; //플레이타임
    private int difficulty; //난이도
    private int ageRating; //연령등급
    */
}
