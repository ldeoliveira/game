package com.bol.game.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/game-ui")
    public String getIndex() {
        return "index";
    }
}
