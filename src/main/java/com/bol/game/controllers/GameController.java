package com.bol.game.controllers;


import com.bol.game.exceptions.InexistentGameException;
import com.bol.game.exceptions.InexistentPlayerException;
import com.bol.game.exceptions.MovementNotAllowedException;
import com.bol.game.pojos.Game;
import com.bol.game.pojos.Movement;
import com.bol.game.pojos.Player;
import com.bol.game.repositories.GameRepository;
import com.bol.game.repositories.PlayerRepository;
import com.bol.game.services.GameEngineService;
import com.bol.game.services.GameLobbyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class GameController {

    public static final String GAME_PATH = "/game";

    private final GameLobbyService joinService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final GameRepository gameRepository;
    private final GameEngineService gameEngineService;
    private final PlayerRepository playerRepository;

    @Autowired
    public GameController(GameLobbyService joinService, SimpMessagingTemplate simpMessagingTemplate,
                          GameRepository gameRepository, GameEngineService gameEngineService,
                          PlayerRepository playerRepository) {
        this.joinService = joinService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.gameRepository = gameRepository;
        this.gameEngineService = gameEngineService;
        this.playerRepository = playerRepository;
    }


    @GetMapping(path = GAME_PATH + "/{playerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Game joinPlayer(@PathVariable(value = "playerId") String playerId) throws InexistentPlayerException {

        Player player = playerRepository.findById(playerId)
                .orElseThrow(InexistentPlayerException::new);

        Game newGame = joinService.join(player);
        simpMessagingTemplate.convertAndSend("/queue/board" + newGame.getFirstPlayer().getId(), newGame);
        return newGame;
    }

    @PostMapping(path = GAME_PATH, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Game moveStones(@RequestBody @Valid Movement movement) {

        Game game = gameRepository.findById(movement.getGameId())
                .orElseThrow(InexistentGameException::new);

        if (!movement.getPlayerId().equals(game.getTurnOfWithId())
                || game.isOver()) {
            throw new MovementNotAllowedException();
        }

        Game updatedGame = gameEngineService.performMovement(game, movement.getPitId());
        simpMessagingTemplate.convertAndSend("/queue/board" + updatedGame.getFirstPlayer().getId(), updatedGame);
        simpMessagingTemplate.convertAndSend("/queue/board" + updatedGame.getSecondPlayer().getId(), updatedGame);

        return updatedGame;
    }

}
