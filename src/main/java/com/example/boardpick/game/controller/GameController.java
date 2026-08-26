package com.example.boardpick.game.controller;

import com.example.boardpick.game.dto.GameRequest;
import com.example.boardpick.game.dto.GameResponse;
import com.example.boardpick.game.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
@Tag(name = "Games", description = "전체 게임 DB 조회, 추천 및 관리자용 게임 관리 API")
public class GameController {

    private final GameService gameService;

    @GetMapping
    @Operation(summary = "전체 게임 조회", description = "DB에 등록된 전체 게임을 조건에 따라 필터링합니다.")
    public List<GameResponse> findAll(
            @Parameter(description = "플레이 인원수") @RequestParam(required = false) Integer players,
            @Parameter(description = "게임 카테고리") @RequestParam(required = false) String category,
            @Parameter(description = "최대 플레이 시간(분)") @RequestParam(required = false) Integer maxPlayTime,
            @Parameter(description = "게임명 또는 카테고리 검색어") @RequestParam(required = false) String keyword) {
        return gameService.search(players, category, maxPlayTime, keyword);
    }

    @GetMapping("/{gameId}")
    @Operation(summary = "게임 단건 조회", description = "게임 ID로 게임 정보를 조회합니다.")
    public GameResponse find(@Parameter(description = "게임 ID", required = true) @PathVariable Long gameId) {
        return gameService.get(gameId);
    }

    @GetMapping("/pick")
    @Operation(summary = "전체 게임 중 무작위 추천", description = "DB 전체 게임에서 조건에 맞는 게임 하나를 무작위로 추천합니다.")
    public GameResponse pick(
            @Parameter(description = "플레이 인원수") @RequestParam(required = false) Integer players,
            @Parameter(description = "게임 카테고리") @RequestParam(required = false) String category,
            @Parameter(description = "최대 플레이 시간(분)") @RequestParam(required = false) Integer maxPlayTime,
            @Parameter(description = "게임명 또는 카테고리 검색어") @RequestParam(required = false) String keyword) {
        return gameService.pick(players, category, maxPlayTime, keyword);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "게임 등록", description = "새 게임을 DB에 등록합니다. 관리자 권한이 필요합니다.")
    public ResponseEntity<GameResponse> create(@Valid @RequestBody GameRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gameService.create(request));
    }

    @PutMapping("/{gameId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "게임 수정", description = "게임 정보를 수정합니다. 관리자 권한이 필요합니다.")
    public GameResponse update(@Parameter(description = "게임 ID", required = true) @PathVariable Long gameId,
                               @Valid @RequestBody GameRequest request) {
        return gameService.update(gameId, request);
    }

    @DeleteMapping("/{gameId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "게임 삭제", description = "게임을 DB에서 삭제합니다. 관리자 권한이 필요합니다.")
    public ResponseEntity<Void> delete(
            @Parameter(description = "게임 ID", required = true) @PathVariable Long gameId) {
        gameService.delete(gameId);
        return ResponseEntity.noContent().build();
    }
}
