package com.example.boardpick.game.service;

import com.example.boardpick.game.domain.Game;
import com.example.boardpick.game.domain.GameCategory;
import com.example.boardpick.game.dto.GameRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GameCategoryTest {
    private final GameService service = new GameService(null);
    private final List<Game> games = List.of(
            new Game("카탄", 3, 4, GameCategory.STRATEGY, 90, 2),
            new Game("도블", 2, 8, GameCategory.PARTY, 15, 1));

    @Test
    void filtersByCategory() {
        assertThat(service.filter(games, null, GameCategory.STRATEGY, null, null))
                .extracting(response -> response.name()).containsExactly("카탄");
        assertThat(service.filter(games, null, null, null, null)).hasSize(2);
    }

    @Test
    void searchesCategoryByKoreanLabelAndEnumName() {
        for (String keyword : List.of("전략", "strategy", "STRATEGY")) {
            assertThat(service.filter(games, null, null, null, keyword))
                    .extracting(response -> response.name()).containsExactly("카탄");
        }
    }

    @Test
    void requestAcceptsOnlyDefinedEnumValues() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        GameRequest request = mapper.readValue("""
                {"name":"카탄","minPlayer":3,"maxPlayer":4,"category":"STRATEGY",
                 "playTimeMinutes":90,"difficulty":2}
                """, GameRequest.class);
        assertThat(request.toEntity().getCategory()).isEqualTo(GameCategory.STRATEGY);
        assertThat(mapper.writeValueAsString(GameCategory.STRATEGY)).isEqualTo("\"STRATEGY\"");
        assertThatThrownBy(() -> mapper.readValue("\"UNKNOWN\"", GameCategory.class))
                .isInstanceOf(InvalidFormatException.class);
    }
}
