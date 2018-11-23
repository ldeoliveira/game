package com.bol.game.services.components;

import com.bol.game.pojos.Pit;
import org.springframework.stereotype.Component;

import static com.bol.game.services.GameEngineService.MANCALA;

@Component
public class CaptureComponent {

    public void captureStonesFrom(Pit lastPit, int playerRow, int[][] board) {
        if (shouldCaptureStonesFrom(lastPit, playerRow, board)) {
            captureStonesFrom(lastPit, board);
        }
    }

    private boolean shouldCaptureStonesFrom(Pit lastPit, int playerRow, int[][] board) {
        return playerRow == lastPit.getRow()
                && board[playerRow][lastPit.getPit()] == 1
                && lastPit.getPit() != MANCALA;
    }

    private void captureStonesFrom(Pit lastPit, int[][] board) {
        int myRow = lastPit.getRow();
        int myPit = lastPit.getPit();
        int opponentRow = (myRow + 1) % 2;
        int myStones = board[myRow][myPit];
        int opponentStones = board[opponentRow][MANCALA - 1 - myPit];
        board[myRow][MANCALA] += myStones + opponentStones;
        board[myRow][myPit] = 0;
        board[opponentRow][MANCALA - 1 - myPit] = 0;
    }
}
