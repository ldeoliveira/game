package com.bol.game.services;

import com.bol.game.exceptions.InsufficientPlayersException;
import com.bol.game.pojos.Game;
import com.bol.game.pojos.Lobby;
import com.bol.game.pojos.Player;
import com.bol.game.repositories.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;


@Service
public class GameLobbyService {

    private static final int STONES_PER_PIT = 6;
    private static final int NUMBER_OF_PITS = 7;

    private AtomicReference<Lobby> lobby;

    private final GameRepository gameRepository;

    @Autowired
    public GameLobbyService(GameRepository gameRepository) {
        this.lobby = new AtomicReference<>(new Lobby());
        this.gameRepository = gameRepository;
    }

    public Game join(Player player) {

        Lobby updatedLobby = this.lobby.updateAndGet(lobby -> addPlayerToLobby(player, lobby));

        if (updatedLobby.hasInsufficientPlayers()) {
            throw new InsufficientPlayersException();
        }

        Game game = new Game(updatedLobby.getFirstPlayer(), updatedLobby.getSecondPlayer());
        game.setTurnOfWithId(game.getFirstPlayer().getId());
        game.getFirstPlayer().setPits(initRow());
        game.getSecondPlayer().setPits(initRow());
        return gameRepository.save(game);
    }

    private Lobby addPlayerToLobby(Player player, Lobby lobby) {

        if (lobby.isEmpty() || lobby.isFull()) {
            return new Lobby(player);
        } else {

            Player waitingPlayer = lobby.getWaitingPlayer();

            if (!waitingPlayer.getId().equals(player.getId())) {
                return new Lobby(waitingPlayer, player);
            } else {
                return new Lobby(player);
            }
        }

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
