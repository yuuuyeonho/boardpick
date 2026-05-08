package com.example.boardpick.controller.api;

import com.example.boardpick.dto.CollectionResponse;
import com.example.boardpick.dto.GameForm;
import com.example.boardpick.dto.GameResponse;
import com.example.boardpick.entity.Game;
import com.example.boardpick.entity.GameCollection;
import com.example.boardpick.repository.GameCollectionRepository;
import com.example.boardpick.entity.User;
import com.example.boardpick.repository.UserRepository;
import com.example.boardpick.service.GameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameApiController {

    private final GameService gameService;
    private final UserRepository userRepository;
    private final GameCollectionRepository gameCollectionRepository;

    @GetMapping
    public List<GameResponse> list(
            @RequestParam(required = false) Integer players,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer maxPlayTime,
            @RequestParam(required = false) String keyword
    ) {
        return gameService.searchGames(players, category, maxPlayTime, keyword).stream()
                .map(GameResponse::from)
                .toList();
    }

    @PostMapping
    public ResponseEntity<GameResponse> create(@Valid @RequestBody GameForm form, Principal principal) {
        verifyAdmin(principal);
        Game game = form.toEntity();
        gameService.save(game);
        return ResponseEntity.status(HttpStatus.CREATED).body(GameResponse.from(game));
    }

    @PutMapping("/{id}")
    public GameResponse update(@PathVariable Long id, @Valid @RequestBody GameForm form, Principal principal) {
        verifyAdmin(principal);
        Game target = gameService.findOne(id);
        if (target == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "게임을 찾을 수 없습니다.");
        }

        target.setName(form.getName());
        target.setMinPlayer(form.getMinPlayer());
        target.setMaxPlayer(form.getMaxPlayer());
        target.setCategory(form.getCategory());
        target.setPlayTimeMinutes(form.getPlayTimeMinutes());
        target.setDifficulty(form.getDifficulty());
        target.setDescription(form.getDescription());
        gameService.save(target);

        return GameResponse.from(target);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Principal principal) {
        verifyAdmin(principal);
        Game target = gameService.findOne(id);
        if (target == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "게임을 찾을 수 없습니다.");
        }

        gameService.delete(target);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pick")
    public GameResponse pick(
            @RequestParam(required = false) Integer players,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer maxPlayTime,
            @RequestParam(required = false) String keyword
    ) {
        Game picked = gameService.pickGame(gameService.findGames(), players, category, maxPlayTime, keyword);
        if (picked == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "조건에 맞는 게임이 없습니다.");
        }

        return GameResponse.from(picked);
    }

    @GetMapping("/collections")
    public List<CollectionResponse> collections() {
        return gameCollectionRepository.findAll().stream()
                .map(CollectionResponse::from)
                .toList();
    }

    @GetMapping("/collections/{slug}")
    public CollectionResponse collection(@PathVariable String slug) {
        return CollectionResponse.from(findCollection(slug));
    }

    @GetMapping("/collections/{slug}/games")
    public List<GameResponse> collectionGames(
            @PathVariable String slug,
            @RequestParam(required = false) Integer players,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer maxPlayTime,
            @RequestParam(required = false) String keyword
    ) {
        GameCollection collection = findCollection(slug);
        return gameService.filterGames(collection.getGames(), players, category, maxPlayTime, keyword).stream()
                .map(GameResponse::from)
                .toList();
    }

    @GetMapping("/collections/{slug}/pick")
    public GameResponse collectionPick(
            @PathVariable String slug,
            @RequestParam(required = false) Integer players,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer maxPlayTime,
            @RequestParam(required = false) String keyword
    ) {
        GameCollection collection = findCollection(slug);
        Game picked = gameService.pickGame(collection.getGames(), players, category, maxPlayTime, keyword);
        if (picked == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "조건에 맞는 게임이 없습니다.");
        }

        return GameResponse.from(picked);
    }

    @PostMapping("/collections/me/games/{gameId}")
    public GameResponse addGameToMyCollection(@PathVariable Long gameId, Principal principal) {
        GameCollection collection = getPrimaryCollection(principal);
        Game game = gameService.findOne(gameId);
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "게임을 찾을 수 없습니다.");
        }

        boolean alreadyAdded = collection.getGames().stream()
                .anyMatch(collectionGame -> collectionGame.getId().equals(game.getId()));
        if (!alreadyAdded) {
            collection.getGames().add(game);
            gameCollectionRepository.save(collection);
        }

        return GameResponse.from(game);
    }

    @DeleteMapping("/collections/me/games/{gameId}")
    public ResponseEntity<Void> removeGameFromMyCollection(@PathVariable Long gameId, Principal principal) {
        GameCollection collection = getPrimaryCollection(principal);
        Game game = gameService.findOne(gameId);
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "게임을 찾을 수 없습니다.");
        }

        collection.getGames().removeIf(collectionGame -> collectionGame.getId().equals(game.getId()));
        gameCollectionRepository.save(collection);

        return ResponseEntity.noContent().build();
    }

    private User getCurrentUser(Principal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "계정을 찾을 수 없습니다."));
    }

    private GameCollection getPrimaryCollection(Principal principal) {
        User user = getCurrentUser(principal);
        return gameCollectionRepository.findByOwner(user).stream()
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "내 게임 목록을 찾을 수 없습니다."));
    }

    private GameCollection findCollection(String slug) {
        return gameCollectionRepository.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게임 목록을 찾을 수 없습니다."));
    }

    private void verifyAdmin(Principal principal) {
        if (principal == null || !"admin".equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "관리자만 DB 게임을 관리할 수 있습니다.");
        }
    }
}
