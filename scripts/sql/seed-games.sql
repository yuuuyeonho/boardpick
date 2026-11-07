-- 보드게임 목록 / 랜덤 추천 테스트용 MySQL 더미 데이터 30개
-- 실행 전: 애플리케이션을 한 번 실행하여 Hibernate가 game 테이블을 생성해야 합니다.
-- 실행 예: mysql --default-character-set=utf8mb4 -h localhost -u <사용자> -p <DB명> < scripts/sql/seed-games.sql
-- 게임 정보는 필터 테스트용 예시이며, 판본별 공식 규칙과 다를 수 있습니다.
-- play_time: 분, difficulty: 1(쉬움) ~ 5(어려움)
-- 음수 ID를 사용하여 Hibernate의 양수 ID 생성 및 game_seq에 영향을 주지 않습니다.
-- 같은 ID 또는 이름이 이미 있으면 건너뜁니다. 기존 데이터는 변경하지 않습니다.
-- 개인별 컬렉션(game_list, list_game)은 생성하지 않습니다.

SET NAMES utf8mb4;

CREATE TEMPORARY TABLE boardpick_seed_games (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    min_player INT NOT NULL,
    max_player INT NOT NULL,
    category VARCHAR(255) NOT NULL,
    play_time INT NOT NULL,
    difficulty INT NOT NULL
);

INSERT INTO boardpick_seed_games
    (id, name, min_player, max_player, category, play_time, difficulty)
VALUES
    (-1, '카탄', 3, 4, 'STRATEGY', 90, 2),
    (-2, '카르카손', 2, 5, 'STRATEGY', 45, 2),
    (-3, '티켓 투 라이드', 2, 5, 'FAMILY', 60, 2),
    (-4, '스플렌더', 2, 4, 'STRATEGY', 30, 2),
    (-5, '아줄', 2, 4, 'ABSTRACT_STRATEGY', 40, 2),
    (-6, '루미큐브', 2, 4, 'FAMILY', 30, 1),
    (-7, '할리갈리', 2, 6, 'PARTY', 15, 1),
    (-8, '도블', 2, 8, 'PARTY', 15, 1),
    (-9, '딕싯', 3, 6, 'PARTY', 30, 1),
    (-10, '코드네임', 4, 8, 'PARTY', 15, 2),
    (-11, '텔레스트레이션', 4, 8, 'PARTY', 30, 1),
    (-12, '러브레터', 2, 4, 'DEDUCTION', 20, 1),
    (-13, '쿠', 2, 6, 'BLUFFING', 15, 2),
    (-14, '레지스탕스 아발론', 5, 10, 'BLUFFING', 30, 2),
    (-15, '뱅', 4, 7, 'BLUFFING', 40, 2),
    (-16, '스컬', 3, 6, 'BLUFFING', 20, 1),
    (-17, '팬데믹', 2, 4, 'COOPERATIVE', 45, 3),
    (-18, '하나비', 2, 5, 'COOPERATIVE', 25, 2),
    (-19, '더 마인드', 2, 4, 'COOPERATIVE', 20, 1),
    (-20, '스피릿 아일랜드', 1, 4, 'COOPERATIVE', 120, 5),
    (-21, '세븐 원더스', 3, 7, 'STRATEGY', 30, 3),
    (-22, '세븐 원더스 듀얼', 2, 2, 'STRATEGY', 30, 3),
    (-23, '패치워크', 2, 2, 'ABSTRACT_STRATEGY', 30, 2),
    (-24, '쿼리도', 2, 4, 'ABSTRACT_STRATEGY', 15, 2),
    (-25, '윙스팬', 1, 5, 'STRATEGY', 70, 3),
    (-26, '테라포밍 마스', 1, 5, 'STRATEGY', 120, 4),
    (-27, '아그리콜라', 1, 4, 'STRATEGY', 120, 4),
    (-28, '브라스 버밍엄', 2, 4, 'STRATEGY', 120, 5),
    (-29, '클루', 3, 6, 'DEDUCTION', 45, 2),
    (-30, '캐스캐디아', 1, 4, 'FAMILY', 45, 2);

START TRANSACTION;

INSERT INTO game
    (id, name, min_player, max_player, category, play_time, difficulty, created_at, updated_at)
SELECT
    seed.id, seed.name, seed.min_player, seed.max_player, seed.category,
    seed.play_time, seed.difficulty, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)
FROM boardpick_seed_games AS seed
WHERE NOT EXISTS (
    SELECT 1
    FROM game AS existing
    WHERE existing.id = seed.id OR existing.name = seed.name
);

COMMIT;

DROP TEMPORARY TABLE boardpick_seed_games;

-- 확인: 전체 목록 및 카테고리별 필터용 데이터 분포
SELECT id, name, min_player, max_player, category, play_time, difficulty
FROM game
ORDER BY name;

SELECT category, COUNT(*) AS game_count
FROM game
GROUP BY category
ORDER BY category;
