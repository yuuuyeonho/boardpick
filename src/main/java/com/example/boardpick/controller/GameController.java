package com.example.boardpick.controller;

import com.example.boardpick.dto.GameForm;
import com.example.boardpick.entity.Game;
import com.example.boardpick.service.GameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @GetMapping("/games/new")
    public String newForm(Model model){
        model.addAttribute("form", new GameForm());
        return "games/cre  ateGameForm";
    }

    @PostMapping("/games/new")
    public String create(@Valid GameForm form, BindingResult result){
        if(result.hasErrors()){
            return "games/createGameForm";
        }

        Game game = form.toEntity();
        log.info(game.toString());
        gameService.save(game);

        return "redirect:/games";
    }

    @GetMapping("/games")
    public String list(Model model){
        List<Game> games = gameService.findGames();
        model.addAttribute("games", games);
        return "games/list";
    }

    @GetMapping("/games/{id}/edit")
    public String updateGameForm(@PathVariable Long id, Model model){
        Game game = gameService.findOne(id);

        GameForm gameForm = new GameForm();
        gameForm.setId(game.getId());
        gameForm.setName(game.getName());
        gameForm.setMinPlayer(game.getMinPlayer());
        gameForm.setMaxPlayer(game.getMaxPlayer());
        gameForm.setCategory(game.getCategory());
        log.info(game.toString());

        model.addAttribute("gameForm", gameForm);

        return "games/updateGameForm";
    }

    @PostMapping("/games/edit")
    public String updateGame(@ModelAttribute("gameForm") GameForm gameForm){

        Game game = gameForm.updateToEntity();
        Game target = gameService.findOne(game.getId());
        if(target!=null){
            gameService.save(game);
        }

        return "redirect:/games";
    }

    @GetMapping("/games/{id}/delete")
    public String delete(@PathVariable Long id) {
        Game game = gameService.findOne(id);
        if(game!=null){
            gameService.delete(game);
        }
        return "redirect:/games";
    }

    @GetMapping("/games/pick")
    public String gamePick(Model model){
        Game game = gameService.getRandGame();

        GameForm gameForm = new GameForm();
        gameForm.setId(game.getId());
        gameForm.setName(game.getName());
        gameForm.setMinPlayer(game.getMinPlayer());
        gameForm.setMaxPlayer(game.getMaxPlayer());
        gameForm.setCategory(game.getCategory());
        log.info(game.toString());

        model.addAttribute("game", gameForm);
        return "games/pick";
    }

    /*@PostMapping("/delete")
    public String delete(@RequestParam List<String> gameIds) {
        log.info("들어옴");
        for(int i = 0; i < gameIds.size(); i++) {
            Long id = Long.valueOf(gameIds.get(i));
            gameService.deleteById(id);
        }

        return "redirect:/games";
    }*/
}
