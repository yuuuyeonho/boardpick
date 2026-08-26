package com.example.boardpick.list.repository;

import com.example.boardpick.list.domain.GameList;
import com.example.boardpick.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface GameListRepository extends JpaRepository<GameList, Long> {
    List<GameList> findByOwner(Member owner);
}
