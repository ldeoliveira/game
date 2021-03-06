package com.bol.game.pojos;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import static com.bol.game.services.GameLobbyService.NUMBER_OF_PITS;
import static com.bol.game.services.GameLobbyService.STONES_PER_PIT;

@Document(collection = "games")
public class Game {

    @Id
    private String id;

    private Player firstPlayer;

    private Player secondPlayer;

    private String turnOfWithId;

    private String winner;

    private boolean isOver;

    public Game(Player firstPlayer) {
        this.firstPlayer = firstPlayer;
    }

    public Game(Player firstPlayer, Player secondPlayer) {
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
        turnOfWithId = firstPlayer.getId();
        firstPlayer.setPits(initRow());
        secondPlayer.setPits(initRow());
    }

    public Game() {

    }

    public Player getFirstPlayer() {
        return firstPlayer;
    }

    public void setFirstPlayer(Player firstPlayer) {
        this.firstPlayer = firstPlayer;
    }

    public Player getSecondPlayer() {
        return secondPlayer;
    }

    public void setSecondPlayer(Player secondPlayer) {
        this.secondPlayer = secondPlayer;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public String getTurnOfWithId() {
        return turnOfWithId;
    }

    public void setTurnOfWithId(String turnOfWithId) {
        this.turnOfWithId = turnOfWithId;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public boolean isOver() {
        return isOver;
    }

    public void setOver(boolean over) {
        isOver = over;
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
