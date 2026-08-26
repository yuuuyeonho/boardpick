package com.example.boardpick.game.service;

import com.example.boardpick.game.domain.Game;
import com.example.boardpick.game.dto.GameRequest;
import com.example.boardpick.game.dto.GameResponse;
import com.example.boardpick.game.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameService {

    private final GameRepository gameRepository;

    @Transactional
    public GameResponse create(GameRequest request) {
        return GameResponse.from(gameRepository.save(request.toEntity()));
    }

    @Transactional
    public GameResponse update(Long gameId, GameRequest request) {
        Game game = getEntity(gameId);
        game.update(request.name(), request.minPlayer(), request.maxPlayer(), request.category(),
                request.playTimeMinutes(), request.difficulty());
        return GameResponse.from(game);
    }

    @Transactional
    public void delete(Long gameId) {
        gameRepository.delete(getEntity(gameId));
    }

    public GameResponse get(Long gameId) {
        return GameResponse.from(getEntity(gameId));
    }

    public Game getEntity(Long gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게임을 찾을 수 없습니다."));
    }

    public List<GameResponse> search(Integer players, String category, Integer maxPlayTime, String keyword) {
        return filterEntities(gameRepository.findAll(), players, category, maxPlayTime, keyword).stream()
                .map(GameResponse::from)
                .toList();
    }

    public List<GameResponse> filter(List<Game> games, Integer players, String category, Integer maxPlayTime, String keyword) {
        return filterEntities(games, players, category, maxPlayTime, keyword).stream()
                .map(GameResponse::from)
                .toList();
    }

    private List<Game> filterEntities(List<Game> games, Integer players, String category,
                                      Integer maxPlayTime, String keyword) {
        return games.stream()
                .filter(game -> players == null || game.getMinPlayer() <= players && players <= game.getMaxPlayer())
                .filter(game -> isBlank(category) || lower(game.getCategory()).equals(lower(category)))
                .filter(game -> maxPlayTime == null || game.getPlayTime() <= maxPlayTime)
                .filter(game -> isBlank(keyword) || lower(game.getName()).contains(lower(keyword))
                        || lower(game.getCategory()).contains(lower(keyword)))
                .toList();
    }

    public GameResponse pick(List<Game> source, Integer players, String category, Integer maxPlayTime, String keyword) {
        List<Game> candidates = filterEntities(source, players, category, maxPlayTime, keyword);
        if (candidates.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "조건에 맞는 게임이 없습니다.");
        }
        return GameResponse.from(candidates.get(ThreadLocalRandom.current().nextInt(candidates.size())));
    }

    public GameResponse pick(Integer players, String category, Integer maxPlayTime, String keyword) {
        return pick(gameRepository.findAll(), players, category, maxPlayTime, keyword);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String lower(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT);
    }
}
