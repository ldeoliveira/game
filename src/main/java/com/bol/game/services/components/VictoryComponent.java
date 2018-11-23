package com.bol.game.services.components;

import com.bol.game.pojos.Game;
import org.springframework.stereotype.Component;

import static com.bol.game.services.GameEngineService.MANCALA;

@Component
public class VictoryComponent {

    public void verifyVictoryFor(Game game) {
        if (isAnySideEmpty(game)) {
            game.setOver(true);
            game.setWinner(getWinnerFor(game));
        }
    }

    private String getWinnerFor(Game game) {
        // in case of ties, player who started wins :)
        return game.getFirstPlayer().getPits()[MANCALA] >= game.getSecondPlayer().getPits()[MANCALA] ?
                game.getFirstPlayer().getId() :
                game.getSecondPlayer().getId();
    }

    private boolean isAnySideEmpty(Game game) {
        return isRowEmpty(game.getFirstPlayer().getPits())
                || isRowEmpty(game.getSecondPlayer().getPits());
    }

    private boolean isRowEmpty(int[] row) {
        for (int i = 0; i < row.length - 1; i++) {
            if (row[i] != 0) {
                return false;
            }
        }
        return true;
    }
}
