package com.example.boardpick.list.service;

import com.example.boardpick.game.domain.Game;
import com.example.boardpick.game.dto.GameResponse;
import com.example.boardpick.game.service.GameService;
import com.example.boardpick.list.domain.GameList;
import com.example.boardpick.list.domain.ListGame;
import com.example.boardpick.list.dto.GameListResponse;
import com.example.boardpick.list.repository.GameListRepository;
import com.example.boardpick.list.repository.ListGameRepository;
import com.example.boardpick.member.domain.Member;
import com.example.boardpick.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameListService {

    private final GameListRepository gameListRepository;
    private final ListGameRepository listGameRepository;
    private final MemberService memberService;
    private final GameService gameService;

    public List<GameListResponse> findAll() {
        return gameListRepository.findAll().stream()
                .map(GameListResponse::from)
                .toList();
    }

    public GameListResponse get(Long id) {
        return GameListResponse.from(getEntity(id));
    }

    private GameList getEntity(Long id) {
        return gameListRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게임 리스트를 찾을 수 없습니다."));
    }

    public GameList getMine(String loginId) {
        Member member = memberService.getEntityByLoginId(loginId);
        return gameListRepository.findByOwner(member).stream().findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "내 게임 리스트를 찾을 수 없습니다."));
    }

    @Transactional
    public GameResponse addGame(String loginId, Long gameId) {
        GameList list = getMine(loginId);
        Game game = gameService.getEntity(gameId);
        if (!listGameRepository.existsByGameListAndGame(list, game)) {
            listGameRepository.save(new ListGame(list, game));
        }
        return GameResponse.from(game);
    }

    @Transactional
    public void removeGame(String loginId, Long gameId) {
        listGameRepository.deleteByGameListAndGame(getMine(loginId), gameService.getEntity(gameId));
    }

    public List<Game> getGames(Long listId) {
        return listGameRepository.findByGameList(getEntity(listId)).stream()
                .map(ListGame::getGame)
                .toList();
    }

    public List<GameResponse> getGames(Long listId, Integer players, String category,
                                       Integer maxPlayTime, String keyword) {
        return gameService.filter(getGames(listId), players, category, maxPlayTime, keyword);
    }

    public GameResponse pickGame(Long listId, Integer players, String category,
                                 Integer maxPlayTime, String keyword) {
        return gameService.pick(getGames(listId), players, category, maxPlayTime, keyword);
    }
}
