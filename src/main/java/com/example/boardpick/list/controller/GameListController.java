package com.example.boardpick.list.controller;

import com.example.boardpick.game.dto.GameResponse;
import com.example.boardpick.list.dto.GameListResponse;
import com.example.boardpick.list.service.GameListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/lists")
@RequiredArgsConstructor
@Tag(name = "Game Lists", description = "회원 게임 리스트 조회, 추천 및 내 리스트 관리 API")
public class GameListController {

    private final GameListService gameListService;

    @GetMapping
    @Operation(summary = "게임 리스트 전체 조회", description = "등록된 게임 리스트를 모두 조회합니다.")
    public List<GameListResponse> findAll() {
        return gameListService.findAll();
    }

    @GetMapping("/{listId}")
    @Operation(summary = "게임 리스트 단건 조회", description = "리스트 ID로 게임 리스트 정보를 조회합니다.")
    public GameListResponse find(
            @Parameter(description = "게임 리스트 ID", required = true) @PathVariable Long listId) {
        return gameListService.get(listId);
    }

    @GetMapping("/{listId}/games")
    @Operation(summary = "리스트의 게임 조회", description = "지정한 리스트에 담긴 게임을 조건에 따라 필터링합니다.")
    public List<GameResponse> games(
            @Parameter(description = "게임 리스트 ID", required = true) @PathVariable Long listId,
            @Parameter(description = "플레이 인원수") @RequestParam(required = false) Integer players,
            @Parameter(description = "게임 카테고리") @RequestParam(required = false) String category,
            @Parameter(description = "최대 플레이 시간(분)") @RequestParam(required = false) Integer maxPlayTime,
            @Parameter(description = "게임명 또는 카테고리 검색어") @RequestParam(required = false) String keyword) {
        return gameListService.getGames(listId, players, category, maxPlayTime, keyword);
    }

    @GetMapping("/{listId}/pick")
    @Operation(summary = "리스트에서 무작위 추천", description = "지정한 리스트에 담긴 게임 중 조건에 맞는 하나를 무작위로 추천합니다.")
    public GameResponse pick(
            @Parameter(description = "게임 리스트 ID", required = true) @PathVariable Long listId,
            @Parameter(description = "플레이 인원수") @RequestParam(required = false) Integer players,
            @Parameter(description = "게임 카테고리") @RequestParam(required = false) String category,
            @Parameter(description = "최대 플레이 시간(분)") @RequestParam(required = false) Integer maxPlayTime,
            @Parameter(description = "게임명 또는 카테고리 검색어") @RequestParam(required = false) String keyword) {
        return gameListService.pickGame(listId, players, category, maxPlayTime, keyword);
    }

    @PostMapping("/me/games/{gameId}")
    @Operation(summary = "내 리스트에 게임 추가", description = "로그인한 회원의 개인 리스트에 게임을 추가합니다.")
    public GameResponse add(@Parameter(description = "추가할 게임 ID", required = true) @PathVariable Long gameId,
                            @Parameter(hidden = true) Principal principal) {
        return gameListService.addGame(principal.getName(), gameId);
    }

    @DeleteMapping("/me/games/{gameId}")
    @Operation(summary = "내 리스트에서 게임 제거", description = "로그인한 회원의 개인 리스트에서 게임을 제거합니다.")
    public ResponseEntity<Void> remove(
            @Parameter(description = "제거할 게임 ID", required = true) @PathVariable Long gameId,
            @Parameter(hidden = true) Principal principal) {
        gameListService.removeGame(principal.getName(), gameId);
        return ResponseEntity.noContent().build();
    }
}
