package com.example.boardpick.controller;

import com.example.boardpick.entity.Game;
import com.example.boardpick.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {

    private final GameService gameService;

    @GetMapping("/boardpick")
    public String home(Model model){
        log.info("home controller");
        List<Game> games = gameService.findGames();
        model.addAttribute("games", games);
        return "home";
    }

}
