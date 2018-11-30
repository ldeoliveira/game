package com.bol.game.pojos;

public final class Lobby {

    private Player firstPlayer;

    private Player secondPlayer;

    public Lobby() {
    }

    public Lobby(Player firstPlayer) {
        this.firstPlayer = firstPlayer;
    }

    public Lobby(Player firstPlayer, Player secondPlayer) {
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
    }

    public Player getFirstPlayer() {
        return firstPlayer;
    }

    public Player getSecondPlayer() {
        return secondPlayer;
    }

    public boolean isEmpty() {
        return firstPlayer == null && secondPlayer == null;

    }

    public boolean isFull() {
        return firstPlayer != null && secondPlayer != null;

    }

    public Player getWaitingPlayer() {
        return firstPlayer != null ? firstPlayer : secondPlayer;

    }

    public boolean hasInsufficientPlayers() {
        return firstPlayer == null ||  secondPlayer == null;

    }
}
