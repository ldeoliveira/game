package com.bol.game.services;

import com.bol.game.exceptions.InsufficientPlayersException;
import com.bol.game.pojos.Game;
import com.bol.game.pojos.Player;
import com.bol.game.repositories.GameRepository;
import com.bol.game.repositories.PlayerRepository;
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
        return lobby.getAndUpdate(gameLobby -> addPlayerToLobby(player, gameLobby));
    }

    private Game addPlayerToLobby(Player player, Game gameLobby) {

        if (gameLobby.getFirstPlayer() == null
                || gameLobby.getFirstPlayer().getId().equals(player.getId())) {
            gameLobby.setFirstPlayer(player);
            throw new InsufficientPlayersException();
        }

        if (gameLobby.getSecondPlayer() == null) {
            gameLobby.setSecondPlayer(player);
            gameLobby.setTurnOfWithId(gameLobby.getFirstPlayer().getId());
            gameLobby.getFirstPlayer().setPits(initRow());
            gameLobby.getSecondPlayer().setPits(initRow());
            gameRepository.save(gameLobby);
        }
        return new Game();
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
