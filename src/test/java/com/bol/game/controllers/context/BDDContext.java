package com.bol.game.controllers.context;

import com.bol.game.controllers.GameController;
import com.bol.game.exceptions.InsufficientPlayersException;
import com.bol.game.pojos.Game;
import com.bol.game.pojos.Movement;
import com.bol.game.pojos.Player;
import com.bol.game.repositories.GameRepository;
import com.bol.game.repositories.PlayerRepository;
import com.bol.game.services.GameLobbyService;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BDDContext {

    public static final String FIRST_PLAYER_ID = "firstPlayerId";
    public static final String SECOND_PLAYER_ID = "secondPlayerId";
    public static final String SECOND_PLAYER_NAME = "secondPlayer";
    public static final String FIRST_PLAYER_NAME = "firstPlayer";
    public static final String GAME_ID = "gameId";

    private final SimpMessagingTemplate simpMessagingTemplate;
    private PlayerRepository playerRepository;
    private GameLobbyService gameLobbyService;
    private GameController gameController;
    private GameRepository gameRepository;

    Given given;
    When when;
    Then then;

    public BDDContext(PlayerRepository playerRepository, GameLobbyService gameLobbyService,
                      GameController gameController, GameRepository gameRepository, SimpMessagingTemplate simpMessagingTemplate) {
        this.playerRepository = playerRepository;
        this.gameLobbyService = gameLobbyService;
        this.gameController = gameController;
        this.gameRepository = gameRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
        given = new Given();
        when = new When();
        then = new Then();
    }

    public Given getGiven() {
        return given;
    }

    public When getWhen() {
        return when;
    }

    public Then getThen() {
        return then;
    }

    public class Given {

        Game game;

        public void firstPlayerExists() {
            Player player = new Player(FIRST_PLAYER_ID, FIRST_PLAYER_NAME);
            when(playerRepository.findById(FIRST_PLAYER_ID)).thenReturn(Optional.of(player));
        }


        public void thereIsOnePlayerWaitingInLobby() {
            Player firstPlayer = new Player(FIRST_PLAYER_ID, FIRST_PLAYER_NAME);

            try {
                gameLobbyService.join(firstPlayer);
            } catch (InsufficientPlayersException ex) {
            }

            Player secondPlayer = new Player(SECOND_PLAYER_ID, SECOND_PLAYER_NAME);
            when(playerRepository.findById(SECOND_PLAYER_ID)).thenReturn(Optional.of(secondPlayer));
        }

        public Given aGameInProgress() {

            Player firstPlayer = new Player(FIRST_PLAYER_ID, FIRST_PLAYER_NAME);
            Player secondPlayer = new Player(SECOND_PLAYER_ID, SECOND_PLAYER_NAME);
            Game inProgressGame = new Game();
            inProgressGame.setId(GAME_ID);
            inProgressGame.setFirstPlayer(firstPlayer);
            inProgressGame.setSecondPlayer(secondPlayer);
            this.game = inProgressGame;

            when(gameRepository.findById(GAME_ID)).thenReturn(Optional.of(inProgressGame));
            when(gameRepository.save(any(Game.class))).thenAnswer(i -> i.getArguments()[0]);
            return this;

        }

        public Given withCurrentBoard(int[][] board) {

            game.getFirstPlayer().setPits(board[0]);
            game.getSecondPlayer().setPits(board[1]);
            return this;
        }

        public Given withPlayerTurn(String player) {
            game.setTurnOfWithId(player);
            return this;
        }

        public Game exists() {
            return game;
        }

    }

    public class When {

        public Game firstPlayerMakeMove(int pitId) {
            return playerMakeMove(pitId, FIRST_PLAYER_ID);
        }

        public Game secondPlayerMakeMove(int pitId) {
            return playerMakeMove(pitId, SECOND_PLAYER_ID);
        }

        public Game playerMakeMoveWithInexistGame() {
            Movement movement = new Movement("inexistentGame", FIRST_PLAYER_ID, 0);
            Game updatedGame = gameController.moveStones(movement);
            then.game = updatedGame;
            return updatedGame;
        }

        public Game playerMakeMove(int pitId, String playerId) {
            Movement movement = new Movement(GAME_ID, playerId, pitId);
            Game updatedGame = gameController.moveStones(movement);
            then.game = updatedGame;
            return updatedGame;
        }

        public Game secondPlayerRequestsToJoin() {
            return playerRequestsToJoin(SECOND_PLAYER_ID);
        }

        public Game firstPlayerRequestsToJoin() {
            return playerRequestsToJoin(FIRST_PLAYER_ID);
        }

        public Game playerRequestsToJoin(String playerId) {
            Game game = gameController.joinPlayer(playerId);
            then.game = game;
            return game;
        }

    }

    public class Then {

        Game game;

        public Then assertGameIsInitialized() {
            assertFirstPlayersPitsAre(new int[]{6, 6, 6, 6, 6, 6, 0});
            assertSecondPlayersPitsAre(new int[]{6, 6, 6, 6, 6, 6, 0});
            assertItsFirstPlayerTurn();
            assertGameIsNotOver();
            return this;
        }

        public Then assertFirstPlayersPitsAre(int[] pits) {
            assertThat(game.getFirstPlayer().getPits(), is(pits));
            return this;
        }

        public Then assertSecondPlayersPitsAre(int[] pits) {
            assertThat(game.getSecondPlayer().getPits(), is(pits));
            return this;
        }

        public Then assertFirstPlayerWasNotified() {
            assertPlayerWasNotified(FIRST_PLAYER_ID);
            return this;
        }

        public Then assertSecondPlayerWasNotified() {
            assertPlayerWasNotified(SECOND_PLAYER_ID);
            return this;
        }


        public Then assertPlayerWasNotified(String playerId) {
            verify(simpMessagingTemplate).convertAndSend("/queue/board"+playerId, game);
            return this;
        }

        public Then assertGameIsOver() {
            assertThat(game.isOver(), is(true));
            return this;
        }

        public Then assertGameIsNotOver() {
            assertThat(game.isOver(), is(false));
            return this;
        }

        public Then assertFirstPlayerWon() {
            assertThat(game.getWinner(), is(FIRST_PLAYER_ID));
            return this;
        }

        public Then assertSecondlayerWon() {
            assertThat(game.getWinner(), is(SECOND_PLAYER_ID));
            return this;
        }

        public Then and() {
            return this;
        }

        public Then assertItsFirstPlayerTurn() {
            assertItsPlayerTurn(FIRST_PLAYER_ID);
            return this;
        }

        public Then assertItsSecondPlayerTurn() {
            assertItsPlayerTurn(SECOND_PLAYER_ID);
            return this;
        }

        public Then assertItsPlayerTurn(String playerId) {
            assertThat(game.getTurnOfWithId(), is(playerId));
            return this;
        }

        public Then assertGameWasSaved() {
            verify(gameRepository, times(1)).save(game);
            return this;
        }

    }
}
