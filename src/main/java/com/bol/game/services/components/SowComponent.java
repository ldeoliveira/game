package com.bol.game.services.components;

import com.bol.game.pojos.Pit;
import org.springframework.stereotype.Component;

import static com.bol.game.services.GameEngineService.MANCALA;

@Component
public class SowComponent {

    public Pit sow(int pit, int row, int[][] board) {

        int stones = board[row][pit];

        board[row][pit] = 0;
        boolean myMancalaPassed = false;

        int lastPit = -1;
        int lastRow = -1;

        while (stones > 0) {

            pit = (pit + 1) % board[row].length;
            // check if it's the player's own mancala
            if (pit == MANCALA && !myMancalaPassed) {
                myMancalaPassed = true;
                board[row][pit]++;
                lastPit = pit;
                lastRow = row;
                row = (row + 1) % 2;
            }
            // otherwise, check if it's the opponent's mancala
            else if (pit == MANCALA) {
                myMancalaPassed = false;
                row = (row + 1) % 2;
                continue;
            }
            // if hits this branch, then it's a regular pit
            else {
                board[row][pit]++;
                lastPit = pit;
                lastRow = row;
            }

            stones--;
        }
        return new Pit(lastRow, lastPit);
    }

}
