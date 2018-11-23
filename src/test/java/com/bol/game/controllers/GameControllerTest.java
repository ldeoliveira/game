package com.bol.game.controllers;

import com.bol.game.controllers.context.BDDContext;
import com.bol.game.exceptions.IllegalMovementException;
import com.bol.game.exceptions.InexistentGameException;
import com.bol.game.exceptions.InexistentPlayerException;
import com.bol.game.exceptions.InsufficientPlayersException;
import com.bol.game.exceptions.MovementNotAllowedException;
import com.bol.game.repositories.GameRepository;
import com.bol.game.repositories.PlayerRepository;
import com.bol.game.services.GameEngineService;
import com.bol.game.services.GameLobbyService;
import com.bol.game.services.components.CaptureComponent;
import com.bol.game.services.components.SowComponent;
import com.bol.game.services.components.TurnControlComponent;
import com.bol.game.services.components.VictoryComponent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static com.bol.game.controllers.context.BDDContext.FIRST_PLAYER_ID;
import static com.bol.game.controllers.context.BDDContext.SECOND_PLAYER_ID;

@RunWith(MockitoJUnitRunner.class)
public class GameControllerTest {

    private BDDContext.Given given;
    private BDDContext.When when;
    private BDDContext.Then then;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Before
    public void setup() {

        GameEngineService gameEngineService = new GameEngineService(gameRepository, new SowComponent(),
                new VictoryComponent(), new CaptureComponent(),
                new TurnControlComponent());

        GameLobbyService gameLobbyService = new GameLobbyService(gameRepository);

        GameController gameController = new GameController(gameLobbyService, simpMessagingTemplate,
                gameRepository, gameEngineService, playerRepository);


        BDDContext context = new BDDContext(playerRepository, gameLobbyService,
                gameController, gameRepository, simpMessagingTemplate);

        when = context.getWhen();
        given = context.getGiven();
        then = context.getThen();

    }

    @Test(expected = InsufficientPlayersException.class)
    public void testJoinPlayer_playerShouldWaitForOpponent() throws Exception {

        given.firstPlayerExists();

        when.firstPlayerRequestsToJoin();

        // InsufficientPlayersException should be thrown
    }

    @Test
    public void testJoinPlayer_thereIsOnePlayerWaiting() throws Exception {

        given.thereIsOnePlayerWaitingInLobby();

        when.secondPlayerRequestsToJoin();

        then.assertGameIsInitialized()
                .and().assertGameWasSaved()
                .and().assertFirstPlayerWasNotified();
    }

    @Test
    public void testGameRules_playerShouldGetFreeTurn_ifStoneEndsInBigPit() throws Exception {

        given.aGameInProgress()
                .withCurrentBoard(new int[][]{{6, 6, 6, 6, 6, 6, 0}, {6, 6, 6, 6, 6, 6, 0}})
                .withPlayerTurn(FIRST_PLAYER_ID)
                .exists();


        when.firstPlayerMakeMove(0);

        then.assertFirstPlayersPitsAre(0, 7, 7, 7, 7, 7, 1)
                .and().assertSecondPlayersPitsAre(6, 6, 6, 6, 6, 6, 0)
                .and().assertItsFirstPlayerTurn()
                .and().assertGameIsNotOver()
                .and().assertGameWasSaved()
                .and().assertFirstPlayerWasNotified()
                .and().assertSecondPlayerWasNotified();

    }

    @Test
    public void testGameRules_playerShouldCaptureStones_ifLastPitWasEmpty() throws Exception {

        given.aGameInProgress()
                .withCurrentBoard(new int[][]{{1,0,8,8,8,8,2}, {0,8,7,7,7,7,1}})
                .withPlayerTurn(FIRST_PLAYER_ID)
                .exists();

        when.firstPlayerMakeMove(0);

        then.assertFirstPlayersPitsAre(0, 0, 8, 8, 8, 8, 10)
                .and().assertSecondPlayersPitsAre(0, 8, 7, 7, 0, 7, 1)
                .and().assertGameIsNotOver()
                .and().assertItsSecondPlayerTurn()
                .and().assertGameWasSaved()
                .and().assertFirstPlayerWasNotified()
                .and().assertSecondPlayerWasNotified();
    }

    @Test
    public void testGameRules_distributeStones_BasicTest() throws Exception {

        given.aGameInProgress()
                .withCurrentBoard(new int[][]{{6, 6, 6, 6, 6, 6, 0}, {6, 6, 6, 6, 6, 6, 0}})
                .withPlayerTurn(SECOND_PLAYER_ID)
                .exists();

        when.secondPlayerMakeMove(4);


        then.assertFirstPlayersPitsAre(7, 7, 7, 7, 6, 6, 0)
                .and().assertSecondPlayersPitsAre(6, 6, 6, 6, 0, 7, 1)
                .and().assertGameIsNotOver()
                .and().assertItsFirstPlayerTurn()
                .and().assertGameWasSaved()
                .and().assertFirstPlayerWasNotified()
                .and().assertSecondPlayerWasNotified();
    }

