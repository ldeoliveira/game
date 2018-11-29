package com.bol.game.services;

import com.bol.game.exceptions.InsufficientPlayersException;
import com.bol.game.pojos.Game;
import com.bol.game.pojos.Player;
import com.bol.game.repositories.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;


@Service
public class GameLobbyService {

    private static final int STONES_PER_PIT = 6;
    private static final int NUMBER_OF_PITS = 7;

    private final AtomicReference<Game> lobby;

    private final GameRepository gameRepository;

    @Autowired
    public GameLobbyService(GameRepository gameRepository) {
        this.lobby = new AtomicReference<>(new Game());
        this.gameRepository = gameRepository;
    }

    public Game join(Player player) {

        Game game = lobby.updateAndGet(gameLobby -> addPlayerToLobby(player, gameLobby));

        if (game.getSecondPlayer()!=null && game.getFirstPlayer()!=null) {
            game.setTurnOfWithId(game.getFirstPlayer().getId());
            game.getFirstPlayer().setPits(initRow());
            game.getSecondPlayer().setPits(initRow());
            return gameRepository.save(game);
        }

        throw new InsufficientPlayersException();
    }

    private Game addPlayerToLobby(Player player, Game gameLobby) {
        if (gameLobby.getFirstPlayer() == null
                || gameLobby.getFirstPlayer().getId().equals(player.getId())) {
            return new Game(player);
        } else if (gameLobby.getSecondPlayer() == null) {
            return new Game(gameLobby.getFirstPlayer(), player);
        }
        throw new IllegalStateException();
    }

    private int[] initRow() {
        int[] row = new int[NUMBER_OF_PITS];
        for (int i = 0; i < NUMBER_OF_PITS - 1; i++) {
            row[i] = STONES_PER_PIT;
        }
        row[NUMBER_OF_PITS - 1] = 0;
        return row;
    }


}
