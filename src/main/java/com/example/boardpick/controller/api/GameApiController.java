package com.example.boardpick.controller.api;

import com.example.boardpick.dto.GameForm;
import com.example.boardpick.entity.Game;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameApiController {

    private final GameService gameService;

    @GetMapping
    public List<Game> list() {
        return gameService.findGames();
    }

    @PostMapping
    public ResponseEntity<Game> create(@Valid @RequestBody GameForm form) {
        Game game = form.toEntity();
        gameService.save(game);
        return ResponseEntity.status(HttpStatus.CREATED).body(game);
    }

    @PutMapping("/{id}")
    public Game update(@PathVariable Long id, @Valid @RequestBody GameForm form) {
        Game target = gameService.findOne(id);
        if (target == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "게임을 찾을 수 없습니다.");
        }

        target.setName(form.getName());
        target.setMinPlayer(form.getMinPlayer());
        target.setMaxPlayer(form.getMaxPlayer());
        target.setCategory(form.getCategory());
        gameService.save(target);

        return target;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Game target = gameService.findOne(id);
        if (target == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "게임을 찾을 수 없습니다.");
        }

        gameService.delete(target);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pick")
    public Game pick() {
        List<Game> games = gameService.findGames();
        if (games.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "등록된 게임이 없습니다.");
        }

        return gameService.getRandGame();
    }
}
