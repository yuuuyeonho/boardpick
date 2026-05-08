package com.example.boardpick.service;

import com.example.boardpick.entity.Game;
import com.example.boardpick.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;

    public void save(Game game){gameRepository.save(game);}

    public List<Game> findGames(){ return gameRepository.findAll(); }

    public List<Game> searchGames(Integer players, String category, Integer maxPlayTime, String keyword) {
        return filterGames(gameRepository.findAll(), players, category, maxPlayTime, keyword);
    }

    public List<Game> filterGames(List<Game> games, Integer players, String category, Integer maxPlayTime, String keyword) {
        return games.stream()
                .filter(game -> players == null || (game.getMinPlayer() <= players && game.getMaxPlayer() >= players))
                .filter(game -> category == null || category.isBlank()
                        || safeLower(game.getCategory()).equals(safeLower(category)))
                .filter(game -> maxPlayTime == null || game.getPlayTimeMinutes() == 0 || game.getPlayTimeMinutes() <= maxPlayTime)
                .filter(game -> keyword == null || keyword.isBlank()
                        || safeLower(game.getName()).contains(safeLower(keyword))
                        || safeLower(game.getCategory()).contains(safeLower(keyword)))
                .collect(Collectors.toList());
    }

    public Game findOne(Long id){ return gameRepository.findById(id).orElse(null); }

    public void delete(Game game){ gameRepository.delete(game); }

    public void deleteById(Long id){ gameRepository.deleteById(id); }

    @Transactional
    public Game getRandGame(){
        List<Game> games = gameRepository.findAll();
        Random rand = new Random();

        Game randGame = games.get(rand.nextInt(games.size()));
        return randGame;
    }

    @Transactional(readOnly = true)
    public Game pickGame(List<Game> source, Integer players, String category, Integer maxPlayTime, String keyword) {
        List<Game> games = filterGames(source, players, category, maxPlayTime, keyword);
        if (games.isEmpty()) {
            return null;
        }

        return games.get(new Random().nextInt(games.size()));
    }

    private String safeLower(String value) {
        return value == null ? "" : value.toLowerCase();
    }
}
