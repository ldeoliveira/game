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
        int row = lastPit.getRow();
        int pit = lastPit.getPit();
        int opponentRow = (row + 1) % 2;
        int myStones = board[row][pit];
        int opponentStones = board[opponentRow][MANCALA - 1  - pit];
        board[row][MANCALA] += myStones + opponentStones;
        board[row][pit] = 0;
        board[opponentRow][MANCALA - 1  - pit] = 0;
    }
}
