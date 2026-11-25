-- 보드게임 목록과 랜덤 추천 테스트용 PostgreSQL 데이터 30개
-- 실행 전: Hibernate가 현재 Game 엔티티에 맞는 game 테이블을 생성해야 합니다.
-- 실행 예: psql -X -v ON_ERROR_STOP=1 -h localhost -U <사용자> -d <DB명> -f scripts/sql/seed-games.sql
-- 게임 정보는 필터 테스트용 예시이며, 판본별 공식 규칙과 다를 수 있습니다.
-- play_time은 분 단위입니다. 음수 ID로 등록해 game_seq를 사용하지 않습니다.
-- 같은 ID 또는 이름이 이미 있으면 건너뜁니다. 기존 데이터는 변경하지 않습니다.
-- 개인별 컬렉션(game_list, list_game)은 생성하지 않습니다.

WITH seed (id, name, min_player, max_player, category, play_time) AS (
    VALUES
        (-1, '카탄', 3, 4, 'STRATEGY', 90),
        (-2, '카르카손', 2, 5, 'STRATEGY', 45),
        (-3, '티켓 투 라이드', 2, 5, 'FAMILY', 60),
        (-4, '스플렌더', 2, 4, 'STRATEGY', 30),
        (-5, '아줄', 2, 4, 'ABSTRACT_STRATEGY', 40),
        (-6, '루미큐브', 2, 4, 'FAMILY', 30),
        (-7, '할리갈리', 2, 6, 'DEXTERITY', 15),
        (-8, '도블', 2, 8, 'DEXTERITY', 15),
        (-9, '딕싯', 3, 6, 'PARTY', 30),
        (-10, '코드네임', 4, 8, 'PARTY', 15),
        (-11, '텔레스트레이션', 4, 8, 'PARTY', 30),
        (-12, '러브레터', 2, 4, 'DEDUCTION', 20),
        (-13, '쿠', 2, 6, 'BLUFFING', 15),
        (-14, '레지스탕스 아발론', 5, 10, 'BLUFFING', 30),
        (-15, '뱅', 4, 7, 'BLUFFING', 40),
        (-16, '스컬', 3, 6, 'BLUFFING', 20),
        (-17, '팬데믹', 2, 4, 'COOPERATIVE', 45),
        (-18, '하나비', 2, 5, 'COOPERATIVE', 25),
        (-19, '더 마인드', 2, 4, 'COOPERATIVE', 20),
        (-20, '스피릿 아일랜드', 1, 4, 'COOPERATIVE', 120),
        (-21, '세븐 원더스', 3, 7, 'STRATEGY', 30),
        (-22, '세븐 원더스 듀얼', 2, 2, 'COMPETITIVE', 30),
        (-23, '패치워크', 2, 2, 'ABSTRACT_STRATEGY', 30),
        (-24, '쿼리도', 2, 4, 'ABSTRACT_STRATEGY', 15),
        (-25, '윙스팬', 1, 5, 'STRATEGY', 70),
        (-26, '테라포밍 마스', 1, 5, 'STRATEGY', 120),
        (-27, '아그리콜라', 1, 4, 'STRATEGY', 120),
        (-28, '브라스 버밍엄', 2, 4, 'STRATEGY', 120),
        (-29, '클루', 3, 6, 'DEDUCTION', 45),
        (-30, '캐스캐디아', 1, 4, 'FAMILY', 45)
)
INSERT INTO game
    (id, name, min_player, max_player, category, play_time, created_at, updated_at)
SELECT
    seed.id, seed.name, seed.min_player, seed.max_player, seed.category,
    seed.play_time, LOCALTIMESTAMP(6), LOCALTIMESTAMP(6)
FROM seed
WHERE NOT EXISTS (
    SELECT 1
    FROM game AS existing
    WHERE existing.id = seed.id OR existing.name = seed.name
)
ON CONFLICT (id) DO NOTHING;

-- 전체 목록과 카테고리별 데이터 분포 확인
SELECT id, name, min_player, max_player, category, play_time
FROM game
ORDER BY name;

SELECT category, COUNT(*) AS game_count
FROM game
GROUP BY category
ORDER BY category;
