package com.example.tictao.controllers;

import com.example.tictao.services.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/play/game")
    public ResponseEntity<String> runGame(@RequestParam("board") String board){
        return gameService.run(board);
    }
}
