package com.bol.game.services.components;

import com.bol.game.pojos.Game;
import com.bol.game.pojos.Pit;
import org.springframework.stereotype.Component;

import static com.bol.game.services.GameEngineService.FIRST_PLAYER;
import static com.bol.game.services.GameEngineService.MANCALA;

@Component
public class TurnControlComponent {

    public void updatePlayerTurn(Game game, int currentPlayer, Pit lastPit) {
        if (!playerShouldGetAFreeTurn(game, currentPlayer, lastPit)) {
            String next = nextPlayer(game, currentPlayer);
            game.setTurnOfWithId(next);
        }
    }

    private boolean playerShouldGetAFreeTurn(Game game, int playerRow, Pit lastPit) {
        return !game.isOver() && lastPit.getPit() == MANCALA && lastPit.getRow() == playerRow;
    }

    private String nextPlayer(Game game, int playerRow) {
        return playerRow == FIRST_PLAYER ?
                game.getSecondPlayer().getId() :
                game.getFirstPlayer().getId();
    }
}