    @Test
    public void testGameRules_distributeStones_shouldNotPutStonesOnOpponentsBigPit() throws Exception {

        given.aGameInProgress()
                .withCurrentBoard(new int[][]{{6, 6, 6, 6, 6, 6, 0}, {6, 6, 6, 6, 3, 9, 0}})
                .withPlayerTurn(SECOND_PLAYER_ID)
                .exists();


        when.secondPlayerMakeMove(5);

        then.assertFirstPlayersPitsAre(7, 7, 7, 7, 7, 7, 0)
                .and().assertSecondPlayersPitsAre(7, 7, 6, 6, 3, 0, 1)
                .and().assertItsFirstPlayerTurn()
                .and().assertGameIsNotOver()
                .and().assertGameWasSaved()
                .and().assertFirstPlayerWasNotified()
                .and().assertSecondPlayerWasNotified();
    }

    @Test
    public void testGameRules_victoryCondition_playerTwoWins() throws Exception {

        given.aGameInProgress()
                .withCurrentBoard(new int[][]{{0, 0, 0, 6, 0, 0, 18}, {0, 1, 0, 0, 0, 0, 14}})
                .withPlayerTurn(SECOND_PLAYER_ID)
                .exists();

        when.secondPlayerMakeMove(1);


        then.assertFirstPlayersPitsAre(0, 0, 0, 0, 0, 0, 18)
                .and().assertSecondPlayersPitsAre(0, 0, 0, 0, 0, 0, 21)
                .and().assertSecondlayerWon()
                .and().assertGameIsOver()
                .and().assertGameWasSaved()
                .and().assertFirstPlayerWasNotified()
                .and().assertSecondPlayerWasNotified();
    }

    @Test
    public void testGameRules_victoryCondition_playerOneWins() throws Exception {

        given.aGameInProgress()
                .withCurrentBoard(new int[][]{{0, 0, 1, 0, 0, 0, 14}, {0, 0, 6, 6, 0, 0, 18}})
                .withPlayerTurn(FIRST_PLAYER_ID)
                .exists();

        when.firstPlayerMakeMove(2);

        then.assertFirstPlayersPitsAre(0, 0, 0, 0, 0, 0, 21)
                .and().assertSecondPlayersPitsAre(0, 0, 0, 6, 0, 0, 18)
                .and().assertFirstPlayerWon()
                .and().assertGameIsOver()
                .and().assertGameWasSaved()
                .and().assertFirstPlayerWasNotified()
                .and().assertSecondPlayerWasNotified();

    }

    @Test(expected = InexistentPlayerException.class)
    public void testJoinPlayer_inexistentPlayer() throws Exception {

        //given exists no player with id inexistentPlayerId

        when.playerRequestsToJoin("inexistingPlayer");

        //assert that InexistentPlayerException is thrown
    }

    @Test(expected = InexistentGameException.class)
    public void testMoveStones_inexistentGame() throws Exception {

        //given exists no game

        when.playerMakeMoveWithInexistGame();

        //assert that InexistentGameException is thrown
    }

    @Test(expected = MovementNotAllowedException.class)
    public void testMoveStones_itsNotPlayersTurn() throws Exception {

        given.aGameInProgress()
                .withPlayerTurn(SECOND_PLAYER_ID)
                .exists();

        when.firstPlayerMakeMove(0);

        //assert that MovementNotAllowedException is thrown
    }

    @Test(expected = IllegalMovementException.class)
    public void testMoveStones_pitIsEmpty() throws Exception {

        given.aGameInProgress()
                .withCurrentBoard(new int[][]{{0, 6, 0, 0, 6, 0, 18}, {1, 0, 0, 0, 0, 0, 14}})
                .withPlayerTurn(SECOND_PLAYER_ID)
                .exists();

        when.secondPlayerMakeMove(1);

        //assert that IllegalMovementException is thrown
    }

    @Test(expected = IllegalMovementException.class)
    public void testMoveStones_pitIsMancala() throws Exception {

        given.aGameInProgress()
                .withCurrentBoard(new int[][]{{0, 6, 0, 0, 6, 0, 18}, {1, 0, 0, 0, 0, 0, 14}})
                .withPlayerTurn(SECOND_PLAYER_ID)
                .exists();

        when.secondPlayerMakeMove(6);

        //assert that IllegalMovementException is thrown
    }


}
