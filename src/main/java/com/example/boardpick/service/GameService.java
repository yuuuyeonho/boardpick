package com.example.boardpick.service;

import com.example.boardpick.entity.Game;
import com.example.boardpick.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;

    public void save(Game game){gameRepository.save(game);}

    public List<Game> findGames(){ return gameRepository.findAll(); }

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
}
