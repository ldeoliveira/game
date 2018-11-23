package com.bol.game.services;

import com.bol.game.exceptions.IllegalMovementException;
import com.bol.game.pojos.Game;
import com.bol.game.pojos.Pit;
import com.bol.game.repositories.GameRepository;
import com.bol.game.services.components.CaptureComponent;
import com.bol.game.services.components.SowComponent;
import com.bol.game.services.components.TurnControlComponent;
import com.bol.game.services.components.VictoryComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameEngineService {

    private final GameRepository gameRepository;
    private final SowComponent sowComponent;
    private final VictoryComponent victoryComponent;
    private final CaptureComponent captureComponent;
    private final TurnControlComponent turnComponent;

    @Autowired
    public GameEngineService(GameRepository gameRepository, SowComponent sowComponent,
                             VictoryComponent victoryComponent, CaptureComponent captureComponent,
                             TurnControlComponent turnComponent) {
        this.gameRepository = gameRepository;
        this.sowComponent = sowComponent;
        this.victoryComponent = victoryComponent;
        this.captureComponent = captureComponent;
        this.turnComponent = turnComponent;
    }

    public final static int MANCALA = 6;
    public final static int FIRST_PLAYER = 0;
    public final static int SECOND_PLAYER = 1;

    public Game performMovement(Game game, int pitId) {

        int actingPlayer = getCurrentPlayerBoardRow(game);

        int[][] board = new int[][]{
                game.getFirstPlayer().getPits(),
                game.getSecondPlayer().getPits()};

        if (board[actingPlayer][pitId] == 0 || pitId == MANCALA) {
            throw new IllegalMovementException();
        }

        Pit lastPit = sowComponent.sow(pitId, actingPlayer, board);
        captureComponent.captureStonesFrom(lastPit, actingPlayer, board);
        victoryComponent.verifyVictoryFor(game);
        turnComponent.updatePlayerTurn(game, actingPlayer, lastPit);

        return gameRepository.save(game);
    }

    private int getCurrentPlayerBoardRow(Game game) {
        return game.getTurnOfWithId().equals(game.getFirstPlayer().getId()) ?
                FIRST_PLAYER :
                SECOND_PLAYER;
    }

}
