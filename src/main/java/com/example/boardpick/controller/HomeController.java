package com.example.boardpick.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class HomeController {

    @GetMapping("/boardpick")
    public String home(){
        log.info("home controller");
        return "home";
    }
}
